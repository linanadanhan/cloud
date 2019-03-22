package com.gsoft.portal.component.pagetemp.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 页面模版信息DTO
 * 
 * @author SN
 *
 */
@ApiModel("页面模版信息DTO")
public class PageTemplateDto extends BaseDto {
	
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
	 * 页面模版名称
	 */
	@ApiModelProperty("页面模版名称")
	private String name;
	
	/**
	 * 页面模版描述
	 */
	@ApiModelProperty("页面模版描述")
	private String desc;
	
	/**
	 * 页面模版布局
	 */
	@ApiModelProperty("页面模版布局")
	private String layoutCode;
	
	/**
	 * 页面模版配置
	 */
	@ApiModelProperty("页面模版配置")
	private String json;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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
}
