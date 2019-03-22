package com.gsoft.web.framework.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gsoft.cos3.dto.BaseDto;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 人员表
 *
 * @author helx
 * @date 2017年8月7日 上午11:06:21
 */
public class PersonnelDto extends BaseDto {

	private static final long serialVersionUID = -364249724627372468L;

	/**
	 * 中文名
	 */
	private String name;
	/**
	 * 登录名
	 */
	private String loginName;
	/**
	 * 密码
	 */
	private String passWord;
	/**
	 * 人员编号
	 */
	private String personNumber;
	/**
	 * 人员编号
	 */
	private Integer number;

	/**
	 * 移动电话
	 */
	private String mobilePhone;

	/**
	 * 电子邮箱
	 */
	private String email;

	/**
	 * 人员可用状态 :1启用，0停用 默认为1
	 */
	private Boolean status = true;

	/**
	 * 密码策略 1=初始化密码，2=短信随机产生密码
	 */
	private Integer passwordPolicy = 1;

	/**
	 * 上次正常登录时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastLogin;
	/**
	 * 上次失败时间登录
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastLoginFailed;

	/**
	 * 上次登录 IP
	 */
	private String lastLoginClientIp;
	/**
	 * 是否允许移动访问 : 1允许，0不允许 默认允许
	 */
	private Boolean isMobileAccess = true;

	/**
	 * 排序号
	 */
	private Integer sortNo = 1;
	
	/**
	 * 允许转授
	 */
	private Boolean isTurnGrant;

	/**
	 * jwt过期时间 单位秒
	 */
	private Long expiration;

	public PersonnelDto(Long id, String name, String loginName, String personNumber, Boolean isTurnGrant) {
		super();
		super.setId(id);
		this.name = name;
		this.loginName = loginName;
		this.personNumber = personNumber;
		this.isTurnGrant = isTurnGrant;
	}

	public PersonnelDto(String name, String loginName, String passWord, String personNumber, String mobilePhone) {
		super();
		this.name = name;
		this.loginName = loginName;
		this.passWord = passWord;
		this.personNumber = personNumber;
		this.mobilePhone = mobilePhone;
	}

	public PersonnelDto() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getPersonNumber() {
		return personNumber;
	}

	public void setPersonNumber(String personNumber) {
		this.personNumber = personNumber;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Integer getPasswordPolicy() {
		return passwordPolicy;
	}

	public void setPasswordPolicy(Integer passwordPolicy) {
		this.passwordPolicy = passwordPolicy;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getLastLoginFailed() {
		return lastLoginFailed;
	}

	public void setLastLoginFailed(Date lastLoginFailed) {
		this.lastLoginFailed = lastLoginFailed;
	}

	public String getLastLoginClientIp() {
		return lastLoginClientIp;
	}

	public void setLastLoginClientIp(String lastLoginClientIp) {
		this.lastLoginClientIp = lastLoginClientIp;
	}

	public Boolean getIsMobileAccess() {
		return isMobileAccess;
	}

	public void setIsMobileAccess(Boolean isMobileAccess) {
		this.isMobileAccess = isMobileAccess;
	}

	public Integer getSortNo() {
		return sortNo;
	}

	public void setSortNo(Integer sortNo) {
		this.sortNo = sortNo;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Boolean getIsTurnGrant() {
		return isTurnGrant;
	}

	public void setIsTurnGrant(Boolean isTurnGrant) {
		this.isTurnGrant = isTurnGrant;
	}

	public Long getExpiration() {
		return expiration;
	}

	public void setExpiration(Long expiration) {
		this.expiration = expiration;
	}
}
