package com.gsoft.portal.component.impmgr.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 导入DTO
 * 
 * @author SN
 *
 */
@ApiModel("组件导入DTO")
public class ImportDto extends BaseDto {
	
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5725285995682130906L;

	/**
	 * 导入文件
	 */
	@ApiModelProperty("文件名")
	private String fileName;
	
	/**
	 * 文件别名
	 */
	@ApiModelProperty("文件别名")
	private String fileAlias;
	
	/**
	 * 失败原因
	 */
	@ApiModelProperty("失败原因")
	private String failReason;
	
	/**
	 * 导入结果
	 */
	@ApiModelProperty("导入结果")
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
