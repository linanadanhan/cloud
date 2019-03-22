package com.gsoft.web.framework.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gsoft.cos3.dto.BaseDto;
import com.gsoft.cos3.util.excel.ExcelVOAttribute;

/**
 * 组织机构
 * 
 * @author SN
 *
 */
public class OrganizationDto extends BaseDto {
	private static final long serialVersionUID = -4073688412926376943L;

	@ExcelVOAttribute(name = "机构名称", column = "B")
	private String name;

	@ExcelVOAttribute(name = "机构代码", column = "A")
	private String code;

	private Integer level;

	private Long parentId;

	private String cascade;
	
	/**
	 * 机构类型
	 */
	@ExcelVOAttribute(name = "机构类型", column = "C")
	private String orgType;
	
	@ExcelVOAttribute(name = "父集机构代码", column = "D")
	private String parentCode;
	
	/**
	 * 归属区域
	 */
	private String areaCode;

	/**
	 * 失效时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date aeadTime;

	public OrganizationDto() {
	}

	public OrganizationDto(String name, String code) {
		this.name = name;
		this.code = code;
	}

	public OrganizationDto(String name, String code, Integer level, Long parentId, String cascade, String orgType) {
		super();
		this.name = name;
		this.code = code;
		this.level = level;
		this.parentId = parentId;
		this.cascade = cascade;
		this.orgType = orgType;
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

	public void setLevel(Integer level) {
		this.level = level;
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

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getAeadTime() {
		return aeadTime;
	}

	public void setAeadTime(Date aeadTime) {
		this.aeadTime = aeadTime;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

}
