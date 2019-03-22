package com.gsoft.portal.component.pagetemp.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.cos3.util.StringUtils;
import com.gsoft.portal.component.pagetemp.dto.PageTemplateConfDto;
import com.gsoft.portal.component.pagetemp.dto.PageTemplateDto;
import com.gsoft.portal.component.pagetemp.entity.PageTemplateConfEntity;
import com.gsoft.portal.component.pagetemp.entity.PageTemplateEntity;
import com.gsoft.portal.component.pagetemp.persistence.PageTemplateConfPersistence;
import com.gsoft.portal.component.pagetemp.persistence.PageTemplatePersistence;
import com.gsoft.portal.component.pagetemp.service.PageTemplateService;
import com.gsoft.portal.webview.widgetconf.entity.ProfileConfEntity;
import com.gsoft.portal.webview.widgetconf.persistence.ProfileConfPersistence;
import com.gsoft.portal.webview.widgetconf.service.WidgetConfService;

/**
 * 页面模版管理Service实现类
 * 
 * @author SN
 *
 */
@Service
public class PageTemplateServiceImpl implements PageTemplateService {

	@Resource
	BaseDao baseDao;

	@Resource
	PageTemplatePersistence pageTemplatePersistence;

	@Resource
	PageTemplateConfPersistence pageTemplateConfPersistence;

	@Resource
	WidgetConfService widgetConfService;
	
	@Resource
	ProfileConfPersistence profileConfPersistence;

	@Override
	public PageDto getPageTempList(String search, Integer page, Integer size, String sortProp, String order) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT c.*,l.c_name as c_layout_name FROM cos_page_template c ");
		sb.append("LEFT JOIN cos_portal_layout l ON c.c_layout_code = l.c_code where 1=1 ");
		Map<String, Object> params = new HashMap<String, Object>();

		if (Assert.isNotEmpty(search)) {
			sb.append(" AND (c.c_name like ${search} or c.c_desc like ${search} )");
			params.put("search", "%" + search + "%");
		}

