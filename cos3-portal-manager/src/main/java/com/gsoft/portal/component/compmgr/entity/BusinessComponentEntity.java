package com.gsoft.portal.component.compmgr.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 业务组件信息表
 * @author SN
 *
 */
@Entity
@Table(name = "cos_portal_yw_component")
public class BusinessComponentEntity  extends BaseEntity {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = -5494518685634330767L;
	
	/**
	 * 业务组件名称
	 */
	@Column(name = "c_comp_name", length = 50)
	private String name;
	
	/**
	 * 业务组件描述信息
	 */
	@Column(name = "c_comp_desc", length = 200)
	private String desc;
	
	/**
	 * 分类
	 */
	@Column(name = "c_category", length = 200)
	private String category;
	
	/**
     * 可用状态 1可用，0停用
     */
    @Column(name = "c_status", length = 5, nullable = false)
    private String status;
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}	
}
