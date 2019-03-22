package com.gsoft.portal.system.organization.entity;

import java.util.Date;

import com.gsoft.cos3.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 组织机构表
 * @author SN
 *
 */
@Entity
@Table(name = "cos_organization_org")
public class OrganizationEntity  extends BaseEntity {

	private static final long serialVersionUID = -5494518685634330767L;
	
	/**
	 * 机构名
	 */
	@Column(name = "c_name", length = 100)
	private String name;
	
	/**
	 * 级联code
	 */
	@Column(name = "c_code", length = 30)
	private String code;
	
	/**
	 * 级别
	 */
	@Column(name = "c_level")
	private Integer level;
	
	/**
	 * 排序号
	 */
	@Column(name = "c_sort_no")
	private Integer sortNo;;
	
	/**
	 * 父ID
	 */
	@Column(name = "c_parent_id", length = 9)
	private Long parentId;
	
	/**
	 * 级联关系
	 */
	@Column(name = "c_cascade", length = 100)
	private String cascade;
	
	/**
	 * 机构类型  0 公司  1 部门  2 单位
	 */
	@Column(name = "c_org_type", length = 50)
	private String orgType;

	/**
	 * 机构维度
	 * 例如：预算单位，主管部门，部门科室等
	 */
	@Column(name = "c_dimension", length = 50)
	private String dimension;
	
	/**
	 * 失效时间
	 */
	@Column(name = "C_AEAD_TIME")
	private Date aeadTime; 
	
	/**
	 * 所属行政规划
	 */
	@Column(name = "c_area_code", length = 30)
	private String areaCode;
	
	
	/**
	 * 地址
	 */
	@Column(name = "c_address", length = 100)
	private String address;
	
	/**
	 * 聯係電話
	 */
	@Column(name = "c_telPhone", length = 30)
	private String telPhone;
	

	public Date getAeadTime() {
		return aeadTime;
	}

	public void setAeadTime(Date aeadTime) {
		this.aeadTime = aeadTime;
	}

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

	public Integer getLevel() {
		return level;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getCascade() {
		return cascade;
	}

	public void setCascade(String cascade) {
		this.cascade = cascade;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public Integer getSortNo() {
		return sortNo;
	}

	public void setSortNo(Integer sortNo) {
		this.sortNo = sortNo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelPhone() {
		return telPhone;
	}

	public void setTelPhone(String telPhone) {
		this.telPhone = telPhone;
	}

	
}
