package com.gsoft.portal.webview.badge.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 页面Badge信息表
 * 
 * @author SN
 *
 */
@Entity
@Table(name = "cos_portal_page_badget")
public class PageBadgeEntity extends BaseEntity{

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = -5494518685634330767L;

	/**
	 * 页面UUID
	 */
	@Column(name = "c_page_uu_id", length = 50)
	private String pageUuId;
	
	/**
	 * widgetUuId
	 */
	@Column(name = "c_widget_uu_id", length = 50)
	private String widgetUuId;	

	/**
	 * badgeName
	 */
	@Column(name = "c_badge_name")
	private String badgeName;

	public String getPageUuId() {
		return pageUuId;
	}

	public void setPageUuId(String pageUuId) {
		this.pageUuId = pageUuId;
	}

	public String getWidgetUuId() {
		return widgetUuId;
	}

	public void setWidgetUuId(String widgetUuId) {
		this.widgetUuId = widgetUuId;
	}

	public String getBadgeName() {
		return badgeName;
	}

	public void setBadgeName(String badgeName) {
		this.badgeName = badgeName;
	}

}