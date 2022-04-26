package com.g.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.g.bean.UserBean;
import com.mezingr.dao.Exp;
import com.mezingr.dao.HDaoUtils;
import com.mezingr.dao.PaginationList;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import com.g.model.UserEntity;
import com.g.model.support.XDAOSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 用户管理控制器
 *
 */
@Controller
@RequestMapping("/user")
public class UserController extends XDAOSupport{
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	private RestController rc;
	/**
	 * 用户登陆
	 */
	@RequestMapping(value="/login",method = RequestMethod.POST)
	public String login(@RequestParam(value="username")String username,
						@RequestParam(value="password")String password,
						HttpServletRequest request,ModelMap map){
		HttpSession session=request.getSession();
		UserEntity user = this.getUserEntityDAO().findUnique("userName", username);
		if(user!=null){
			//用户名存在
			if(password.equals(user.getPassword())){
				if(user.getStatus()!=1){
					map.put("message", "账户被锁定,请联系管理员！");
				}else{
					session.setAttribute("loginUser", user);
					return "redirect:/index";
				}
			}else{
				map.put("message", "登录密码错误！");
			}
		}else{
			map.put("message", "账户不存在！");
		}
		return "login";
	}
	
	/**
	 * 退出
	 */
	@RequestMapping(value="logOut")
	public String logOut(HttpServletRequest request){
		request.getSession().removeAttribute("loginUser");
		return "login";
	}
	/**
	 * 获取用户列表
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	public String list(@RequestParam(value="username",required=false)String username,
					   @RequestParam(value="pageIndex",defaultValue="1")Integer pageIndex,
					   @RequestParam(value="pageSize",defaultValue="20")Integer pageSize,
					   HttpServletRequest request,ModelMap mm){
		UserEntity user = (UserEntity)request.getSession().getAttribute("loginUser");
		if(user == null){
			return "login";
		}
		if(!user.getUserName().equals("admin")){
			return "index";
		}
		PaginationList<UserEntity> list=null;
		Exp<Criterion> exp = HDaoUtils.eq("status", 1);

		if(username!=null && !"".equals(username)){
			exp=exp.andEq("userName", username);
		}
		if(exp!=null){
			list=this.getUserEntityDAO().list(exp.toCondition(),pageIndex,pageSize, Order.desc("addTime"));
		}else{
			list=this.getUserEntityDAO().list(pageIndex,pageSize,Order.desc("addTime"));
		}
		List<UserBean> users=new ArrayList<UserBean>();
		for(UserEntity ue :list.getItems()){
			UserBean ub=new UserBean(ue);
			if(rc.getUserPassword().get(ue.getUserName())!=null&&!rc.getUserPassword().get(ue.getUserName()).equals("")){
				ub.setUnlock(true);
			}
			users.add(ub);
		}
		mm.put("username",username);
		mm.put("list", users);
		mm.put("total", list.getTotalCount());
		mm.put("pageIndex", pageIndex);
		mm.put("pageSize", pageSize);
		mm.put("totalPage", (int) Math.ceil(list.getTotalCount() / pageSize.doubleValue()));
		return "user_list";
	}

	/**
	 * 删除
	 * */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String delete(@PathVariable(value = "id")
								 String id,ModelMap mm){
		UserEntity user = this.getUserEntityDAO().get(id);
		if(user!=null&&user.getStatus()==1){
			user.setStatus(2);
			this.getUserEntityDAO().update(user);
			return "redirect:/user";
		}
		return "redirect:/user";
	}
	/**
	 * 去添加
	 * */
	@RequestMapping(value = "/adduser", method = RequestMethod.GET)
	public String adduser(ModelMap map,HttpServletRequest request) {
		UserEntity user = (UserEntity)request.getSession().getAttribute("loginUser");
		if(user == null){
			return "login";
		}
		if(!user.getUserName().equals("admin")){
			return "index";
		}
		return "user_add";
	}
	/**
	 * 添加用户
	 * */
	@RequestMapping(value="add",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> add(@RequestParam(value="userName")String userName,
								  @RequestParam(value="password")String password,
								  HttpServletRequest request){
		UserEntity user = (UserEntity)request.getSession().getAttribute("loginUser");
		Map<String,Object> map=new HashMap<String, Object>();
		if(user == null){
			map.put("msg","登录超时");
			return map;
		}
		if(!user.getUserName().equals("admin")){
			map.put("msg","没权限!");
			return map;
		}
		Boolean flag=false;
		UserEntity user2=new UserEntity();
		boolean f=this.getUserEntityDAO().exist(HDaoUtils.eq("userName",userName).toCondition());
		if(f){
			map.put("msg","用户名已存在!");
			return map;
		}
		user2.setUserName(userName);
		user2.setPassword(password);
		user2.setRealName(userName);
		this.getUserEntityDAO().create(user2);
		flag=true;
		map.put("msg","添加成功！");
		map.put("flag",flag);
		return map;
	}

	/**
	 * 去修改
	 * */
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	public String findById(@PathVariable(value="id")String id,
			   HttpServletRequest request,ModelMap map){
		UserEntity user1 = (UserEntity)request.getSession().getAttribute("loginUser");
		if(user1 == null){
			return "login";
		}
		if(!user1.getUserName().equals("admin")){
			return "index";
		}
		UserEntity user=this.getUserEntityDAO().get(id);
		if(user==null){
			map.put("message", "用户不存在,请刷新重试");
			return "redirect:/user";
		}
		map.put("user", user);
		return "user_update";
	}
	/**
	 * 修改用户信息
	 * */
	@RequestMapping(value="/{id}",method=RequestMethod.PUT)
	@ResponseBody
	public Map<String,Object> update(@PathVariable(value="id")String id,
			 @RequestParam(value="userName")String userName,
			 @RequestParam(value="password")String password,
			 HttpServletRequest request,ModelMap mm){
		String msg="修改失败";
		Boolean flag=false;
		UserEntity user=(UserEntity)request.getSession().getAttribute("loginUser");
		Map<String,Object> map=new HashMap<String, Object>();
		if(user == null){
			map.put("msg","登录超时");
			return map;
		}
		if(!user.getUserName().equals("admin")){
			map.put("msg","没权限!");
			return map;
		}
		UserEntity user2=this.getUserEntityDAO().get(id);
		if(user2!=null){
			user2.setUserName(userName);
			user2.setPassword(password);
			this.getUserEntityDAO().update(user2);
			msg="修改成功！";
			flag=true;
		}
		map.put("msg", msg);
		map.put("flag", flag);
		return map;
	}

}
