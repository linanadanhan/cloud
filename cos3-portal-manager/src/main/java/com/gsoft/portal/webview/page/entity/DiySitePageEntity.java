package com.gsoft.portal.webview.page.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 个性化站点页面信息表
 * @author SN
 *
 */
@Entity
@Table(name = "cos_portal_diy_page")
public class DiySitePageEntity  extends BaseEntity {

	private static final long serialVersionUID = -5494518685634330767L;
	
	/**
	 * 页面uuId
	 */
	@Column(name = "c_uu_id", length = 50)
	private String uuId;
	
	/**
	 * 用户ID
	 */
	@Column(name = "c_user_id", length = 9)
	private Long userId;
	
	/**
	 * 页面名称
	 */
	@Column(name = "c_name", length = 200)
	private String name;
	
	/**
	 * 页面path
	 */
	@Column(name = "c_path", length = 50)
	private String path;
	
	/**
	 * 页面path 级联
	 */
	@Column(name = "c_cascade", length = 200)
	private String cascade;
	
	/**
	 * 父页面ID
	 */
	@Column(name = "c_parent_id", length = 9)
	private Long parentId;	
	
	/**
	 * 站点代码
	 */
	@Column(name = "c_site_code", length = 50)
	private String siteCode;	
	
	/**
	 * 页面主题code
	 */
	@Column(name = "c_theme_code", length = 200)
	private String themeCode;
	
	/**
	 * 页面布局code
	 */
	@Column(name = "c_layout_code", length = 200)
	private String layoutCode;
	
	/**
	 * 是否在新窗口打开
	 */
	@Column(name = "c_open_self", length = 1)
	private String openSelf;
	
	/**
	 * 是否在导航菜单栏隐藏
	 */
	@Column(name = "c_nav_hidden", length = 1)
	private String navHidden;
	
	/**
	 * 是否允许个性化配置widget
	 */
	@Column(name = "c_allow_widget", length = 1)
	private String allowWidget;
	
	/**
	 * 是否允许个性化配置布局
	 */
	@Column(name = "c_allow_layout", length = 1)
	private String allowLayout;
	
	/**
	 * 页面状态
	 */
	@Column(name = "c_status")
	private String status;
	
	/**
	 * 是否仅为菜单
	 */
	@Column(name = "c_is_menu",columnDefinition = "BIT(1)")
	private Boolean isMenu;
	
	/**
	 * 是否为文件夹
	 */
	@Column(name = "c_is_folder",columnDefinition = "BIT(1)")
	private Boolean isFolder;
	
	/**
	 * 主题样式
	 */
	@Column(name = "c_theme_style", length = 50)
	private String themeStyle;
	
	/**
	 * 排序号
	 */
	@Column(name = "C_SORT_NO")
	private Integer sortNo;
	
	/**
	 * 链接地址
	 */
	@Lob
	@Column(name = "C_LINK_URL")
	private String linkUrl;
	
	/**
	 * 作为链接项
	 */
	@Column(name = "c_is_link",columnDefinition = "BIT(1)")
	private Boolean isLink;
	
	/**
	 * 是否为系统页
	 */
	@Column(name = "c_is_system",columnDefinition = "BIT(1)")
	private Boolean isSystem;
	
	/**
	 * 系统页面ID
	 */
	@Column(name = "c_page_id", length = 9)
	private Long sysPageId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getThemeCode() {
		return themeCode;
	}

	public void setThemeCode(String themeCode) {
		this.themeCode = themeCode;
	}

	public String getLayoutCode() {
		return layoutCode;
	}

	public void setLayoutCode(String layoutCode) {
		this.layoutCode = layoutCode;
	}

	public String getOpenSelf() {
		return openSelf;
	}

	public void setOpenSelf(String openSelf) {
		this.openSelf = openSelf;
	}

	public String getNavHidden() {
		return navHidden;
	}

	public void setNavHidden(String navHidden) {
		this.navHidden = navHidden;
	}

	public String getAllowWidget() {
		return allowWidget;
	}

	public void setAllowWidget(String allowWidget) {
		this.allowWidget = allowWidget;
	}

	public String getAllowLayout() {
		return allowLayout;
	}

	public void setAllowLayout(String allowLayout) {
		this.allowLayout = allowLayout;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getIsMenu() {
		return isMenu;
	}

	public void setIsMenu(Boolean isMenu) {
		this.isMenu = isMenu;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getCascade() {
		return cascade;
	}

	public void setCascade(String cascade) {
		this.cascade = cascade;
	}

	public Boolean getIsFolder() {
		return isFolder;
	}

	public void setIsFolder(Boolean isFolder) {
		this.isFolder = isFolder;
	}

	public String getThemeStyle() {
		return themeStyle;
	}

	public void setThemeStyle(String themeStyle) {
		this.themeStyle = themeStyle;
	}

	public String getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	public String getUuId() {
		return uuId;
	}

	public void setUuId(String uuId) {
		this.uuId = uuId;
	}

	public Integer getSortNo() {
		return sortNo;
	}

	public void setSortNo(Integer sortNo) {
		this.sortNo = sortNo;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public Boolean getIsLink() {
		return isLink;
	}

	public void setIsLink(Boolean isLink) {
		this.isLink = isLink;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Boolean getIsSystem() {
		return isSystem;
	}

	public void setIsSystem(Boolean isSystem) {
		this.isSystem = isSystem;
	}

	public Long getSysPageId() {
		return sysPageId;
	}

	public void setSysPageId(Long sysPageId) {
		this.sysPageId = sysPageId;
	}

	
}
