package com.gsoft.portal.component.compmgr.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 部件包记录信息表
 * @author SN
 *
 */
@Entity
@Table(name = "cos_component_package")
public class ComponentPackageEntity  extends BaseEntity {

	private static final long serialVersionUID = -5494518685634330767L;
	
	/**
	 * 部件包名称
	 */
	@Column(name = "c_component_name", length = 50)
	private String componentName;
	
	/**
	 * 版本号
	 */
	@Column(name = "c_version", length = 50)
	private String version;
	
	/**
	 * 部件包
	 */
	@Lob
	@Column(name = "c_reference_id")
	private String referenceId;

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
}
