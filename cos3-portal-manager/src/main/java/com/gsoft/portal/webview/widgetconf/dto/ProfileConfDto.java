package com.gsoft.portal.webview.widgetconf.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 系统偏好配置
 * 
 * @author SN
 *
 */
@ApiModel("偏好配置DTO")
public class ProfileConfDto extends BaseDto {
	
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
	 * 模版widgetUuId
	 */
	@ApiModelProperty("模版widgetUuId")
	private String tmpWidgetUuId;
	
	/**
	 * 实例JSON
	 */
	@ApiModelProperty("实例json")
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
