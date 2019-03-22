package com.gsoft.portal.component.compmgr.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 业务组件配置信息DTO
 * 
 * @author SN
 *
 */
@ApiModel("业务组件配置DTO")
public class BusinessCompConfDto extends BaseDto {
	
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5725285995682130906L;
	
	/**
	 * 组件ID
	 */
	@ApiModelProperty("组件ID")
	private Long compId;	
	
	/**
	 * 实例json数据
	 */
	@ApiModelProperty("实例json")
	private String json;

	public Long getCompId() {
		return compId;
	}

	public void setCompId(Long compId) {
		this.compId = compId;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
}
