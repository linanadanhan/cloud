package com.gsoft.filemanager.local.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

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
@Entity
@Table(name = "cos_file_reference")
public class FileReference extends BaseEntity {

	private static final long serialVersionUID = 5477828067597395431L;

	@Column(name = "c_reference_id")
	private String referenceId;
	
	/**
	 * 关联文件指纹
	 */
	@Column(name = "c_file_code")
	private String code;

	/**
	 * 文件名
	 */
	@Column(name = "c_file_name")
	private String name;

	/**
	 * 文件类型
	 */
	@Column(name = "c_file_type")
	private String type;

	/**
	 * 文件扩展名
	 */
	@Column(name = "c_file_suffix")
	private String suffix;

	/**
	 * 使用次数
	 */
	@Column(name = "c_used")
	private int used = 0;// 是否使用，为>0时，不可垃圾回收

	/**
	 * 应用ID
	 */
	@Column(name = "c_app_id")
	private String appId;

	/**
	 * 业务主键
	 */
	@Column(name = "c_business_key")
	private String businessKey;

	/**
	 * 业务类型
	 */
	@Column(name = "c_business_type")
	private String businessType;
	
	/**
	 * 虚拟路径
	 */
	@Column(name = "c_virtual_path")
	private String virtualPath;

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

	public int getUsed() {
		return used;
	}

	public void setUsed(int used) {
		this.used = used;
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

	public String getVirtualPath() {
		return virtualPath;
	}

	public void setVirtualPath(String virtualPath) {
		this.virtualPath = virtualPath;
	}

}
