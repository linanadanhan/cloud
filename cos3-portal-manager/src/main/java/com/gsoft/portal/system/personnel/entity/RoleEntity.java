package com.gsoft.portal.system.personnel.entity;

import com.gsoft.cos3.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 角色实体
 *
 * @author helx
 * @date 2017年8月7日 上午10:44:57
 */
@Entity
@Table(name = "COS_SYS_ROLE")
public class RoleEntity extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 角色名称
	 */
	@Column(name = "C_NAME",length=100)
	private String name;
	
	/**
	 * 角色代码
	 */
	@Column(name = "C_CODE",length=30)
	private String code;
	
	/**
	 * 角色描述
	 */
	@Column(name = "C_DESCRIBE",length=200)
	private String describe;
	
	/**
	 * 角色状态： true：启用，false：停用 
	 * 默认为true
	 */
	@Column(name = "C_STATUS")
	private Boolean status = true; 
	
	/**
	 * 分类名,直接输入，不用数据字典维护
	 */
	@Column(name = "C_TYPE",length=100)
	private String type;

	/**
	 * 维度，用数据字典维护
	 */
	@Column(name = "C_DIMENSION",length=20)
	private String dimension;
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

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}
}