		sb.append(" ORDER BY c.c_id DESC ");
		PageDto pageDto = baseDao.query(page, size, sb.toString(), params);
		return pageDto;
	}

	@Override
	public PageTemplateDto savePageTempInfo(PageTemplateDto PageTemplateDto) throws Exception {
		PageTemplateEntity entity = BeanUtils.convert(PageTemplateDto, PageTemplateEntity.class);
		
		if (!Assert.isEmpty(PageTemplateDto.getId())) {
			// 根据ID查询原页面模版信息
			PageTemplateEntity oEntity = pageTemplatePersistence.findOne(PageTemplateDto.getId());
			// 模版布局有修改
			if (!PageTemplateDto.getLayoutCode().equals(oEntity.getLayoutCode())) {
				this.changePageTempConf(PageTemplateDto.getCode(), oEntity.getLayoutCode(), PageTemplateDto.getLayoutCode());
			}
		}
		
		// 若配置实例不为空则新增模版实例及对应配置
		if (!Assert.isEmpty(PageTemplateDto.getJson())) {
			JSONObject confJo = new JSONObject(PageTemplateDto.getJson());
			entity.setLayoutCode(MathUtils.stringObj(confJo.get("layout")));
			
			// 旧的widget实例信息
			JSONArray oldJsonArr = confJo.getJSONArray("widgets");
			
			// 新的widget下的组件信息
			JSONArray newJsonArr = new JSONArray();
			// 新的widget的参数信息
			JSONObject newWidgetParamJsonObj = new JSONObject();
			
			JSONObject relWidgetObj = new JSONObject();
			widgetConfService.copyBusinessCompConf(oldJsonArr, newJsonArr, relWidgetObj, newWidgetParamJsonObj, true);
			
			// 需要保存的对应参数配置信息
			List<ProfileConfEntity> widgetConfList = new ArrayList<ProfileConfEntity>();
			ProfileConfEntity profileConfEntity = null;
			
			@SuppressWarnings("rawtypes")
			Iterator iterator = newWidgetParamJsonObj.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				JSONObject valObj = newWidgetParamJsonObj.getJSONObject(key);
				profileConfEntity = new ProfileConfEntity();
				profileConfEntity.setWidgetUuId(MathUtils.stringObj(key));
				profileConfEntity.setJson(valObj.has("nestedPage") ? MathUtils.stringObj(valObj.get("nestedPage")) : MathUtils.stringObj(valObj));
				widgetConfList.add(profileConfEntity);
			}
			
			if (widgetConfList.size() > 0) {
				profileConfPersistence.save(widgetConfList);
			}
			
			this.savePageTempConf(PageTemplateDto.getCode(), newJsonArr.toString());
		}
		
		PageTemplateEntity reEntity = pageTemplatePersistence.save(entity);
		return BeanUtils.convert(reEntity, PageTemplateDto.class);
	}

	/**
	 * 复制页面配置信息
	 * @param jsonArr
	 * @param newJsonArr
	 * @param relWidgetObj
	 * @param newWidgetParamJsonObj
	 * @param levelOne
	 * @throws JSONException 
	 */
	public void copyPageTempConf(JSONArray jsonArr, JSONArray newJsonArr,JSONObject oldWidgetParamJsonObj,
			JSONObject newWidgetParamJsonObj, boolean levelOne) throws JSONException {
		if (!Assert.isEmpty(jsonArr) && jsonArr.length() > 0) {
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONArray jo = jsonArr.getJSONArray(i);
				JSONArray newJo = new JSONArray();
				if (!Assert.isEmpty(jo) && jo.length() > 0) {
					for (int j = 0; j < jo.length(); j++) {
						JSONObject jobj = jo.getJSONObject(j);

						JSONObject resJo = new JSONObject();

						// 旧的widget实例
						String oldWidgetUuId = MathUtils.stringObj(jobj.get("id"));
						// 新的widget实例
						String newWidgetUuId = MathUtils.stringObj(System.nanoTime());
						
						JSONObject paramJo = oldWidgetParamJsonObj.getJSONObject(oldWidgetUuId);
						
						jobj.put("id", newWidgetUuId);
						
						if (levelOne) {
							JSONObject tmpJo = new JSONObject();
							tmpJo.put("id", jobj.get("id"));
							tmpJo.put("name", jobj.get("name"));
							newJo.put(tmpJo);
						}
						
						if ("nested".equals(jobj.get("name"))) {// 嵌套
							@SuppressWarnings("rawtypes")
							Iterator iterator = paramJo.getJSONObject("nestedPage").keys();
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
								copyPageTempConf(nestJsonArr, newJsonArr, oldWidgetParamJsonObj, newWidgetParamJsonObj,
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
											copyPageTempConf(nestJsonArr, newJsonArr, oldWidgetParamJsonObj,
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

						newWidgetParamJsonObj.put(newWidgetUuId, resJo);
					}
				}
				if (levelOne) {
					newJsonArr.put(newJo);
				}
			}
		}
	}

	@Override
	@Transactional
	public ReturnDto delPageTemplate(Long id) throws JSONException {

		PageTemplateEntity entity = pageTemplatePersistence.getOne(id);

		// 删除页面模版下的所有配置信息
		PageTemplateConfEntity pageTemplateConfEntity = pageTemplateConfPersistence.findByCode(entity.getCode());

		if (!Assert.isEmpty(pageTemplateConfEntity) && !Assert.isEmpty(pageTemplateConfEntity.getJson())) {
			JSONObject widgetParamJsonObj = new JSONObject();
			widgetConfService.getBusinessWidgetInstanceParams(new JSONArray(pageTemplateConfEntity.getJson()),
					widgetParamJsonObj);

			Set<String> widgetIds = new HashSet<String>();
			@SuppressWarnings("rawtypes")
			Iterator iterator = widgetParamJsonObj.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				widgetIds.add(key);
			}

			if (widgetIds.size() > 0) {
				baseDao.update("delete from cos_sys_profile WHERE c_widget_uu_id in (" + "'"
						+ StringUtils.join(widgetIds, ",").replace(",", "','") + "'" + ")");
			}
		}
		
		if (!Assert.isEmpty(pageTemplateConfEntity) && !Assert.isEmpty(pageTemplateConfEntity.getId())) {
			pageTemplateConfPersistence.delete(pageTemplateConfEntity.getId());
		}
		
		pageTemplatePersistence.delete(id);

		return new ReturnDto("删除成功!");
	}

	@Override
	public PageTemplateDto getPageTempInfoById(Long id) {
		return BeanUtils.convert(pageTemplatePersistence.getOne(id), PageTemplateDto.class);
	}

	@Override
	public ReturnDto isUniquPageTempCode(Long id, String code) {
		PageTemplateEntity entity = null;

		if (Assert.isEmpty(id)) {
			entity = pageTemplatePersistence.findByCode(code);
		} else {
			entity = pageTemplatePersistence.findByCode(code, id);
		}

		if (entity != null) {
			return new ReturnDto(true);
		}

		return new ReturnDto(false);
	}

	@Override
	public void savePageTempConf(String code, String json) {
		// 查询之前是否已存在对应数据
		PageTemplateConfEntity pageTemplateConfEntity = pageTemplateConfPersistence.findByCode(code);
		if (!Assert.isEmpty(pageTemplateConfEntity) && !Assert.isEmpty(pageTemplateConfEntity.getId())) {
			pageTemplateConfEntity.setJson(json);
		} else {
			pageTemplateConfEntity = new PageTemplateConfEntity();
			pageTemplateConfEntity.setCode(code);
			pageTemplateConfEntity.setJson(json);
		}
		pageTemplateConfPersistence.save(pageTemplateConfEntity);
	}

	@Override
	public ReturnDto getPageTempConfInfo(String layout, String code, String pageUuId) throws JSONException {
		JSONObject rtnJo = new JSONObject();

		// 1. 根据页面模版code获取页面模版配置信息
		PageTemplateConfEntity pageTemplateConfEntity = pageTemplateConfPersistence.findByCode(code);

		JSONObject dataJo = new JSONObject();
		// 设置默认页面模式和布局
		dataJo.put("mode", "standard");
		dataJo.put("layout", layout);

		if (!Assert.isEmpty(pageTemplateConfEntity) && !Assert.isEmpty(pageTemplateConfEntity.getId())) {
			String json = pageTemplateConfEntity.getJson();
			if (!Assert.isEmpty(json)) {
				JSONArray jsonArr = new JSONArray(json);
				dataJo.put("widgets", jsonArr);

				JSONObject widgetParamJsonObj = new JSONObject();
				widgetConfService.getBusinessWidgetInstanceParams(jsonArr, widgetParamJsonObj);
				dataJo.put("widgetParams", widgetParamJsonObj);
				rtnJo.put("widgetParams", widgetParamJsonObj);
			}
		}
		rtnJo.put("nestedPage", dataJo);
		return new ReturnDto(rtnJo.toString());
	}

	@Override
	public PageTemplateConfDto getPageTempConfInfo(String pageTempCode) {
		PageTemplateConfEntity pageTemplateConfEntity = pageTemplateConfPersistence.findByCode(pageTempCode);
		return BeanUtils.convert(pageTemplateConfEntity, PageTemplateConfDto.class);
	}

	@Override
	public PageTemplateDto getPageTempInfo(String pageTempCode) {
		PageTemplateEntity entity = pageTemplatePersistence.findByCode(pageTempCode);
		return BeanUtils.convert(entity, PageTemplateDto.class);
	}

	/**
	 * 改变页面模版配置实例数据
	 * 
	 * @param code
	 * @param oLayout
	 * @param nLayout
	 * @throws Exception
	 */
	public void changePageTempConf(String code, String oLayout, String nLayout) throws Exception {
		JSONArray nJsonArray = new JSONArray();
		JSONArray tmpArr = null;

		for (int i = 0; i < nLayout.split("-").length; i++) {
			tmpArr = new JSONArray();
			nJsonArray.put(tmpArr);
		}

		JSONArray oJsonArray = new JSONArray();
		JSONArray resJsonArr = new JSONArray();

		// 查询页面模版配置实例信息
		PageTemplateConfDto pageTemplateConfDto = this.getPageTempConfInfo(code);
		String json = pageTemplateConfDto.getJson();
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

		String sql = "UPDATE cos_page_template_conf SET c_json = ? WHERE c_code = ? ";
		baseDao.update(sql, resJsonArr.toString(), code);
	}

	@Override
	public List<PageTemplateDto> getAllPageTempList() {
		return BeanUtils.convert(pageTemplatePersistence.getAllPageTempList(), PageTemplateDto.class);
	}
	
	@Override
	public void batchSave(List<PageTemplateConfDto> nPageTemplateConfList) {
		pageTemplateConfPersistence.save(BeanUtils.convert(nPageTemplateConfList, PageTemplateConfEntity.class));
	}
}
