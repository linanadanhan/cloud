package com.gsoft.portal.component.decorate.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 修饰器信息表
 * @author SN
 *
 */
@Entity
@Table(name = "cos_portal_decorate")
public class DecorateEntity  extends BaseEntity {

	private static final long serialVersionUID = -5494518685634330767L;
	
	/**
	 * 修饰器代码
	 */
	@Column(name = "c_code", length = 200)
	private String code;
	
	/**
	 * 项目CODE
	 */
	@Column(name = "c_project_code", length = 50)
	private String projectCode;
	
	/**
	 * 修饰器名称
	 */
	@Column(name = "c_name", length = 200)
	private String name;
	
	/**
	 * 是否导入修饰器
	 */
	@Column(name = "c_is_imp", length = 1)
	private String isImp;
	
	/**
	 * 是否系统内置
	 */
	@Column(name = "c_is_system",columnDefinition = "BIT(1)")
	private Boolean isSystem;
	
	/**
	 * 修饰器包文件ID
	 */
	@Column(name = "c_reference_id", length = 50)
	private String referenceId;

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
