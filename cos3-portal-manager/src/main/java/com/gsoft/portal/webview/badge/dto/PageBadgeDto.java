package com.gsoft.portal.webview.badge.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 页面Badge
 * 
 * @author SN
 *
 */
@ApiModel("页面Badge DTO")
public class PageBadgeDto extends BaseDto {
	
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5725285995682130906L;

	/**
	 * 页面UUID
	 */
	@ApiModelProperty("页面UUID")
	private String pageUuId;
	
	/**
	 * widgetUuId
	 */
	@ApiModelProperty("widgetUuId")
	private String widgetUuId;	


	/**
	 * badgeName
	 */
	@ApiModelProperty("badgeName")
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
