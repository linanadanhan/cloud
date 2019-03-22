package com.gsoft.portal.webview.widget.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 嵌套widget
 * 
 * @author SN
 *
 */
@ApiModel("嵌套widget DTO")
public class NestWidgetDto extends BaseDto {
	
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5725285995682130906L;

	/**
	 * widget实例uuId
	 */
	@ApiModelProperty("实例ID")
	private String widgetUuId;
	
	/**
	 * 嵌套widget布局
	 */
	@ApiModelProperty("嵌套布局")
	private String nestLayoutCode;

	public String getNestLayoutCode() {
		return nestLayoutCode;
	}

	public void setNestLayoutCode(String nestLayoutCode) {
		this.nestLayoutCode = nestLayoutCode;
	}

	public String getWidgetUuId() {
		return widgetUuId;
	}

	public void setWidgetUuId(String widgetUuId) {
		this.widgetUuId = widgetUuId;
	}
}
