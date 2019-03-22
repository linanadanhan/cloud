package com.gsoft.portal.system.customer.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 租户管理
 * 
 * @author chenxx
 *
 */
@Entity
@Table(name = "COS_SAAS_CUSTOMER")
public class Customer extends BaseEntity {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 客户名称
	 */
	@Column(name = "C_NAME", length = 100)
	private String name;
	
	/**
	 * 客户标识
	 */
	@Column(name = "C_CODE", length = 20)
	private String code;

	/**
	 * 数据库类型
	 */
	@Column(name = "C_DB_TYPE", length = 20)
	private String dbType;
	/**
	 * 数据库方言
	 */
	@Column(name = "C_DB_DIALECT", length = 50)
	private String dbDialect;
	/**
	 * 数据源地址
	 */
	@Column(name = "C_DSURL", length = 200)
	private String dsUrl;
	/**
	 * 数据源驱动
	 */
	@Column(name = "C_DSDRIVERCLASSNAME", length = 150)
	private String dsDriverClassName;
	/**
	 * 数据源用户名
	 */
	@Column(name = "C_DSUSERNAME", length = 30)
	private String dsUserName;
	/**
	 * 数据源密码
	 */
	@Column(name = "C_DSPASSWORD", length = 50)
	private String dsPassword;
	/**
	 * 数据源连接数
	 */
	@Column(name = "C_DSMAXACTIVE")
	private Integer dsMaxActive;
	/**
	 * 数据源初始数量
	 */
	@Column(name = "C_DSMINIDLE")
	private Integer dsMinIdle;
	/**
	 * 最大在线用户数
	 */
	@Column(name = "C_ONLINE_USER")
	private Integer onlineUser;
	
	/**
	 * 域名
	 */
	@Column(name = "C_DOMAIN", length = 100)
	private String domain;

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