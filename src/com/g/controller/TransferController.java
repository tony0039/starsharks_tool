package com.g.controller;

import com.g.bean.KeyBean;
import com.g.bean.StatusBean;
import com.g.bean.UserBean;
import com.g.model.KeyEntity;
import com.g.model.UserEntity;
import com.g.model.support.XDAOSupport;
import com.mezingr.dao.Exp;
import com.mezingr.dao.HDaoUtils;
import org.hibernate.criterion.Criterion;
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
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 账号管理控制器
 */
@Controller
@RequestMapping(value = "transfer")
public class TransferController extends XDAOSupport {

	@Autowired
	private RestController rc;

	private Web3j web3j = Web3j.build(new HttpService(rc.MAIN_NET));

	/**
	 * 前往转账页面
	 */
	@RequestMapping(method=RequestMethod.GET)
	public String toTransfer(HttpServletRequest request,ModelMap mm){
		UserEntity user=(UserEntity)request.getSession().getAttribute("loginUser");
		if(user==null){
			return "login";
		}
		List<UserEntity> users=this.getUserEntityDAO().list(HDaoUtils.eq("status",1).toCondition());
		List<UserBean> results=new ArrayList<UserBean>();
		for(UserEntity ue : users){
			results.add(new UserBean(ue));
		}
		mm.put("users",results);
		return "transfer";
	}

