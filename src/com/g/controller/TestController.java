package com.g.controller;

import com.g.bean.StatusBean;
import com.g.model.KeyEntity;
import com.g.model.UserEntity;
import com.g.model.support.XDAOSupport;
import com.g.utils.AESUtil;
import com.mezingr.dao.HDaoUtils;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Sign;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 测试用
 *
 */
@Controller
@RequestMapping(value = "test")
public class TestController extends XDAOSupport {

    private static String ADMIN="admin";

    public static String SECRETKEY="dr123456dr123456";

    /**
     * 测试
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public StatusBean get(@RequestParam(value = "address") String address, HttpServletRequest request, ModelMap mm) {
        Document doc = null;
        try {
            Connection connect = Jsoup.connect("https://starsharks.com/zh-Hant/market");
            KeyEntity key=this.getKeyEntityDAO().findUnique(HDaoUtils.eq("address",address).toCondition());
            connect.header("authorization", key.getAuth());
            doc = connect.get();
            String title = doc.title();
            System.out.println(title);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Autowired
    private AESUtil aes;
    /**
     * 上传秘钥文件
     * @throws IOException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    @RequestMapping(method=RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> init(HttpServletRequest request) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        Map<String,Object> result=new HashMap<String,Object>();
        UserEntity user=(UserEntity)request.getSession().getAttribute("loginUser");
        String msg="";
        Boolean flag=false;
        if(user!=null){
            if(user.getUserName().equals(ADMIN)){
                CommonsMultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
                //判断是否是文件
                if(resolver.isMultipart(request)){
                    //进行转换
                    MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)(request);
                    //获取所有文件名称
                    Iterator<String> it = multiRequest.getFileNames();
                    while(it.hasNext()){
                        //根据文件名称取文件
                        MultipartFile file = multiRequest.getFile(it.next());
                        String sourceName = file.getOriginalFilename(); // 原始文件名
                        String fileType = sourceName.substring(sourceName.lastIndexOf("."));
                        if (file.isEmpty()) {
                            msg="上传失败，文件内容为空";
                        }
                        if (!".txt".equals(fileType.toLowerCase()) && !".csv".equals(fileType.toLowerCase())) {
                            msg="文件暂时只支持txt,csv格式";
                        }
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                        String lineTxt;
                        while ((lineTxt=bufferedReader.readLine())!=null){
                            System.out.println(lineTxt);
                            String[] arr=lineTxt.split(",");
                            if(arr.length>0){
                                String content=arr[0];
                                KeyEntity keyEntity=new KeyEntity();
                                if(content.length()==64){
                                    // 加密
                                    System.out.println("加密前：" + content);
                                    byte[] encode = aes.encrypt(content, SECRETKEY);
                                    //传输过程,不转成16进制的字符串，就等着程序崩溃掉吧
                                    String code = aes.parseByte2HexStr(encode);
                                    System.out.println("密文字符串：" + code);
                                    keyEntity.setKey(code);
                                    Credentials credentials = Credentials.create(content);
                                    String address=credentials.getAddress();
                                    System.out.println("账号地址:"+address);
                                    keyEntity.setAddress(address);
                                }
                                if(arr.length>1){
                                    String desc=arr[1];
                                    keyEntity.setDesc(desc);
                                }
                                if(arr.length>2){
                                    String userId=arr[2];
                                    UserEntity userEntity=this.getUserEntityDAO().get(userId);
                                    if(userEntity!=null){
                                        keyEntity.setUser(userEntity);
                                    }
                                }
                                if(!this.getKeyEntityDAO().exist(HDaoUtils.eq("key", keyEntity.getKey()).toCondition())) {
                                    this.getKeyEntityDAO().create(keyEntity);
                                }
                            }
                        }
                        msg="上传成功";
                        flag=true;
                    }
                }
            }else{
                msg="权限不够";
                flag=false;
            }
        }
        result.put("msg", msg);
        result.put("flag",flag);
        return result;
    }

    /**
     * 测试
     */
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> test(@RequestParam(value = "privatekey") String privatekey,
                                    @RequestParam(value = "content") String content, HttpServletRequest request, ModelMap mm) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Credentials credentials = Credentials.create(privatekey);
            credentials= WalletUtils.loadCredentials("","");
            try {
                System.out.println("privatekey:" + privatekey);
                System.out.println("content:" + content);
                Sign.SignatureData data = Sign.signMessage(content.getBytes(), credentials.getEcKeyPair());
                BigInteger publickey=credentials.getEcKeyPair().getPublicKey();
                System.out.println("publickey:" + publickey.toString(16));
                System.out.println("result1:" + data.toString());
                String s= Base64.getEncoder().encodeToString(data.getR());
                String s2=Base64.getEncoder().encodeToString(data.getS());
                String s3=new String(data.getR(),"utf-8");
                String s4=new String(data.getS(),"utf-8");
                BigInteger big = Sign.signedMessageToKey(content.getBytes(), data);
                System.out.println("result2:" + big);
                System.out.println("result3:" + big.toString(16));
                ECDSASignature signature = credentials.getEcKeyPair().sign(content.getBytes());
                System.out.println("result4:" + signature.toString());
                System.out.println("s"+s);
                System.out.println("s1"+s2);
                System.out.println("s2"+s3);
                System.out.println("s3"+s4);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 解密
    private String decode(String before) throws UnsupportedEncodingException {
        byte[] decode = aes.parseHexStr2Byte(before);
        // 解密
        byte[] decryptResult = aes.decrypt(decode, SECRETKEY);
        String result = new String(decryptResult, "UTF-8");
        return result;
    }
}
