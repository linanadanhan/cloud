package com.gsoft.portal.webview.page.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.component.pagetemp.dto.PageTemplateConfDto;
import com.gsoft.portal.component.pagetemp.dto.PageTemplateDto;
import com.gsoft.portal.component.pagetemp.service.PageTemplateService;
import com.gsoft.portal.webview.page.dto.DiySitePageDto;
import com.gsoft.portal.webview.page.entity.DiySitePageEntity;
import com.gsoft.portal.webview.page.persistence.DiySitePagePersistence;
import com.gsoft.portal.webview.page.service.DiySitePageService;
import com.gsoft.portal.webview.page.service.SitePageConfService;
import com.gsoft.portal.webview.site.entity.SiteEntity;
import com.gsoft.portal.webview.site.persistence.SitePersistence;
import com.gsoft.portal.webview.widgetconf.dto.WidgetConfDto;
import com.gsoft.portal.webview.widgetconf.entity.ProfileConfEntity;
import com.gsoft.portal.webview.widgetconf.persistence.ProfileConfPersistence;
import com.gsoft.portal.webview.widgetconf.service.WidgetConfService;

/**
 * 个性化页面管理Service实现类
 * 
 * @author SN
 *
 */
@Service
public class DiySitePageServiceImpl implements DiySitePageService {

	@Resource
	DiySitePagePersistence diySitePagePersistence;
	
	@Resource
	SitePersistence sitePersistence;
	
	@Resource
	WidgetConfService widgetConfService;
	
	@Resource
	SitePageConfService sitePageConfService;
	
	@Resource
	ProfileConfPersistence profileConfPersistence;
	
	@Resource
	BaseDao baseDao;
	
	@Resource
	PageTemplateService pageTemplateService;

	@Override
	public List<DiySitePageDto> getDiyPageTree(String siteCode, Long personnelId) {
		List<DiySitePageEntity> entityList = diySitePagePersistence.getDiySitePageTree(siteCode, personnelId);
		return BeanUtils.convert(entityList, DiySitePageDto.class);
	}

	@Override
	public boolean isExitPagePath(Long personnelId, Long id, String path, String cascade, String siteCode) {
		DiySitePageEntity entity = null;
		if (Assert.isEmpty(id)) {
			entity = diySitePagePersistence.findPageByPath(path, cascade, siteCode, personnelId);
		} else {
			entity = diySitePagePersistence.findPageByPath(path, id, cascade, siteCode, personnelId);
		}
		if (entity != null) {
			return true;
		}
		return false;
	}

