package com.gsoft.portal.component.pagetemp.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 页面模版配置信息DTO
 * 
 * @author SN
 *
 */
@ApiModel("页面模版配置信息DTO")
public class PageTemplateConfDto extends BaseDto {
	
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5725285995682130906L;
	
	/**
	 * 页面模版标识
	 */
	@ApiModelProperty("页面模版标识")
	private String code;
	
	/**
	 * 页面模版配置json
	 */
	@ApiModelProperty("页面模版配置json")
	private String json;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
	
}
