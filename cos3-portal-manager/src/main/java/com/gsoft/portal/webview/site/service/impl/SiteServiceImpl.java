package com.gsoft.portal.webview.site.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.gsoft.cos3.datasource.DynamicDataSourceContextHolder;
import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.component.decorate.dto.DecorateDto;
import com.gsoft.portal.component.decorate.service.DecorateService;
import com.gsoft.portal.component.layout.dto.LayoutDto;
import com.gsoft.portal.component.layout.service.LayoutService;
import com.gsoft.portal.component.theme.dto.ThemeDto;
import com.gsoft.portal.component.theme.entity.ThemeEntity;
import com.gsoft.portal.component.theme.persistence.ThemePersistence;
import com.gsoft.portal.component.theme.service.ThemeService;
import com.gsoft.portal.webview.page.dto.SitePageDto;
import com.gsoft.portal.webview.page.dto.SitePageHelpDto;
import com.gsoft.portal.webview.page.service.SitePageConfService;
import com.gsoft.portal.webview.page.service.SitePageHelpService;
import com.gsoft.portal.webview.page.service.SitePageService;
import com.gsoft.portal.webview.site.dto.SiteDto;
import com.gsoft.portal.webview.site.entity.SiteEntity;
import com.gsoft.portal.webview.site.persistence.SiteCustomerPersistence;
import com.gsoft.portal.webview.site.persistence.SitePersistence;
import com.gsoft.portal.webview.site.service.SiteService;
import com.gsoft.portal.webview.widget.dto.WidgetDto;
import com.gsoft.portal.webview.widget.service.WidgetService;
import com.gsoft.portal.webview.widgetconf.dto.ProfileConfDto;
import com.gsoft.portal.webview.widgetconf.dto.WidgetConfDto;
import com.gsoft.portal.webview.widgetconf.entity.ProfileConfEntity;
import com.gsoft.portal.webview.widgetconf.persistence.ProfileConfPersistence;
import com.gsoft.portal.webview.widgetconf.service.CustomWidgetConfService;
import com.gsoft.portal.webview.widgetconf.service.ProfileConfService;
import com.gsoft.portal.webview.widgetconf.service.WidgetConfService;

/**
 * 站点管理Service实现类
 * 
 * @author SN
 *
 */
@Service
public class SiteServiceImpl implements SiteService {

	@Resource
	SitePersistence sitePersistence;

	@Resource
	BaseDao baseDao;

	@Resource
	ThemeService themeService;

	@Resource
	LayoutService layoutService;

	@Resource
	DecorateService decorateService;

	@Resource
	WidgetService widgetService;

	@Resource
	SitePageService sitePageService;

	@Resource
	WidgetConfService widgetConfService;

	@Resource
	CustomWidgetConfService customWidgetConfService;

	@Resource
	SitePageConfService sitePageConfService;

	@Resource
	ProfileConfService profileConfService;
	
	@Resource
	ThemePersistence themePersistence;
	
	@Resource
	ProfileConfPersistence profileConfPersistence;
	
	@Resource
	SiteCustomerPersistence siteCustomerPersistence;
	
	@Resource
	SitePageHelpService sitePageHelpService;

	@Autowired
	private DiscoveryClient discoveryClient;
	
	@Autowired
	RestTemplate restTemplate;

	@Override
	public PageDto querySiteDataTable(String search, Integer page, Integer size, String sortProp, String order) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.*,t.c_name as publicThemeName,tt.c_name as privateThemeName FROM cos_portal_site s  ");
		sb.append("LEFT JOIN cos_portal_theme t ON s.c_public_theme = t.c_code ");
		sb.append("LEFT JOIN cos_portal_theme tt ON s.c_private_theme = tt.c_code where 1=1");

		Map<String, Object> params = new HashMap<String, Object>();

		if (Assert.isNotEmpty(search)) {
			sb.append(" AND (s.c_code like ${search} or s.c_name like ${search} or s.c_title like  ${search}) ");
			params.put("search", "%" + search + "%");
		}
		sb.append(" ORDER BY s.c_id DESC ");

