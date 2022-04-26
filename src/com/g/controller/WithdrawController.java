package com.g.controller;

import com.g.bean.KeyBean;
import com.g.bean.StatusBean;
import com.g.model.KeyEntity;
import com.g.model.UserEntity;
import com.g.model.support.XDAOSupport;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.mezingr.dao.HDaoUtils;
import org.apache.poi.util.IOUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 提现管理控制器，包括下载游戏二维码
 */
@Controller
@RequestMapping(value = "withdraw")
public class WithdrawController extends XDAOSupport {

	@Autowired
	private RestController rc;

	private Web3j web3j = Web3j.build(new HttpService(rc.MAIN_NET));

	/**
	 * 前往提现页面
	 */
	@RequestMapping(method=RequestMethod.GET)
	public String withdraw(HttpServletRequest request,ModelMap mm){
		UserEntity user=(UserEntity)request.getSession().getAttribute("loginUser");
		if(user==null){
			return "login";
		}
		user=this.getUserEntityDAO().get(user.getId());
		List<KeyEntity> list=this.getKeyEntityDAO().list(HDaoUtils.eq("status", 1).andEq("user", user).toCondition(),Order.asc("sort"));
		List<KeyBean> result=new ArrayList<KeyBean>();
		for(KeyEntity ke:list) {
			result.add(new KeyBean(ke));
		}
		mm.put("list", result);
		return "withdraw";
	}


	/**
	 * 二维码下载
	 */
	@RequestMapping(value = "/download",method=RequestMethod.GET)
	@ResponseBody
	public StatusBean download(HttpServletRequest request, HttpServletResponse response, ModelMap mm){
		UserEntity user=(UserEntity)request.getSession().getAttribute("loginUser");
		if(user==null){
			return new StatusBean(-1,"登录超时",null);
		}
		ZipOutputStream zos = null;
		try {
			List<KeyEntity> list=this.getKeyEntityDAO().list(HDaoUtils.eq("user",user).andEq("status",1).andEq("isAuth",true).toCondition());
			String downloadFilename ="codes";//文件的名称
			response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
			response.setHeader("Content-Disposition", "attachment;filename=" + downloadFilename+".zip");// 设置在下载框默认显示的文件名
			zos = new ZipOutputStream(response.getOutputStream());
			for(KeyEntity ke :list){
				if(ke.getQrauth()!=null&&ke.getQrauth()!=""){
					zos.putNextEntry(new ZipEntry(ke.getKeystoreName()+".png"));//命名
					getBarCodeImgByUrl(ke.getQrauth(), zos);
				}
			};
			zos.flush();
			zos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//公共方法根据url生成二维码图片后写入输出流里
	public static void getBarCodeImgByUrl(String url, OutputStream os) throws WriterException,IOException{
		//二维码参数
		int width = 300; // 图像宽度
		int height = 300; // 图像高度
		String format = "gif";// 图像类型
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height, hints);
		MatrixToImageWriter.writeToStream(bitMatrix, format, os);
	}

	@RequestMapping(value = "/url", method = RequestMethod.GET)
	public void getUrl(@RequestParam(value="address")String address, HttpServletResponse response) {
		KeyEntity ke=this.getKeyEntityDAO().findUnique(HDaoUtils.eq("address",address).toCondition());
		String content=ke.getQrauth();
		int width = 300;
		int height = 300;
		//二维码的图片格式
		Hashtable hints = new Hashtable();
		//内容所使用编码
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		BitMatrix bitMatrix;
		try {
			if(content!=null) {
				bitMatrix = new MultiFormatWriter().encode(content,
						BarcodeFormat.QR_CODE, width, height, hints);
				BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(image, "gif", os);
				InputStream is = new ByteArrayInputStream(os.toByteArray());
				IOUtils.copy(is, response.getOutputStream());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 将订单提交到合约交互
	 */
	@RequestMapping(value = "/order", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> order(@RequestParam(value = "order") String order,
									 @RequestParam(value = "address") String from, HttpServletRequest request, ModelMap mm) {
		String to = "0xF63AfD42813116f9cb26FA409f10Cd3788Db4633"; // 提现合约地址 - 提现钱包地址
		Map<String, Object> result = new HashMap<String, Object>();
		KeyEntity key = this.getKeyEntityDAO().findUnique(HDaoUtils.eq("address", from).toCondition());
		String privatekey;
		result.put("message", "未知错误");
		result.put("code", "-1");
		try {
			String root =request.getSession().getServletContext().getRealPath("");
			String keystoreRoot=root+"keystore";
			try {
				String filePath=keystoreRoot+"\\"+key.getKeystoreName();
				Credentials credentials=rc.getCredentials(rc.getUserPassword().get(key.getUser().getUserName()),key,filePath);//通过密码和keystore来解锁
				BigInteger gasPrice = BigInteger.valueOf(Convert.toWei( "5", Convert.Unit.GWEI).longValue());
				String data = order;
				// 不同交易nonce应该不同，相同的nonce会产生覆盖
				EthGetTransactionCount transactionCount = null;
				transactionCount = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.LATEST).send();
				BigInteger nonce = transactionCount.getTransactionCount();
				BigInteger gasLimit=BigInteger.valueOf(700000);
				RawTransaction rawTx = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to,
						BigInteger.valueOf(0), data);
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
}
final class MatrixToImageWriter {

	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0xFFFFFFFF;

	private MatrixToImageWriter() {
	}

	public static BufferedImage toBufferedImage(BitMatrix matrix) {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
			}
		}
		return image;
	}

	public static void writeToFile(BitMatrix matrix, String format, File file)
			throws IOException {
		BufferedImage image = toBufferedImage(matrix);
		if (!ImageIO.write(image, format, file)) {
			throw new IOException("Could not write an image of format "
					+ format + " to " + file);
		}
	}

	public static void writeToStream(BitMatrix matrix, String format,
									 OutputStream stream) throws IOException {
		BufferedImage image = toBufferedImage(matrix);
		if (!ImageIO.write(image, format, stream)) {
			throw new IOException("Could not write an image of format "
					+ format);
		}
	}
}
