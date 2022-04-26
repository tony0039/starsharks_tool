package com.g.controller;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.criterion.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.g.bean.KeyBean;
import com.g.model.KeyEntity;
import com.g.model.UserEntity;
import com.g.model.support.XDAOSupport;
import com.mezingr.dao.HDaoUtils;

@Controller
public class PageController extends XDAOSupport {
	/**
	 * 前往首页
	 */
	@RequestMapping(value="index",method=RequestMethod.GET)
	public String index(HttpServletRequest request,ModelMap mm){
		Object obj=request.getSession().getAttribute("loginUser");
		if(obj==null){
			return "login";
		}
		return "index";
	}
	/**
	 * 前往租鱼页面
	 */
	@RequestMapping(value="shark",method=RequestMethod.GET)
	public String shark(HttpServletRequest request,ModelMap mm){
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
		return "shark";
	}
}
