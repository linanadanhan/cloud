package com.gsoft.portal.webview.widget.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * widget
 * 
 * @author SN
 *
 */
@ApiModel("widget DTO")
public class WidgetDto extends BaseDto {
	
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5725285995682130906L;

	/**
	 * widget代码
	 */
	@ApiModelProperty("widget代码")
	private String code;
	
	/**
	 * 项目CODE
	 */
	@ApiModelProperty("项目代码")
	private String projectCode;
	
	/**
	 * widget名称
	 */
	@ApiModelProperty("widget名称")
	private String name;
	
	/**
	 * widget 标题
	 */
	@ApiModelProperty("widget标题")
	private String title;
	
	/**
	 * widget 描述
	 */
	@ApiModelProperty("widget描述")
	private String desc;
	
	/**
	 * 是否开放
	 */
	@ApiModelProperty("是否开放")
	private String isOpen;
	
	/**
	 * widget文件包ID
	 */
	@ApiModelProperty("widget包ID")
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
	
	/**
	 * 是否为嵌套widget
	 */
	@ApiModelProperty("是否嵌套")
	private Boolean isNested;
	
	/**
	 * 分类
	 */
	@ApiModelProperty("分类")
	private String category;
	
	/**
	 * 是否为业务widget
	 */
	@ApiModelProperty("业务widget")
	private Boolean isBusiness;
	
	/**
	 * 默认参数
	 */
	@ApiModelProperty("默认参数")
	private String params;

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getIsSystem() {
		return isSystem;
	}

	public void setIsSystem(Boolean isSystem) {
		this.isSystem = isSystem;
	}

	public Boolean getIsNested() {
		return isNested;
	}

	public void setIsNested(Boolean isNested) {
		this.isNested = isNested;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Boolean getIsBusiness() {
		return isBusiness;
	}

	public void setIsBusiness(Boolean isBusiness) {
		this.isBusiness = isBusiness;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
