package com.gsoft.portal.component.decorate.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 修饰器
 * 
 * @author SN
 *
 */
@ApiModel("修饰器信息DTO")
public class DecorateDto extends BaseDto {
	
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5725285995682130906L;

	/**
	 * 修饰器代码
	 */
	@ApiModelProperty("修饰器代码")
	private String code;
	
	/**
	 * 项目CODE
	 */
	@ApiModelProperty("项目代码")
	private String projectCode;
	
	/**
	 * 修饰器名称
	 */
	@ApiModelProperty("修饰器名称")
	private String name;
	
	/**
	 * 是否导入
	 */
	@ApiModelProperty("是否导入")
	private String isImp;
	
	/**
	 * 修饰器文件包ID
	 */
	@ApiModelProperty("修饰器文件ID")
	private String referenceId;
	
	/**
	 * 是否系统内置
	 */
	@ApiModelProperty("是否系统内置")
	private Boolean isSystem;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIsImp() {
		return isImp;
	}

	public void setIsImp(String isImp) {
		this.isImp = isImp;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public Boolean getIsSystem() {
		return isSystem;
	}

	public void setIsSystem(Boolean isSystem) {
		this.isSystem = isSystem;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

}
