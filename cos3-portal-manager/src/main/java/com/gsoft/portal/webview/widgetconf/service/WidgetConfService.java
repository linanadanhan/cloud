package com.gsoft.portal.webview.widgetconf.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gsoft.portal.webview.widgetconf.dto.WidgetConfDto;

/**
 * widget配置管理Service接口
 * @author SN
 *
 */
public interface WidgetConfService {

	/**
	 * 查询所有页面widget配置信息
	 * @param search
	 * @param pageUuId
	 * @param layoutCode
	 * @param position
	 * @return
	 */
	List<Map<String, Object>> queryWidgetConfig(String search, String pageUuId, String layoutCode, String position, String ywType, String nestUuId);

	/**
	 * 判断页面布局特定位置下widget是否已存在
	 * @param id
	 * @param code
	 * @param pageId
	 * @param layoutCode
	 * @param position
	 * @return
	 */
	Boolean isExitWidgetCode(Long id, String code, Long pageId, String layoutCode, String position);

	/**
	 * 保存页面布局widget配置信息
	 * @param widgetConfDto
	 * @return
	 */
	WidgetConfDto saveWidgetConf(WidgetConfDto widgetConfDto, List<String> widgetIds);

	/**
	 * 删除页面widge配置信息
	 * @param id
	 */
	void delWidgetConf(Long id);
	
	/**
	 * 切换布局后移动widget实例
	 * @param pageId
	 * @param oLayoutCode
	 * @param nLayoutCode
	 * @throws IOException 
	 * @throws JSONException 
	 */
	void moveWidgetInstance(Long pageId, String nestWidgetCode, String oLayoutCode, String nLayoutCode) throws IOException, JSONException;

	/**
	 * 获取页面布局widget配置信息
	 * @param pageUuId
	 * @param layoutCode
	 * @param nPosition
	 * @return
	 */
	List<Map<String, Object>> getWidgetList(String pageUuId, String layoutCode, String nPosition);

	/**
	 * @param nestUuId
	 * @param layoutCode
	 * @param nPosition
	 * @return
	 */
	List<Map<String, Object>> getNestedWidgetList(String nestUuId, String layoutCode, String nPosition);

	/**
	 * 获取页面widget实例json数据
	 * @param pageUuId
	 * @return
	 */
	String getWidgetJson(String pageUuId);
	
	/**
	 * 根据页面ID获取widget实例信息
	 * @param pageUuId
	 * @return
	 */
	WidgetConfDto getWidgetConfInfo(String pageUuId);

	/**
	 * 修改widget实例配置
	 * @param pageUuId
	 * @param oLayoutCode
	 * @param layoutCode
	 * @throws IOException 
	 * @throws JSONException 
	 */
	void changeWidgetInstance(String pageUuId, String oLayoutCode, String layoutCode) throws IOException, JSONException;

	/**
	 * 获取widget实例参数
	 * @param jsonArr
	 * @return
	 * @throws JSONException 
	 */
	void getWidgetInstanceParams(JSONArray jsonArr, JSONObject jsonObj, boolean isDefault, HttpServletRequest request) throws JSONException;

	/**
	 * 获取业务widget实例参数
	 * @param jsonArr
	 * @param widgetParamJsonObj
	 * @throws JSONException 
	 */
	void getBusinessWidgetInstanceParams(JSONArray jsonArr, JSONObject widgetParamJsonObj) throws JSONException;

	/**
	 * 复制业务组件配置实例信息
	 * @param jsonArr
	 * @param newJsonArr
	 * @param newWidgetParamJsonObj
	 * @throws JSONException 
	 */
	void copyBusinessCompConf(JSONArray jsonArr, JSONArray newJsonArr, JSONObject relWidgetObj, JSONObject newWidgetParamJsonObj, boolean levelOne) throws JSONException;

	/**
	 * 获取页面widget实例配置信息
	 * @param pageUuId
	 * @param widgetParamJsonObj
	 * @param isDefault
	 * @param personnelId
	 * @throws JSONException 
	 */
	void getWidgetInstanceParams(String pageUuId, JSONObject widgetParamJsonObj, boolean isDefault, Long personnelId) throws JSONException;

}
