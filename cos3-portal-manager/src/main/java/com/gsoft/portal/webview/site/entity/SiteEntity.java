package com.gsoft.portal.webview.site.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 站点信息表
 * @author SN
 *
 */
@Entity
@Table(name = "cos_portal_site")
public class SiteEntity  extends BaseEntity {

	private static final long serialVersionUID = -5494518685634330767L;
	
	/**
	 * 站点代码
	 */
	@Column(name = "c_code", length = 50)
	private String code;
	
	/**
	 * 站点名称
	 */
	@Column(name = "c_name", length = 200)
	private String name;
	
	/**
	 * 站点标题
	 */
	@Column(name = "c_title", length = 200)
	private String title;
	
	/**
	 * 站点公开主题
	 */
	@Column(name = "c_public_theme", length = 200)
	private String publicTheme;
	
	/**
	 * 站点私有主题
	 */
	@Column(name = "c_private_theme", length = 200)
	private String privateTheme;
	
	/**
	 * 站点logo图片
	 */
	@Column(name = "c_logo", length = 50)
	private String logo;
	
	/**
	 * 站点状态
	 */
	@Column(name = "c_status")
	private Integer status;
	
	/**
	 * 登录类型
	 */
	@Column(name = "c_login_type", length = 20)
	private String loginType;
	
	/**
	 * 登录widget
	 */
	@Column(name = "c_login_widget", length = 50)
	private String loginWidget;
	
	/**
	 * 版权信息
	 */
	@Column(name = "c_copy_right")
	private String copyright;
	
	/**
	 * 随机主题
	 */
	@Column(name = "c_random_theme")
	private String randomTheme;
	
	/**
	 * 节日主题
	 */
	@Column(name = "c_holiday_theme")
	private String holidayTheme;
	
	/**
	 * 节日主题时间范围
	 */
	@Column(name = "c_holidy_range")
	private String holidayRange;
	
	/**
	 * 个性化主题范围
	 */
	@Column(name = "c_profile_theme")
	private String profileTheme;
	
	/**
	 * 域名
	 */
	@Column(name = "c_domain_name")
	private String domainName;
	
	/**
	 * 是否开启IM服务
	 */
	@Column(name = "c_open_im",columnDefinition = "BIT(1)")
	private Boolean openIm;

	/**
	 * 开启个性化菜单项
	 */
	@Column(name = "c_open_diy_menu",columnDefinition = "BIT(1) default true")
	private Boolean openDiyMenu;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getPublicTheme() {
		return publicTheme;
	}

	public void setPublicTheme(String publicTheme) {
		this.publicTheme = publicTheme;
	}

	public String getPrivateTheme() {
		return privateTheme;
	}

	public void setPrivateTheme(String privateTheme) {
		this.privateTheme = privateTheme;
	}

	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	public String getLoginWidget() {
		return loginWidget;
	}

	public void setLoginWidget(String loginWidget) {
		this.loginWidget = loginWidget;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getRandomTheme() {
		return randomTheme;
	}

	public void setRandomTheme(String randomTheme) {
		this.randomTheme = randomTheme;
	}

	public String getHolidayTheme() {
		return holidayTheme;
	}

	public void setHolidayTheme(String holidayTheme) {
		this.holidayTheme = holidayTheme;
	}

	public String getHolidayRange() {
		return holidayRange;
	}

	public void setHolidayRange(String holidayRange) {
		this.holidayRange = holidayRange;
	}

	public String getProfileTheme() {
		return profileTheme;
	}

	public void setProfileTheme(String profileTheme) {
		this.profileTheme = profileTheme;
	}

	public Boolean getOpenIm() {
		return openIm;
	}

	public void setOpenIm(Boolean openIm) {
		this.openIm = openIm;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public Boolean getOpenDiyMenu() {
		return openDiyMenu;
	}

	public void setOpenDiyMenu(Boolean openDiyMenu) {
		this.openDiyMenu = openDiyMenu;
	}
	
}
