package com.gsoft.portal.webview.page.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.component.decorate.dto.DecorateDto;
import com.gsoft.portal.component.decorate.service.DecorateService;
import com.gsoft.portal.webview.page.dto.SitePageConfDto;
import com.gsoft.portal.webview.page.entity.SitePageConfEntity;
import com.gsoft.portal.webview.page.persistence.SitePageConfPersistence;
import com.gsoft.portal.webview.page.service.SitePageConfService;
import com.gsoft.portal.webview.widget.service.WidgetService;
import com.gsoft.portal.webview.widgetconf.service.CustomProfileConfService;
import com.gsoft.portal.webview.widgetconf.service.CustomWidgetConfService;
import com.gsoft.portal.webview.widgetconf.service.ProfileConfService;
import com.gsoft.portal.webview.widgetconf.service.WidgetConfService;

/**
 * 页面配置管理Service实现类
 * 
 * @author SN
 *
 */
@Service
public class SitePageConfServiceImpl implements SitePageConfService {
	
	@Resource
	SitePageConfPersistence sitePageConfPersistence;
	
	@Resource
	DecorateService decorateService;
	
	@Resource
	WidgetService widgetService;

	@Resource
	BaseDao baseDao;
	
	@Resource
	WidgetConfService widgetConfService;
	
	@Resource
	CustomWidgetConfService customWidgetConfService;
	
	@Resource
	CustomProfileConfService customProfileConfService;
	
	@Resource
	ProfileConfService profileConfService;

	@Override
	public PageDto querySitePageConfInfo(String search, String pageUuId, Integer page, Integer size, String sortProp,
			String order) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT pw.*,w.c_name as c_widget_name,d.c_name as c_decorator_name ");
		sb.append("FROM cos_page_widget_info pw LEFT JOIN cos_portal_widget w ON pw.c_widget_code = w.c_code ");
		sb.append("LEFT JOIN cos_portal_decorate d ON pw.c_decorator_code = d.c_code ");
		sb.append("WHERE pw.c_page_uu_id = ${pageUuId} ");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pageUuId", pageUuId);

		if (Assert.isNotEmpty(search)) {
			sb.append(" AND (pw.c_widget_title like ${search}) ");
			params.put("search", "%" + search + "%");
		}
		sb.append(" ORDER BY pw.c_id DESC ");

