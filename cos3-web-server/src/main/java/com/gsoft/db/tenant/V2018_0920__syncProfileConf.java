package com.gsoft.db.tenant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.gsoft.cos3.jdbc.UpperCaseColumnMapRowMapper;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BooleanUtils;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.cos3.util.StringUtils;


/**
 * 同步更新库中widget配置信息--增加页面ID
 * @author chenxx
 *
 */
public class V2018_0920__syncProfileConf implements SpringJdbcMigration{
	
	/**
	 * 日志对象
	 */
	private Logger logger = Logger.getLogger(V2018_0920__syncProfileConf.class);

	@Override
	public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
		
		// 查询所有系统页面信息
		List<Map<String, Object>> pageList = this.query(jdbcTemplate, "SELECT c_uu_id FROM cos_portal_page");

		if (!Assert.isEmpty(pageList) && pageList.size() > 0) {
			
			// 组装保存系统widget配置信息的数据集合
			Set<String> sysWidgetIds = null;

			// 组装用户widget配置信息的数据集合
			Set<String> cusWidgetIds = null;

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

				// 查询系统页面widget实例
				sysInsList = this.query(jdbcTemplate, "SELECT c_json FROM cos_widget_instance WHERE c_page_uu_id = ?", map.get("C_UU_ID"));

				if (!Assert.isEmpty(sysInsList) && sysInsList.size() > 0) {
					for (Map<String, Object> insMap : sysInsList) {
						if (!Assert.isEmpty(insMap.get("C_JSON"))) {
							// widgets 数据集合
							try {
								jsonArr = new JSONArray(MathUtils.stringObj(insMap.get("C_JSON")));
								getAllSysWidgetIds(jsonArr, sysWidgetIds, jdbcTemplate, MathUtils.stringObj(map.get("C_UU_ID")));

							} catch (Exception e) {
								logger.error("页面ID：" + map.get("C_UU_ID") + "|json is " + insMap.get("C_JSON") + " 错误！");
							}
						}
					}
				}

				// 查询个人页面widget实例
				cusInsList = this.query(jdbcTemplate, "SELECT c_json, C_USER_ID FROM cos_custom_widget_instance WHERE c_page_uu_id = ?", map.get("C_UU_ID"));
				if (!Assert.isEmpty(cusInsList) && cusInsList.size() > 0) {
					for (Map<String, Object> insMap : cusInsList) {
						if (!Assert.isEmpty(insMap.get("C_JSON")) && !Assert.isEmpty(insMap.get("C_USER_ID"))) {
							// widgets 数据集合
							try {
								cusWidgetIds = new HashSet<String>();
								
								jsonArr = new JSONArray(MathUtils.stringObj(insMap.get("C_JSON")));
								getAllCusWidgetIds(jsonArr, cusWidgetIds, jdbcTemplate, MathUtils.stringObj(map.get("C_UU_ID")), MathUtils.numObj2Long(insMap.get("C_USER_ID")));
								
								// 更新个人偏好配置
								if (!Assert.isEmpty(cusWidgetIds) && cusWidgetIds.size() > 0) {
									jdbcTemplate.update("UPDATE cos_custom_profile SET c_page_uu_id = '"+map.get("C_UU_ID")+"' WHERE c_widget_uu_id in ("+"'"+StringUtils.join(cusWidgetIds, ",").replace(",","','")+"'"+")");
								}
								
							} catch (Exception e) {
								logger.error("页面ID：" + map.get("C_UU_ID") + "|json is " + insMap.get("C_JSON") + " 错误！");
							}
						}
					}
				}
				
				// 更新系统配置
				if (!Assert.isEmpty(sysWidgetIds) && sysWidgetIds.size() > 0) {
					jdbcTemplate.update("UPDATE cos_sys_profile SET c_page_uu_id = '"+map.get("C_UU_ID")+"' WHERE c_widget_uu_id in ("+"'"+StringUtils.join(sysWidgetIds, ",").replace(",","','")+"'"+")");
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
	private void getAllCusWidgetIds(JSONArray jsonArr, Set<String> widgetIds, JdbcTemplate jdbcTemplate, String pageUuId, long userId) throws JSONException {
		if (!Assert.isEmpty(jsonArr) && jsonArr.length() > 0) {
			
			Map<String, Object> tmpMap = null;
			Map<String, Object> widgetMap = null;
			
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONArray jo = jsonArr.getJSONArray(i);
				if (!Assert.isEmpty(jo) && jo.length() > 0) {
					for (int j = 0; j < jo.length(); j++) {
						JSONObject jobj = jo.getJSONObject(j);
						JSONObject paramJo = new JSONObject();
						
						Map<String, Object> pMap = this.load(jdbcTemplate, "SELECT c_json FROM cos_custom_profile WHERE c_widget_uu_id = ? and c_json LIKE '%\"widgets\"%' and c_page_uu_id is null and c_user_id = ? ", MathUtils.stringObj(jobj.get("id")), userId);
						if (!Assert.isEmpty(pMap)) {
							String json = MathUtils.stringObj(pMap.get("C_JSON"));
							paramJo = new JSONObject(json);
							handleCusSpeWidget(widgetIds, jobj, paramJo, jdbcTemplate, pageUuId, userId);
						}
						
						// 查询widget实例ID是否存在配置，若不存在新增默认配置
						Map<String, Object> cusConfInfo = this.load(jdbcTemplate, "SELECT * FROM cos_custom_profile WHERE c_widget_uu_id = ?", MathUtils.stringObj(jobj.get("id")));
						if (Assert.isEmpty(cusConfInfo)) {
							
							tmpMap = new HashMap<String, Object>();
							tmpMap.put("C_WIDGET_UU_ID", MathUtils.stringObj(jobj.get("id")));
							tmpMap.put("C_PAGE_UU_ID", pageUuId);
							widgetMap = this.load(jdbcTemplate, "SELECT c_params FROM cos_portal_widget WHERE c_code = ?", MathUtils.stringObj(jobj.get("name")));
							tmpMap.put("C_JSON", widgetMap.get("C_PARAMS"));
							tmpMap.put("C_USER_ID", userId);

							this.insert(jdbcTemplate, "cos_custom_profile", "C_ID", tmpMap);
							
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
	private void getAllSysWidgetIds(JSONArray jsonArr, Set<String> widgetIds, JdbcTemplate jdbcTemplate, String pageUuId) throws JSONException {
		if (!Assert.isEmpty(jsonArr) && jsonArr.length() > 0) {
			
			Map<String, Object> tmpMap = null;
			Map<String, Object> widgetMap = null;
			
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONArray jo = jsonArr.getJSONArray(i);
				if (!Assert.isEmpty(jo) && jo.length() > 0) {
					for (int j = 0; j < jo.length(); j++) {
						JSONObject jobj = jo.getJSONObject(j);
						JSONObject paramJo = new JSONObject();
						
						Map<String, Object> sMap = this.load(jdbcTemplate, "SELECT c_json FROM cos_sys_profile WHERE c_widget_uu_id = ? and c_json LIKE '%\"widgets\"%' and c_page_uu_id is null ", MathUtils.stringObj(jobj.get("id")));
						if (!Assert.isEmpty(sMap)) {
							String json = MathUtils.stringObj(sMap.get("C_JSON"));
							paramJo = new JSONObject(json);
							handleSysSpeWidget(widgetIds, jobj, paramJo, jdbcTemplate, pageUuId);
						}
						
						// 查询widget实例ID是否存在配置，若不存在新增默认配置
						Map<String, Object> sysConfInfo = this.load(jdbcTemplate, "SELECT * FROM cos_sys_profile WHERE c_widget_uu_id = ?", MathUtils.stringObj(jobj.get("id")));
						if (Assert.isEmpty(sysConfInfo)) {
							
							tmpMap = new HashMap<String, Object>();
							tmpMap.put("C_WIDGET_UU_ID", MathUtils.stringObj(jobj.get("id")));
							tmpMap.put("C_PAGE_UU_ID", pageUuId);
							widgetMap = this.load(jdbcTemplate, "SELECT c_params FROM cos_portal_widget WHERE c_code = ?", MathUtils.stringObj(jobj.get("name")));
							tmpMap.put("C_JSON", widgetMap.get("C_PARAMS"));

							this.insert(jdbcTemplate, "cos_sys_profile", "C_ID", tmpMap);
						}
						widgetIds.add(MathUtils.stringObj(jobj.get("id")));
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
	private void handleSysSpeWidget(Set<String> widgetIds, JSONObject jobj, JSONObject paramJo, JdbcTemplate jdbcTemplate, String pageUuId)
			throws JSONException {
		if ("nested".equals(jobj.get("name"))) {// 嵌套面板
			if (paramJo.has("widgets")) {
				JSONArray nestJsonArr = paramJo.getJSONArray("widgets");
				getAllSysWidgetIds(nestJsonArr, widgetIds, jdbcTemplate, pageUuId);
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
							getAllSysWidgetIds(nestJsonArr, widgetIds, jdbcTemplate, pageUuId);
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
	private void handleCusSpeWidget(Set<String> widgetIds, JSONObject jobj, JSONObject paramJo, JdbcTemplate jdbcTemplate, String pageUuId, long userId)
			throws JSONException {
		if ("nested".equals(jobj.get("name"))) {// 嵌套面板
			if (paramJo.has("widgets")) {
				JSONArray nestJsonArr = paramJo.getJSONArray("widgets");
				getAllCusWidgetIds(nestJsonArr, widgetIds, jdbcTemplate, pageUuId, userId);
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
							getAllCusWidgetIds(nestJsonArr, widgetIds, jdbcTemplate, pageUuId, userId);
						}
					}
				}
			}
		}
	}
	
	public List<Map<String, Object>> query(JdbcTemplate jdbcTemplate, String sql, Object... values) {
		return jdbcTemplate.query(sql, values, new UpperCaseColumnMapRowMapper());
	}
	
	public Map<String, Object> load(JdbcTemplate jdbcTemplate, String sql, Object... values) {
		List<Map<String, Object>> results = query(jdbcTemplate, sql, values);
		int size = (results != null ? results.size() : 0);
		if (size > 1) {
			throw new IncorrectResultSizeDataAccessException(1, size);
		}
		return size == 1 ? results.get(0) : null;
	}
	
	public Object insert(JdbcTemplate jdbcTemplate, String tableName, String pkName, Map<String, Object> values) {
		Assert.notEmpty(values, "新增记录的字段不能为空！");
		tableName = tableName.toUpperCase();
		Object pk = values.get(pkName);
		Object id;
		if (BooleanUtils.isEmpty(pk)) {
			Map<String, Object> resId = this.load(jdbcTemplate, "SELECT MAX(c_id)+1 AS c_id FROM " + tableName);
			id = MathUtils.numObj2Long(resId.get("C_ID"));
		} else {
			id = pk;
		}
		values.put(pkName, id);
		String sql = String.format("INSERT INTO %s ( %S ) VALUES ( %s)", tableName,
				StringUtils.join(values.keySet(), ", "), StringUtils.repeat("?", ", ", values.size()));
		if (logger.isInfoEnabled()) {
			logger.info("SQL: " + sql);
			logger.info("SQL Params:  " + StringUtils.join(values.values().toArray(), ", "));
		}
		jdbcTemplate.update(sql, values.values().toArray());
		return id;
	}
}
