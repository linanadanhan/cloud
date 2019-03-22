package com.gsoft.portal.component.compmgr.service;

import org.json.JSONException;

import com.gsoft.cos3.dto.ReturnDto;

/**
 * 业务组件配置Service接口
 * 
 * @author SN
 *
 */
public interface BusinessCompConfService {
	
	/**
	 * 保存业务组件配置数据
	 * 
	 * @param compId
	 * @param widgetIds
	 */
	void saveBusinessCompConf(Long compId, String widgetIds);

	/**
	 * 根据组件ID获取业务组件配置信息
	 * 
	 * @param compId
	 * @return
	 * @throws JSONException 
	 */
	ReturnDto getBusinessCompConfInfo(Long compId) throws JSONException;

	/**
	 * 复制业务组件配置数据信息
	 * @param compId
	 * @param widgetUuId
	 * @return
	 * @throws JSONException 
	 */
	ReturnDto copyBusinessCompConf(Long compId, String widgetUuId) throws JSONException;
}
