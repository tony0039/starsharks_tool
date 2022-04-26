package com.g.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


/**
 * 用户实体
 */

@Entity
@Table(name="sys_user")
public class UserEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GenericGenerator(name = "uuidhex", strategy = "uuid.hex")
	@GeneratedValue(generator = "uuidhex")
	@Column(name="id",length = 32)
	private String id;
	
	/**
	 * 用户名
	 */
	@Column(name="_user_name",unique=true)
	private String userName;
	
	/**
	 * 密码
	 */
	@Column(name="_password")
	private String password;
	
	/**
	 * 真实姓名
	 */
	@Column(name="_realName")
	private String realName;
	/**
	 * 添加时间
	 */
	@Column(name="_add_time")
	private Date addTime=new Date();
	
	/**
	 * 账户状态(1.正常 2.冻结 3.删除)
	 */
	@Column(name="_status")
	private Integer status=1;


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getRealName() {
		return realName;
	}


	public void setRealName(String realName) {
		this.realName = realName;
	}


	public Date getAddTime() {
		return addTime;
	}


	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}


	public Integer getStatus() {
		return status;
	}


	public void setStatus(Integer status) {
		this.status = status;
	}

}
