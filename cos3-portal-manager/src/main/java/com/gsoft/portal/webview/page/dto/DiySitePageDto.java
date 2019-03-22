package com.gsoft.portal.webview.page.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 个性化站点页面
 * 
 * @author SN
 *
 */
@ApiModel("个性化站点页面DTO")
public class DiySitePageDto extends BaseDto {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5725285995682130906L;

	/**
	 * 页面uuId
	 */
	@ApiModelProperty("页面uuId")
	private String uuId;

	/**
	 * 用户ID
	 */
	@ApiModelProperty("用户ID")
	private Long userId;

	/**
	 * 页面名称
	 */
	@ApiModelProperty("页面名称")
	private String name;

	/**
	 * 页面path
	 */
	@ApiModelProperty("页面path")
	private String path;

	/**
	 * 页面path 级联
	 */
	@ApiModelProperty("页面级联")
	private String cascade;

	/**
	 * 父页面ID
	 */
	@ApiModelProperty("父页面ID")
	private long parentId;

	/**
	 * 站点Code
	 */
	@ApiModelProperty("站点代码")
	private String siteCode;

	/**
	 * 页面主题code
	 */
	@ApiModelProperty("页面主题")
	private String themeCode;

	/**
	 * 页面布局code
	 */
	@ApiModelProperty("页面布局")
	private String layoutCode;

	/**
	 * 是否在新窗口打开
	 */
	@ApiModelProperty("是否新窗口打开")
	private String openSelf;

	/**
	 * 是否在导航菜单栏隐藏
	 */
	@ApiModelProperty("是否隐藏")
	private String navHidden;

	/**
	 * 是否允许个性化配置widget
	 */
	@ApiModelProperty("个性化widget")
	private String allowWidget;

	/**
	 * 是否允许个性化配置布局
	 */
	@ApiModelProperty("个性化布局")
	private String allowLayout;

	/**
	 * 页面状态
	 */
	@ApiModelProperty("页面状态")
	private int status;

	/**
	 * 仅作为菜单
	 */
	@ApiModelProperty("作为菜单")
	private Boolean isMenu;

	/**
	 * 是否为文件夹
	 */
	@ApiModelProperty("是否为文件夹")
	private Boolean isFolder = false;

	/**
	 * 主题样式
	 */
	@ApiModelProperty("主题样式")
	private String themeStyle;

	/**
	 * 排序号
	 */
	@ApiModelProperty("排序号")
	private Integer sortNo;

	/**
	 * 链接地址
	 */
	@ApiModelProperty("链接地址")
	private String linkUrl;

	/**
	 * 作为链接项
	 */
	@ApiModelProperty("链接项")
	private Boolean isLink;

	/**
	 * 是否为系统页
	 */
	@ApiModelProperty("是否为系统页")
	private Boolean isSystem;

	/**
	 * 系统页面ID
	 */
	@ApiModelProperty("是否为系统页")
	private Long sysPageId;
	
	/**
	 * 页面模版
	 */
	@ApiModelProperty("页面模版")
	private String pageTempCode;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
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

	public String getPageTempCode() {
		return pageTempCode;
	}

	public void setPageTempCode(String pageTempCode) {
		this.pageTempCode = pageTempCode;
	}

}
