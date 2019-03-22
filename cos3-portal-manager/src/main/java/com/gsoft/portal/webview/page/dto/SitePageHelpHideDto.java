package com.gsoft.portal.webview.page.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("页面帮助隐藏DTO")
public class SitePageHelpHideDto extends BaseDto{
	private static final long serialVersionUID = -4397177647926515882L;
	/**
	 * uuId
	 */
	@ApiModelProperty("uuId")
	private String uuId;
	
	/**
	 * 站点代码
	 */
	@ApiModelProperty("站点代码")
	private String siteCode;

	/**
	 * 所属用户登录名
	 */
	@ApiModelProperty("登录用户")
	private String owner;

	public String getUuId() {
		return uuId;
	}

	public void setUuId(String uuId) {
		this.uuId = uuId;
	}

	public String getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
}
