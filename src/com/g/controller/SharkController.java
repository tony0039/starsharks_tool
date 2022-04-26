package com.g.controller;

import com.g.bean.StatusBean;
import com.g.model.SharkEntity;
import com.g.model.UserEntity;
import com.g.model.support.XDAOSupport;
import com.mezingr.dao.Exp;
import com.mezingr.dao.HDaoUtils;
import com.mezingr.dao.PaginationList;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 鲨鱼控制器
 */
@Controller
@RequestMapping(value = "shark")
public class SharkController extends XDAOSupport {

	private Map<Integer,Long>  sharkPoints=new HashMap<Integer,Long>();

	public SharkController(){//初始化执行sharkPoints的日期判断，超时的删除
		Timer timer = new Timer();
		timer.schedule(new SessionTask(),30000L);
	}

	/**
	 * 根据关键字查询鲨鱼
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public StatusBean list(@RequestParam(value = "price",defaultValue = "0") Integer price,
						   @RequestParam(value = "count",defaultValue = "1") Integer count,
						   @RequestParam(value = "start",defaultValue = "20") Integer beforeStart,
						   @RequestParam(value = "end",defaultValue = "3") Integer beforeEnd,
								 HttpServletRequest request, ModelMap mm){
		UserEntity user=(UserEntity)request.getSession().getAttribute("loginUser");
		Calendar calendar=new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(Calendar.SECOND,+beforeStart);
		Date start=calendar.getTime();
		calendar.setTime(new Date());
		calendar.add(Calendar.SECOND,+beforeEnd);
		Date end=calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("起始时间:"+sdf.format(start));
		System.out.println("结束时间:"+sdf.format(end));
		Exp<Criterion> exp= HDaoUtils.eq("status",2).
				andEq("autoRent",1).andEq("nextPrice",price*100).
				andBetween("expireTime",start,end);
		for (Map.Entry<Integer, Long> entry : sharkPoints.entrySet()) {
			exp.andNe("id",entry.getKey());
		}
		PaginationList<SharkEntity> list = this.getSharkEntityDAO().list(exp.toCondition(), 1, count, Order.asc("expireTime"));
		if(list.getTotalCount()>0){
			for(SharkEntity se :list.getItems()){
				sharkPoints.put(se.getId(),new Date().getTime());
			}
			return new StatusBean(0,"success",list.getItems());
		}
		return new StatusBean(0,"success",null);
	}

	public class SessionTask extends TimerTask {
		@Override
		public void run() {
			try {
				while(true) {
					for (Map.Entry<Integer, Long> entry : sharkPoints.entrySet()) {
						Integer key = entry.getKey();
						Long now = new Date().getTime();
						Long value = entry.getValue();
						Long cha = now - value;
						if (cha > 20*1000) {
							sharkPoints.remove(key);
							System.out.println("Key = " + key + ", Value = " + value + "超时，删除");
						}
					}
					Thread.sleep(5000);
				}
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
	}

}
