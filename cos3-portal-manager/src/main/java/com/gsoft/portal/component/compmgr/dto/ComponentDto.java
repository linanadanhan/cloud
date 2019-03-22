package com.gsoft.portal.component.compmgr.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 部件信息DTO
 * 
 * @author SN
 *
 */
@ApiModel("部件信息DTO")
public class ComponentDto extends BaseDto {
	
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5725285995682130906L;
	
	/**
	 * 部件代码
	 */
	@ApiModelProperty("部件代码")
	private String code;
	
	/**
	 * 部件名称
	 */
	@ApiModelProperty("部件名称")
	private String name;
	
	/**
	 * 部件信息
	 */
	@ApiModelProperty("部件信息")
	private String desc;
	
	/**
	 * 是否认证
	 */
	@ApiModelProperty("是否认证")
	private Boolean isAuth;
	
	/**
	 * 状态
	 */
	@ApiModelProperty("状态")
	private Boolean status;
	
	/**
	 * 命名文件
	 */
	@ApiModelProperty("命名文件")
	private String chunkFile;

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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Boolean getIsAuth() {
		return isAuth;
	}

	public void setIsAuth(Boolean isAuth) {
		this.isAuth = isAuth;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getChunkFile() {
		return chunkFile;
	}

	public void setChunkFile(String chunkFile) {
		this.chunkFile = chunkFile;
	}

}
