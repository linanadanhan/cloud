package com.gsoft.portal.webview.page.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 不在提示帮助信息对应的用户表
 * @author zz
 *
 */
@Entity
@Table(name = "cos_page_help_hide_info")
public class SitePageHelpHideEntity extends BaseEntity{

	private static final long serialVersionUID = -837806777932355126L;
	
	/**
	 * uuId
	 */
	@Column(name = "c_uu_id", length = 50)
	private String uuId;
	
	/**
	 * 站点代码
	 */
	@Column(name = "c_site_code", length = 50)
	private String siteCode;

	/**
	 * 所属用户登录名
	 */
	@Column(name = "c_owner")
	private String owner;

	public String getUuId() {
		return uuId;
	}

	public void setUuId(String uuId) {
		this.uuId = uuId;
	}

	public String getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
}
