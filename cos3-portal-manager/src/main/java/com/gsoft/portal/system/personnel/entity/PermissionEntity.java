package com.gsoft.portal.system.personnel.entity;

import com.gsoft.cos3.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 权限表
 *
 * @author helx
 * @date 2017年8月7日 上午11:30:02
 */
@Entity
@Table(name = "COS_SYS_PERMISSION")
public class PermissionEntity extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 权限名称
	 */
	@Column(name = "C_NAME",length=100)
	private String name;
	
	/**
	 * 权限代码
	 */
	@Column(name = "C_CODE",length=32)
	private String code;
	
	/**
	 * 权限描述
	 */
	@Column(name = "C_DESCRIBE",length=200)
	private String describe;
	
	/**
	 * 分类名,直接输入，不用数据字典维护
	 */
	@Column(name = "C_TYPE",length=100)
	private String type;
	
	
	/**
	 * 包含资源url
	 * 按/user/add/**规则写,多个用逗号分隔
	 */
	@Column(name = "C_INCLUDE_RESOURCE_URL",length=500)
	private String includeResourceRul; 
	
	/**
	 * 排斥资源url
	 * 按/user/add/**规则写,多个用逗号分隔
	 */
	@Column(name = "C_EXCLUDE_RESOURCE_URL",length=500)
	private String excludeResourceRul;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIncludeResourceRul() {
		return includeResourceRul;
	}

	public void setIncludeResourceRul(String includeResourceRul) {
		this.includeResourceRul = includeResourceRul;
	}

	public String getExcludeResourceRul() {
		return excludeResourceRul;
	}

	public void setExcludeResourceRul(String excludeResourceRul) {
		this.excludeResourceRul = excludeResourceRul;
	}
	

}
