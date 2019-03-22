package com.gsoft.portal.webview.page.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 站点页面配置信息表
 * @author SN
 *
 */
@Entity
@Table(name = "cos_page_widget_info")
public class SitePageConfEntity  extends BaseEntity {

	private static final long serialVersionUID = -5494518685634330767L;
	
	/**
	 * uuId
	 */
	@Column(name = "c_uu_id", length = 50)
	private String uuId;
	
	/**
	 * uuId
	 */
	@Column(name = "c_page_uu_id", length = 50)
	private String pageUuId;
	
	/**
	 * widget标题
	 */
	@Column(name = "c_widget_title", length = 200)
	private String widgetTitle;
	
	/**
	 * widget代码
	 */
	@Column(name = "c_widget_code", length = 200)
	private String widgetCode;
	
	/**
	 * 修饰器代码
	 */
	@Column(name = "c_decorator_code", length = 200)
	private String decoratorCode;

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

	public String getWidgetTitle() {
		return widgetTitle;
	}

	public void setWidgetTitle(String widgetTitle) {
		this.widgetTitle = widgetTitle;
	}

	public String getWidgetCode() {
		return widgetCode;
	}

	public void setWidgetCode(String widgetCode) {
		this.widgetCode = widgetCode;
	}

	public String getDecoratorCode() {
		return decoratorCode;
	}

	public void setDecoratorCode(String decoratorCode) {
		this.decoratorCode = decoratorCode;
	}
	
}
