package com.g.controller;

import com.g.model.UserEntity;
import com.g.model.support.XDAOSupport;
import com.g.utils.JdbcUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计控制器
 */
@Controller
@RequestMapping(value = "statistics")
public class StatisticsController extends XDAOSupport {
	/**
	 * 前往统计页面
	 */
	@RequestMapping(method=RequestMethod.GET)
	public String withdraw(HttpServletRequest request,ModelMap mm){
		UserEntity user=(UserEntity)request.getSession().getAttribute("loginUser");
		if(user==null){
			return "login";
		}
		user=this.getUserEntityDAO().get(user.getId());
		return "statistics";
	}
	//分时统计
	@RequestMapping(value="chartData",method=RequestMethod.GET)
	@ResponseBody
	public Object chartData1(
			@RequestParam(value = "date",required=false) String  date,//时间 格式 yyyy-mm-dd
			@RequestParam(value = "oveDate",required=false) String  oveDate,
			HttpServletRequest request,ModelMap mm){
		Integer[] s = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};
		Map<Integer, Map> data = new HashMap<Integer, Map>();
		for (Integer i : s) {
			Map<Integer, Map> m2 = new HashMap<Integer, Map>();
			data.put(i, m2);
		}
		List<Object> params =new ArrayList<Object>();
		params.add(date);
		params.add(oveDate);
		String sql = "SELECT DATE_FORMAT( expire_time, '%H' ) AS gjtime,COUNT(o.id) AS num FROM t_shark_info AS o WHERE o.next_rent_price=1400 and o.expire_time > ? AND o.expire_time < ? GROUP BY gjtime ORDER BY gjtime";
		List<Object> sum = JdbcUtil.getList(sql, params.toArray(), this.getTemplateFactory());
		for (Object object : sum) {
			Integer m = Integer.valueOf(((Object[])object)[0].toString());
			Float v = Float.valueOf(((Object[])object)[1].toString());
			data.get(m).put("sea14", v);
		}
		String sql2 = "SELECT DATE_FORMAT( expire_time, '%H' ) AS gjtime,COUNT(o.id) AS num FROM t_shark_info AS o WHERE o.next_rent_price=1500 and o.expire_time > ? AND o.expire_time < ? GROUP BY gjtime ORDER BY gjtime";
		List<Object> sum2 = JdbcUtil.getList(sql2, params.toArray(), this.getTemplateFactory());
		for (Object object : sum2) {
			Integer m = Integer.valueOf(((Object[])object)[0].toString());
			Float v = Float.valueOf(((Object[])object)[1].toString());
			data.get(m).put("sea15", v);
		}
		mm.put("data", data);
		return mm;
	}
	//分钟统计
	@RequestMapping(value="chartData2",method=RequestMethod.GET)
	@ResponseBody
	public Object chartData2(
			@RequestParam(value = "price",defaultValue = "1400") Integer price,
			@RequestParam(value = "date",required=false) String  date,//时间 格式 yyyy-MM-dd mm:hh:ss
			@RequestParam(value = "oveDate",required=false) String  oveDate,
			HttpServletRequest request,ModelMap mm){
		Map<Integer, Map> data = new HashMap<Integer, Map>();
		for (int i=0;i<60;i++) {
			Map<Integer, Map> m2 = new HashMap<Integer, Map>();
			data.put(i, m2);
		}
		List<Object> params =new ArrayList<Object>();
		params.add(date);
		params.add(oveDate);
		String sql = "SELECT DATE_FORMAT( expire_time, '%i' ) AS gjtime,COUNT(o.id) AS num FROM t_shark_info AS o WHERE o.next_rent_price="+price+" and o.expire_time > ? AND o.expire_time < ? GROUP BY gjtime ORDER BY gjtime";
		List<Object> sum = JdbcUtil.getList(sql, params.toArray(), this.getTemplateFactory());
		for (Object object : sum) {
			Integer m = Integer.valueOf(((Object[])object)[0].toString());
			Float v = Float.valueOf(((Object[])object)[1].toString());
			data.get(m).put("sea14", v);
		}
		mm.put("data", data);
		return mm;
	}

}
