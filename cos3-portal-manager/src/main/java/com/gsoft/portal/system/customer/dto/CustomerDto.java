package com.gsoft.portal.system.customer.dto;

import com.gsoft.cos3.dto.BaseDto;

/**
 * 租户管理Dto
 * 
 * @author chenxx
 *
 */
public class CustomerDto extends BaseDto {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 客户名称
	 */
	private String name;
	/**
	 * 客户标识
	 */
	private String code;
	/**
	 * 数据库类型
	 */
	private String dbType;
	/**
	 * 数据库方言
	 */
	private String dbDialect;
	/**
	 * 数据源地址
	 */
	private String dsUrl;
	/**
	 * 数据源驱动
	 */
	private String dsDriverClassName;
	/**
	 * 数据源用户名
	 */
	private String dsUserName;
	/**
	 * 数据源密码
	 */
	private String dsPassword;
	/**
	 * 数据源连接数
	 */
	private Integer dsMaxActive;
	/**
	 * 数据源初始数量
	 */
	private Integer dsMinIdle;
	/**
	 * 最大在线用户数
	 */
	private Integer onlineUser;
	
	/**
	 * 域名
	 */
	private String domain;

	public CustomerDto() {
		super();
	}

	/**
	 * 客户名称
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 客户标识
	 */
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * 数据源地址
	 */
	public String getDsUrl() {
		return dsUrl;
	}

	public void setDsUrl(String dsUrl) {
		this.dsUrl = dsUrl;
	}

	/**
	 * 数据源驱动
	 */
	public String getDsDriverClassName() {
		return dsDriverClassName;
	}

	public void setDsDriverClassName(String dsDriverClassName) {
		this.dsDriverClassName = dsDriverClassName;
	}

	/**
	 * 数据源用户名
	 */
	public String getDsUserName() {
		return dsUserName;
	}

	public void setDsUserName(String dsUserName) {
		this.dsUserName = dsUserName;
	}

	/**
	 * 数据源密码
	 */
	public String getDsPassword() {
		return dsPassword;
	}

	public void setDsPassword(String dsPassword) {
		this.dsPassword = dsPassword;
	}

	/**
	 * 数据源连接数
	 */
	public Integer getDsMaxActive() {
		return dsMaxActive;
	}

	public void setDsMaxActive(Integer dsMaxActive) {
		this.dsMaxActive = dsMaxActive;
	}

	/**
	 * 数据源初始数量
	 */
	public Integer getDsMinIdle() {
		return dsMinIdle;
	}

	public void setDsMinIdle(Integer dsMinIdle) {
		this.dsMinIdle = dsMinIdle;
	}

	public Integer getOnlineUser() {
		return onlineUser;
	}

	public void setOnlineUser(Integer onlineUser) {
		this.onlineUser = onlineUser;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getDbDialect() {
		return dbDialect;
	}

	public void setDbDialect(String dbDialect) {
		this.dbDialect = dbDialect;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}