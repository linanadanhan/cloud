package com.gsoft.portal.webview.widgetconf.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 系统偏好配置信息表
 * @author SN
 *
 */
@Entity
@Table(name = "cos_sys_profile")
public class ProfileConfEntity  extends BaseEntity {

	private static final long serialVersionUID = -5494518685634330767L;
	
	/**
	 * widgetUuId
	 */
	@Column(name = "c_widget_uu_id", length = 50)
	private String widgetUuId;	
	
	/**
	 * 模版widgetUuId
	 */
	@Column(name = "c_tmp_uu_id", length = 50)
	private String tmpWidgetUuId;
	
	/**
	 * json数据
	 */
	@Column(name = "c_json")
	private String json;
	
	/**
	 * 页面UUID
	 */
	@Column(name = "c_page_uu_id", length = 50)
	private String pageUuId;

	public String getWidgetUuId() {
		return widgetUuId;
	}

	public void setWidgetUuId(String widgetUuId) {
		this.widgetUuId = widgetUuId;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public String getTmpWidgetUuId() {
		return tmpWidgetUuId;
	}

	public void setTmpWidgetUuId(String tmpWidgetUuId) {
		this.tmpWidgetUuId = tmpWidgetUuId;
	}

	public String getPageUuId() {
		return pageUuId;
	}

	public void setPageUuId(String pageUuId) {
		this.pageUuId = pageUuId;
	}

}
