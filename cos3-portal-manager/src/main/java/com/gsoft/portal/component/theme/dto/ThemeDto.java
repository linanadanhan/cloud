package com.gsoft.portal.component.theme.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 主题
 * 
 * @author SN
 *
 */
@ApiModel("主题DTO")
public class ThemeDto extends BaseDto {
	
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5725285995682130906L;

	/**
	 * 主题代码
	 */
	@ApiModelProperty("主题代码")
	private String code;
	
	/**
	 * 项目CODE
	 */
	@ApiModelProperty("项目代码")
	private String projectCode;
	
	/**
	 * 主题名称
	 */
	@ApiModelProperty("主题名称")
	private String name;
	
	/**
	 * 是否开放
	 */
	@ApiModelProperty("是否开放")
	private String isOpen;
	
	/**
	 * 主题文件包ID
	 */
	@ApiModelProperty("主题包ID")
	private String referenceId;
	
	/**
	 * 是否导入
	 */
	@ApiModelProperty("是否导入")
	private String isImp;
	
	/**
	 * 是否系统内置
	 */
	@ApiModelProperty("是否内置")
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

	public String getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(String isOpen) {
		this.isOpen = isOpen;
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
