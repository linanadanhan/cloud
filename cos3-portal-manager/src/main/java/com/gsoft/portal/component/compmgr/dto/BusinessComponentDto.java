package com.gsoft.portal.component.compmgr.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 业务组件信息DTO
 * 
 * @author SN
 *
 */
@ApiModel("业务组件DTO")
public class BusinessComponentDto extends BaseDto {
	
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5725285995682130906L;
	
	/**
	 * 业务组件名称
	 */
	@ApiModelProperty("业务组件名称")
	private String name;
	
	/**
	 * 业务组件描述信息
	 */
	@ApiModelProperty("业务组件描述")
	private String desc;
	
	/**
	 * 分类
	 */
	@ApiModelProperty("分类")
	private String category;
	
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
