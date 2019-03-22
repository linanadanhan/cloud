package com.gsoft.portal.component.pagetemp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 页面模版信息表
 * 
 * @author SN
 *
 */
@Entity
@Table(name = "cos_page_template")
public class PageTemplateEntity extends BaseEntity {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = -5494518685634330767L;

	/**
	 * 页面模版标识
	 */
	@Column(name = "c_code", length = 50)
	private String code;

	/**
	 * 页面模版名称
	 */
	@Column(name = "c_name", length = 100)
	private String name;

	/**
	 * 页面模版描述
	 */
	@Column(name = "c_desc", length = 200)
	private String desc;
	
	/**
	 * 页面模版布局code
	 */
	@Column(name = "c_layout_code", length = 200)
	private String layoutCode;

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

	public String getLayoutCode() {
		return layoutCode;
	}

	public void setLayoutCode(String layoutCode) {
		this.layoutCode = layoutCode;
	}

}
