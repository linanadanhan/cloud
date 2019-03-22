package com.gsoft.portal.component.appreltemp.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 应用与页面模版关联信息DTO
 * 
 * @author SN
 *
 */
@ApiModel("应用与页面模版关联信息DTO")
public class AppRelPageTempDto extends BaseDto {
	
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5725285995682130906L;
	
	/**
	 * 应用标识
	 */
	@ApiModelProperty("应用标识")
	private String appCode;	
	
	/**
	 * 页面模版标识
	 */
	@ApiModelProperty("页面模版标识")
	private String pageTempCode;

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getPageTempCode() {
		return pageTempCode;
	}

	public void setPageTempCode(String pageTempCode) {
		this.pageTempCode = pageTempCode;
	}
	
}
