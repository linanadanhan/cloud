package com.gsoft.portal.webview.widgetconf.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.common.constans.ParameterConstant;
import com.gsoft.portal.component.layout.dto.LayoutDto;
import com.gsoft.portal.component.layout.service.LayoutService;
import com.gsoft.portal.system.basicdata.service.ParameterService;
import com.gsoft.portal.webview.page.service.SitePageConfService;
import com.gsoft.portal.webview.widget.dto.WidgetDto;
import com.gsoft.portal.webview.widget.service.WidgetService;
import com.gsoft.portal.webview.widgetconf.dto.CustomProfileConfDto;
import com.gsoft.portal.webview.widgetconf.dto.ProfileConfDto;
import com.gsoft.portal.webview.widgetconf.dto.WidgetConfDto;
import com.gsoft.portal.webview.widgetconf.entity.WidgetConfEntity;
import com.gsoft.portal.webview.widgetconf.persistence.WidgetConfPersistence;
import com.gsoft.portal.webview.widgetconf.service.CustomProfileConfService;
import com.gsoft.portal.webview.widgetconf.service.ProfileConfService;
import com.gsoft.portal.webview.widgetconf.service.WidgetConfService;

/**
 * widget管理Service实现类
 * 
 * @author SN
 *
 */
@Service
public class WidgetConfServiceImpl implements WidgetConfService {

	@Resource
	WidgetConfPersistence widgetConfPersistence;

	@Resource
	BaseDao baseDao;

	@Resource
	ParameterService parameterService;

	@Resource
	LayoutService layoutService;

	@Resource
	ProfileConfService profileConfService;

	@Resource
	CustomProfileConfService customProfileConfService;

	@Resource
	SitePageConfService sitePageConfService;

	@Resource
	WidgetService widgetService;

	@Override
	public List<Map<String, Object>> queryWidgetConfig(String search, String pageUuId, String layoutCode,
			String position, String ywType, String nestUuId) {

		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();

		if ("1".equals(ywType) && Assert.isNotEmpty(nestUuId)) {// 嵌套widget

			sb.append(
					"SELECT i.*,l.c_name as c_layout_name,w.c_name as c_widget_name,d.c_name as c_decorate_name,w.c_is_nested FROM cos_widget_instance i ");
			sb.append("LEFT JOIN cos_portal_layout l ON i.c_layout_code = l.c_code ");
			sb.append("LEFT JOIN cos_portal_decorate d on i.c_decorate_code = d.c_code ");
			sb.append("LEFT JOIN cos_portal_widget w ON i.c_widget_code = w.c_code ");
			sb.append(
					"WHERE i.c_uu_id = ${nestUuId} AND i.c_layout_code = ${layoutCode} AND i.c_layout_position = ${position} ");
			sb.append("AND i.c_type = '1' ");
			sb.append(" order by i.c_sort_no asc ");

			params.put("nestUuId", nestUuId);
			params.put("layoutCode", layoutCode);
			params.put("position", position);

		} else {

			sb.append(
					"SELECT i.*,l.c_name as c_layout_name,w.c_name as c_widget_name,d.c_name as c_decorate_name,w.c_is_nested ,wi.c_nest_layout_code FROM cos_widget_instance i ");
			sb.append(
					"LEFT JOIN cos_portal_page p ON i.c_page_uu_id = p.c_uu_id LEFT JOIN cos_portal_layout l ON i.c_layout_code = l.c_code ");
			sb.append("LEFT JOIN cos_portal_decorate d on i.c_decorate_code = d.c_code ");
			sb.append("LEFT JOIN cos_portal_widget w ON i.c_widget_code = w.c_code ");
			sb.append("LEFT JOIN cos_nested_widget_instance wi ON i.c_uu_id = wi.c_widget_uu_id ");
			sb.append(
					"WHERE i.c_page_uu_id = ${pageUuId} AND i.c_layout_code = ${layoutCode} AND i.c_layout_position = ${position} AND i.c_type = '0' ");
			sb.append(" order by i.c_sort_no asc ");
			params.put("pageUuId", pageUuId);
			params.put("layoutCode", layoutCode);
			params.put("position", position);
		}

		return baseDao.query(sb.toString(), params);
	}

