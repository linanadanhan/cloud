package com.gsoft.portal.component.theme.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 自定义主题信息表
 * @author SN
 *
 */
@Entity
@Table(name = "cos_custom_theme")
public class CustomThemeEntity  extends BaseEntity {

	private static final long serialVersionUID = -5494518685634330767L;
	
	/**
	 * 用户ID
	 */
	@Column(name = "c_user_id", length = 9)
	private Long userId;
	
	/**
	 * 站点代码
	 */
	@Column(name = "c_site_code", length = 50)
	private String siteCode;
	
	/**
	 * 主题code
	 */
	@Column(name = "c_theme_code", length = 200)
	private String themeCode;
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	public String getThemeCode() {
		return themeCode;
	}

	public void setThemeCode(String themeCode) {
		this.themeCode = themeCode;
	}
}
