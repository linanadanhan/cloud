package com.gsoft.portal.component.appreltemp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 应用与页面模版关联信息表
 * 
 * @author SN
 *
 */
@Entity
@Table(name = "cos_app_rel_template")
public class AppRelPageTempEntity extends BaseEntity {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = -5494518685634330767L;

	/**
	 * 应用标识
	 */
	@Column(name = "c_app_code", length = 50)
	private String appCode;

	/**
	 * 页面模版标识
	 */
	@Column(name = "c_page_temp_code", length = 50)
	private String pageTempCode;

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getPageTempCode() {
		return pageTempCode;
	}

	public void setPageTempCode(String pageTempCode) {
		this.pageTempCode = pageTempCode;
	}

}