	@Override
	public Boolean isExitWidgetCode(Long id, String code, Long pageId, String layoutCode, String position) {
		return null;
	}

	@Override
	@Transactional
	public WidgetConfDto saveWidgetConf(WidgetConfDto widgetConfDto, List<String> widgetIds) {

		if (Assert.isEmpty(widgetConfDto.getId()) && Assert.isEmpty(widgetConfDto.getUuId())) {
			widgetConfDto.setUuId(UUID.randomUUID().toString().replace("-", ""));
		}

		WidgetConfEntity entity = BeanUtils.convert(widgetConfDto, WidgetConfEntity.class);
		WidgetConfEntity reEntity = widgetConfPersistence.save(entity);

		if (widgetIds.size() > 0) {
			for (String widgetId : widgetIds) {
				baseDao.update("DELETE FROM cos_sys_profile WHERE c_widget_uu_id = ? ", widgetId);
				baseDao.update("DELETE FROM cos_portal_page_badget WHERE c_widget_uu_id = ? and c_page_uu_id = ? ",
						widgetId, widgetConfDto.getPageUuId());
			}
		}

		return BeanUtils.convert(reEntity, WidgetConfDto.class);
	}

	@Override
	public void delWidgetConf(Long id) {
		baseDao.delete("cos_widget_instance", "c_id", id);
	}

