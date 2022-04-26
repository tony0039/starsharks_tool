package com.g.controller;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;

import com.g.bean.UserBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.web3j.crypto.Credentials;

import com.g.model.KeyEntity;
import com.g.model.UserEntity;
import com.g.model.support.XDAOSupport;
import com.g.utils.AESUtil;
import com.mezingr.dao.HDaoUtils;

/**
 * 管理员用，因为没有权限控制，所以判断写死
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value="admin")
public class ManagerController extends XDAOSupport {
	private static String ADMIN="admin";

	/**
	 * 前往管理员页面
	 */
	@RequestMapping(method=RequestMethod.GET)
	public String index(HttpServletRequest request,ModelMap mm){
		UserEntity user=(UserEntity)request.getSession().getAttribute("loginUser");
		if(user==null){
			return "login";
		}else if(user.getUserName().equals(ADMIN)){
			List<UserEntity> users=this.getUserEntityDAO().list(HDaoUtils.eq("status",1).toCondition());
			List<UserBean> results=new ArrayList<UserBean>();
			for(UserEntity ue : users){
				results.add(new UserBean(ue));
			}
			mm.put("users",results);
			return "manager";
		}
		return "index";
	}

	/**
	 * 将keystore同步到数据库
	 */
	@RequestMapping(value="/keystore",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> keystore(HttpServletRequest request,ModelMap mm){
		Map<String,Object> result=new HashMap<String,Object>();
		UserEntity user=(UserEntity)request.getSession().getAttribute("loginUser");
		if(user!=null){
			if(user.getUserName().equals(ADMIN)){
				List<UserEntity> users=this.getUserEntityDAO().listAll();
				Integer success=0;
				Integer fail=0;
				for(UserEntity ue :users) {
					//String root = Thread.currentThread().getContextClassLoader().getResource("/").getPath();//获取的是类路径目录
					String root =request.getSession().getServletContext().getRealPath("");
					String keystoreRoot=root+"keystore\\"+ue.getUserName();
					System.out.println("keystore路径:"+keystoreRoot);
					List<String> list=getFiles(keystoreRoot);
					for(String item:list) {
			        	KeyEntity ke=new KeyEntity();
						String itemResult=readFile(item);
			        	String[] str=item.split("\\\\");
			        	String fileName=str[str.length-1];
			        	String userName=str[str.length-3];
			        	str=fileName.split("_");
			        	ke.setKeystoreName(fileName);
			        	if(str.length==2) {
			        		Integer sort=Integer.parseInt(str[0]);
			        		ke.setSort(sort);
			        		String address=str[str.length-1];
			        		System.out.println("address：" + address);
			        		ke.setAddress(address);
			        	}
			        	ke.setUser(ue);
			        	ke.setKeystore(itemResult);
			        	if(!this.getKeyEntityDAO().exist(HDaoUtils.eq("address", ke.getAddress()).toCondition())) {
		        			this.getKeyEntityDAO().create(ke);	
		        			success++;
		        		}else {
		        			fail++;
		        		}
						System.out.println("文件内容["+fileName+"]:"+itemResult);
					}
				}
				result.put("code", 0);;
				result.put("message", "成功:["+success+"]失败:["+fail+"]");
			}else {
				result.put("code", -1);;
				result.put("message", "非管理员，权限不足");
			}
		}
		return result;
	}
	/**
	 * 读取文件夹下的所有文件路径
	 * @param path
	 * @return
	 */
	public List<String> getFiles(String path) {
	    List<String> files = new ArrayList<String>();
	    File file = new File(path);
	    if(file.exists()) {
		    File[] tempList = file.listFiles();
		    for (int i = 0; i < tempList.length; i++) {
		        if (tempList[i].isFile()) {
		            files.add(tempList[i].toString());
		        }
		        /**
		        if (tempList[i].isDirectory()) {
		        	System.out.println("文件夹：" + tempList[i]);
		        }
		        **/
		    }
		    return files;
	    }else {
	    	file.mkdirs();
	    	return getFiles(path);
	    }
	}
	/**
     * 读取文件，取出最后一行的内容返回
     */
    public String readFile(String filePath){
        String result="";
        try
        {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists())
            { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null)
                {
                	result=lineTxt;
                }
                bufferedReader.close();
                read.close();
            }
            else
            {
                System.out.println("找不到指定的文件");
            }
        }
        catch (Exception e)
        {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }

        return result;
    }
}
