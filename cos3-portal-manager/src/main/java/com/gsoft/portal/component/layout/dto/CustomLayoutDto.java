package com.gsoft.portal.component.layout.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 自定义布局
 * 
 * @author SN
 *
 */
@ApiModel("自定义布局DTO")
public class CustomLayoutDto extends BaseDto {
	
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
	 * 页面UUID
	 */
	@ApiModelProperty("页面UUID")
	private String pageUuId;
	
	/**
	 * 布局代码
	 */
	@ApiModelProperty("布局代码")
	private String layoutCode;
	
	/**
	 * 主题样式
	 */
	@ApiModelProperty("主题样式")
	private String themeStyle;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getPageUuId() {
		return pageUuId;
	}

	public void setPageUuId(String pageUuId) {
		this.pageUuId = pageUuId;
	}

	public String getLayoutCode() {
		return layoutCode;
	}

	public void setLayoutCode(String layoutCode) {
		this.layoutCode = layoutCode;
	}

	public String getThemeStyle() {
		return themeStyle;
	}

	public void setThemeStyle(String themeStyle) {
		this.themeStyle = themeStyle;
	}

}