		PageDto pageDto = baseDao.query(page, size, sb.toString(), params);
		return pageDto;
	}

	@Override
	public SitePageConfDto getPageConfInfoById(Long id) {
		SitePageConfEntity entity = sitePageConfPersistence.findOne(id);
		SitePageConfDto dto = BeanUtils.convert(entity, SitePageConfDto.class);
		return dto;
	}

	@Override
	public SitePageConfDto saveSitePageConf(SitePageConfDto sitePageConfDto) {
		
		SitePageConfEntity entity = BeanUtils.convert(sitePageConfDto, SitePageConfEntity.class);
		SitePageConfEntity reEntity = sitePageConfPersistence.save(entity);
		return BeanUtils.convert(reEntity, SitePageConfDto.class);
	}

	@Override
	@Transactional
	public void delSitePageConf(String uuId, String pageUuId) throws JSONException {
		//1. 删除页面widget配置实例
		baseDao.delete("cos_page_widget_info", "c_uu_id", uuId);
		//2. 删除页面个性化widget及系统widget信息
		String widgetJson = widgetConfService.getWidgetJson(pageUuId);
		if (Assert.isNotEmpty(widgetJson) && widgetJson.length() > 0) {
			JSONArray jsonArr = new JSONArray(widgetJson);
			JSONArray newJsonArr = new JSONArray();
			resWidgetInstance(jsonArr, newJsonArr, uuId, true);
			
			//更新新的实例信息
			baseDao.update("UPDATE cos_widget_instance SET c_json = ? WHERE c_page_uu_id = ?", newJsonArr.toString(), pageUuId);
		}
		
		List<Map<String, Object>> resList = baseDao.query("SELECT * FROM cos_custom_widget_instance WHERE c_page_uu_id = ? ", pageUuId);
		if (!Assert.isEmpty(resList) && resList.size() > 0) {
			for (Map<String, Object> tmpMap : resList) {
				if (Assert.isNotEmpty(tmpMap.get("C_JSON"))) {
					JSONArray jsonArr = new JSONArray(MathUtils.stringObj(tmpMap.get("C_JSON")));
					JSONArray newJsonArr = new JSONArray();
					resWidgetInstance(jsonArr, newJsonArr, uuId, false);
					//更新新的实例信息
					baseDao.update("UPDATE cos_custom_widget_instance SET c_json = ? WHERE c_id = ?", newJsonArr.toString(), tmpMap.get("C_ID"));
				}
			}
		}
		
		//3. 删除实例系统配置及个性化配置信息
		baseDao.update("DELETE FROM cos_custom_profile where c_widget_uu_id = ?", uuId);
		baseDao.update("DELETE FROM cos_sys_profile where c_widget_uu_id = ?", uuId);
	}
	
	/**
	 * 重新组装页面widget实例
	 * @param jsonArr
	 * @param newJsonArr
	 * @param uuId
	 * @param isDefault
	 * @throws JSONException
	 */
	public void resWidgetInstance(JSONArray jsonArr, JSONArray newJsonArr, String uuId, boolean isDefault) throws JSONException {
		if (!Assert.isEmpty(jsonArr) && jsonArr.length() > 0) {
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONArray jo = jsonArr.getJSONArray(i);
				JSONArray newArr = new JSONArray();
				if (!Assert.isEmpty(jo) && jo.length() > 0) {
					for (int j = 0; j < jo.length(); j++) {
						JSONObject jobj = jo.getJSONObject(j);
						if (!jobj.get("id").equals(uuId)) {
							newArr.put(jobj);
						}
						if ("nested".equals(jobj.get("name"))) {//嵌套 TODO
							String sql = "";
							if (isDefault) {
								sql = "SELECT * FROM cos_sys_profile WHERE c_widget_uu_id = ?";
							}else {
								sql = "SELECT * FROM cos_custom_profile WHERE c_widget_uu_id = ?";
							}
							List<Map<String, Object>> resList = baseDao.query(sql, MathUtils.stringObj(jobj.get("id")));
							if (!Assert.isEmpty(resList) && resList.size() > 0) {
								for (Map<String, Object> tmpMap : resList) {
									String sJson = MathUtils.stringObj(tmpMap.get("C_JSON"));
									JSONObject paramJo = new JSONObject(sJson);
									if (paramJo.has("widgets")) {
										JSONArray nestJsonArr = paramJo.getJSONArray("widgets");
										resWidgetInstanceParams(nestJsonArr, uuId, isDefault, paramJo, MathUtils.numObj2Long(tmpMap.get("C_ID")));
									}
								}
							}
						}
					}
				}
				newJsonArr.put(newArr);
			}
		}
	}
	
	/**
	 * @param jsonArr
	 * @param uuId
	 * @param isDefault
	 * @param id
	 * @throws JSONException
	 */
	public void resWidgetInstanceParams(JSONArray jsonArr, String uuId, boolean isDefault, JSONObject paramJo, Long id) throws JSONException {
		JSONArray newJsonArr  = new JSONArray();
		if (!Assert.isEmpty(jsonArr) && jsonArr.length() > 0) {
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONArray jo = jsonArr.getJSONArray(i);
				JSONArray newArr = new JSONArray();
				if (!Assert.isEmpty(jo) && jo.length() > 0) {
					for (int j = 0; j < jo.length(); j++) {
						JSONObject jobj = jo.getJSONObject(j);
						if (!jobj.get("id").equals(uuId)) {
							newArr.put(jobj);
						}
						if ("nested".equals(jobj.get("name"))) {//嵌套
							String sql = "";
							if (isDefault) {
								sql = "SELECT * FROM cos_sys_profile WHERE c_widget_uu_id = ?";
							}else {
								sql = "SELECT * FROM cos_custom_profile WHERE c_widget_uu_id = ?";
							}
							List<Map<String, Object>> resList = baseDao.query(sql, MathUtils.stringObj(jobj.get("id")));
							if (!Assert.isEmpty(resList) && resList.size() > 0) {
								for (Map<String, Object> tmpMap : resList) {
									String sJson = MathUtils.stringObj(tmpMap.get("C_JSON"));
									JSONObject paramJo1 = new JSONObject(sJson);
									if (paramJo.has("widgets")) {
										JSONArray nestJsonArr = paramJo1.getJSONArray("widgets");
										resWidgetInstanceParams(nestJsonArr, uuId, isDefault, paramJo1, MathUtils.numObj2Long(tmpMap.get("C_ID")));
									}
								}
							}
						}
					}
				}
				newJsonArr.put(newArr);
			}
		}
		
		paramJo.put("widgets", newJsonArr);
		
		//更新
		if (isDefault) {
			baseDao.update("update cos_sys_profile set c_json = ? where c_id = ?", paramJo.toString(),id);
		}else {
			baseDao.update("update cos_custom_profile set c_json = ? where c_id = ?", paramJo.toString(),id);
		}
	}

	@Override
	public JSONArray getPageWidgetJson(String pageUuId) throws JSONException {
		
		List<SitePageConfEntity> entityList = sitePageConfPersistence.getgetPageWidgets(pageUuId);
		
		JSONArray joArr = new JSONArray();
		
		if (Assert.isNotEmpty(entityList)) {
			for(SitePageConfEntity entity : entityList) {
				JSONObject jo = new JSONObject();
				jo.put("name", entity.getWidgetCode());
				jo.put("id", entity.getUuId());
				DecorateDto decorateDto = decorateService.getDecorateInfoByCode(entity.getDecoratorCode());
				jo.put("description", "");
				
				Map<String, Object> paramsMap = new HashMap<String, Object>();
				paramsMap.put("decorator", decorateDto.getCode());
				paramsMap.put("title", entity.getWidgetTitle());
				paramsMap.put("text", entity.getWidgetTitle());
				jo.put("params", paramsMap);
				joArr.put(jo);
			}
		}
		
		return joArr;
	}

	@Override
	public SitePageConfDto getPageConfInfoByUuId(String uuId) {
		SitePageConfEntity entity = sitePageConfPersistence.getPageConfInfoByUuId(uuId);
		return BeanUtils.convert(entity, SitePageConfDto.class);
	}
}
