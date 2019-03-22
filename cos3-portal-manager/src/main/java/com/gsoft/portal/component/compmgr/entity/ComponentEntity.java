package com.gsoft.portal.component.compmgr.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 部件信息表
 * @author SN
 *
 */
@Entity
@Table(name = "cos_portal_component")
public class ComponentEntity  extends BaseEntity {

	private static final long serialVersionUID = -5494518685634330767L;
	
	/**
	 * 部件代码
	 */
	@Column(name = "c_code", length = 50)
	private String code;
	
	/**
	 * 部件名称
	 */
	@Column(name = "c_name", length = 200)
	private String name;
	
	/**
	 * 部件信息
	 */
	@Lob
	@Column(name = "c_desc")
	private String desc;
	
	/**
	 * 是否认证
	 */
	@Column(name = "c_is_auth",columnDefinition = "BIT(1)")
	private Boolean isAuth;
	
	/**
	 * 状态
	 */
	@Column(name = "c_status",columnDefinition = "BIT(1)")
	private Boolean status;
	
	/**
	 * 命名文件
	 */
	@Column(name = "c_chunk_file")
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
