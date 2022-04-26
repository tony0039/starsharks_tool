package com.g.bean;

import com.g.model.KeyEntity;

public class KeyBean {
	private String id;
	private Integer status;
	private String address;
	private String desc;
	private String auth;
	private Boolean isAuth;
	private String keystore;
	private Integer sort;
	public KeyBean(KeyEntity key) {
		this.id=key.getId();
		this.status=key.getStatus();
		this.address=key.getAddress();
		this.desc=key.getDesc();
		this.auth=key.getAuth();
		this.isAuth=key.getIsAuth();
		//this.keystore=key.getKeystore();
		this.sort=key.getSort();
	}
	public String getKeystore() {
		return keystore;
	}
	public void setKeystore(String keystore) {
		this.keystore = keystore;
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
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
}
