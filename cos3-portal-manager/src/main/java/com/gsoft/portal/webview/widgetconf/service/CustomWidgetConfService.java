package com.gsoft.portal.webview.widgetconf.service;

import java.util.List;

import com.gsoft.portal.webview.widgetconf.dto.CustomWidgetConfDto;

/**
 * 用户自定义widget配置管理Service接口
 * @author SN
 *
 */
public interface CustomWidgetConfService {

	/**
	 * 保存用户自定义widget实例信息
	 * @param customWidgetConfDto
	 * @return
	 */
	CustomWidgetConfDto saveWidgetInstance(CustomWidgetConfDto customWidgetConfDto, List<String> widgetIds);

	/**
	 * 获取单笔用户自定义widget实例配置信息
	 * @param personnelId
	 * @param pageUuId
	 * @return
	 */
	CustomWidgetConfDto getCustomWidgetConfInfo(Long personnelId, String pageUuId);

}
