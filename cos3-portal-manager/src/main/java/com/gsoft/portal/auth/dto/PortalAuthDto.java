package com.gsoft.portal.auth.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 门户业务授权DTO
 * 
 * @author SN
 *
 */
@ApiModel("站点页面授权DTO")
public class PortalAuthDto extends BaseDto {
	
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5725285995682130906L;

	/**
	 * 授权ID  用户ID or 角色ID
	 */
	@ApiModelProperty("授权ID")
	private Long grantId;
	
	/**
	 * 业务ID 站点ID 页面ID widgetID
	 */
	@ApiModelProperty("业务ID")
	private Long ywId;
	
	/**
	 * 授权类型
	 */
	@ApiModelProperty("授权类型")
	private String grantType;
	
	/**
	 * 业务类型
	 */
	@ApiModelProperty("业务类型")
	private String ywType;

	public Long getGrantId() {
		return grantId;
	}

	public void setGrantId(Long grantId) {
		this.grantId = grantId;
	}

	public Long getYwId() {
		return ywId;
	}

	public void setYwId(Long ywId) {
		this.ywId = ywId;
	}

	public String getGrantType() {
		return grantType;
	}

	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}

	public String getYwType() {
		return ywType;
	}

	public void setYwType(String ywType) {
		this.ywType = ywType;
	}
	
}