	/**
	 * 根据账号关键字查询用户下的账号列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public StatusBean list(@RequestParam(value = "userId",defaultValue = "") String userId,
						   @RequestParam(value = "address",defaultValue = "") String address,
								 HttpServletRequest request, ModelMap mm){
		UserEntity user=(UserEntity)request.getSession().getAttribute("loginUser");
		if(user==null){
			return new StatusBean(-1,"登录超时",null);
		}
		UserEntity search=user;
		if(!userId.equals("")) {
			search = this.getUserEntityDAO().get(userId);
		}
		address=address.trim();
		Exp<Criterion> exp=HDaoUtils.eq("status",1).andEq("user", search);
		if(!address.equals("")){
			exp.andLike("address",address);
		}
		List<KeyEntity> list=this.getKeyEntityDAO().list(exp.toCondition(), Order.asc("sort"));
		List<KeyBean> result=new ArrayList<KeyBean>();
		for(KeyEntity ke:list) {
			result.add(new KeyBean(ke));
		}
		return new StatusBean(0,"success",result);
	}

	/**
	 * 转账
	 * @param from 转账来源
	 * @param to 转账到账户
	 * @param type 类型（1.SEA 2.BNB）
	 * @param value 金额
	 * @param request
	 * @param mm
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	@ResponseBody
	public StatusBean transfer(@RequestParam(value = "from") String from,
							   @RequestParam(value = "to") String to,
							   @RequestParam(value = "type") Integer type,
							   @RequestParam(value = "value") BigDecimal value,
							   @RequestParam(value = "nonce",defaultValue = "0")BigInteger nonce,
							   HttpServletRequest request,ModelMap mm){
		UserEntity user=(UserEntity)request.getSession().getAttribute("loginUser");
		if(user==null){
			return new StatusBean(-1,"登录超时",null);
		}
		if(from.equals(to)) {
			return new StatusBean(-1, "转账账户和目标账户不能相同", null);
		}
		if(!validateAddress(to,request)){
			return new StatusBean(-1,"接收者解锁失败,转账驳回",null);
		}
		if(type==1){
			return transferSea(request,from,to,value,nonce);
		}else if(type==2){
			return transferBnb(request,from,to,value,nonce);
		}
		return new StatusBean(0,"成功",null);
	}

	public StatusBean transferSea(HttpServletRequest request,String from,String to,BigDecimal value,BigInteger nonce){
		BigInteger v= new BigInteger(Convert.toWei(value + "", Convert.Unit.ETHER).setScale(0).toString());
		BigInteger bnbValue=BigInteger.valueOf(0);
		try {
			System.out.println("value:" + v);
			BigInteger balanceSea=balanceSea(from);
			bnbValue=balance(from);
			if(v.compareTo(balanceSea)==1){//如果要转的金额大于余额，则直接把余额全转过去
				v=balanceSea;
			}
			System.out.println("from:" + from);
			System.out.println("to:" + to);
			System.out.println("balanceSea:" + balanceSea);
			System.out.println("bnbValue:" + bnbValue);
			System.out.println("value:" + v);
		} catch (IOException e) {
			return new StatusBean(-1,e.getMessage(),null);
		}
		String contact="0x26193C7fa4354AE49eC53eA2cEBC513dc39A10aa";//转账合约地址
		KeyEntity key = this.getKeyEntityDAO().findUnique(HDaoUtils.eq("address", from).toCondition());
		String root =request.getSession().getServletContext().getRealPath("");
		String data="0xa9059cbb";
		data+=fillZero(to);
		data+=fillZero(v.toString(16));
		System.out.println("data:" + data);
		String keystoreRoot=root+"keystore";
		try {
			String filePath=keystoreRoot+"\\"+key.getKeystoreName();
			Credentials credentials=rc.getCredentials(rc.getUserPassword().get(key.getUser().getUserName()),key,filePath);//通过密码和keystore来解锁
			BigInteger gasPrice = BigInteger.valueOf(Convert.toWei(5+"", Convert.Unit.GWEI).longValue());
			if(nonce.compareTo(BigInteger.valueOf(0))==0) {
				nonce = rc.getNonce(from,0);
			}
			Transaction transaction=new Transaction(from,nonce,BigInteger.valueOf(0),BigInteger.valueOf(0),contact,BigInteger.valueOf(0),data);
			BigInteger gasPriceEst=GasSum(transaction);
			BigInteger xiaohao=gasPriceEst.multiply(BigInteger.valueOf(5));
			System.out.println("xiaohao:" + xiaohao);
			System.out.println("bnbValue:" + bnbValue);
			System.out.println("nonce:" + nonce);
			if(xiaohao.compareTo(bnbValue)==1){
				return new StatusBean(-1,"BNB不足支付gas费",null);
			}
			BigInteger gasLimit=BigInteger.valueOf(60000);

			RawTransaction rawTx = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contact,
					BigInteger.valueOf(0), data);
			byte[] signedMessage = TransactionEncoder.signMessage(rawTx, credentials);
			String hexValue = Numeric.toHexString(signedMessage);
			EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
			if (ethSendTransaction.getError() != null) {
				System.out.println(ethSendTransaction.getError().getMessage());
				System.out.println(ethSendTransaction.getError().getMessage());
				return new StatusBean(-1,ethSendTransaction.getError().getMessage(),null);
			} else {
				System.out.println("广播成功");
			}
			String hash = ethSendTransaction.getTransactionHash();
			StatusBean bean =new StatusBean(0,null,hash);
			bean.setNonce(nonce);
			return bean;
		}catch (IOException e) {
			return new StatusBean(-1,"keystore读取错误",null);
		} catch (CipherException e) {
			return new StatusBean(-1,"解锁密码错误",null);
		} catch (ExecutionException e) {
			return new StatusBean(-1,e.getMessage(),null);
		} catch (InterruptedException e) {
			return new StatusBean(-1,e.getMessage(),null);
		}
	}
	public StatusBean transferBnb(HttpServletRequest request,String from,String to,BigDecimal value,BigInteger nonce){
		BigInteger v= new BigInteger(Convert.toWei(value + "", Convert.Unit.ETHER).setScale(0).toString());//将value转换成wei后去掉小数位后转换成BigInteger
		try {
			//BigInteger gas=requestCurrentGasPrice();//获取交易的gas费
			BigInteger bnbValue=balance(from);  //获取当前账号的bnb余额
			if(nonce.compareTo(BigInteger.valueOf(0))==0) {
				nonce = rc.getNonce(from,0);
			}
			Transaction transaction=new Transaction(from,nonce,BigInteger.valueOf(0),BigInteger.valueOf(0),to,BigInteger.valueOf(1),null);
			BigInteger gas=GasSum(transaction);//获取交易的gas费
			BigInteger xiaohao=gas.multiply(BigInteger.valueOf(5));
			System.out.println("xiaohao:" + xiaohao);
			BigInteger keyong=bnbValue.subtract(xiaohao);//可用金额等于当前余额减去gas费
			if(v.compareTo(keyong)==1){//如果要转的金额大于可用金额，则直接把可用金额全转过去
				v=keyong;
			}
			if(xiaohao.compareTo(keyong)==1){//消耗的gas费比余额还高，则转账失败
				return new StatusBean(-1,"余额不足以支付gas",null);
			}
			System.out.println("gasprice:"+xiaohao);
			System.out.println("bnbValue:"+bnbValue);
			System.out.println("keyong:"+keyong);
			System.out.println("fact:"+v);
		} catch (IOException e) {
			return new StatusBean(-1,e.getMessage(),null);
		} catch (Exception e) {
			return new StatusBean(-1,e.getMessage(),null);
		}
		KeyEntity key = this.getKeyEntityDAO().findUnique(HDaoUtils.eq("address", from).toCondition());
		String root =request.getSession().getServletContext().getRealPath("");
		String keystoreRoot=root+"keystore";
		try {
			String filePath=keystoreRoot+"\\"+key.getKeystoreName();
			Credentials credentials=rc.getCredentials(rc.getUserPassword().get(key.getUser().getUserName()),key,filePath);
			TransactionReceipt transactionReceipt = Transfer.sendFunds(web3j, credentials, to,new BigDecimal(v), Convert.Unit.WEI).send();
			String hash = transactionReceipt.getTransactionHash();
			StatusBean bean =new StatusBean(0,null,hash);
			bean.setNonce(nonce);
			return bean;
			//return new StatusBean(0,null,null);
		}catch (IOException e) {
			return new StatusBean(-1,"keystore读取错误",null);
		} catch (CipherException e) {
			return new StatusBean(-1,"解锁密码错误",null);
		} catch (Exception e) {
			return new StatusBean(-1,e.getMessage(),null);
		}
	}

	/**
	 * 预估gas费
	 * @param transaction 交易信息
	 * @return
	 * @throws IOException
	 */
	public BigInteger GasSum(Transaction transaction) throws IOException {
		EthEstimateGas gas=web3j.ethEstimateGas(transaction).send();
		BigInteger gasPriceEst =BigInteger.valueOf(Convert.toWei(Integer.parseInt(gas.getResult().substring(2),16)+"",Convert.Unit.GWEI).longValue());
		return gasPriceEst;
	}

