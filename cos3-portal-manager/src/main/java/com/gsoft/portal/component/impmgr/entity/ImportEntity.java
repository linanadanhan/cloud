package com.gsoft.portal.component.impmgr.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 导入记录信息表
 * @author SN
 *
 */
@Entity
@Table(name = "cos_import_record")
public class ImportEntity  extends BaseEntity {

	private static final long serialVersionUID = -5494518685634330767L;
	
	/**
	 * 文件名
	 */
	@Column(name = "c_file_name", length = 100)
	private String fileName;
	
	/**
	 * 文件别名
	 */
	@Column(name = "c_file_alias", length = 100)
	private String fileAlias;
	
	/**
	 * 失败原因
	 */
	@Column(name = "c_fail_reason")
	private String failReason;
	
	/**
	 * 导入结果
	 */
	@Column(name = "c_result", length = 1)
	private String result;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getFileAlias() {
		return fileAlias;
	}

	public void setFileAlias(String fileAlias) {
		this.fileAlias = fileAlias;
	}

	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}
	
}
