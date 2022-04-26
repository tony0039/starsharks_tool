package com.g.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * 鲨鱼实体
 */

@Entity
@Table(name="t_shark_info")
public class SharkEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * 鲨鱼id
	 */
	@Id
	@Column(name="id")
	private Integer id;

	/**
	 * 星级
	 */
	@Column(name="star")
	private Integer star;

	/**
	 * 出租状态
	 */
	@Column(name="rent_status")
	private Integer status;

	/**
	 * 是否续租
	 */
	@Column(name="auto_rent")
	private Integer autoRent;

	/**
	 * 出租失效时间
	 */
	@Column(name="expire_time")
	private Date expireTime;

	/**
	 * 当前出租价格
	 */
	@Column(name="current_rent_price")
	private Integer currentPrice;

	/**
	 * 续租价格
	 */
	@Column(name="next_rent_price")
	private Integer nextPrice;

	/**
	 * 创建时间
	 */
	@Column(name="create_time")
	private Date createTime;

	/**
	 * 更新时间
	 */
	@Column(name="update_time")
	private Date updateTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStar() {
		return star;
	}

	public void setStar(Integer star) {
		this.star = star;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getAutoRent() {
		return autoRent;
	}

	public void setAutoRent(Integer autoRent) {
		this.autoRent = autoRent;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public Integer getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(Integer currentPrice) {
		this.currentPrice = currentPrice;
	}

	public Integer getNextPrice() {
		return nextPrice;
	}

	public void setNextPrice(Integer nextPrice) {
		this.nextPrice = nextPrice;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
