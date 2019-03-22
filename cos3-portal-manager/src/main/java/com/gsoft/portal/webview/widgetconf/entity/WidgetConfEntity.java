package com.gsoft.portal.webview.widgetconf.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * widget配置信息表
 * @author SN
 *
 */
@Entity
@Table(name = "cos_widget_instance")
public class WidgetConfEntity  extends BaseEntity {

	private static final long serialVersionUID = -5494518685634330767L;
	
	/**
	 * uuid
	 */
	@Column(name = "c_uu_id", length = 50)
	private String uuId;
	
	/**
	 * 页面UUID
	 */
	@Column(name = "c_page_uu_id", length = 50)
	private String pageUuId;
	
	/**
	 * json数据
	 */
	@Column(name = "c_json")
	private String json;
	
	/**
	 * widgetIds
	 */
	@Lob
	@Column(name = "c_widget_ids")
	private String widgetIds;

	public String getUuId() {
		return uuId;
	}

	public void setUuId(String uuId) {
		this.uuId = uuId;
	}

	public String getPageUuId() {
		return pageUuId;
	}

	public void setPageUuId(String pageUuId) {
		this.pageUuId = pageUuId;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public String getWidgetIds() {
		return widgetIds;
	}

	public void setWidgetIds(String widgetIds) {
		this.widgetIds = widgetIds;
	}
	
}