	@Override
	public DiySitePageDto saveDiySitePage(DiySitePageDto diySitePageDto) throws JSONException {
		// 若页面主题为空则默认为站点主题
		if (Assert.isEmpty(diySitePageDto.getThemeCode())) {
			SiteEntity siteEntity = sitePersistence.findByCode(diySitePageDto.getSiteCode());
			diySitePageDto.setThemeCode(siteEntity.getPublicTheme());
		}
		DiySitePageEntity reEntity = null;

		// 修改时布局切换后，保存成功后页面实例中的配置需同步移动
		if (Assert.isNotEmpty(diySitePageDto.getId())) {
			// 根据ID查询原站点页面信息
			DiySitePageEntity oEntity = diySitePagePersistence.findOne(diySitePageDto.getId());
			String oLayoutCode = oEntity.getLayoutCode();

			DiySitePageEntity entity = BeanUtils.convert(diySitePageDto, DiySitePageEntity.class);
			reEntity = diySitePagePersistence.save(entity);

			if (!diySitePageDto.getLayoutCode().equals(oLayoutCode)) {
				try {
					widgetConfService.changeWidgetInstance(reEntity.getUuId(), oLayoutCode,
							diySitePageDto.getLayoutCode());
				} catch (Exception e) {
					// 实例移动失败，还原原布局
					entity.setLayoutCode(oEntity.getLayoutCode());
					reEntity = diySitePagePersistence.save(entity);
				}
			}
		} else {
			if (!diySitePageDto.getIsSystem()) {
				diySitePageDto.setUuId(UUID.randomUUID().toString().replace("-", ""));
			}
			
			// 页面有选择页面模式时，增加页面实例及配置
			if (!Assert.isEmpty(diySitePageDto.getPageTempCode())) {
				// 获取页面模版及模版配置信息
				PageTemplateDto pageTemplateDto = pageTemplateService.getPageTempInfo(diySitePageDto.getPageTempCode());
				PageTemplateConfDto pageTemplateConfDto = pageTemplateService.getPageTempConfInfo(diySitePageDto.getPageTempCode());
				
				if (!Assert.isEmpty(pageTemplateConfDto) && !Assert.isEmpty(pageTemplateConfDto.getJson())) {
					
					JSONArray pageTmpConfJoArr = new JSONArray(pageTemplateConfDto.getJson());
					
					// 嵌套widget下的组件信息
					JSONArray newJsonArr = new JSONArray();
					// 嵌套widget的参数信息
					JSONObject newWidgetParamJsonObj = new JSONObject();
					// 保存新实例化的widgetId 与 组件模版中实例Id关系
					JSONObject relWidgetObj = new JSONObject();
					
					widgetConfService.copyBusinessCompConf(pageTmpConfJoArr, newJsonArr, relWidgetObj, newWidgetParamJsonObj, true);
					
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
						profileConfEntity.setPageUuId(diySitePageDto.getUuId());
						profileConfEntity.setJson(valObj.has("nestedPage") ? MathUtils.stringObj(valObj.get("nestedPage")) : MathUtils.stringObj(valObj));
						widgetConfList.add(profileConfEntity);
					}

					// 页面widget实例配置
					if (Assert.isNotEmpty(newJsonArr) && newJsonArr.length() > 0) {
						savePageWidgetInstances(diySitePageDto.getUuId(), newJsonArr);
					}

					// 页面widget实例系统配置信息
					if (Assert.isNotEmpty(widgetConfList) && widgetConfList.size() > 0) {
						profileConfPersistence.save(widgetConfList);
					}
				}
				diySitePageDto.setLayoutCode(pageTemplateDto.getLayoutCode());
			}
			
			DiySitePageEntity entity = BeanUtils.convert(diySitePageDto, DiySitePageEntity.class);
			reEntity = diySitePagePersistence.save(entity);
			if (diySitePageDto.getParentId() != 0) {
				// 更新父集节点为文件夹
				baseDao.update("update cos_portal_diy_page set c_is_folder = 1 where c_id = ? ", diySitePageDto.getParentId());
			}
		}
		return BeanUtils.convert(reEntity, DiySitePageDto.class);
	}

	@Override
	public DiySitePageDto getDiySitePageInfoById(Long id) {
		DiySitePageEntity entity = diySitePagePersistence.findOne(id);
		return BeanUtils.convert(entity, DiySitePageDto.class);
	}

	@Override
	public void delDiySitePage(List<Long> ids, Long parentId, Long personnelId) {
		baseDao.delete("cos_portal_diy_page", "c_id", ids.toArray());
		// 父集节点下是否还有子集节点
		List<Map<String, Object>> childList = baseDao.query("select * from cos_portal_diy_page where c_parent_id = ? and c_user_id = ?",
				parentId, personnelId);
		if (Assert.isEmpty(childList)) {
			baseDao.update("update cos_portal_diy_page set c_is_folder = 0 where c_id = ? ", parentId);
		}
	}

	@Override
	public ReturnDto saveDiySitePageTree(Long personnelId, String siteCode, String draggingNode, String dataTree,
			Long parentId, boolean isExist) {
		try {
			JSONObject draggingJo = new JSONObject(draggingNode);
			// 根据ID查询父页面级联信息
			String cascade = "";
			if (parentId == 2) {
				cascade = "/";
			} else {
				DiySitePageEntity entity = null;
				if (isExist) {
					entity = diySitePagePersistence.getOne(parentId);
				} else {
					entity = diySitePagePersistence.getSitePageInfoByPageId(parentId, personnelId);
					parentId = entity.getId();
				}
				cascade = entity.getCascade() + entity.getPath() + "/";
			}

			// 组装需要更新的数据，批量更新
			List<Map<String, Object>> needList = new ArrayList<Map<String, Object>>();
			long id = MathUtils.numObj2Long(draggingJo.get("id"));
			if (!isExist) {
				DiySitePageEntity entity = diySitePagePersistence.getSitePageInfoByPageId(id, personnelId);
				id = entity.getId();
			}
			JSONObject attrJo = draggingJo.getJSONObject("attributes");
			String path = attrJo.getString("path");
			String pageUuId = attrJo.getString("uuId");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("C_ID", id);
			map.put("C_PARENT_ID", parentId);
			map.put("C_UPDATE_BY", personnelId);
			map.put("C_UPDATE_TIME", new Date());
			map.put("C_UU_ID", pageUuId);
			
			//判断是否存在，若存在则重新生成新的path，避免每次都要做校验
			boolean isExitPath = this.isExitPagePath(personnelId, id, path, cascade, siteCode);
			if (isExitPath) {
				path = MathUtils.stringObj(System.nanoTime());
			}
			map.put("C_CASCADE", cascade);
			map.put("C_PATH", path);
			map.put("C_USER_ID", personnelId);
			
			JSONArray childArr = draggingJo.isNull("children") ? new JSONArray() : draggingJo.getJSONArray("children");
			if (childArr.length() > 0) {
				map.put("C_IS_FOLDER", true);
            	cascade = cascade + path + "/";
            	saveDragTreeData(childArr, id, personnelId, cascade, needList, isExist);
            }else {
            	map.put("C_IS_FOLDER", false);
            }
			needList.add(map);
			
			baseDao.modify("cos_portal_diy_page", "C_ID", needList);
			
			//保存成功后重新自动更新排序号
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			JSONArray treeArr = new JSONArray(dataTree);
			JSONObject tmpJo = treeArr.getJSONObject(0);
			Long tmpParentId = MathUtils.numObj2Long(tmpJo.get("id"));
			if (!isExist) {
				DiySitePageEntity entity = diySitePagePersistence.getSitePageInfoByPageId(tmpParentId, personnelId);
				tmpParentId = entity.getId();
			}
			updateTreeSortNo(treeArr, tmpParentId, dataList, isExist, personnelId);
			baseDao.modify("cos_portal_diy_page", "C_ID", dataList);
			return new ReturnDto("移动成功！");

		} catch (Exception e) {
			return new ReturnDto(500, e.getMessage());
		}
	}
	
	/**
	 * 更新树顺序号
	 * @param treeArr
	 * @param tmpParentId
	 * @param dataList
	 * @throws JSONException 
	 */
	private void updateTreeSortNo(JSONArray treeArr, Long tmpParentId, List<Map<String, Object>> dataList, boolean isExist, long personnelId) throws JSONException {
		int sortNo = 1;
		for (int i = 0;i<treeArr.length();i++) {
    		JSONObject tmpJo = treeArr.getJSONObject(i);
    		long id = MathUtils.numObj2Long(tmpJo.get("id"));
    		
			if (!isExist) {
				DiySitePageEntity entity = diySitePagePersistence.getSitePageInfoByPageId(id, personnelId);
				id = entity.getId();
			}
    		JSONArray childArr = tmpJo.isNull("children") ? new JSONArray() : tmpJo.getJSONArray("children");
    		
    		if (id != 1 && id != 2) {
        		Map<String, Object> map = new HashMap<String, Object>();
        		map.put("C_ID", id);
        		map.put("C_SORT_NO", sortNo);
        		
        		if (childArr.length() > 0) {
        			map.put("C_IS_FOLDER", true);
        		}else {
        			map.put("C_IS_FOLDER", false);
        		}
        		dataList.add(map);
    		}
    		
    		if (childArr.length() > 0) {
    			updateTreeSortNo(childArr, id, dataList, isExist, personnelId);
    		}
    		sortNo++;
    	}
	}
	
	/**
	 * 保存页面树信息
	 * @param joArr
	 * @param parentId
	 * @param personnelId
	 * @param cascade
	 * @param needList
	 * @throws JSONException
	 */
	private void saveDragTreeData(JSONArray joArr, Long parentId, Long personnelId, String cascade,
			List<Map<String, Object>> needList, boolean isExist) throws JSONException {
		for (int i = 0; i < joArr.length(); i++) {
			JSONObject tmpJo = joArr.getJSONObject(i);
			long id = MathUtils.numObj2Long(tmpJo.get("id"));
			if (!isExist) {
				DiySitePageEntity entity = diySitePagePersistence.getSitePageInfoByPageId(id, personnelId);
				id = entity.getId();
			}
			JSONObject attrJo = tmpJo.getJSONObject("attributes");
			String path = attrJo.getString("path");
			String siteCode = attrJo.getString("siteCode");
			String pageUuId = attrJo.getString("uuId");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("C_ID", id);
			map.put("C_PARENT_ID", parentId);
			map.put("C_UPDATE_BY", personnelId);
			map.put("C_UPDATE_TIME", new Date());
			map.put("C_UU_ID", pageUuId);
			
			//重新生成新的path，避免每次都要做校验
			boolean isExitPath = this.isExitPagePath(personnelId, id, path, cascade, siteCode);
			if (isExitPath) {
				path = MathUtils.stringObj(System.nanoTime());
			}
			
			map.put("C_CASCADE", cascade);
			map.put("C_PATH", path);
			map.put("C_USER_ID", personnelId);

			JSONArray childArr = tmpJo.isNull("children") ? new JSONArray() : tmpJo.getJSONArray("children");
			if (childArr.length() > 0) {
				map.put("C_IS_FOLDER", true);
				cascade = cascade + path + "/";
				saveDragTreeData(childArr, id, personnelId, cascade, needList, isExist);
			} else {
				map.put("C_IS_FOLDER", false);
			}
			needList.add(map);
		}
	}
	
	/**
	 * 保存页面树信息
	 * @param joArr
	 * @param parentId
	 * @param personnelId
	 * @param cascade
	 * @param needList
	 * @throws JSONException
	 */
	private void saveTreeData(JSONArray joArr, Long parentId, Long personnelId, String cascade,
			List<Map<String, Object>> needList, List<Long> delIdList, boolean isExist) throws JSONException {
		for (int i = 0; i < joArr.length(); i++) {
			JSONObject tmpJo = joArr.getJSONObject(i);
			long id = MathUtils.numObj2Long(tmpJo.get("id"));
			DiySitePageEntity entity = diySitePagePersistence.getSitePageInfoByPageId(id, personnelId);
			if (!Assert.isEmpty(entity)) {
				id = entity.getId();
			}
			JSONObject attrJo = tmpJo.getJSONObject("attributes");
			String path = attrJo.getString("path");
			String siteCode = attrJo.getString("siteCode");
			String pageUuId = attrJo.getString("uuId");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("C_PAGE_ID", MathUtils.numObj2Long(tmpJo.get("id")));
			map.put("C_PARENT_ID", parentId);
			map.put("C_UPDATE_BY", personnelId);
			map.put("C_UPDATE_TIME", new Date());
			map.put("C_UU_ID", pageUuId);
			
			String name = tmpJo.getString("text");
			String layoutCode = attrJo.getString("layoutCode");
			String openSelf = attrJo.getString("openSelf");
			String navHidden = attrJo.getString("navHidden");
			String allowWidget = attrJo.getString("allowWidget");
			String allowLayout = attrJo.getString("allowLayout");
			String status = attrJo.getString("status");
			boolean isMenu = attrJo.has("isMenu") ? attrJo.getBoolean("isMenu") : false;
			boolean isFolder = attrJo.has("isFolder") ? attrJo.getBoolean("isFolder") : false;
			String themeStyle = attrJo.getString("themeStyle");
			int sortNo = attrJo.getInt("sortNo");
			String linkUrl = attrJo.getString("linkUrl");
			boolean isLink = attrJo.has("isLink") ? attrJo.getBoolean("isLink") : false;
			
			map.put("C_NAME", name);
			map.put("C_SITE_CODE", siteCode);
			map.put("C_LAYOUT_CODE", layoutCode);
			map.put("C_OPEN_SELF", openSelf);
			map.put("C_NAV_HIDDEN", navHidden);
			map.put("C_ALLOW_WIDGET", allowWidget);
			map.put("C_ALLOW_LAYOUT", allowLayout);
			map.put("C_STATUS", status);
			map.put("C_IS_MENU", isMenu);
			map.put("C_IS_FOLDER", isFolder);
			map.put("C_THEME_STYLE", themeStyle);
			map.put("C_SORT_NO", sortNo);
			map.put("C_LINK_URL", linkUrl);
			map.put("C_IS_LINK", isLink);
			map.put("C_IS_SYSTEM", true);
			
			if (!Assert.isEmpty(delIdList)) {
				// 根据pageUuId 查询该页面是否已存在及其子集
				List<Map<String, Object>> existList = this.getDiySitePageInfoByPageUuId(pageUuId);
				if (!Assert.isEmpty(existList) && existList.size() > 0) {
					for (Map<String, Object> idMap : existList) {
						delIdList.add(MathUtils.numObj2Long(idMap.get("C_ID")));
					}
				}
			}
			
			//重新生成新的path，避免每次都要做校验
			boolean isExitPath = this.isExitPagePath(personnelId, id, path, cascade, siteCode);
			if (isExitPath) {
				path = MathUtils.stringObj(System.nanoTime());
			}
			
			map.put("C_CASCADE", cascade);
			map.put("C_PATH", path);
			map.put("C_USER_ID", personnelId);

			JSONArray childArr = tmpJo.isNull("children") ? new JSONArray() : tmpJo.getJSONArray("children");
			if (childArr.length() > 0) {
				map.put("C_IS_FOLDER", true);
				cascade = cascade + path + "/";
				saveTreeData(childArr, id, personnelId, cascade, needList, delIdList, isExist);
			} else {
				map.put("C_IS_FOLDER", false);
			}
			needList.add(map);
		}
	}

	@Override
	public void resetDefSitePage(String siteCode, Long personnelId) {
		baseDao.update("delete from cos_portal_diy_page where c_user_id = ? and c_site_code = ?", personnelId, siteCode);
	}

	@Override
	public void batchSaveDiySitePage(List<Map<String, Object>> itemList) {
		baseDao.insert("cos_portal_diy_page", "C_ID", itemList);
	}

	@Override
	public DiySitePageDto getDiySitePageInfoByPath(String path, String cascade, String siteCode, Long personnelId) {
		DiySitePageEntity entity = diySitePagePersistence.findPageByPath(path, cascade, siteCode, personnelId);
		return BeanUtils.convert(entity, DiySitePageDto.class);
	}

	@Override
	@Transactional
	public ReturnDto copyDiyPage(DiySitePageDto diySitePageDto) {
		
		try {
			// 页面widgets配置实例信息组织
			JSONArray pageWidgetInstanceJoArr = new JSONArray();
			String widgetInstanceJson = widgetConfService.getWidgetJson(diySitePageDto.getUuId());
			if (!Assert.isEmpty(widgetInstanceJson)) {
				pageWidgetInstanceJoArr = new JSONArray(widgetInstanceJson);
			}
			
			// 保存页面信息
			diySitePageDto.setId(null);
			diySitePageDto.setUuId(UUID.randomUUID().toString().replace("-", ""));
			diySitePageDto.setPath(MathUtils.stringObj(System.nanoTime()));
			DiySitePageDto saveDto = this.saveDiySitePage(diySitePageDto);
			
			// 嵌套widget下的组件信息
			JSONArray newJsonArr = new JSONArray();
			// 嵌套widget的参数信息
			JSONObject newWidgetParamJsonObj = new JSONObject();
			// 保存新实例化的widgetId 与 组件模版中实例Id关系
			JSONObject relWidgetObj = new JSONObject();
			
			widgetConfService.copyBusinessCompConf(pageWidgetInstanceJoArr, newJsonArr, relWidgetObj, newWidgetParamJsonObj, true);
			
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

			// 页面widget实例配置
			if (Assert.isNotEmpty(newJsonArr) && newJsonArr.length() > 0) {
				savePageWidgetInstances(saveDto.getUuId(), newJsonArr);
			}

			// 页面widget实例系统配置信息
			if (Assert.isNotEmpty(widgetConfList) && widgetConfList.size() > 0) {
				profileConfPersistence.save(widgetConfList);
			}
			
			return new ReturnDto("复制成功");
		}catch(Exception e) {
			return new ReturnDto(500, e.getMessage());
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

	@Override
	@Transactional
	public ReturnDto addSysSitePage(Long personnelId, String siteCode, String currentNode, boolean isExist) {
		try {
			JSONObject draggingJo = new JSONObject(currentNode);
			String cascade = "/";

			// 组装需要更新的数据，批量更新
			List<Map<String, Object>> needList = new ArrayList<Map<String, Object>>();
			List<Long> delList = new ArrayList<Long>();
			
			long id = MathUtils.numObj2Long(draggingJo.get("id"));
			DiySitePageEntity entity = diySitePagePersistence.getSitePageInfoByPageId(id, personnelId);
			if (!Assert.isEmpty(entity)) {
				id = entity.getId();
			}
			JSONObject attrJo = draggingJo.getJSONObject("attributes");
			String path = attrJo.getString("path");
			String pageUuId = attrJo.getString("uuId");
			String name = draggingJo.getString("text");
			String layoutCode = attrJo.getString("layoutCode");
			String openSelf = attrJo.getString("openSelf");
			String navHidden = attrJo.getString("navHidden");
			String allowWidget = attrJo.getString("allowWidget");
			String allowLayout = attrJo.getString("allowLayout");
			String status = attrJo.getString("status");
			boolean isMenu = attrJo.has("isMenu") ? attrJo.getBoolean("isMenu") : false;
			boolean isFolder = attrJo.has("isFolder") ? attrJo.getBoolean("isFolder") : false;
			String themeStyle = attrJo.getString("themeStyle");
			int sortNo = attrJo.getInt("sortNo");
			String linkUrl = attrJo.getString("linkUrl");
			boolean isLink = attrJo.has("isLink") ? attrJo.getBoolean("isLink") : false;
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("C_PAGE_ID", MathUtils.numObj2Long(draggingJo.get("id")));
			map.put("C_PARENT_ID", 2);
			map.put("C_UPDATE_BY", personnelId);
			map.put("C_UPDATE_TIME", new Date());
			map.put("C_UU_ID", pageUuId);
			map.put("C_NAME", name);
			map.put("C_SITE_CODE", siteCode);
			map.put("C_LAYOUT_CODE", layoutCode);
			map.put("C_OPEN_SELF", openSelf);
			map.put("C_NAV_HIDDEN", navHidden);
			map.put("C_ALLOW_WIDGET", allowWidget);
			map.put("C_ALLOW_LAYOUT", allowLayout);
			map.put("C_STATUS", status);
			map.put("C_IS_MENU", isMenu);
			map.put("C_IS_FOLDER", isFolder);
			map.put("C_THEME_STYLE", themeStyle);
			map.put("C_SORT_NO", sortNo);
			map.put("C_LINK_URL", linkUrl);
			map.put("C_IS_LINK", isLink);
			map.put("C_IS_SYSTEM", true);
			
			// 根据pageUuId 查询该页面是否已存在及其子集
			List<Map<String, Object>> existList = this.getDiySitePageInfoByPageUuId(pageUuId);
			if (!Assert.isEmpty(existList) && existList.size() > 0) {
				for (Map<String, Object> idMap : existList) {
					delList.add(MathUtils.numObj2Long(idMap.get("C_ID")));
				}
			}
			
			//判断是否存在，若存在则重新生成新的path，避免每次都要做校验
			boolean isExitPath = this.isExitPagePath(personnelId, id, path, cascade, siteCode);
			if (isExitPath) {
				path = MathUtils.stringObj(System.nanoTime());
			}
			map.put("C_CASCADE", cascade);
			map.put("C_PATH", path);
			map.put("C_USER_ID", personnelId);
			JSONArray childArr = draggingJo.isNull("children") ? new JSONArray() : draggingJo.getJSONArray("children");
			
            if (childArr.length() > 0) {
            	map.put("C_IS_FOLDER", true);
            	cascade = cascade + path + "/";
				saveTreeData(childArr, id, personnelId, cascade, needList, delList, isExist);
            } else {
            	map.put("C_IS_FOLDER", false);
            }
            needList.add(map);
            
            if (delList.size() > 0) {
            	baseDao.delete("cos_portal_diy_page", "C_ID", delList.toArray());
            }
			baseDao.insert("cos_portal_diy_page", "C_ID", needList);
			return new ReturnDto("添加成功！");

		} catch (Exception e) {
			return new ReturnDto(500, e.getMessage());
		}
	}

	/**
	 * 根据pageUuId 获取对应页面及子页面
	 * @param pageUuId
	 * @return
	 */
	private List<Map<String, Object>> getDiySitePageInfoByPageUuId(String pageUuId) {
		
		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		sb.append("SELECT c_id FROM cos_portal_diy_page WHERE c_uu_id = ${pageUuId} ");
		sb.append("UNION all (");
		sb.append("SELECT c_id FROM cos_portal_diy_page WHERE c_cascade LIKE (");
		sb.append("SELECT CONCAT(c_cascade,c_path,'/') FROM cos_portal_diy_page WHERE c_uu_id = ${pageUuId}))");
		params.put("pageUuId", pageUuId);
		
		return baseDao.query(sb.toString(), params);
	}

	@Override
	public DiySitePageDto getDiySitePageInfoByUuId(long personnelId, String pageUuId) {
		DiySitePageEntity entity = diySitePagePersistence.getDiySitePageInfoByUuId(personnelId, pageUuId);
		return BeanUtils.convert(entity, DiySitePageDto.class);
	}

	@Override
	public void changeLayout(String pageUuId, String layoutCode, long personnelId) {
		String sql = "UPDATE cos_portal_diy_page SET c_layout_code = ? WHERE c_uu_id = ? and c_user_id = ?";
		baseDao.update(sql, layoutCode, pageUuId, personnelId);
	}
}
