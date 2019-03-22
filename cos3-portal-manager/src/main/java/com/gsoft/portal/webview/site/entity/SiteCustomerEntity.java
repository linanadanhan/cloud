package com.gsoft.portal.webview.site.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 站点租户信息表
 * @author SN
 *
 */
@Entity
@Table(name = "cos_custormer_site_domain")
public class SiteCustomerEntity  extends BaseEntity {

	private static final long serialVersionUID = -5494518685634330767L;
	
	/**
	 * 站点代码
	 */
	@Column(name = "c_site_code", length = 50)
	private String siteCode;
	
	/**
	 * 租户标识
	 */
	@Column(name = "c_custormer_code", length = 50)
	private String customer;
	
	/**
	 * 域名
	 */
	@Column(name = "c_domain", length = 100)
	private String domain;

	public String getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}
