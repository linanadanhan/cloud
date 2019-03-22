package com.gsoft.portal.component.theme.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户自定义主题
 * 
 * @author SN
 *
 */
@ApiModel("用户自定义主题DTO")
public class CustomThemeDto extends BaseDto {
	
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5725285995682130906L;

	/**
	 * 用户ID
	 */
	@ApiModelProperty("用户ID")
	private Long userId;
	
	/**
	 * 站点CODE
	 */
	@ApiModelProperty("站点代码")
	private String siteCode;
	
	/**
	 * 主题code
	 */
	@ApiModelProperty("主题代码")
	private String themeCode;
	

	public String getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getThemeCode() {
		return themeCode;
	}

	public void setThemeCode(String themeCode) {
		this.themeCode = themeCode;
	}

}
