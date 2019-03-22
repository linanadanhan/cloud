package com.gsoft.portal.webview.widgetconf.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 用户自定义widget配置信息表
 * @author SN
 *
 */
@Entity
@Table(name = "cos_custom_widget_instance")
public class CustomWidgetConfEntity  extends BaseEntity {

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
	 * 用户ID
	 */
	@Column(name = "c_user_id", length = 9)
	private Long userId;
	
	/**
	 * 布局code
	 */
	@Column(name = "c_layout_code", length = 200)
	private String layoutCode;
	
	/**
	 * widgetIds
	 */
	@Lob
	@Column(name = "c_widget_ids")
	private String widgetIds;
	
	/**
	 * 实例json数据
	 */
	@Column(name = "c_json")
	private String json;

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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getLayoutCode() {
		return layoutCode;
	}

	public void setLayoutCode(String layoutCode) {
		this.layoutCode = layoutCode;
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
