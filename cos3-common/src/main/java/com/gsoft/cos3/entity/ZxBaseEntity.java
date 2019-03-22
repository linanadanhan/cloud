package com.gsoft.cos3.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 *  所有实体类的基类
 *
 * @author helx
 * @date 2017年8月7日 上午11:30:46
 */
@MappedSuperclass
public class ZxBaseEntity implements java.io.Serializable {
	private static final long serialVersionUID = -8365029147413123827L;
	/**
	 * 主键Id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "C_ID", length = 9)
	private Long id;	
	/**
	 * 创建者
	 * updatable=false,就不会出现此列为空的情况了
	 */
	@Column(name = "C_CREATE_BY", length = 9,updatable = false)
	private Long createBy;
	/**
	 * 最后修改者
	 */
	@Column(name = "C_UPDATE_BY", length = 9)
	private Long updateBy ;
	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@Column(name = "C_CREATE_TIME",updatable = false)
	private Date createTime = new Date();
	/**
	 * 修改时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@Column(name = "C_UPDATE_TIME")
	private Date updateTime;
	
	/**
	 * 行政区划级联code
	 */
	@Column(name = "C_AREA_CASCADE",length=100)
	private String areaCascade;
	/**
	 * 
	 */
	@Column(name = "C_TENANT_CODE",length=32)
	private String tenantCode;
	
	/**
	 * 逻辑删除标记:1删除，0不删除
	 * 默认没有删除false
	 */
	@Column(name = "C_DELETED")
	private Boolean deleted = false;
	
	
	public Long getCreateBy() {
		return createBy;
	}
	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}
	public Long getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(Long updateBy) {
		this.updateBy = updateBy;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	public String getAreaCascade() {
		return areaCascade;
	}
	public void setAreaCascade(String areaCascade) {
		this.areaCascade = areaCascade;
	}
	public String getTenantCode() {
		return tenantCode;
	}
	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}
	
}
