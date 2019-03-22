package com.gsoft.portal.component.pagetemp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 页面模版配置信息表
 * 
 * @author SN
 *
 */
@Entity
@Table(name = "cos_page_template_conf")
public class PageTemplateConfEntity extends BaseEntity {

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
	 * 配置json数据
	 */
	@Lob
	@Column(name = "c_json")
	private String json;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

}
