package com.gsoft.portal.webview.widgetconf.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.gsoft.portal.webview.widgetconf.dto.CustomProfileConfDto;

/**
 * 个性化偏好配置Service接口
 * @author SN
 *
 */
public interface CustomProfileConfService {

	/**
	 * 获取实例偏好设置
	 * @param widgetId
	 * @param personnelId
	 * @return
	 */
	CustomProfileConfDto getCustomProfileConfInfo(String widgetId, long personnelId);

	/**
	 * 保存实例偏好设置
	 * @param customProfileConfDto
	 * @return
	 */
	CustomProfileConfDto saveCustomProfileConf(CustomProfileConfDto customProfileConfDto, String delWidgetIds);

	/**
	 * 保存所有个性化偏好设置
	 * @param pageUuId
	 * @param personnelId
	 * @return
	 * @throws JSONException 
	 */
	JSONObject getCusConfByPageUuId(String pageUuId, Long personnelId) throws JSONException;
	
}
