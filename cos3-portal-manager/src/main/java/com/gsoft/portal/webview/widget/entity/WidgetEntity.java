package com.gsoft.portal.webview.widget.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * widget信息表
 * @author SN
 *
 */
@Entity
@Table(name = "cos_portal_widget")
public class WidgetEntity  extends BaseEntity {

	private static final long serialVersionUID = -5494518685634330767L;
	
	/**
	 * widget代码
	 */
	@Column(name = "c_code", length = 200)
	private String code;
	
	/**
	 * 项目CODE
	 */
	@Column(name = "c_project_code", length = 50)
	private String projectCode;
	
	/**
	 * 分类
	 */
	@Column(name = "c_category", length = 200)
	private String category;
	
	/**
	 * widget名称
	 */
	@Column(name = "c_name", length = 200)
	private String name;
	
	/**
	 * widget标题
	 */
	@Column(name = "c_title", length = 200)
	private String title;
	
	/**
	 * widget 描述信息
	 */
	@Column(name = "c_desc", length = 200)
	private String desc;
	
	/**
	 * widget包文件ID
	 */
	@Column(name = "c_reference_id", length = 50)
	private String referenceId;
	
	/**
	 * 是否开放
	 */
	@Column(name = "c_is_open", length = 1)
	private String isOpen;
	
	/**
	 * 是否导入widget
	 */
	@Column(name = "c_is_imp", length = 1)
	private String isImp;
	
	/**
	 * 是否系统内置
	 */
	@Column(name = "c_is_system",columnDefinition = "BIT(1)")
	private Boolean isSystem;
	
	/**
	 * 是否为嵌套widget
	 */
	@Column(name = "c_is_nested",columnDefinition = "BIT(1)")
	private Boolean isNested;
	
	/**
	 * 是否为业务widget
	 */
	@Column(name = "c_is_business",columnDefinition = "BIT(1)")
	private Boolean isBusiness = false;
	
	/**
	 * 默认参数
	 */
	@Lob
	@Column(name = "c_params")
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
