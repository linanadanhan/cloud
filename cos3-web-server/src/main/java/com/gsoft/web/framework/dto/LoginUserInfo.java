package com.gsoft.web.framework.dto;

import java.io.Serializable;

public class LoginUserInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 用户名称
	 */
	private String name;
	/**
	 * 用户id
	 */
	private Long id;
	
	/**
	 * 用户所在机构code
	 */
	private String orgCode;
	
	/**
	 * 用户机构级联code
	 */
	private String orgCascade;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getOrgCascade() {
		return orgCascade;
	}
	public void setOrgCascade(String orgCascade) {
		this.orgCascade = orgCascade;
	}

}