	/**
	 * 获取当前以太坊网络中最近一笔交易的gasPrice
	 * @return
	 * @throws Exception
	 */
	public BigInteger requestCurrentGasPrice() throws Exception {
		EthGasPrice ethGasPrice = web3j.ethGasPrice().sendAsync().get();
		return ethGasPrice.getGasPrice();
	}

	/**
	 * 查询BNB余额
	 * @param from 查询地址
	 * @return
	 * @throws IOException
	 */
	public BigInteger balance(String from) throws IOException {
		return web3j.ethGetBalance(from, DefaultBlockParameterName.LATEST).send().getBalance();
	}

	/**
	 * 查询SEA余额
	 * @param from 查询地址
	 * @return
	 * @throws IOException
	 */
	public BigInteger balanceSea(String from) throws IOException {
		String contact="0x26193c7fa4354ae49ec53ea2cebc513dc39a10aa";
		String data="0x70a08231000000000000000000000000"+Numeric.cleanHexPrefix(from);
		Transaction transaction=new Transaction(from,BigInteger.valueOf(0),BigInteger.valueOf(5),BigInteger.valueOf(25000),contact,BigInteger.valueOf(0),data);
		String result=web3j.ethCall(transaction,DefaultBlockParameterName.LATEST).send().getResult();
		return Numeric.toBigInt(result);
	}

	public String fillZero(String str){
		str=str.replace("0x","");
		String result="";
		if(str.length()>63){
			return str;
		}
		for(int i=str.length();i<64;i++){
			result+="0";
		}
		result+=str;
		return result;
	}

	/**
	 * 校验接收转账的地址是否属于能解锁
	 * @return
	 */
	public Boolean validateAddress(String address,HttpServletRequest request){
		KeyEntity key = this.getKeyEntityDAO().findUnique(HDaoUtils.eq("address", address).toCondition());
		String root =request.getSession().getServletContext().getRealPath("");
		String keystoreRoot=root+"keystore";
		String filePath=keystoreRoot+"\\"+key.getKeystoreName();
		try {
			Credentials credentials=rc.getCredentials(rc.getUserPassword().get(key.getUser().getUserName()),key,filePath);//通过密码和keystore来解锁
		} catch (IOException e) {
			return false;
		} catch (CipherException e) {
			return false;
		}
		return true;
	}

}
