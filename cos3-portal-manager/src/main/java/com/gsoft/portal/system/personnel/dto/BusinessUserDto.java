package com.gsoft.portal.system.personnel.dto;

import java.util.Date;

import com.gsoft.cos3.dto.BaseDto;

/**
 * 业务用户
 */
public class BusinessUserDto extends BaseDto {

	private static final long serialVersionUID = -364249724627372468L;

	/**
	 * 作为用户的唯一标识，用户移动至另一个行政区划下时，就是用这个唯一标识联系新旧用户
	 */
	@SuppressWarnings("unused")
	private String uuid;
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
	 * 移动电话
	 */
	private String mobilePhone;

	/**
	 * 电子邮箱
	 */
	private String email;

	/**
	 * 人员可用状态 :1启用，0停用
	 *  默认为1
	 */
	private Boolean status=true;

	/**
	 * 密码策略 1=初始化密码，2=短信随机产生密码
	 */
	private Integer passwordPolicy = 1;

	/**
	 * 上次正常登录时间
	 */
	private Date lastLogin;
	/**
	 * 上次失败时间登录
	 */
	private Date lastLoginFailed;

	/**
	 * 上次登录 IP
	 */
	private String lastLoginClientIp;
	/**
	 * 是否允许移动访问 : 1允许，0不允许
	 * 默认允许
	 */
	private Boolean isMobileAccess=true;

	/**
	 * 排序号
	 */
	private Integer sortNo;

	/**
	 * 失效时间
	 */
	private Date aeadTime;

	/**
	 * 固定电话
	 */
	private String phone;

	/**
	 * 地址
	 */
	private String address;

	/**
	 * 用户头像
	 */
	private String headImg;

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

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

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

	public Boolean getMobileAccess() {
		return isMobileAccess;
	}

	public void setMobileAccess(Boolean mobileAccess) {
		isMobileAccess = mobileAccess;
	}

	public Integer getSortNo() {
		return sortNo;
	}

	public void setSortNo(Integer sortNo) {
		this.sortNo = sortNo;
	}

	public Date getAeadTime() {
		return aeadTime;
	}

	public void setAeadTime(Date aeadTime) {
		this.aeadTime = aeadTime;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}
}
