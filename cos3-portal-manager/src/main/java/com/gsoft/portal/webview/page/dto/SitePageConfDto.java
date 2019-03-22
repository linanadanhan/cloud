package com.gsoft.portal.webview.page.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 站点页面配置
 * 
 * @author SN
 *
 */
@ApiModel("页面配置DTO")
public class SitePageConfDto extends BaseDto {
	
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5725285995682130906L;
	
	/**
	 * uuId
	 */
	@ApiModelProperty("uuId")
	private String uuId;
	
	/**
	 * 页面UUID
	 */
	@ApiModelProperty("页面uuId")
	private String pageUuId;
	
	/**
	 * widget标题
	 */
	@ApiModelProperty("widget标题")
	private String widgetTitle;

	/**
	 * widget代码
	 */
	@ApiModelProperty("widget代码")
	private String widgetCode;
	
	/**
	 * 修饰器代码
	 */
	@ApiModelProperty("修饰器代码")
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
