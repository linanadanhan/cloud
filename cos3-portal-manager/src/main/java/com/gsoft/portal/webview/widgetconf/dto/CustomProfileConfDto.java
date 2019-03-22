package com.gsoft.portal.webview.widgetconf.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 个性化偏好配置
 * 
 * @author SN
 *
 */
@ApiModel("偏好配置DTO")
public class CustomProfileConfDto extends BaseDto {
	
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5725285995682130906L;
	
	/**
	 * widgetUuId
	 */
	@ApiModelProperty("widgetUuId")
	private String widgetUuId;	
	
	/**
	 * 用户id
	 */
	@ApiModelProperty("用户ID")
	private long userId;
	
	/**
	 * 偏好json
	 */
	@ApiModelProperty("偏好json")
	private String json;
	
	/**
	 * 页面UUID
	 */
	@ApiModelProperty("页面UUID")
	private String pageUuId;

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public String getWidgetUuId() {
		return widgetUuId;
	}

	public void setWidgetUuId(String widgetUuId) {
		this.widgetUuId = widgetUuId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getPageUuId() {
		return pageUuId;
	}

	public void setPageUuId(String pageUuId) {
		this.pageUuId = pageUuId;
	}
}
