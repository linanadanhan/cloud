package com.gsoft.portal.api.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;

@ApiModel("偏好设置DTO")
public class ProfileConfigDto extends BaseDto{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2263852743830827733L;
	
	private String json;
  
	private String widgetUuId;
  
	private boolean diyMode = false;
  
	private String delWidgetIds;
  
	private String pageUuId;
  
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	public String getWidgetUuId() {
		return widgetUuId;
	}
	public void setWidgetUuId(String widgetUuId) {
		this.widgetUuId = widgetUuId;
	}
	public boolean isDiyMode() {
		return diyMode;
	}
	public void setDiyMode(boolean diyMode) {
	  this.diyMode = diyMode;
	}
	public String getDelWidgetIds() {
	  return delWidgetIds;
	}
	public void setDelWidgetIds(String delWidgetIds) {
		this.delWidgetIds = delWidgetIds;
	}
	public String getPageUuId() {
		return pageUuId;
	}
	public void setPageUuId(String pageUuId) {
		this.pageUuId = pageUuId;
	}

}
