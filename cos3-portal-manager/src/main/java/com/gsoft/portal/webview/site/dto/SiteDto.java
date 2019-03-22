package com.gsoft.portal.webview.site.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 站点
 * 
 * @author SN
 *
 */
@ApiModel("站点DTO")
public class SiteDto extends BaseDto {
	
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5725285995682130906L;

	/**
	 * 站点代码
	 */
	@ApiModelProperty("站点代码")
	private String code;
	
	/**
	 * 站点名称
	 */
	@ApiModelProperty("站点名称")
	private String name;
	
	/**
	 * 站点标题
	 */
	@ApiModelProperty("站点标题")
	private String title;
	
	/**
	 * 站点公开主题
	 */
	@ApiModelProperty("公开主题")
	private String publicTheme;
	
	/**
	 * 站点私有主题
	 */
	@ApiModelProperty("私有主题")
	private String privateTheme;
	
	/**
	 * 站点logo图片
	 */
	@ApiModelProperty("站点logo")
	private String logo;
	
	/**
	 * 站点状态
	 */
	@ApiModelProperty("状态")
	private int status;
	
	/**
	 * 登录类型
	 */
	@ApiModelProperty("登录类型")
	private String loginType;
	
	/**
	 * 登录widget
	 */
	@ApiModelProperty("登录widget")
	private String loginWidget;
	
	/**
	 * 版权说明
	 */
	@ApiModelProperty("版权说明")
	private String copyright;
	
	/**
	 * 随机主题
	 */
	@ApiModelProperty("随机主题")
	private String randomTheme;
	
	/**
	 * 节日主题
	 */
	@ApiModelProperty("节日主题")
	private String holidayTheme;
	
	/**
	 * 节日主题时间范围
	 */
	@ApiModelProperty("节日主题范围")
	private String holidayRange;
	
	/**
	 * 个性化主题范围
	 */
	@ApiModelProperty("个性化主题")
	private String profileTheme;
	
	/**
	 * 开启IM服务
	 */
	@ApiModelProperty("开启IM服务")
	private Boolean openIm;
	
	/**
	 * 域名
	 */
	@ApiModelProperty("域名")
	private String domainName;
	
	/**
	 * 租户
	 */
	@ApiModelProperty("租户")
	private String customer;
	
	/**
	 * 开启个性化菜单项
	 */
	@ApiModelProperty("开启个性化菜单项")
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
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

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public Boolean getOpenDiyMenu() {
		return openDiyMenu;
	}

	public void setOpenDiyMenu(Boolean openDiyMenu) {
		this.openDiyMenu = openDiyMenu;
	}
}
