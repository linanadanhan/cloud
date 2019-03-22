package com.gsoft.portal.component.layout.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 用户自定义布局信息表
 * @author SN
 *
 */
@Entity
@Table(name = "cos_custom_layout")
public class CustomLayoutEntity  extends BaseEntity {

	private static final long serialVersionUID = -5494518685634330767L;
	
	/**
	 * 用户ID
	 */
	@Column(name = "c_user_id", length = 9)
	private Long userId;
	
	/**
	 * 页面UUID
	 */
	@Column(name = "c_page_uu_id", length = 50)
	private String pageUuId;
	
	/**
	 * 布局代码
	 */
	@Column(name = "c_layout_code", length = 200)
	private String layoutCode;
	
	/**
	 * 主题样式
	 */
	@Column(name = "c_theme_style", length = 50)
	private String themeStyle;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getPageUuId() {
		return pageUuId;
	}

	public void setPageUuId(String pageUuId) {
		this.pageUuId = pageUuId;
	}

	public String getLayoutCode() {
		return layoutCode;
	}

	public void setLayoutCode(String layoutCode) {
		this.layoutCode = layoutCode;
	}

	public String getThemeStyle() {
		return themeStyle;
	}

	public void setThemeStyle(String themeStyle) {
		this.themeStyle = themeStyle;
	}

}
