package com.gsoft.portal.api.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.cos3.util.StringUtils;
import com.gsoft.portal.api.service.SyncProfileConfService;
import com.gsoft.portal.webview.widget.dto.WidgetDto;
import com.gsoft.portal.webview.widget.service.WidgetService;
import com.gsoft.portal.webview.widgetconf.dto.CustomProfileConfDto;
import com.gsoft.portal.webview.widgetconf.dto.ProfileConfDto;
import com.gsoft.portal.webview.widgetconf.entity.CustomProfileConfEntity;
import com.gsoft.portal.webview.widgetconf.entity.ProfileConfEntity;
import com.gsoft.portal.webview.widgetconf.persistence.CustomProfileConfPersistence;
import com.gsoft.portal.webview.widgetconf.persistence.ProfileConfPersistence;
import com.gsoft.portal.webview.widgetconf.service.CustomProfileConfService;
import com.gsoft.portal.webview.widgetconf.service.ProfileConfService;

/**
 * 同步widget配置信息
 * 
 * @author chenxx
 *
 */
@Service
public class SyncProfileConfServiceImpl implements SyncProfileConfService {

	/**
	 * 日志对象
	 */
	Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	BaseDao baseDao;
	
	@Resource
	ProfileConfService profileConfService;
	
	@Resource
	WidgetService widgetService;
	
	@Resource
	ProfileConfPersistence profileConfPersistence;
	
	@Resource
	CustomProfileConfService customProfileConfService;
	
	@Resource
	CustomProfileConfPersistence customProfileConfPersistence;

	@Override
	public void syncProfileConf() {
		// 查询所有系统页面信息
		List<Map<String, Object>> pageList = baseDao.query("SELECT c_uu_id FROM cos_portal_page");

		if (!Assert.isEmpty(pageList) && pageList.size() > 0) {
			
			// 组装保存系统widget配置信息的数据集合
			Set<String> sysWidgetIds = null;

			// 组装用户widget配置信息的数据集合
			Set<String> cusWidgetIds = null;
			
			// 组装需要进行初始化系统widget的配置信息的数据集合
			List<ProfileConfEntity> addSysConfList = null;
			
			// 组装需要进行初始化个性化widget的配置信息的数据集合
			List<CustomProfileConfEntity> addCusConfList = null;

			// 查询系统页面widget实例
			List<Map<String, Object>> sysInsList = null;
			// 查询个人页面widget实例
			List<Map<String, Object>> cusInsList = null;
			
			// widgets 数据集合
			JSONArray jsonArr = null;
			
			for (Map<String, Object> map : pageList) {

				// 组装保存系统widget配置信息
				sysWidgetIds = new HashSet<String>();

				// 组装用户widget配置信息
				cusWidgetIds = new HashSet<String>();
				
				// 组装需要进行初始化系统widget的配置信息的数据集合
				addSysConfList = new ArrayList<ProfileConfEntity>();
				
				// 组装需要进行初始化个性化widget的配置信息的数据集合
				addCusConfList = new ArrayList<CustomProfileConfEntity>();

				// 查询系统页面widget实例
				sysInsList = baseDao.query("SELECT c_json FROM cos_widget_instance WHERE c_page_uu_id = ?", map.get("C_UU_ID"));

				if (!Assert.isEmpty(sysInsList) && sysInsList.size() > 0) {
					for (Map<String, Object> insMap : sysInsList) {
						if (!Assert.isEmpty(insMap.get("C_JSON"))) {
							// widgets 数据集合
							try {
								jsonArr = new JSONArray(MathUtils.stringObj(insMap.get("C_JSON")));
								getAllSysWidgetIds(jsonArr, sysWidgetIds, addSysConfList, MathUtils.stringObj(map.get("C_UU_ID")));

							} catch (Exception e) {
								logger.error("页面ID：" + map.get("C_UU_ID") + "|json is " + insMap.get("C_JSON") + " 错误！");
							}
						}
					}
				}

				// 查询个人页面widget实例
				cusInsList = baseDao.query("SELECT c_json, C_USER_ID FROM cos_custom_widget_instance WHERE c_page_uu_id = ?", map.get("C_UU_ID"));
				if (!Assert.isEmpty(cusInsList) && cusInsList.size() > 0) {
					for (Map<String, Object> insMap : cusInsList) {
						if (!Assert.isEmpty(insMap.get("C_JSON")) && !Assert.isEmpty(insMap.get("C_USER_ID"))) {
							// widgets 数据集合
							try {
								cusWidgetIds = new HashSet<String>();
								addCusConfList = new ArrayList<CustomProfileConfEntity>();
								
								jsonArr = new JSONArray(MathUtils.stringObj(insMap.get("C_JSON")));
								getAllCusWidgetIds(jsonArr, cusWidgetIds, addCusConfList, MathUtils.stringObj(map.get("C_UU_ID")), MathUtils.numObj2Long(insMap.get("C_USER_ID")));
								
								// 更新个人偏好配置
								if (!Assert.isEmpty(cusWidgetIds) && cusWidgetIds.size() > 0) {
									baseDao.update("UPDATE cos_custom_profile SET c_page_uu_id = '"+map.get("C_UU_ID")+"' WHERE c_widget_uu_id in ("+"'"+StringUtils.join(cusWidgetIds, ",").replace(",","','")+"'"+")");
								}
								
								// 新增widget个性化默认配置
								if (!Assert.isEmpty(addCusConfList) && addCusConfList.size() > 0) {
									customProfileConfPersistence.save(addCusConfList);
								}
								
							} catch (Exception e) {
								logger.error("页面ID：" + map.get("C_UU_ID") + "|json is " + insMap.get("C_JSON") + " 错误！");
							}
						}
					}
				}
				
				// 更新系统配置
				if (!Assert.isEmpty(sysWidgetIds) && sysWidgetIds.size() > 0) {
					baseDao.update("UPDATE cos_sys_profile SET c_page_uu_id = '"+map.get("C_UU_ID")+"' WHERE c_widget_uu_id in ("+"'"+StringUtils.join(sysWidgetIds, ",").replace(",","','")+"'"+")");
				}
				
				// 新增widget系统默认配置
				if (!Assert.isEmpty(addSysConfList) && addSysConfList.size() > 0) {
					profileConfPersistence.save(addSysConfList);
				}
			}
		}
	}
	
