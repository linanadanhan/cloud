package com.gsoft.portal.system.personnel.dto;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gsoft.cos3.dto.BaseDto;
import com.gsoft.cos3.util.excel.ExcelVOAttribute;

/**
 * 人员表
 * 
 * @author helx
 * @date 2017年8月7日 上午11:06:21
 */
public class PersonnelDto extends BaseDto {

	private static final long serialVersionUID = -364249724627372468L;

	/**
	 * 作为用户的唯一标识，用户移动至另一个行政区划下时，就是用这个唯一标识联系新旧用户
	 */
	private String uuid;
	/**
	 * 中文名
	 */
	@ExcelVOAttribute(name = "中文名", column = "A")
	private String name;
	/**
	 * 登录名
	 */
	@ExcelVOAttribute(name = "登录名", column = "B")
	private String loginName;
	/**
	 * 密码
	 */
	private String passWord;
	
	/**
	 * 机构ID
	 */
	private Long orgId;

	//与机构orgId一对一的纬度,便于根据纬度过滤人
	private String dimension;

	/**
	 * 机构代码
	 */
	@ExcelVOAttribute(name = "机构代码", column = "C")
	private String orgCode;

	/**
	 * 移动电话
	 */
	@ExcelVOAttribute(name = "移动电话", column = "D")
	private String mobilePhone;

	/**
	 * 电子邮箱
	 */
	@ExcelVOAttribute(name = "电子邮箱", column = "G")
	private String email;
	
	@ExcelVOAttribute(name = "导入结果", column = "H")
	private String impResult;

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
	 * 失效时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date aeadTime;

	/**
	 * 允许转授
	 */
	private Boolean isTurnGrant;
	
	/**
	 * 用户头像
	 */
	private String headImg;
	
	/**
	 * 失效时间
	 */
	private String idCard;
	
	/**
	 * 性别
	 */
	private String sex;
	
	/**
	 * 固定电话
	 */
	@ExcelVOAttribute(name = "座机号", column = "E")
	private String phone;
	
	/**
	 * 地址
	 */
	@ExcelVOAttribute(name = "办公地址", column = "F")
	private String address;
	
	/**
	 * 出生日期
	 */
	private String birth;
	/**
	 * 单位
	 */
	private String unit;
	
	/**
	 * 部门
	 */
	private String department;

	private Long expiration;

	public PersonnelDto(Long id, String name, String loginName, Boolean isTurnGrant) {
		super();
		super.setId(id);
		this.name = name;
		this.loginName = loginName;
		this.isTurnGrant = isTurnGrant;
	}

	public PersonnelDto(String name, String loginName, String passWord, String mobilePhone) {
		super();
		this.name = name;
		this.loginName = loginName;
		this.passWord = passWord;
		this.mobilePhone = mobilePhone;
	}

	public PersonnelDto(Long id,String name,String loginName,String orgCode,Long orgId,String dimension) {
		super.setId(id);
		this.name = name;
		this.loginName = loginName;
		this.orgCode = orgCode;
		this.orgId = orgId;
		this.dimension=dimension;
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

	public Boolean getIsTurnGrant() {
		return isTurnGrant;
	}

	public void setIsTurnGrant(Boolean isTurnGrant) {
		this.isTurnGrant = isTurnGrant;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getAeadTime() {
		return aeadTime;
	}

	public void setAeadTime(Date aeadTime) {
		this.aeadTime = aeadTime;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
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

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getImpResult() {
		return impResult;
	}

	public void setImpResult(String impResult) {
		this.impResult = impResult;
	}

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public Boolean getMobileAccess() {
		return isMobileAccess;
	}

	public void setMobileAccess(Boolean mobileAccess) {
		isMobileAccess = mobileAccess;
	}

	public Boolean getTurnGrant() {
		return isTurnGrant;
	}

	public void setTurnGrant(Boolean turnGrant) {
		isTurnGrant = turnGrant;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Long getExpiration() {
		return expiration;
	}

	public void setExpiration(Long expiration) {
		this.expiration = expiration;
	}
}
