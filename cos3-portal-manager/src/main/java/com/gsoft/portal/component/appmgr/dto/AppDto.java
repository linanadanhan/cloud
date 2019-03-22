package com.gsoft.portal.component.appmgr.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 应用信息DTO
 * 
 * @author SN
 *
 */
@ApiModel("应用信息DTO")
public class AppDto extends BaseDto {
	
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5725285995682130906L;
	
	/**
	 * 应用标识
	 */
	@ApiModelProperty("应用标识")
	private String code;	
	
	/**
	 * 应用名称
	 */
	@ApiModelProperty("应用名称")
	private String name;
	
	/**
	 * 应用描述
	 */
	@ApiModelProperty("应用描述")
	private String desc;
	
	/**
     * 可用状态 1可用，0停用
     */
	@ApiModelProperty("状态")
    private String status;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