	/**
	 * 获取所有个性化widget实例ID
	 * @param jsonArr
	 * @param sysWidgetIds
	 * @throws JSONException 
	 */
	private void getAllCusWidgetIds(JSONArray jsonArr, Set<String> widgetIds, List<CustomProfileConfEntity> addCusConfList, String pageUuId, long userId) throws JSONException {
		if (!Assert.isEmpty(jsonArr) && jsonArr.length() > 0) {
			
			CustomProfileConfEntity tmpCusEntity = null;
			WidgetDto widgetDto = null;
			
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONArray jo = jsonArr.getJSONArray(i);
				if (!Assert.isEmpty(jo) && jo.length() > 0) {
					for (int j = 0; j < jo.length(); j++) {
						JSONObject jobj = jo.getJSONObject(j);
						JSONObject paramJo = new JSONObject();
						
						Map<String, Object> pMap = baseDao.load("SELECT c_json FROM cos_custom_profile WHERE c_widget_uu_id = ? and c_json LIKE '%\"widgets\"%' and c_page_uu_id is null and c_user_id = ? ", MathUtils.stringObj(jobj.get("id")), userId);
						if (!Assert.isEmpty(pMap)) {
							String json = MathUtils.stringObj(pMap.get("C_JSON"));
							paramJo = new JSONObject(json);
							handleCusSpeWidget(widgetIds, jobj, paramJo, addCusConfList, pageUuId, userId);
						}
						
						// 查询widget实例ID是否存在配置，若不存在新增默认配置
						CustomProfileConfDto dto = customProfileConfService.getCustomProfileConfInfo(MathUtils.stringObj(jobj.get("id")), userId);
						if (Assert.isEmpty(dto.getId())) {
							tmpCusEntity = new CustomProfileConfEntity();
							tmpCusEntity.setWidgetUuId(MathUtils.stringObj(jobj.get("id")));
							widgetDto = widgetService.getWidgetInfoByCode(MathUtils.stringObj(jobj.get("name")));
							tmpCusEntity.setJson(widgetDto.getParams());
							tmpCusEntity.setUserId(userId);
							tmpCusEntity.setPageUuId(pageUuId);
							// 校验集合中是否已存在
							boolean isExist = checkCusConfList(tmpCusEntity.getWidgetUuId(), userId, addCusConfList);
							if (!isExist) {
								addCusConfList.add(tmpCusEntity);
							}
						}
						widgetIds.add(MathUtils.stringObj(jobj.get("id")));
					}
				}
			}
		}
	}

	/**
	 * 获取所有widget实例ID
	 * @param jsonArr
	 * @param sysWidgetIds
	 * @throws JSONException 
	 */
	private void getAllSysWidgetIds(JSONArray jsonArr, Set<String> widgetIds, List<ProfileConfEntity> addSysConfList, String pageUuId) throws JSONException {
		if (!Assert.isEmpty(jsonArr) && jsonArr.length() > 0) {
			
			ProfileConfEntity tmpSysEntity = null;
			WidgetDto widgetDto = null;
			
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONArray jo = jsonArr.getJSONArray(i);
				if (!Assert.isEmpty(jo) && jo.length() > 0) {
					for (int j = 0; j < jo.length(); j++) {
						JSONObject jobj = jo.getJSONObject(j);
						JSONObject paramJo = new JSONObject();
						
						Map<String, Object> sMap = baseDao.load("SELECT c_json FROM cos_sys_profile WHERE c_widget_uu_id = ? and c_json LIKE '%\"widgets\"%' and c_page_uu_id is null ", MathUtils.stringObj(jobj.get("id")));
						if (!Assert.isEmpty(sMap)) {
							String json = MathUtils.stringObj(sMap.get("C_JSON"));
							paramJo = new JSONObject(json);
							handleSysSpeWidget(widgetIds, jobj, paramJo, addSysConfList, pageUuId);
						}
						
						// 查询widget实例ID是否存在配置，若不存在新增默认配置
						ProfileConfDto dto = profileConfService.getProfileConfInfo(MathUtils.stringObj(jobj.get("id")));
						if (Assert.isEmpty(dto.getId())) {
							tmpSysEntity = new ProfileConfEntity();
							tmpSysEntity.setWidgetUuId(MathUtils.stringObj(jobj.get("id")));
							widgetDto = widgetService.getWidgetInfoByCode(MathUtils.stringObj(jobj.get("name")));
							tmpSysEntity.setJson(widgetDto.getParams());
							tmpSysEntity.setPageUuId(pageUuId);
							// 校验集合中是否已存在
							boolean isExist = checkSysConfList(tmpSysEntity.getWidgetUuId(), addSysConfList);
							if (!isExist) {
								addSysConfList.add(tmpSysEntity);
							}
						}
						widgetIds.add(MathUtils.stringObj(jobj.get("id")));
					}
				}
			}
		}
	}

	/**
	 * 校验是否存在个性化配置
	 * @param widgetUuId
	 * @param userId
	 * @param addCusConfList
	 * @return
	 */
	private boolean checkCusConfList(String widgetUuId, Long userId, List<CustomProfileConfEntity> addCusConfList) {
		boolean flag = false;
		
		for (CustomProfileConfEntity entity : addCusConfList) {
			if (widgetUuId.equals(entity.getWidgetUuId()) && userId.equals(entity.getUserId())) {
				flag = true;
			}
		}
		
		if (flag) {
			return true;
		}
		return false;
	}

	/**
	 * 校验是否存在系统配置widgetId
	 * @param widgetUuId
	 * @param addSysConfList
	 * @return
	 */
	private boolean checkSysConfList(String widgetUuId, List<ProfileConfEntity> addSysConfList) {
		boolean flag = false;
		for (ProfileConfEntity entity : addSysConfList) {
			if (widgetUuId.equals(entity.getWidgetUuId())) {
				flag = true;
				break;
			}
		}
		if (flag) {
			return true;
		}
		return false;
	}

	/**
	 * 解析嵌套widget
	 * @param sysWidgetIds
	 * @param isDefault
	 * @param jobj
	 * @param paramJo
	 * @throws JSONException
	 */
	private void handleSysSpeWidget(Set<String> widgetIds, JSONObject jobj, JSONObject paramJo, List<ProfileConfEntity> addSysConfList, String pageUuId)
			throws JSONException {
		if ("nested".equals(jobj.get("name"))) {// 嵌套面板
			if (paramJo.has("widgets")) {
				JSONArray nestJsonArr = paramJo.getJSONArray("widgets");
				getAllSysWidgetIds(nestJsonArr, widgetIds, addSysConfList, pageUuId);
			}
		} else if ("tab".equals(jobj.get("name"))) {// tab面板

			JSONArray tabArr = new JSONArray();
			// 循环tabs
			if (paramJo.has("tabs")) {
				tabArr = paramJo.getJSONArray("tabs");

				if (tabArr.length() > 0) {
					for (int m = 0; m < tabArr.length(); m++) {
						JSONObject tabJo = tabArr.getJSONObject(m);
						if (tabJo.has("widgets")) {
							JSONArray nestJsonArr = tabJo.getJSONArray("widgets");
							getAllSysWidgetIds(nestJsonArr, widgetIds, addSysConfList, pageUuId);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 解析嵌套widget
	 * @param sysWidgetIds
	 * @param isDefault
	 * @param jobj
	 * @param paramJo
	 * @throws JSONException
	 */
	private void handleCusSpeWidget(Set<String> widgetIds, JSONObject jobj, JSONObject paramJo, List<CustomProfileConfEntity> addCusConfList, String pageUuId, long userId)
			throws JSONException {
		if ("nested".equals(jobj.get("name"))) {// 嵌套面板
			if (paramJo.has("widgets")) {
				JSONArray nestJsonArr = paramJo.getJSONArray("widgets");
				getAllCusWidgetIds(nestJsonArr, widgetIds, addCusConfList, pageUuId, userId);
			}
		} else if ("tab".equals(jobj.get("name"))) {// tab面板

			JSONArray tabArr = new JSONArray();
			// 循环tabs
			if (paramJo.has("tabs")) {
				tabArr = paramJo.getJSONArray("tabs");

				if (tabArr.length() > 0) {
					for (int m = 0; m < tabArr.length(); m++) {
						JSONObject tabJo = tabArr.getJSONObject(m);
						if (tabJo.has("widgets")) {
							JSONArray nestJsonArr = tabJo.getJSONArray("widgets");
							getAllCusWidgetIds(nestJsonArr, widgetIds, addCusConfList, pageUuId, userId);
						}
					}
				}
			}
		}
	}
}
