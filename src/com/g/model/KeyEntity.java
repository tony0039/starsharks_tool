package com.g.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


/**
 * 秘钥实体
 */

@Entity
@Table(name="sys_key")
public class KeyEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GenericGenerator(name = "uuidhex", strategy = "uuid.hex")
	@GeneratedValue(generator = "uuidhex")
	@Column(name="id",length = 32)
	private String id;
	
	@Column(name="_key",unique=true)
	private String key;
	
	@Column(name="_address")
	private String address;
	
	@Column(name="_desc")
	private String desc;
	
	/**
	 * 添加时间
	 */
	@Column(name="_add_time")
	private Date addTime=new Date();

	/**
	 * 所属用户
	 */
	@ManyToOne
	@JoinColumn(name="_user")
	private UserEntity user;
	
	/**
	 * 状态(1.正常 2.冻结 3.删除)
	 */
	@Column(name="_status")
	private Integer status=1;

	/**
	 * 授权字符串
	 */
	@Column(name="_auth")
	private String auth;

	/**
	 * 登录字符串
	 */
	@Column(name="_qrauth")
	private String qrauth;
	
	/**
	 * 是否授权
	 */
	@Column(name="_is_auth")
	private Boolean isAuth=false;

	/**
	 * 用于排序
	 */
	@Column(name="_sort")
	private Integer sort=0;
	
	/**
	 * keystore文件名
	 */
	@Column(name="_keystore_name")
	private String keystoreName;

	/**
	 * keystore内容
	 */
	@Column(name="_keystore",length=1000)
	private String keystore;

	public String getQrauth() {
		return qrauth;
	}

	public void setQrauth(String qrauth) {
		this.qrauth = qrauth;
	}

	public Integer getSort() {
		return sort;
	}


	public void setSort(Integer sort) {
		this.sort = sort;
	}


	public String getAuth() {
		return auth;
	}


	public void setAuth(String auth) {
		this.auth = auth;
	}


	public Boolean getIsAuth() {
		return isAuth;
	}


	public void setIsAuth(Boolean isAuth) {
		this.isAuth = isAuth;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}


	public String getDesc() {
		return desc;
	}


	public void setDesc(String desc) {
		this.desc = desc;
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


	public UserEntity getUser() {
		return user;
	}


	public void setUser(UserEntity user) {
		this.user = user;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getKeystoreName() {
		return keystoreName;
	}
	
	public void setKeystoreName(String keystoreName) {
		this.keystoreName = keystoreName;
	}

	public String getKeystore() {
		return keystore;
	}


	public void setKeystore(String keystore) {
		this.keystore = keystore;
	}



}