		PageDto pageDto = baseDao.query(page, size, sb.toString(), params);
		return pageDto;
	}

	@Override
	public SiteDto getSiteInfoById(Long id) {
		SiteEntity entity = sitePersistence.findOne(id);
		SiteDto dto = BeanUtils.convert(entity, SiteDto.class);
		return dto;
	}

	@Override
	public Boolean isExitSiteCode(Long id, String code) {

		SiteEntity entity = null;

		if (Assert.isEmpty(id)) {
			entity = sitePersistence.findByCode(code);
		} else {
			entity = sitePersistence.findByCode(code, id);
		}

		if (entity != null) {
			return true;
		}

		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	@Transactional
	public SiteDto saveSite(SiteDto siteDto) {
		SiteEntity entity = BeanUtils.convert(siteDto, SiteEntity.class);
		SiteEntity reEntity = sitePersistence.save(entity);
		//刷新当前服务
		restTemplate.postForObject(discoveryClient.getLocalServiceInstance().getUri() + "/refresh", null, String.class);
		// 同步站点域名租户信息到主库
		this.syncSiteCustomerInfo(siteDto);
		return BeanUtils.convert(reEntity, SiteDto.class);
	}

	/**
	 * 同步站点租户域名信息
	 * @param code
	 * @param domainName
	 * @param customer
	 */
	private void syncSiteCustomerInfo(SiteDto siteDto) {
		// 切换到主库
		DynamicDataSourceContextHolder.clearDataSource();
		// 先查询是否已存在
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM cos_custormer_site_domain WHERE c_site_code = '"+siteDto.getCode()+"' ");
		if (Assert.isEmpty(siteDto.getCustomer())) {
			sb.append(" and (c_custormer_code is null or c_custormer_code = '') ");
		} else {
			sb.append(" AND c_custormer_code = '"+siteDto.getCustomer()+"' ");
		}
		
		Map<String, Object> dataMap = baseDao.load(sb.toString());
		if (!Assert.isEmpty(dataMap) && !Assert.isEmpty(dataMap.get("C_ID"))) {
			dataMap.put("C_UPDATE_BY", siteDto.getUpdateBy());
			dataMap.put("C_UPDATE_TIME", siteDto.getUpdateTime());
		} else {
			dataMap = new HashMap<String, Object>();
			dataMap.put("C_CREATE_BY", siteDto.getCreateBy());
			dataMap.put("C_CREATE_TIME", siteDto.getCreateTime());
		}
		
		dataMap.put("C_SITE_CODE", siteDto.getCode());
		dataMap.put("C_DOMAIN", siteDto.getDomainName());
		dataMap.put("C_CUSTORMER_CODE", siteDto.getCustomer());
		
		if (Assert.isEmpty(dataMap.get("C_ID"))) {
			baseDao.insert("cos_custormer_site_domain", "C_ID", dataMap);
		}else {
			baseDao.modify("cos_custormer_site_domain", "C_ID", dataMap);
		}
		
		if ("web".equals(siteDto.getCode())) {
			// 修改默认web站点域名时同步更新租户信息表中的域名
			baseDao.update("UPDATE cos_saas_customer SET c_domain = ? WHERE c_code = ?", siteDto.getDomainName(), siteDto.getCustomer());
		}
	}

	@Override
	@Transactional
	public void delSite(Long id, String code) {
		// 1.删除站点
		baseDao.delete("cos_portal_site", "c_id", id);

		// 2.删除用户自定义主题、布局
		String sql = "DELETE FROM cos_custom_theme WHERE c_site_code = ? ";
		baseDao.update(sql, code);

		sql = "DELETE FROM cos_custom_layout WHERE c_page_uu_id IN (SELECT c_uu_id FROM cos_portal_page WHERE c_site_code = ?)";
		baseDao.update(sql, code);

		// 3.删除页面widget配置信息
		sql = "DELETE FROM cos_page_widget_info WHERE c_page_uu_id IN (SELECT c_uu_id FROM cos_portal_page WHERE c_site_code = ?)";
		baseDao.update(sql, code);

		// 4.删除页面个性化配置
		sql = "DELETE FROM cos_custom_widget_instance WHERE c_page_uu_id IN (SELECT c_uu_id FROM cos_portal_page WHERE c_site_code = ?)";
		baseDao.update(sql, code);

		// 5.删除widget实例系统配置信息
		sql = "select * FROM cos_widget_instance WHERE c_page_uu_id IN (SELECT c_uu_id FROM cos_portal_page WHERE c_site_code = ?)";
		List<Map<String, Object>> resList = baseDao.query(sql, code);
		if (!Assert.isEmpty(resList) && resList.size() > 0) {
			for (Map<String, Object> map : resList) {
				String widgetJson = MathUtils.stringObj(map.get("C_JSON"));
				if (Assert.isNotEmpty(widgetJson) && widgetJson.length() > 0) {
					try {
						JSONArray jsonArr = new JSONArray(widgetJson);
						Set<String> paramIds = new HashSet<String>();
						getParamIdSets(jsonArr, paramIds);
						if (paramIds.size() > 0) {
							for (String paramId : paramIds) {
								sql = "DELETE FROM cos_sys_profile WHERE c_widget_uu_id = ?";
								baseDao.update(sql, paramId);
							}
						}
					} catch (Exception e) {
					}
				}
			}
		}

		// 6.删除页面widget实例配置信息
		sql = "DELETE FROM cos_widget_instance WHERE c_page_uu_id IN (SELECT c_uu_id FROM cos_portal_page WHERE c_site_code = ?)";
		baseDao.update(sql, code);

		// 7.删除页面信息
		baseDao.update("DELETE FROM cos_portal_page WHERE c_site_code = ?", code);
	}

	/**
	 * 获取所有widget实例Id
	 * 
	 * @param jsonArr
	 * @param paramIds
	 * @throws JSONException
	 */
	private void getParamIdSets(JSONArray jsonArr, Set<String> paramIds) throws JSONException {
		if (!Assert.isEmpty(jsonArr) && jsonArr.length() > 0) {
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONArray jo = jsonArr.getJSONArray(i);
				if (!Assert.isEmpty(jo) && jo.length() > 0) {
					for (int j = 0; j < jo.length(); j++) {
						JSONObject jobj = jo.getJSONObject(j);
						JSONObject paramJo = new JSONObject();

						ProfileConfDto profileConfDto = profileConfService
								.getProfileConfInfo(MathUtils.stringObj(jobj.get("id")));
						if (!Assert.isEmpty(profileConfDto) && !Assert.isEmpty(profileConfDto.getId())) {
							String json = profileConfDto.getJson();
							paramJo = new JSONObject(json);
						}
						if ("nested".equals(jobj.get("name"))) {// 嵌套
							if (paramJo.has("widgets")) {
								JSONArray nestJsonArr = paramJo.getJSONArray("widgets");
								getParamIdSets(nestJsonArr, paramIds);
							}
						} else if ("tab".equals(jobj.get("name"))) {
							if (paramJo.has("tabs")) {
								JSONArray tabArr = paramJo.getJSONArray("tabs");
								if (tabArr.length() > 0) {
									for (int m = 0; m < tabArr.length(); m++) {
										JSONObject tabJo = tabArr.getJSONObject(m);
										if (tabJo.has("widgets")) {
											JSONArray tabJsonArr = tabJo.getJSONArray("widgets");
											getParamIdSets(tabJsonArr, paramIds);
										}
									}
								}
							}
						}
						
						if (paramJo.length() > 0) {
							paramIds.add(MathUtils.stringObj(jobj.get("id")));
						}
					}
				}
			}
		}
	}

	@Override
	public List<SiteDto> getAllSiteList() {

		List<SiteEntity> entityList = sitePersistence.getAllSiteList();
		return BeanUtils.convert(entityList, SiteDto.class);
	}

	@Override
	public SiteDto getSiteInfoByCode(String siteCode) {

		SiteEntity entity = sitePersistence.findByCode(siteCode);
		return BeanUtils.convert(entity, SiteDto.class);
	}

	@Override
	@Transactional
	public boolean handleImportData(JSONObject jsonObj) throws Exception {

		String siteCode = jsonObj.getString("siteCode");
		if (jsonObj.has("isCover") && "1".equals(jsonObj.get("isCover"))) {
			// 站点覆盖时先删除站点相关的所有数据
			// 1.删除站点信息
			baseDao.update("DELETE FROM cos_portal_site WHERE c_code = ?", siteCode);

			// 2.删除页面个性化配置
			String sql = "DELETE FROM cos_custom_widget_instance WHERE c_page_uu_id IN (SELECT c_uu_id FROM cos_portal_page WHERE c_site_code = ?)";
			baseDao.update(sql, siteCode);

			// 3.删除widget实例系统配置信息
			sql = "DELETE FROM cos_sys_profile WHERE c_page_uu_id IN (SELECT c_uu_id FROM cos_portal_page WHERE c_site_code = ?)";
			baseDao.update(sql, siteCode);
			
			// 4.删除widget实例信息
			sql = "DELETE FROM cos_widget_instance WHERE c_page_uu_id IN (SELECT c_uu_id FROM cos_portal_page WHERE c_site_code = ?)";
			baseDao.update(sql, siteCode);

			// 5.删除用户自定义主题
			sql = "DELETE FROM cos_custom_theme WHERE c_site_code = ? ";
			baseDao.update(sql, siteCode);
			
			// 6.删除个性化布局
			sql = "DELETE FROM cos_custom_layout WHERE c_page_uu_id IN (SELECT c_uu_id FROM cos_portal_page WHERE c_site_code = ?)";
			baseDao.update(sql, siteCode);

			// 7.删除页面信息
			baseDao.update("DELETE FROM cos_portal_page WHERE c_site_code = ?", siteCode);
			
			// 8.删除页面帮助及引导页信息
			baseDao.update("DELETE FROM cos_page_help_info WHERE c_site_code = ?", siteCode);
		}

		// 新增数据
		// 站点信息
		SiteDto siteDto = new SiteDto();
		siteDto.setCode(jsonObj.getString("siteCode"));
		siteDto.setName(jsonObj.getString("siteName"));
		siteDto.setTitle(jsonObj.getString("title"));
		siteDto.setLoginType(jsonObj.getString("loginType"));
		siteDto.setPublicTheme(jsonObj.getString("publicTheme"));
		siteDto.setPrivateTheme(jsonObj.getString("privateTheme"));
		siteDto.setLogo(jsonObj.getString("logo"));
		siteDto.setCopyright(jsonObj.getString("copyright"));
		siteDto.setRandomTheme(MathUtils.stringObj(jsonObj.get("randomTheme")));
		siteDto.setHolidayTheme(MathUtils.stringObj(jsonObj.get("holidayTheme")));
		siteDto.setHolidayRange(MathUtils.stringObj(jsonObj.get("holidayRange")));
		siteDto.setProfileTheme(MathUtils.stringObj(jsonObj.get("profileTheme")));
		siteDto.setOpenIm(jsonObj.has("openIm") ? jsonObj.getBoolean("openIm") : false);
		siteDto.setDomainName(jsonObj.has("domainName") ? MathUtils.stringObj(jsonObj.get("domainName")) : "");
		this.saveSite(siteDto);

		// 页面信息及页面配置信息
		addPageInfo(jsonObj, true);

		// 主题信息
		JSONArray themeArray = jsonObj.getJSONArray("themes");
		for (int i = 0; i < themeArray.length(); i++) {
			JSONObject obj = themeArray.getJSONObject(i);
			if (!obj.has("isCover") || (obj.has("isCover") && "1".equals(obj.get("isCover")))) {
				String themeCode = obj.getString("code");
				baseDao.update("DELETE FROM cos_portal_theme WHERE c_code = ? ", themeCode);

				ThemeDto themeDto = new ThemeDto();
				themeDto.setCode(themeCode);
				themeDto.setName(obj.getString("name"));
				themeDto.setProjectCode(obj.getString("projectCode"));
				themeDto.setIsOpen(obj.getString("isOpen"));
				themeDto.setIsSystem(obj.getBoolean("isSystem"));
				themeService.saveTheme(themeDto);
			}
		}

		// 布局信息
		JSONArray layoutArray = jsonObj.getJSONArray("layouts");
		for (int i = 0; i < layoutArray.length(); i++) {
			JSONObject obj = layoutArray.getJSONObject(i);
			if (!obj.has("isCover") || (obj.has("isCover") && "1".equals(obj.get("isCover")))) {
				String layoutCode = obj.getString("code");
				baseDao.update("DELETE FROM cos_portal_layout WHERE c_code = ? ", layoutCode);

				LayoutDto layoutDto = new LayoutDto();
				layoutDto.setCode(layoutCode);
				layoutDto.setName(obj.getString("name"));
				layoutDto.setProjectCode(obj.getString("projectCode"));
				layoutDto.setIsSystem(obj.getBoolean("isSystem"));
				layoutService.saveLayout(layoutDto);
			}
		}

		// 修饰器
		JSONArray decorateArray = jsonObj.getJSONArray("decorates");
		for (int i = 0; i < decorateArray.length(); i++) {
			JSONObject obj = decorateArray.getJSONObject(i);
			if (!obj.has("isCover") || (obj.has("isCover") && "1".equals(obj.get("isCover")))) {
				String decorateCode = obj.getString("code");
				baseDao.update("DELETE FROM cos_portal_decorate WHERE c_code = ? ", decorateCode);

				DecorateDto decorateDto = new DecorateDto();
				decorateDto.setCode(decorateCode);
				decorateDto.setName(obj.getString("name"));
				decorateDto.setProjectCode(obj.getString("projectCode"));
				decorateDto.setIsSystem(obj.getBoolean("isSystem"));
				decorateService.saveDecorate(decorateDto);
			}
		}

		// widget
		JSONArray widgetArray = jsonObj.getJSONArray("widgets");
		for (int i = 0; i < widgetArray.length(); i++) {
			JSONObject obj = widgetArray.getJSONObject(i);
			if (!obj.has("isCover") || (obj.has("isCover") && "1".equals(obj.get("isCover")))) {
				String widgetCode = obj.getString("code");
				baseDao.update("DELETE FROM cos_portal_widget WHERE c_code = ? ", widgetCode);

				WidgetDto widgetDto = new WidgetDto();
				widgetDto.setCode(widgetCode);
				widgetDto.setName(obj.getString("name"));
				widgetDto.setProjectCode(obj.getString("projectCode"));
				widgetDto.setIsSystem(obj.getBoolean("isSystem"));
				widgetDto.setIsNested((obj.has("isNested") ? obj.getBoolean("isNested") : false));
				widgetService.saveWidget(widgetDto);
			}
		}

		return true;
	}

	/**
	 * 添加页面及页面widget实例信息
	 * 
	 * @param jsonObj
	 * @throws JSONException
	 */
	private void addPageInfo(JSONObject jsonObj, boolean defaultUuId) throws Exception {
		
		// 新增公开页面和私有页面引导页信息
		JSONArray publicGuide = jsonObj.getJSONArray("publicGuide");
		if (!Assert.isEmpty(publicGuide) && publicGuide.length() > 0) {
			addPageGuide(publicGuide);
		}
		
		JSONArray privateGuide = jsonObj.getJSONArray("privateGuide");
		if (!Assert.isEmpty(privateGuide) && privateGuide.length() > 0) {
			addPageGuide(privateGuide);
		}

		JSONArray pageJoArrs = jsonObj.getJSONArray("pages");
		if (!Assert.isEmpty(pageJoArrs)) {
			// 1.公开页面
			JSONArray pJsonArray = (JSONArray) pageJoArrs.get(0);
			if (Assert.isNotEmpty(pJsonArray)) {
				for (int i = 0; i < pJsonArray.length(); i++) {
					JSONObject obj = pJsonArray.getJSONObject(i);
					SitePageDto saveDto = saveSitePage(obj, 1l, defaultUuId);
					
					// 页面帮助信息
					if (obj.has("pageHelp")) {
						JSONObject pageHelpJo = obj.getJSONObject("pageHelp");
						SitePageHelpDto addSitePageHelpDto = new SitePageHelpDto();
						addSitePageHelpDto.setUuId(UUID.randomUUID().toString().replace("-", ""));
						addSitePageHelpDto.setPageUuId(MathUtils.stringObj(pageHelpJo.get("pageUuId")));
						addSitePageHelpDto.setSiteCode(MathUtils.stringObj(pageHelpJo.get("siteCode")));
						addSitePageHelpDto.setType(MathUtils.stringObj(pageHelpJo.get("type")));
						addSitePageHelpDto.setPhoto(MathUtils.stringObj(pageHelpJo.get("photo")));
						addSitePageHelpDto.setContent(MathUtils.stringObj(pageHelpJo.get("content")));
						addSitePageHelpDto.setTitle(MathUtils.stringObj(pageHelpJo.get("title")));
						addSitePageHelpDto.setFiles(MathUtils.stringObj(pageHelpJo.get("files")));
						sitePageHelpService.saveSitePageHelp(addSitePageHelpDto);
					}
					
					JSONArray pWidgetInstanceJoArrs = obj.getJSONArray("pageWidgetInstances");
					if (Assert.isNotEmpty(pWidgetInstanceJoArrs)) {
						if (defaultUuId) {
							// 页面widget实例配置
							if (Assert.isNotEmpty(pWidgetInstanceJoArrs)) {
								savePageWidgetInstances(saveDto.getUuId(), pWidgetInstanceJoArrs);
							}
							
							// 页面widget实例系统配置信息
							JSONObject widgetInstanceParamsJo = obj.getJSONObject("widgetInstanceParams");
							if (Assert.isNotEmpty(widgetInstanceParamsJo) && widgetInstanceParamsJo.length() > 0) {
								saveWidgetSysParams(widgetInstanceParamsJo, saveDto.getUuId());
							}
							
						}else {
							// 嵌套widget下的组件信息
			    			JSONArray newJsonArr = new JSONArray();
			    			// 嵌套widget的参数信息
			    			JSONObject newWidgetParamJsonObj = new JSONObject();
			    			// 保存新实例化的widgetId 与 组件模版中实例Id关系
			    			JSONObject relWidgetObj = new JSONObject();
			    			
			    			widgetConfService.copyBusinessCompConf(pWidgetInstanceJoArrs, newJsonArr, relWidgetObj, newWidgetParamJsonObj, true);
			    			
			    			savePageWidgetInstances(saveDto.getUuId(), newJsonArr);
			    			
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
								profileConfEntity.setPageUuId(saveDto.getUuId());
								profileConfEntity.setJson(valObj.has("nestedPage") ? MathUtils.stringObj(valObj.get("nestedPage")) : MathUtils.stringObj(valObj));
								widgetConfList.add(profileConfEntity);
							}
			    			
							if (widgetConfList.size() > 0) {
								profileConfPersistence.save(widgetConfList);
							}
						}
					}

					traverse(obj, "1", saveDto.getId(), defaultUuId);
				}
			}

			// 2.私有页面
			JSONArray sJsonArray = (JSONArray) pageJoArrs.get(1);
			if (Assert.isNotEmpty(sJsonArray)) {
				for (int i = 0; i < sJsonArray.length(); i++) {
					JSONObject obj = sJsonArray.getJSONObject(i);
					SitePageDto saveDto = saveSitePage(obj, 2l, defaultUuId);
					
					// 页面帮助信息
					if (obj.has("pageHelp")) {
						JSONObject pageHelpJo = obj.getJSONObject("pageHelp");
						SitePageHelpDto addSitePageHelpDto = new SitePageHelpDto();
						addSitePageHelpDto.setUuId(UUID.randomUUID().toString().replace("-", ""));
						addSitePageHelpDto.setPageUuId(MathUtils.stringObj(pageHelpJo.get("pageUuId")));
						addSitePageHelpDto.setSiteCode(MathUtils.stringObj(pageHelpJo.get("siteCode")));
						addSitePageHelpDto.setType(MathUtils.stringObj(pageHelpJo.get("type")));
						addSitePageHelpDto.setPhoto(MathUtils.stringObj(pageHelpJo.get("photo")));
						addSitePageHelpDto.setContent(MathUtils.stringObj(pageHelpJo.get("content")));
						addSitePageHelpDto.setTitle(MathUtils.stringObj(pageHelpJo.get("title")));
						addSitePageHelpDto.setFiles(MathUtils.stringObj(pageHelpJo.get("files")));
						sitePageHelpService.saveSitePageHelp(addSitePageHelpDto);
					}

					// 页面widget实例配置
					JSONArray pWidgetInstanceJoArrs = obj.getJSONArray("pageWidgetInstances");
					if (Assert.isNotEmpty(pWidgetInstanceJoArrs)) {
						
						if (defaultUuId) {
							savePageWidgetInstances(saveDto.getUuId(), pWidgetInstanceJoArrs);
							
							// 页面widget实例系统配置信息
							JSONObject widgetInstanceParamsJo = obj.getJSONObject("widgetInstanceParams");
							if (Assert.isNotEmpty(widgetInstanceParamsJo) && widgetInstanceParamsJo.length() > 0) {
								saveWidgetSysParams(widgetInstanceParamsJo, saveDto.getUuId());
							}
						}else {
							// 嵌套widget下的组件信息
			    			JSONArray newJsonArr = new JSONArray();
			    			// 嵌套widget的参数信息
			    			JSONObject newWidgetParamJsonObj = new JSONObject();
			    			// 保存新实例化的widgetId 与 组件模版中实例Id关系
			    			JSONObject relWidgetObj = new JSONObject();
			    			
			    			widgetConfService.copyBusinessCompConf(pWidgetInstanceJoArrs, newJsonArr, relWidgetObj, newWidgetParamJsonObj, true);
			    			
			    			savePageWidgetInstances(saveDto.getUuId(), newJsonArr);
			    			
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
								profileConfEntity.setPageUuId(saveDto.getUuId());
								profileConfEntity.setJson(valObj.has("nestedPage") ? MathUtils.stringObj(valObj.get("nestedPage")) : MathUtils.stringObj(valObj));
								widgetConfList.add(profileConfEntity);
							}
			    			
							if (widgetConfList.size() > 0) {
								profileConfPersistence.save(widgetConfList);
							}
						}
					}
					traverse(obj, "0", saveDto.getId(), defaultUuId);
				}
			}
		}
	}

	/**
	 * 新增页面引导页信息
	 * @param guideJsonArr
	 * @throws JSONException 
	 */
	private void addPageGuide(JSONArray guideJsonArr) throws JSONException {
		SitePageHelpDto addSitePageHelpDto = null;
		for (int i = 0; i < guideJsonArr.length(); i++) {
			JSONObject jo = guideJsonArr.getJSONObject(i);
			addSitePageHelpDto = new SitePageHelpDto();
			addSitePageHelpDto.setUuId(UUID.randomUUID().toString().replace("-", ""));
			addSitePageHelpDto.setPageUuId(MathUtils.stringObj(jo.get("pageUuId")));
			addSitePageHelpDto.setSiteCode(MathUtils.stringObj(jo.get("siteCode")));
			addSitePageHelpDto.setType(MathUtils.stringObj(jo.get("type")));
			addSitePageHelpDto.setPhoto(MathUtils.stringObj(jo.get("photo")));
			addSitePageHelpDto.setContent(MathUtils.stringObj(jo.get("content")));
			addSitePageHelpDto.setTitle(MathUtils.stringObj(jo.get("title")));
			addSitePageHelpDto.setFiles(MathUtils.stringObj(jo.get("files")));
			sitePageHelpService.saveSitePageHelp(addSitePageHelpDto);
		}
	}

	/**
	 * 保存widget 系统配置信息
	 * 
	 * @param widgetInstanceParamsJo
	 * @throws JSONException
	 */
	private void saveWidgetSysParams(JSONObject widgetInstanceParamsJo, String pageUuId) throws Exception {
		@SuppressWarnings("unchecked")
		Iterator<String> it = widgetInstanceParamsJo.keys();
		while (it.hasNext()) {
			String widgetUuId = it.next();
			JSONObject vJo = widgetInstanceParamsJo.getJSONObject(widgetUuId);
			ProfileConfDto profileConfDto = new ProfileConfDto();
			profileConfDto.setWidgetUuId(widgetUuId);
			profileConfDto.setJson(vJo.toString());
			profileConfDto.setPageUuId(pageUuId);
			profileConfService.saveProfileConf(profileConfDto, null, pageUuId);
		}
	}

	/**
	 * 保存页面widget实例配置信息
	 * 
	 * @param uuId
	 * @param pWidgetInstanceJoArrs
	 */
	private void savePageWidgetInstances(String pageUuId, JSONArray pWidgetInstanceJoArrs) {
		WidgetConfDto widgetConfDto = widgetConfService.getWidgetConfInfo(pageUuId);
		List<String> dIds = new ArrayList<String>();
		if (!Assert.isEmpty(widgetConfDto) && !Assert.isEmpty(widgetConfDto.getId())) {

			String oldWidgetIds = widgetConfDto.getWidgetIds();
			if (!Assert.isEmpty(oldWidgetIds)) {
				String[] oldArr = oldWidgetIds.split(",");
				for (String oldWidgetId : oldArr) {
					dIds.add(oldWidgetId);
				}
			}

			widgetConfDto.setJson(pWidgetInstanceJoArrs.toString());
		} else {
			widgetConfDto = new WidgetConfDto();
			widgetConfDto.setPageUuId(pageUuId);
			widgetConfDto.setJson(pWidgetInstanceJoArrs.toString());
		}
		widgetConfService.saveWidgetConf(widgetConfDto, dIds);
	}

	/**
	 * 保存站点页面信息
	 * 
	 * @param obj
	 * @return
	 * @throws JSONException
	 */
	private SitePageDto saveSitePage(JSONObject obj, long parentId, boolean defaultUuId) throws JSONException {
		SitePageDto sitePageDto = new SitePageDto();
		sitePageDto.setUuId(defaultUuId ? obj.getString("uuId") : UUID.randomUUID().toString().replace("-", ""));
		sitePageDto.setName(obj.getString("name"));
		sitePageDto.setCascade(obj.getString("cascade"));
		sitePageDto.setType(obj.getString("type"));
		sitePageDto.setOpenSelf(obj.getString("openSelf"));
		sitePageDto.setNavHidden(obj.getString("navHidden"));
		sitePageDto.setAllowLayout(obj.getString("allowLayout"));
		sitePageDto.setAllowWidget(obj.getString("allowWidget"));
		sitePageDto.setIsMenu(obj.getBoolean("isMenu"));
		sitePageDto.setIsFolder(obj.getBoolean("isFolder"));
		sitePageDto.setThemeStyle(obj.getString("themeStyle"));
		sitePageDto.setLayoutCode(obj.getString("layoutCode"));
		sitePageDto.setSiteCode(obj.getString("siteCode"));
		sitePageDto.setParentId(parentId);
		sitePageDto.setPath(obj.getString("path"));
		sitePageDto.setSortNo(obj.getInt("sortNo"));
		sitePageDto.setLinkUrl(obj.has("linkUrl") ? obj.getString("linkUrl") : null);
		sitePageDto.setIsLink(obj.has("isLink") ? obj.getBoolean("isLink") : false);

		SitePageDto saveDto = sitePageService.saveSitePage(sitePageDto);

		return saveDto;
	}

	/**
	 * 页面tree遍历
	 * 
	 * @param obj
	 * @param string
	 * @throws Exception 
	 */
	private void traverse(JSONObject obj, String type, Long parentId, boolean defaultUuId) throws Exception {

		JSONArray childs = getChilds(obj);

		if (Assert.isNotEmpty(childs)) {

			for (int j = 0; j < childs.length(); j++) {
				JSONObject json = childs.getJSONObject(j);
				SitePageDto saveDto = saveSitePage(json, parentId, defaultUuId);

				// 页面widget实例配置
				JSONArray pWidgetInstanceJoArrs = json.getJSONArray("pageWidgetInstances");
				if (Assert.isNotEmpty(pWidgetInstanceJoArrs)) {
					savePageWidgetInstances(saveDto.getUuId(), pWidgetInstanceJoArrs);
				}
				
				// 页面widget实例系统配置信息
				JSONObject widgetInstanceParamsJo = json.getJSONObject("widgetInstanceParams");
				if (Assert.isNotEmpty(widgetInstanceParamsJo) && widgetInstanceParamsJo.length() > 0) {
					saveWidgetSysParams(widgetInstanceParamsJo, saveDto.getUuId());
				}

				traverse(json, type, saveDto.getId(), defaultUuId);
			}
		}
	}

	/**
	 * 获取页面子集
	 * 
	 * @param obj
	 * @return
	 * @throws JSONException
	 */
	private JSONArray getChilds(JSONObject obj) throws JSONException {
		if (obj.has("children")) {
			return obj.getJSONArray("children");
		}
		return null;
	}

	@Override
	@Transactional
	public void copySite(JSONObject jo, Long personnelId) throws Exception{
		// 新增数据
		// 站点信息
		SiteDto siteDto = new SiteDto();
		siteDto.setCode(jo.getString("siteCode"));
		siteDto.setName(jo.getString("siteName"));
		siteDto.setTitle(jo.getString("title"));
		siteDto.setLoginType(jo.getString("loginType"));
		siteDto.setPublicTheme(jo.getString("publicTheme"));
		siteDto.setPrivateTheme(jo.getString("privateTheme"));
		siteDto.setLogo(jo.has("logo") ? jo.getString("logo") : null);
		siteDto.setCopyright(jo.has("copyright") ? jo.getString("copyright"): null);
		siteDto.setRandomTheme(MathUtils.stringObj(jo.get("randomTheme")));
		siteDto.setHolidayTheme(MathUtils.stringObj(jo.get("holidayTheme")));
		siteDto.setHolidayRange(MathUtils.stringObj(jo.get("holidayRange")));
		siteDto.setProfileTheme(MathUtils.stringObj(jo.get("profileTheme")));
		siteDto.setOpenIm(jo.has("openIm") ? jo.getBoolean("openIm") : false);
		siteDto.setDomainName(jo.has("domainName") ? MathUtils.stringObj(jo.get("domainName")) : "");
		siteDto.setCreateBy(personnelId);
		this.saveSite(siteDto);
		// 页面信息及页面配置信息
		addPageInfo(jo, false);
	}

	@Override
	public List<ThemeEntity> getProfileThemeList(String siteCode, String isOpen) {
		List<ThemeEntity> profileThemeList = new ArrayList<ThemeEntity>();
		SiteEntity entity = sitePersistence.findByCode(siteCode);
		if (!Assert.isEmpty(entity)) {
			String profileTheme = entity.getProfileTheme();
			if (!Assert.isEmpty(profileTheme)) {
				String[] themeCodeArr = profileTheme.split(",");
				for (String themeCode : themeCodeArr) {
					ThemeEntity themeEntity = null;
					if (Assert.isEmpty(isOpen)) {
						themeEntity = themePersistence.findByCode(themeCode);
					}else {
						themeEntity = themePersistence.findByCodeAndOpen(themeCode, isOpen);
					}
					profileThemeList.add(themeEntity);
				}
			}
		}
		
		return profileThemeList;
	}

	@Override
	public Boolean isExitSiteDomain(String domain, String siteCode) {
		// 切换到主库
		DynamicDataSourceContextHolder.clearDataSource();
		List<Map<String, Object>> resList = baseDao.query("SELECT * FROM cos_custormer_site_domain WHERE c_domain = ? and c_site_code != ?", domain,siteCode);
		if (!Assert.isEmpty(resList) && resList.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
}
