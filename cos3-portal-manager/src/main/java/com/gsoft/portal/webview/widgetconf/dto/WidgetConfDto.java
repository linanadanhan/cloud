package com.gsoft.portal.webview.widgetconf.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * widget 配置实例
 * 
 * @author SN
 *
 */
@ApiModel("widget配置DTO")
public class WidgetConfDto extends BaseDto {
	
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5725285995682130906L;
	
	/**
	 * uuId
	 */
	@ApiModelProperty("uuId")
	private String uuId;

	/**
	 * 页面UUID
	 */
	@ApiModelProperty("页面uuId")
	private String pageUuId;		
	
	/**
	 * 布局代码
	 */
	@ApiModelProperty("布局代码")
	private String layoutCode;
	
	
	/**
	 * widget代码
	 */
	@ApiModelProperty("widget代码")
	private String widgetCode;
	
	/**
	 * widget 实例标题
	 */
	@ApiModelProperty("widget标题")
	private String title;
	
	/**
	 * 修饰器代码
	 */
	@ApiModelProperty("修饰器代码")
	private String decorateCode;
	
	/**
	 * 布局位置
	 */
	@ApiModelProperty("布局位置")
	private String layoutPosition;
	
	/**
	 * 排序号
	 */
	@ApiModelProperty("排序号")
	private Integer sortNo;
	
	/**
	 * 是否显示标题
	 */
	@ApiModelProperty("显示标题")
	private Boolean showTitle;
	
	/**
	 * 是否显示边框
	 */
	@ApiModelProperty("显示边框")
	private Boolean showBorder;
	
	/**
	 * 上边距
	 */
	@ApiModelProperty("上边距")
	private Integer marginUp;
	
	/**
	 * 下边距
	 */
	@ApiModelProperty("下边距")
	private Integer marginDown;
	
	/**
	 * 左边距
	 */
	@ApiModelProperty("左边距")
	private Integer marginLeft;
	
	/**
	 * 右边距
	 */
	@ApiModelProperty("右边距")
	private Integer marginRight;
	
	/**
	 * 按钮名称
	 */
	@ApiModelProperty("按钮名称")
	private String buttonName;
	
	/**
	 * 操作URL
	 */
	@ApiModelProperty("操作URL")
	private String handleUrl;
	
	/**
	 * 类型 0  页面 1 嵌套widget
	 */
	@ApiModelProperty("类型")
	private String type;
	
	/**
	 * 是否运行自定义
	 */
	@ApiModelProperty("允许自定义")
	private Boolean isAllowSetting;
	
	/**
	 * 自定义样式
	 */
	@ApiModelProperty("自定义样式")
	private String customStyle;
	
	/**
	 * widget高度
	 */
	@ApiModelProperty("widget高度")
	private Integer widgetHeight;
	
	/**
	 * 嵌套布局代码
	 */
	@ApiModelProperty("嵌套布局")
	private String nestLayoutCode;
	
	/**
	 * json数据
	 */
	@ApiModelProperty("实例json")
	private String json;
	
	/**
	 * widgetIds
	 */
	@ApiModelProperty("widgetIds")
	private String widgetIds;

	public String getLayoutCode() {
		return layoutCode;
	}

	public void setLayoutCode(String layoutCode) {
		this.layoutCode = layoutCode;
	}

	public String getWidgetCode() {
		return widgetCode;
	}

	public void setWidgetCode(String widgetCode) {
		this.widgetCode = widgetCode;
	}

	public String getLayoutPosition() {
		return layoutPosition;
	}

	public void setLayoutPosition(String layoutPosition) {
		this.layoutPosition = layoutPosition;
	}

	public Integer getSortNo() {
		return sortNo;
	}

	public void setSortNo(Integer sortNo) {
		this.sortNo = sortNo;
	}

	public Boolean getShowTitle() {
		return showTitle;
	}

	public void setShowTitle(Boolean showTitle) {
		this.showTitle = showTitle;
	}

	public Boolean getShowBorder() {
		return showBorder;
	}

	public void setShowBorder(Boolean showBorder) {
		this.showBorder = showBorder;
	}

	public Integer getMarginUp() {
		return marginUp;
	}

	public void setMarginUp(Integer marginUp) {
		this.marginUp = marginUp;
	}

	public Integer getMarginDown() {
		return marginDown;
	}

	public void setMarginDown(Integer marginDown) {
		this.marginDown = marginDown;
	}

	public Integer getMarginLeft() {
		return marginLeft;
	}

	public void setMarginLeft(Integer marginLeft) {
		this.marginLeft = marginLeft;
	}

	public Integer getMarginRight() {
		return marginRight;
	}

	public void setMarginRight(Integer marginRight) {
		this.marginRight = marginRight;
	}

	public String getButtonName() {
		return buttonName;
	}

	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}

	public String getHandleUrl() {
		return handleUrl;
	}

	public void setHandleUrl(String handleUrl) {
		this.handleUrl = handleUrl;
	}

	public String getDecorateCode() {
		return decorateCode;
	}

	public void setDecorateCode(String decorateCode) {
		this.decorateCode = decorateCode;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getIsAllowSetting() {
		return isAllowSetting;
	}

	public void setIsAllowSetting(Boolean isAllowSetting) {
		this.isAllowSetting = isAllowSetting;
	}

	public String getCustomStyle() {
		return customStyle;
	}

	public void setCustomStyle(String customStyle) {
		this.customStyle = customStyle;
	}

	public Integer getWidgetHeight() {
		return widgetHeight;
	}

	public void setWidgetHeight(Integer widgetHeight) {
		this.widgetHeight = widgetHeight;
	}

	public String getUuId() {
		return uuId;
	}

	public void setUuId(String uuId) {
		this.uuId = uuId;
	}

	public String getNestLayoutCode() {
		return nestLayoutCode;
	}

	public void setNestLayoutCode(String nestLayoutCode) {
		this.nestLayoutCode = nestLayoutCode;
	}

	public String getPageUuId() {
		return pageUuId;
	}

	public void setPageUuId(String pageUuId) {
		this.pageUuId = pageUuId;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public String getWidgetIds() {
		return widgetIds;
	}

	public void setWidgetIds(String widgetIds) {
		this.widgetIds = widgetIds;
	}
}
