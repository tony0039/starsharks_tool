package com.g.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.g.bean.StatusBean;
import com.mezingr.dao.PaginationList;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import com.g.model.KeyEntity;
import com.g.model.UserEntity;
import com.g.model.support.XDAOSupport;
import com.g.utils.AESUtil;
import com.mezingr.dao.HDaoUtils;

@Controller
@RequestMapping(value = "rest")
public class RestController extends XDAOSupport {

	public static String MAIN_NET = "https://bsc-dataseed.binance.org/";

	public Web3j web3j = Web3j.build(new HttpService(MAIN_NET));

	private Map<String,String> userPassword=new HashMap<String,String>();

	public Map<String,BigInteger> nonceMap=new HashMap<String,BigInteger>();

	public Map<String, String> getUserPassword() {
		return userPassword;
	}

	/**
	 * 将订单提交到合约交互
	 */
	@RequestMapping(value = "/order", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> index(@RequestParam(value = "order") String order,
			@RequestParam(value = "address") String from, @RequestParam(value = "gas") Integer gas,
			@RequestParam(value = "gasLimit") BigInteger gasLimit, HttpServletRequest request, ModelMap mm) {
		String to = "0x0dd8232F57D0563A88d8F1a4F539EB4558385D56"; // 租鱼合约地址 - 租鱼钱包地址
		Map<String, Object> result = new HashMap<String, Object>();
		KeyEntity key = this.getKeyEntityDAO().findUnique(HDaoUtils.eq("address", from).toCondition());
		result.put("message", "未知错误");
		result.put("code", "-1");
		try {
			String root =request.getSession().getServletContext().getRealPath("");
			String keystoreRoot=root+"keystore";
			try {
				String filePath=keystoreRoot+"\\"+key.getKeystoreName();
				Credentials credentials=getCredentials(userPassword.get(key.getUser().getUserName()),key,filePath);//通过密码和keystore来解锁
				Web3j web3j = Web3j.build(new HttpService(MAIN_NET));
				BigInteger gasPrice = BigInteger.valueOf(Convert.toWei(gas + "", Convert.Unit.GWEI).longValue());
				String data = order;
				// 不同交易nonce应该不同，相同的nonce会产生覆盖
				BigInteger nonce = getNonce(from,0);
				RawTransaction rawTx = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to,BigInteger.valueOf(0), data);
				// 签名和转换成16进制
				byte[] signedMessage = TransactionEncoder.signMessage(rawTx, credentials);
				String hexValue = Numeric.toHexString(signedMessage);
				System.out.println("order:" + order);
				System.out.println("address:" + from);
				System.out.println("gas:" + gasPrice);
				System.out.println("gasLimit:" + gasLimit);
				System.out.println("nonce:" + nonce);
				EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
				if (ethSendTransaction.getError() != null) {
					System.out.println(ethSendTransaction.getError().getMessage());
					result.put("message", ethSendTransaction.getError().getMessage());
					result.put("code", "-1");
				} else {
					result.put("message", "广播成功");
					result.put("code", "0");
				}
				String hash = ethSendTransaction.getTransactionHash();
				result.put("hash", hash);
				result.put("address", from);
				return result;
			} catch (Exception e) {
				e.printStackTrace();
				result.put("message", e.getMessage());
				result.put("code", "-1");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			result.put("code", "-1");
		}
		return result;
	}

	@RequestMapping(value = "/password",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> setPassword(@RequestParam(value = "userId") String userId,
	   	@RequestParam(value = "password") String pwd,HttpServletRequest request, ModelMap mm) {
		UserEntity user=(UserEntity)request.getSession().getAttribute("loginUser");
		Map<String, Object> result = new HashMap<String, Object>();
		if(user==null){
			result.put("message", "登录超时");
			return result;
		}else if(user.getUserName().equals("admin")){
			UserEntity ue=this.getUserEntityDAO().get(userId);
			if(ue==null){
				result.put("code", -1);
				result.put("message", "账号不存在");
				return result;
			}
			PaginationList<KeyEntity> pages = this.getKeyEntityDAO().list(HDaoUtils.eq("user",ue).toCondition(),1,1,Order.desc("addTime"));
			if(pages.getItems()==null|| pages.getItems().size()==0){
				result.put("code", -1);
				result.put("message", "账号:"+ue.getUserName()+"没有可解锁的keystore");
				return result;
			}
			KeyEntity key =pages.getItems().get(0);
			String root =request.getSession().getServletContext().getRealPath("");
			String keystoreRoot=root+"keystore";
			try {
				String filePath=keystoreRoot+"\\"+key.getKeystoreName();
				getCredentials(pwd,key,filePath);
				result.put("message", "success");
				userPassword.put(ue.getUserName(),pwd);
			} catch (IOException e) {
				e.printStackTrace();
				result.put("message", "exception");
			} catch (CipherException e) {
				result.put("message", "password error");
			}
			return result;
		}else {
			result.put("message", "not admin");
			return result;
		}
	}
	
	public Credentials getCredentials(String pwd,KeyEntity key,String filePath) throws IOException, CipherException {
		File file=new File(filePath);
		if(pwd==null){
			throw new CipherException("password is null");
		}
		if (file.exists()){//keystore是从数据库取出来的，然后映射读取服务器硬盘文件，如果服务器没有则写入服务器
			return WalletUtils.loadCredentials(pwd,filePath);
		}else {
			file.createNewFile();
			FileOutputStream outStream = new FileOutputStream(file);	//文件输出流用于将数据写入文件
			outStream.write(key.getKeystore().getBytes());
			outStream.close();	//关闭文件输出流
			return WalletUtils.loadCredentials(pwd,filePath);
		}
	}

	/**
	 * 授权账号
	 */
	@RequestMapping(value = "/auth", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> auth(@RequestParam(value = "address") String address,
			@RequestParam(value = "message") String message,
			@RequestParam(value = "messageSign") String messageSign, HttpServletRequest request, ModelMap mm) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("account", address);
		result.put("message", message);
		result.put("referer", "");
		result.put("code", 0);
		KeyEntity key = this.getKeyEntityDAO().findUnique(HDaoUtils.eq("address", address).toCondition());
		String root =request.getSession().getServletContext().getRealPath("");
		String keystoreRoot=root+"keystore";
		try {
			String filePath=keystoreRoot+"\\"+key.getKeystoreName();
			Credentials credentials=getCredentials(userPassword.get(key.getUser().getUserName()),key,filePath);//通过密码和keystore来解锁
			byte[] hash = message.getBytes(StandardCharsets.UTF_8);
			Sign.SignatureData signature = Sign.signPrefixedMessage(hash, credentials.getEcKeyPair());
			String r = Numeric.toHexString(signature.getR());
			String s = Numeric.toHexString(signature.getS()).substring(2);
			String v = Numeric.toHexString(signature.getV()).substring(2);
			String s1 = new StringBuilder(r).append(s).append(v).toString();
			System.out.println("message:"+message);
			System.out.println("messageSign:"+messageSign);
			System.out.println("hexsign:"+s1);
			result.put("hexsign", s1);
		} catch (IOException e) {
			result.put("code", -1);
			result.put("error", "读取文件异常！");
		} catch (CipherException e) {
			result.put("code", -1);
			result.put("error", "解锁密码不正确，请联系管理员!");
		}
		return result;
	}


	/**
	 * 保存授权
	 */
	@RequestMapping(value = "/saveAuth", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveAuth(@RequestParam(value = "address") String address,
			@RequestParam(value = "authorization") String authorization,
			@RequestParam(value = "qrauth") String qrauth,HttpServletRequest request, ModelMap mm) {
		Map<String, Object> result = new HashMap<String, Object>();
		KeyEntity key = this.getKeyEntityDAO().findUnique(HDaoUtils.eq("address", address).toCondition());
		key.setIsAuth(true);
		key.setAuth(authorization);
		key.setQrauth(qrauth);
		this.getKeyEntityDAO().update(key);
		return result;
	}
	/**
	 * 批准交易
	 */
	@RequestMapping(value = "/approval", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> approval(@RequestParam(value = "from") String from, HttpServletRequest request, ModelMap mm) {
		String to = "0x26193c7fa4354ae49ec53ea2cebc513dc39a10aa"; // 批准交易的合约地址
		String data="0x095ea7b3000000000000000000000000e9e092e46a75d192d9d7d3942f11f116fd2f7ca9ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
		Map<String, Object> result = new HashMap<String, Object>();
		KeyEntity key = this.getKeyEntityDAO().findUnique(HDaoUtils.eq("address", from).toCondition());
		String root =request.getSession().getServletContext().getRealPath("");
		String keystoreRoot=root+"keystore";
		try {
			String filePath=keystoreRoot+"\\"+key.getKeystoreName();
			Credentials credentials=getCredentials(userPassword.get(key.getUser().getUserName()),key,filePath);//通过密码和keystore来解锁
			//开始发送批准合约
			BigInteger gasPrice = BigInteger.valueOf(Convert.toWei(5+"", Convert.Unit.GWEI).longValue());
			// 不同交易nonce应该不同，相同的nonce会产生覆盖
			BigInteger nonce = getNonce(from,0);
			BigInteger gasLimit=BigInteger.valueOf(60000);
			RawTransaction rawTx = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to,
					BigInteger.valueOf(0), data);
			// 签名和转换成16进制
			System.out.println("address:" + from);
			System.out.println("gas:" + gasPrice);
			System.out.println("gasLimit:" + gasLimit);
			System.out.println("nonce:" + nonce);
			byte[] signedMessage = TransactionEncoder.signMessage(rawTx, credentials);
			String hexValue = Numeric.toHexString(signedMessage);
			EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
			if (ethSendTransaction.getError() != null) {
				System.out.println(ethSendTransaction.getError().getMessage());
				result.put("message", ethSendTransaction.getError().getMessage());
				result.put("code", "-1");
			} else {
				result.put("message", "广播成功");
				result.put("code", "0");
			}
			String hash = ethSendTransaction.getTransactionHash();
			result.put("hash", hash);
		}catch (Exception e) {
			e.printStackTrace();
			result.put("message", "未知错误");
			result.put("code", "-1");
		}
		return result;
	}

	/**
	 * 测试
	 */
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@ResponseBody
	public StatusBean auth(@RequestParam(value = "from") String from,HttpServletRequest request, ModelMap mm) {
		try {
			BigInteger nonce=getNonce(from,0);
			return new StatusBean(0,"success",nonce);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}


	public BigInteger getNonce(String from,Integer times) throws IOException, InterruptedException {
		EthGetTransactionCount transactionCount = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.LATEST).send();
		BigInteger nonce = transactionCount.getTransactionCount();
		BigInteger n=nonceMap.get(from);
		if(n!=null){
			if(nonce.compareTo(n)==0){
				if(times>2){
					return nonce;
				}
				System.out.println("nonce already used,reload");
				Thread.sleep(5000);
				times+=1;
				return getNonce(from,times);
			}
		}
		nonceMap.put(from,nonce);
		return nonce;
	}

}
