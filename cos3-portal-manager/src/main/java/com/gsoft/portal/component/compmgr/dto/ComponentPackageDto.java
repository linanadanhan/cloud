package com.gsoft.portal.component.compmgr.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;

/**
 * 部件包记录信息DTO
 * 
 * @author SN
 *
 */
@ApiModel("部件信息DTO")
public class ComponentPackageDto extends BaseDto {
	
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 部件包名称
	 */
	private String componentName;
	
	/**
	 * 版本号
	 */
	private String version;
	
	/**
	 * 部件包
	 */
	private String referenceId;
	
	/**
	 * 状态
	 */
	private Boolean status;

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

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

}
