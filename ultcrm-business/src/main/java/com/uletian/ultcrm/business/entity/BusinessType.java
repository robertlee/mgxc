package com.uletian.ultcrm.business.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Date;

/**
 * The persistent class for the business_type database table.
 * 
 */
@Entity
@Table(name="business_type")
@NamedQuery(name="BusinessType.findAll", query="SELECT b FROM BusinessType b")
public class BusinessType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="name")
	private String name;

	@Column(name="create_time")
	private Timestamp createTime;

	@Column(name="create_user_id")
	private Long createUserId;
	
	@Column(name="last_update_time")
	private Timestamp lastUpdateTime;
	
	@Column(name="last_update_userid")
	private Long lastUpdateUserid;
	
	private String introduce;//课程说明   吴云
	private String price;//课程报价   Robert 
	private float cost;//课程报价   Robert 
	private String pic;//课程报价   Robert 

	
	public BusinessType() {
	}


	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Long getCreateUserId() {
		return this.createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public String getIntroduce() {
		return this.introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public Timestamp getLastUpdateTime() {
		return this.lastUpdateTime;
	}

	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Long getLastUpdateUserid() {
		return this.lastUpdateUserid;
	}

	public void setLastUpdateUserid(Long lastUpdateUserid) {
		this.lastUpdateUserid = lastUpdateUserid;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getPic() {
		return this.pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getPrice() {
		return this.price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
	public float getCost() {
		return this.cost;
	}
	public void setCost(float cost) {
		this.cost = cost;
	}	

}