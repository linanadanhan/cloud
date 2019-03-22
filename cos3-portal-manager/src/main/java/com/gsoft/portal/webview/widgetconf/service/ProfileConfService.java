package com.gsoft.portal.webview.widgetconf.service;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.gsoft.portal.webview.widgetconf.dto.ProfileConfDto;

/**
 * 系统偏好配置Service接口
 * @author SN
 *
 */
public interface ProfileConfService {

	/**
	 * 获取实例偏好设置
	 * @param widgetId
	 * @return
	 */
	ProfileConfDto getProfileConfInfo(String widgetId);

	/**
	 * 保存实例偏好设置
	 * @param profileConfDto
	 * @return
	 * @throws JSONException 
	 */
	ProfileConfDto saveProfileConf(ProfileConfDto profileConfDto, String delWidgetIds, String pageUuId) throws Exception;

	/**
	 * 查询引用widget模版的实例信息
	 * @param widgetUuId
	 * @return
	 */
	List<ProfileConfDto> getRelInstanceList(String widgetUuId);

	/**
	 * 同步业务组件实例配置
	 * @param syncList
	 */
	void handleBusinessCompInstanceConf(List<Map<String, Object>> syncList);

	/**
	 * 根据页面ID获取当前页面所有widget的配置信息
	 * @param pageUuId
	 * @return
	 * @throws JSONException 
	 */
	JSONObject getSysConfListByPageUuId(String pageUuId) throws JSONException;

	/**
	 * 批量保存
	 * @param widgetConfList
	 */
	void batchSave(List<ProfileConfDto> widgetConfList);
	
}