	@Override
	public void moveWidgetInstance(Long pageId, String nestUuId, String oLayoutCode, String nLayoutCode)
			throws IOException, JSONException {

		// 1.查询原布局是否有配置widget实例
		String destDir = parameterService.getParmValueByKey(ParameterConstant.PORTAL_MODULES_PATH[0],
				ParameterConstant.PORTAL_MODULES_PATH[1]);

		if (!destDir.endsWith("/")) {
			destDir = destDir + "/";
		}

		LayoutDto oDto = layoutService.getLayoutInfoByCode(oLayoutCode);
		LayoutDto nDto = layoutService.getLayoutInfoByCode(nLayoutCode);

		File nFile = new File(destDir + nDto.getProjectCode() + "/layouts/" + nLayoutCode + "/mainfest.json");// 读取新布局json文件
		File oFile = new File(destDir + oDto.getProjectCode() + "/layouts/" + oLayoutCode + "/mainfest.json");// 读取原布局json文件

		try {

			// 新布局json读取
			String nContent = FileUtils.readFileToString(nFile, "UTF-8");
			JSONObject nJsonObject = new JSONObject(nContent);
			JSONArray nJsonArray = nJsonObject.getJSONArray("params");

			// 原布局json读取
			String oContent = FileUtils.readFileToString(oFile, "UTF-8");
			JSONObject oJsonObject = new JSONObject(oContent);
			JSONArray oJsonArray = oJsonObject.getJSONArray("params");

			if (oJsonArray.length() > nJsonArray.length()) {

				// 新布局第一个位置
				JSONObject json = nJsonArray.getJSONObject(0);
				String fPosition = json.getString("position");

				for (int i = 0; i < oJsonArray.length(); i++) {

					boolean isExist = false;
					JSONObject oJson = oJsonArray.getJSONObject(i);
					String oPosition = oJson.getString("position");

					for (int j = 0; j < nJsonArray.length(); j++) {

						JSONObject nJson = nJsonArray.getJSONObject(j);
						String nPosition = nJson.getString("position");

						if (oPosition.equals(nPosition)) {
							isExist = true;
							break;
						}
					}

					if (isExist) {

						if (!Assert.isEmpty(pageId)) {// 页面

							String sql = "UPDATE cos_widget_instance SET c_layout_code = ?,c_layout_position = ? WHERE c_page_id = ? AND c_layout_code = ? AND c_layout_position = ?";
							baseDao.update(sql, nLayoutCode, oPosition, pageId, oLayoutCode, oPosition);

						} else {// 嵌套widget

							String sql = "UPDATE cos_widget_instance SET c_layout_code = ?,c_layout_position = ? WHERE c_uu_id = ? AND c_layout_code = ? AND c_layout_position = ? and c_type = '1'";
							baseDao.update(sql, nLayoutCode, oPosition, nestUuId, oLayoutCode, oPosition);
						}

					} else {

						if (!Assert.isEmpty(pageId)) {// 页面

							String sql = "UPDATE cos_widget_instance SET c_layout_code = ?,c_layout_position = ? WHERE c_page_id = ? AND c_layout_code = ? AND c_layout_position = ?";
							baseDao.update(sql, nLayoutCode, fPosition, pageId, oLayoutCode, oPosition);

						} else {// 嵌套widget

							String sql = "UPDATE cos_widget_instance SET c_layout_code = ?,c_layout_position = ? WHERE c_uu_id = ? AND c_layout_code = ? AND c_layout_position = ? and c_type = '1'";
							baseDao.update(sql, nLayoutCode, fPosition, nestUuId, oLayoutCode, oPosition);
						}
					}
				}

			} else {

				for (int i = 0; i < oJsonArray.length(); i++) {

					boolean isExist = false;

					JSONObject oJson = oJsonArray.getJSONObject(i);
					String oPosition = oJson.getString("position");

					for (int j = 0; j < nJsonArray.length(); j++) {

						JSONObject nJson = nJsonArray.getJSONObject(i);
						String nPosition = nJson.getString("position");

						if (oPosition.equals(nPosition)) {
							isExist = true;
							break;
						}
					}

					if (isExist) {

						if (!Assert.isEmpty(pageId)) {// 页面

							String sql = "UPDATE cos_widget_instance SET c_layout_code = ?,c_layout_position = ? WHERE c_page_id = ? AND c_layout_code = ? AND c_layout_position = ?";
							baseDao.update(sql, nLayoutCode, oPosition, pageId, oLayoutCode, oPosition);

						} else {// 嵌套widget

							String sql = "UPDATE cos_widget_instance SET c_layout_code = ?,c_layout_position = ? WHERE c_uu_id = ? AND c_layout_code = ? AND c_layout_position = ? and c_type = '1'";
							baseDao.update(sql, nLayoutCode, oPosition, nestUuId, oLayoutCode, oPosition);
						}
					}
				}
			}

		} catch (IOException e) {
			throw e;
		} catch (JSONException e) {
			throw e;
		}
	}

	@Override
	public List<Map<String, Object>> getWidgetList(String pageUuId, String layoutCode, String position) {

		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();

		sb.append(
				"SELECT i.*,l.c_name as c_layout_name,w.c_name as c_widget_name,d.c_name as c_decorate_name,d.c_is_system as c_decorate_system, w.c_is_system as c_widget_system FROM cos_widget_instance i ");
		sb.append(
				"LEFT JOIN cos_portal_layout l ON i.c_layout_code = l.c_code LEFT JOIN cos_portal_decorate d on i.c_decorate_code = d.c_code ");
		sb.append("LEFT JOIN cos_portal_widget w ON i.c_widget_code = w.c_code ");
		sb.append(
				"WHERE i.c_page_uu_id = ${pageUuId} AND i.c_layout_code = ${layoutCode} AND i.c_layout_position = ${position} AND i.c_type = '0' ");
		sb.append(" order by i.c_sort_no asc");
		params.put("pageUuId", pageUuId);
		params.put("layoutCode", layoutCode);
		params.put("position", position);

		return baseDao.query(sb.toString(), params);
	}

