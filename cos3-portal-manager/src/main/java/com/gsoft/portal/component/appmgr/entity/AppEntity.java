package com.gsoft.portal.component.appmgr.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 应用信息表
 * 
 * @author SN
 *
 */
@Entity
@Table(name = "cos_app_info")
public class AppEntity extends BaseEntity {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = -5494518685634330767L;

	/**
	 * 应用标识
	 */
	@Column(name = "c_code", length = 50)
	private String code;

	/**
	 * 应用名称
	 */
	@Column(name = "c_name", length = 100)
	private String name;

	/**
	 * 应用描述
	 */
	@Column(name = "c_desc", length = 200)
	private String desc;

	/**
	 * 可用状态 1可用，0停用
	 */
	@Column(name = "c_status", length = 5, nullable = false)
	private String status;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

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

}
