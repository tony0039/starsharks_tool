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
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
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
@RequestMapping(value = "transferShark")
public class TransferSharkController extends XDAOSupport {

	@Autowired
	private RestController rc;

	@Autowired
	private TransferController tc;

	private Web3j web3j = Web3j.build(new HttpService(rc.MAIN_NET));


	/**
	 * 前往转鱼
	 */
	@RequestMapping(method=RequestMethod.GET)
	public String toTransferShark(HttpServletRequest request,ModelMap mm){
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
		return "transfer_shark";
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
		address=address.trim();
		UserEntity search=user;
		if(!userId.equals("")) {
			search = this.getUserEntityDAO().get(userId);
		}
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
	 * @param sharkId 转出鱼id
	 * @param request
	 * @param mm
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	@ResponseBody
	public StatusBean transfer(@RequestParam(value = "from") String from,
							   @RequestParam(value = "to") String to,
							   @RequestParam(value = "sharkId") Integer sharkId,
							   HttpServletRequest request,ModelMap mm){
		UserEntity user=(UserEntity)request.getSession().getAttribute("loginUser");
		if(user==null){
			return new StatusBean(-1,"登录超时",null);
		}
		if(from.equals(to)) {
			return new StatusBean(-1, "转账账户和目标账户不能相同", null);
		}
		if(!tc.validateAddress(to,request)){
			return new StatusBean(-1,"NFT接收者解锁失败,转账驳回",null);
		}
		return transferShark(request,from,to,sharkId);
	}
	public StatusBean transferShark(HttpServletRequest request,String from,String to,Integer value){
		BigInteger bnbValue=BigInteger.valueOf(0);
		try {
			bnbValue=tc.balance(from);
		} catch (IOException e) {
			return new StatusBean(-1,e.getMessage(),null);
		}
		String contact="0x416f1D70c1C22608814d9f36c492EfB3Ba8cad4c";//转账地址 nftshark
		String data="0x23b872dd";
		data+=tc.fillZero(from);
		data+=tc.fillZero(to);
		data+=tc.fillZero(Numeric.toHexStringNoPrefix(BigInteger.valueOf(value)));
		System.out.println("data:" + data);
		KeyEntity key = this.getKeyEntityDAO().findUnique(HDaoUtils.eq("address", from).toCondition());
		String root =request.getSession().getServletContext().getRealPath("");
		String keystoreRoot=root+"keystore";
		try {
			String filePath=keystoreRoot+"\\"+key.getKeystoreName();
			Credentials credentials=rc.getCredentials(rc.getUserPassword().get(key.getUser().getUserName()),key,filePath);//通过密码和keystore来解锁
			BigInteger gasPrice = BigInteger.valueOf(Convert.toWei(5+"", Convert.Unit.GWEI).longValue());
			BigInteger nonce = rc.getNonce(from,0);
			Transaction transaction=new Transaction(from,nonce,BigInteger.valueOf(0),BigInteger.valueOf(0),contact,BigInteger.valueOf(0),data);
			BigInteger gasPriceEst=tc.GasSum(transaction);
			BigInteger xiaohao=gasPriceEst.multiply(BigInteger.valueOf(5));
			System.out.println("from:" + from);
			System.out.println("to:" + to);
			System.out.println("bnbValue:" + bnbValue);
			System.out.println("nonce:" + nonce);
			if(xiaohao.compareTo(bnbValue)==1){
				return new StatusBean(-1,"BNB不足支付gas费",null);
			}
			BigInteger gasLimit=BigInteger.valueOf(400000);
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
			//return new StatusBean(1,null,null);
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

}