	@Override
	public List<Map<String, Object>> getNestedWidgetList(String nestUuId, String layoutCode, String nPosition) {

		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();

		sb.append(
				"SELECT i.*,l.c_name as c_layout_name,w.c_name as c_widget_name,d.c_name as c_decorate_name,d.c_is_system as c_decorate_system, w.c_is_system as c_widget_system FROM cos_widget_instance i  ");
		sb.append(
				"LEFT JOIN cos_portal_layout l ON i.c_layout_code = l.c_code LEFT JOIN cos_portal_decorate d on i.c_decorate_code = d.c_code ");
		sb.append("LEFT JOIN cos_portal_widget w ON i.c_widget_code = w.c_code ");
		sb.append(
				"WHERE i.c_uu_id = ${nestUuId} AND i.c_layout_code = ${layoutCode} AND i.c_layout_position = ${nPosition} and i.c_type = '1' ");

		params.put("nestUuId", nestUuId);
		params.put("layoutCode", layoutCode);
		params.put("nPosition", nPosition);

		return baseDao.query(sb.toString(), params);
	}

	@Override
	public String getWidgetJson(String pageUuId) {
		WidgetConfEntity entity = widgetConfPersistence.getWidgetJson(pageUuId);
		if (Assert.isNotEmpty(entity)) {
			return entity.getJson();
		}
		return null;
	}

	@Override
	public WidgetConfDto getWidgetConfInfo(String pageUuId) {
		WidgetConfEntity entity = widgetConfPersistence.getWidgetJson(pageUuId);
		return BeanUtils.convert(entity, WidgetConfDto.class);
	}

	@Override
	public void changeWidgetInstance(String pageUuId, String oLayoutCode, String nLayoutCode)
			throws IOException, JSONException {

		JSONArray nJsonArray = new JSONArray();
		JSONArray tmpArr = null;

		for (int i = 0; i < nLayoutCode.split("-").length; i++) {
			tmpArr = new JSONArray();
			nJsonArray.put(tmpArr);
		}

		JSONArray oJsonArray = new JSONArray();
		JSONArray resJsonArr = new JSONArray();

		// 查询原布局实例json数据
		String json = this.getWidgetJson(pageUuId);
		if (!Assert.isEmpty(json) && !"[]".equals(json)) {
			oJsonArray = new JSONArray(json);
		}

		if (oJsonArray.length() > nJsonArray.length()) {
			for (int i = 0; i < oJsonArray.length(); i++) {
				JSONArray layoutJsonArr = oJsonArray.getJSONArray(i);
				if (i < nJsonArray.length()) {
					resJsonArr.put(layoutJsonArr);
				} else {
					// 多余的实例放入第一个布局块中
					JSONArray fJsonArr = resJsonArr.getJSONArray(0);
					for (int m = 0; m < layoutJsonArr.length(); m++) {
						JSONObject tmpJsonObj = layoutJsonArr.getJSONObject(m);
						fJsonArr.put(tmpJsonObj);
					}
				}
			}
		} else {
			for (int i = 0; i < nJsonArray.length(); i++) {
				tmpArr = new JSONArray();
				if (i <= oJsonArray.length() - 1) {
					tmpArr = oJsonArray.getJSONArray(i);
				}
				resJsonArr.put(tmpArr);
			}
		}

		// 更新实例json数据，同时更新页面布局信息
		String sql = "UPDATE cos_portal_page SET c_layout_code = ? WHERE c_uu_id = ?";
		baseDao.update(sql, nLayoutCode, pageUuId);

		sql = "UPDATE cos_widget_instance SET c_json = ? WHERE c_page_uu_id = ? ";
		baseDao.update(sql, resJsonArr.toString(), pageUuId);
	}

