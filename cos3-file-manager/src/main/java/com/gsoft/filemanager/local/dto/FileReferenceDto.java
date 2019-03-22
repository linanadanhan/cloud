package com.gsoft.filemanager.local.dto;

import com.gsoft.cos3.dto.BaseDto;

/**
 * <p>
 * FileReference.java
 * </p>
 * <p>
 * 业务文档对象
 * </p>
 * 
 * @author $Author: shencq $
 * @version $Revision: V3.0 $
 */
public class FileReferenceDto extends BaseDto {

	private static final long serialVersionUID = 5477828067597395431L;
	/**
	 * 文件关联ID
	 */
	private String referenceId;
	
	/**
	 * 关联文件指纹
	 */
	private String code;

	/**
	 * 文件名
	 */
	private String name;

	/**
	 * 文件类型
	 */
	private String type;

	/**
	 * 文件扩展名
	 */
	private String suffix;

	/**
	 * 应用ID
	 */
	private String appId;

	/**
	 * 业务主键
	 */
	private String businessKey;

	/**
	 * 业务类型
	 */
	private String businessType;
	
	/**
	 * 虚拟路径
	 */
	private String virtualPath;

	public String getVirtualPath() {
		return virtualPath;
	}

	public void setVirtualPath(String virtualPath) {
		this.virtualPath = virtualPath;
	}

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getBusinessKey() {
		return businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

}
