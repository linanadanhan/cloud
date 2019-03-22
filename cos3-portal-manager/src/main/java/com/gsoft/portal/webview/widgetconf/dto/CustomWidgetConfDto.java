package com.gsoft.portal.webview.widgetconf.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 个性化widget 配置实例
 * 
 * @author SN
 *
 */
@ApiModel("自定义widget配置DTO")
public class CustomWidgetConfDto extends BaseDto {
	
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5725285995682130906L;
	
	/**
	 * uuid
	 */
	@ApiModelProperty("uuId")
	private String uuId;
	
	/**
	 * 页面UUID
	 */
	@ApiModelProperty("页面uuId")
	private String pageUuId;	
	
	/**
	 * 用户ID
	 */
	@ApiModelProperty("用户ID")
	private Long userId;
	
	/**
	 * 布局code
	 */
	@ApiModelProperty("布局代码")
	private String layoutCode;
	
	/**
	 * 实例json数据
	 */
	@ApiModelProperty("实例json")
	private String json;
	
	/**
	 * widgetIds
	 */
	@ApiModelProperty("widgetIds")
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