	@Override
	public void getWidgetInstanceParams(JSONArray jsonArr, JSONObject jsonObj, boolean isDefault,
			HttpServletRequest request) throws JSONException {
		if (!Assert.isEmpty(jsonArr) && jsonArr.length() > 0) {
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONArray jo = jsonArr.getJSONArray(i);
				if (!Assert.isEmpty(jo) && jo.length() > 0) {
					for (int j = 0; j < jo.length(); j++) {
						JSONObject jobj = jo.getJSONObject(j);

						JSONObject resJo = new JSONObject();
						JSONObject paramJo = new JSONObject();
						String personnelId = request.getHeader("personnelId");

						// 偏好设置
						CustomProfileConfDto customProfileConfDto = null;
						if (!Assert.isEmpty(personnelId) && !isDefault) {
							customProfileConfDto = customProfileConfService.getCustomProfileConfInfo(
									MathUtils.stringObj(jobj.get("id")), MathUtils.numObj2Long(personnelId));
						}

						if (!Assert.isEmpty(customProfileConfDto) && !Assert.isEmpty(customProfileConfDto.getId())) {
							String json = customProfileConfDto.getJson();
							paramJo = new JSONObject(json);

							// 系统配置
							ProfileConfDto profileConfDto = profileConfService
									.getProfileConfInfo(MathUtils.stringObj(jobj.get("id")));
							if (!Assert.isEmpty(profileConfDto) && !Assert.isEmpty(profileConfDto.getId())) {
								String sJson = profileConfDto.getJson();
								JSONObject sJo = new JSONObject(sJson);

								@SuppressWarnings("rawtypes")
								Iterator iterator = sJo.keys();
								while (iterator.hasNext()) {
									String key = (String) iterator.next();
									Object value = sJo.get(key);
									if (!paramJo.has(key) && !"widgets".equals(key)) {
										paramJo.put(key, value);
									}
								}
							}

						} else {
							ProfileConfDto profileConfDto = profileConfService
									.getProfileConfInfo(MathUtils.stringObj(jobj.get("id")));
							if (!Assert.isEmpty(profileConfDto) && !Assert.isEmpty(profileConfDto.getId())) {
								String json = profileConfDto.getJson();
								paramJo = new JSONObject(json);

								@SuppressWarnings("rawtypes")
								Iterator iterator = paramJo.keys();
								while (iterator.hasNext()) {
									String key = (String) iterator.next();
									Object value = paramJo.get(key);
									if (!paramJo.has(key) && !"widgets".equals(key)) {
										paramJo.put(key, value);
									}
								}
							}
						}

						// 根据widget名称获取widget信息
						WidgetDto widgetDto = widgetService.getWidgetInfoByCode(MathUtils.stringObj(jobj.get("name")));

						if ("nested".equals(jobj.get("name"))) {// 嵌套面板
							@SuppressWarnings("rawtypes")
							Iterator iterator = paramJo.keys();
							while (iterator.hasNext()) {
								String key = (String) iterator.next();
								if (!"widgets".equals(key)) {
									Object valObj = paramJo.get(key);
									resJo.put(key, valObj);
								}
							}

							resJo.put("nestedPage", paramJo);
							if (paramJo.has("widgets")) {
								JSONArray nestJsonArr = paramJo.getJSONArray("widgets");
								getWidgetInstanceParams(nestJsonArr, jsonObj, isDefault, request);
							}
						} else if ("tab".equals(jobj.get("name"))) {// tab面板
							if (!Assert.isEmpty(widgetDto.getParams())) {
								resJo = new JSONObject(widgetDto.getParams());
							}
							@SuppressWarnings("rawtypes")
							Iterator iterator = paramJo.keys();
							while (iterator.hasNext()) {
								String key = (String) iterator.next();
								if (!"widgets".equals(key) && !"tabs".equals(key)) {
									Object valObj = paramJo.get(key);
									resJo.put(key, valObj);
								}
							}

							JSONArray tabArr = new JSONArray();

							// 循环tabs
							if (paramJo.has("tabs")) {
								tabArr = paramJo.getJSONArray("tabs");
								resJo.put("tabs", tabArr);

								if (tabArr.length() > 0) {
									for (int m = 0; m < tabArr.length(); m++) {
										JSONObject tabJo = tabArr.getJSONObject(m);
										if (tabJo.has("widgets")) {
											JSONArray nestJsonArr = tabJo.getJSONArray("widgets");
											getWidgetInstanceParams(nestJsonArr, jsonObj, isDefault, request);
										}
									}
								}

							} else {
								resJo.put("tabs", tabArr);
							}

						} else {// 非嵌套
							@SuppressWarnings("rawtypes")
							Iterator iterator = paramJo.keys();
							while (iterator.hasNext()) {
								String key = (String) iterator.next();
								Object valObj = paramJo.get(key);
								resJo.put(key, valObj);
							}
						}
						// 为空时给默认widget参数值
						if (Assert.isEmpty(resJo) || resJo.length() == 0) {
							if (!Assert.isEmpty(widgetDto.getParams())) {
								resJo = new JSONObject(widgetDto.getParams());
							}
						}

						jsonObj.put(MathUtils.stringObj(jobj.get("id")), resJo);
					}
				}
			}
		}
	}

	@Override
	public void getBusinessWidgetInstanceParams(JSONArray jsonArr, JSONObject jsonObj) throws JSONException {
		if (!Assert.isEmpty(jsonArr) && jsonArr.length() > 0) {
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONArray jo = jsonArr.getJSONArray(i);
				if (!Assert.isEmpty(jo) && jo.length() > 0) {
					for (int j = 0; j < jo.length(); j++) {
						JSONObject jobj = jo.getJSONObject(j);

						JSONObject resJo = new JSONObject();
						JSONObject paramJo = new JSONObject();

						ProfileConfDto profileConfDto = profileConfService
								.getProfileConfInfo(MathUtils.stringObj(jobj.get("id")));
						if (!Assert.isEmpty(profileConfDto) && !Assert.isEmpty(profileConfDto.getId())) {
							String json = profileConfDto.getJson();
							paramJo = new JSONObject(json);
						}

						// 根据widget名称获取widget信息
						WidgetDto widgetDto = widgetService.getWidgetInfoByCode(MathUtils.stringObj(jobj.get("name")));

						if ("nested".equals(jobj.get("name"))) {// 嵌套
							@SuppressWarnings("rawtypes")
							Iterator iterator = paramJo.keys();
							while (iterator.hasNext()) {
								String key = (String) iterator.next();
								if (!"widgets".equals(key)) {
									Object valObj = paramJo.get(key);
									resJo.put(key, valObj);
								}
							}

							resJo.put("nestedPage", paramJo);
							if (paramJo.has("widgets")) {
								JSONArray nestJsonArr = paramJo.getJSONArray("widgets");
								getBusinessWidgetInstanceParams(nestJsonArr, jsonObj);
							}
						} else if ("tab".equals(jobj.get("name"))) {// tab面板
							if (!Assert.isEmpty(widgetDto.getParams())) {
								resJo = new JSONObject(widgetDto.getParams());
							}
							@SuppressWarnings("rawtypes")
							Iterator iterator = paramJo.keys();
							while (iterator.hasNext()) {
								String key = (String) iterator.next();
								if (!"widgets".equals(key) && !"tabs".equals(key)) {
									Object valObj = paramJo.get(key);
									resJo.put(key, valObj);
								}
							}

							JSONArray tabArr = new JSONArray();

							// 循环tabs
							if (paramJo.has("tabs")) {
								tabArr = paramJo.getJSONArray("tabs");
								resJo.put("tabs", tabArr);

								if (tabArr.length() > 0) {
									for (int m = 0; m < tabArr.length(); m++) {
										JSONObject tabJo = tabArr.getJSONObject(m);
										if (tabJo.has("widgets")) {
											JSONArray nestJsonArr = tabJo.getJSONArray("widgets");
											getBusinessWidgetInstanceParams(nestJsonArr, jsonObj);
										}
									}
								}
							} else {
								resJo.put("tabs", tabArr);
							}
						} else {// 非嵌套
							@SuppressWarnings("rawtypes")
							Iterator iterator = paramJo.keys();
							while (iterator.hasNext()) {
								String key = (String) iterator.next();
								Object valObj = paramJo.get(key);
								resJo.put(key, valObj);
							}
						}
						// 为空时给默认修饰器属性
						if (Assert.isEmpty(resJo) || resJo.length() == 0) {
							if (!Assert.isEmpty(widgetDto.getParams())) {
								resJo = new JSONObject(widgetDto.getParams());
							}
						}
						jsonObj.put(MathUtils.stringObj(jobj.get("id")), resJo);
					}
				}
			}
		}
	}

	@Override
	public void copyBusinessCompConf(JSONArray jsonArr, JSONArray newJsonArr, JSONObject relWidgetObj,
			JSONObject newWidgetParamJsonObj, boolean levelOne) throws JSONException {
		if (!Assert.isEmpty(jsonArr) && jsonArr.length() > 0) {
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONArray jo = jsonArr.getJSONArray(i);
				JSONArray newJo = new JSONArray();
				if (!Assert.isEmpty(jo) && jo.length() > 0) {
					for (int j = 0; j < jo.length(); j++) {
						JSONObject jobj = jo.getJSONObject(j);

						JSONObject resJo = new JSONObject();
						JSONObject paramJo = new JSONObject();

						ProfileConfDto profileConfDto = profileConfService
								.getProfileConfInfo(MathUtils.stringObj(jobj.get("id")));
						if (!Assert.isEmpty(profileConfDto) && !Assert.isEmpty(profileConfDto.getId())) {
							String json = profileConfDto.getJson();
							paramJo = new JSONObject(json);
						}

						// 旧的widget实例
						String oldWidgetUuId = MathUtils.stringObj(jobj.get("id"));
						// 新的widget实例
						String newWidgetUuId = MathUtils.stringObj(System.nanoTime());
						jobj.put("id", newWidgetUuId);
						if (levelOne) {
							JSONObject tmpJo = new JSONObject();
							tmpJo.put("id", jobj.get("id"));
							tmpJo.put("name", jobj.get("name"));
							newJo.put(tmpJo);
						}

						// 根据widget名称获取widget信息
						WidgetDto widgetDto = widgetService.getWidgetInfoByCode(MathUtils.stringObj(jobj.get("name")));

						if ("nested".equals(jobj.get("name"))) {// 嵌套
							@SuppressWarnings("rawtypes")
							Iterator iterator = paramJo.keys();
							while (iterator.hasNext()) {
								String key = (String) iterator.next();
								if (!"widgets".equals(key)) {
									Object valObj = paramJo.get(key);
									resJo.put(key, valObj);
								}
							}

							resJo.put("nestedPage", paramJo);
							if (paramJo.has("widgets")) {
								JSONArray nestJsonArr = paramJo.getJSONArray("widgets");
								copyBusinessCompConf(nestJsonArr, newJsonArr, relWidgetObj, newWidgetParamJsonObj,
										false);
							}
						} else if ("tab".equals(jobj.get("name"))) {// tab面板
							@SuppressWarnings("rawtypes")
							Iterator iterator = paramJo.keys();
							while (iterator.hasNext()) {
								String key = (String) iterator.next();
								if (!"widgets".equals(key) && !"tabs".equals(key)) {
									Object valObj = paramJo.get(key);
									resJo.put(key, valObj);
								}
							}

							JSONArray tabArr = new JSONArray();

							// 循环tabs
							if (paramJo.has("tabs")) {
								tabArr = paramJo.getJSONArray("tabs");
								resJo.put("tabs", tabArr);

								if (tabArr.length() > 0) {
									for (int m = 0; m < tabArr.length(); m++) {
										JSONObject tabJo = tabArr.getJSONObject(m);
										if (tabJo.has("widgets")) {
											JSONArray nestJsonArr = tabJo.getJSONArray("widgets");
											copyBusinessCompConf(nestJsonArr, newJsonArr, relWidgetObj,
													newWidgetParamJsonObj, false);
										}
									}
								}
							} else {
								resJo.put("tabs", tabArr);
							}
						} else {// 非嵌套
							@SuppressWarnings("rawtypes")
							Iterator iterator = paramJo.keys();
							while (iterator.hasNext()) {
								String key = (String) iterator.next();
								Object valObj = paramJo.get(key);
								resJo.put(key, valObj);
							}
						}

						// 为空时给默认修饰器属性
						if (Assert.isEmpty(resJo) || resJo.length() == 0) {
							if (!Assert.isEmpty(widgetDto.getParams())) {
								resJo = new JSONObject(widgetDto.getParams());
							}
						}

						newWidgetParamJsonObj.put(newWidgetUuId, resJo);
						relWidgetObj.put(newWidgetUuId, oldWidgetUuId);
					}
				}
				if (levelOne) {
					newJsonArr.put(newJo);
				}
			}
		}
	}

	@Override
	public void getWidgetInstanceParams(String pageUuId, JSONObject widgetParamJsonObj, boolean isDefault,
			Long personnelId) throws JSONException {

		// 根据页面ID获取所有widget系统配置信息
		JSONObject sysJo = profileConfService.getSysConfListByPageUuId(pageUuId);

		// 根据页面ID获取所有widget个性化配置信息
		JSONObject cusJo = null;
		if (!Assert.isEmpty(personnelId) && !isDefault) {
			cusJo = customProfileConfService.getCusConfByPageUuId(pageUuId, personnelId);

			if (!Assert.isEmpty(cusJo) && cusJo.length() > 0) {
				@SuppressWarnings("rawtypes")
				Iterator iterator = cusJo.keys();
				while (iterator.hasNext()) {
					String key = (String) iterator.next();
					JSONObject tmpJo = new JSONObject();
					JSONObject cObj = cusJo.getJSONObject(key);// 个性化
					JSONObject sObj = (Assert.isEmpty(sysJo) || !sysJo.has(key)) ? null : sysJo.getJSONObject(key);// 系统
					if (!Assert.isEmpty(sObj)) {
						@SuppressWarnings("rawtypes")
						Iterator sIterator = sObj.keys();
						while (sIterator.hasNext()) {
							String kk = (String) sIterator.next();
							Object value = cObj.get(kk);
							if (!cObj.has(kk) && !"widgets".equals(kk)) {
								cObj.put(kk, value);
							}
						}
					}
					tmpJo.put(key, cObj);
					rtnWidgetParamsJo(widgetParamJsonObj, tmpJo);
				}

				// 增加非个性化的系统配置项
				@SuppressWarnings("rawtypes")
				Iterator sIterator = sysJo.keys();
				JSONObject tmpJo = null;
				while (sIterator.hasNext()) {
					String sk = (String) sIterator.next();
					if (!cusJo.has(sk)) {
						tmpJo = new JSONObject();
						tmpJo.put(sk, sysJo.getJSONObject(sk));
						rtnWidgetParamsJo(widgetParamJsonObj, tmpJo);
					}
				}

			} else {
				rtnWidgetParamsJo(widgetParamJsonObj, sysJo);
			}
		} else {
			rtnWidgetParamsJo(widgetParamJsonObj, sysJo);
		}
	}

	/**
	 * 处理嵌套widget返回数据
	 * 
	 * @param widgetParamJsonObj
	 * @param sysJo
	 * @throws JSONException
	 */
	private void rtnWidgetParamsJo(JSONObject widgetParamJsonObj, JSONObject sysJo) throws JSONException {
		if (!Assert.isEmpty(sysJo) && sysJo.length() > 0) {
			@SuppressWarnings("rawtypes")
			Iterator iterator = sysJo.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				try {
					JSONObject value = sysJo.getJSONObject(key);
					if (value.has("widgets")) {
						value.put("nestedPage", new JSONObject(value.toString()));
						value.remove("widgets");
						widgetParamJsonObj.put(key, value);
					} else {
						widgetParamJsonObj.put(key, value);
					}
				} catch (Exception e) {
					widgetParamJsonObj.put(key, sysJo.get(key));
				}
			}
		}
	}
}
