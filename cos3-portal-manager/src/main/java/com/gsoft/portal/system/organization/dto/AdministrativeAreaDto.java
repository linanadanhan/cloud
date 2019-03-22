package com.gsoft.portal.system.organization.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gsoft.cos3.dto.BaseDto;
import com.gsoft.cos3.util.excel.ExcelVOAttribute;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class AdministrativeAreaDto extends BaseDto {
	private static final long serialVersionUID = -4073688412926376943L;

	@ExcelVOAttribute(name = "名称", column = "A")
	private String name;
	
	@ExcelVOAttribute(name = "编码", column = "B")
	private String code;
	
	private Integer level;
	
	private Long parentId;
	
	private String cascade;
	
	/**
	 * 失效时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date aeadTime; 
	
	public AdministrativeAreaDto(){}
	
	public AdministrativeAreaDto(String name, String code){
		this.name = name;
		this.code = code;
	}
	

	public AdministrativeAreaDto(String name, String code, Integer level,
                                 Long parentId, String cascade) {
		super();
		this.name = name;
		this.code = code;
		this.level = level;
		this.parentId = parentId;
		this.cascade = cascade;
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
	
	
	
}
