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
import com.gsoft.portal.common.constans.ResultConstant;
import com.gsoft.portal.component.pagetemp.dto.PageTemplateConfDto;
import com.gsoft.portal.component.pagetemp.dto.PageTemplateDto;
import com.gsoft.portal.component.pagetemp.service.PageTemplateService;
import com.gsoft.portal.webview.page.dto.DiySitePageDto;
import com.gsoft.portal.webview.page.dto.SitePageDto;
import com.gsoft.portal.webview.page.entity.DiySitePageEntity;
import com.gsoft.portal.webview.page.entity.SitePageEntity;
import com.gsoft.portal.webview.page.persistence.DiySitePagePersistence;
import com.gsoft.portal.webview.page.persistence.SitePagePersistence;
import com.gsoft.portal.webview.page.service.DiySitePageService;
import com.gsoft.portal.webview.page.service.SitePageConfService;
import com.gsoft.portal.webview.page.service.SitePageService;
import com.gsoft.portal.webview.site.entity.SiteEntity;
import com.gsoft.portal.webview.site.persistence.SitePersistence;
import com.gsoft.portal.webview.widgetconf.dto.WidgetConfDto;
import com.gsoft.portal.webview.widgetconf.entity.ProfileConfEntity;
import com.gsoft.portal.webview.widgetconf.persistence.ProfileConfPersistence;
import com.gsoft.portal.webview.widgetconf.service.ProfileConfService;
import com.gsoft.portal.webview.widgetconf.service.WidgetConfService;

/**
 * 页面管理Service实现类
 * 
 * @author SN
 *
 */
@Service
public class SitePageServiceImpl implements SitePageService {

	@Resource
	SitePagePersistence sitePagePersistence;
	
	@Resource
	DiySitePagePersistence diySitePagePersistence;

	@Resource
	SitePersistence sitePersistence;
	
	@Resource
	ProfileConfPersistence profileConfPersistence;

	@Resource
	BaseDao baseDao;

	@Resource
	WidgetConfService widgetConfService;
	
	@Resource
	SitePageConfService sitePageConfService;
	
	@Resource
	ProfileConfService profileConfService;
	
	@Resource
	DiySitePageService diySitePageService;
	
	@Resource
	PageTemplateService pageTemplateService;

	@Override
	public List<SitePageDto> getSitePageTree(String siteCode, String type) {

		List<SitePageEntity> entityList = null;
		if (Assert.isEmpty(type)) {
			entityList = sitePagePersistence.getSitePageTree(siteCode);
		} else {
			entityList = sitePagePersistence.getSitePageTree(siteCode, type);
		}
		return BeanUtils.convert(entityList, SitePageDto.class);
	}

	@Override
	public SitePageDto getSitePageInfoById(Long id) {
		SitePageEntity entity = sitePagePersistence.findOne(id);
		return BeanUtils.convert(entity, SitePageDto.class);
	}

	@Override
	public SitePageDto saveSitePage(SitePageDto sitePageDto) throws JSONException {

		// 若页面主题为空则默认为站点主题
		if (Assert.isEmpty(sitePageDto.getThemeCode())) {
			SiteEntity siteEntity = sitePersistence.findByCode(sitePageDto.getSiteCode());
			sitePageDto.setThemeCode(siteEntity.getPublicTheme());
		}
		SitePageEntity reEntity = null;

		// 修改时布局切换后，保存成功后页面实例中的配置需同步移动
		if (Assert.isNotEmpty(sitePageDto.getId())) {
			// 根据ID查询原站点页面信息
			SitePageEntity oEntity = sitePagePersistence.findOne(sitePageDto.getId());
			String oLayoutCode = oEntity.getLayoutCode();

			SitePageEntity entity = BeanUtils.convert(sitePageDto, SitePageEntity.class);
			reEntity = sitePagePersistence.save(entity);

			if (!sitePageDto.getLayoutCode().equals(oLayoutCode)) {
				try {
					widgetConfService.changeWidgetInstance(reEntity.getUuId(), oLayoutCode,
							sitePageDto.getLayoutCode());
				} catch (Exception e) {
					// 实例移动失败，还原原布局
					entity.setLayoutCode(oEntity.getLayoutCode());
					reEntity = sitePagePersistence.save(entity);
				}
			}
		} else {
			
			String pageUuId = UUID.randomUUID().toString().replace("-", "");
			
			// 页面有选择页面模式时，增加页面实例及配置
			if (!Assert.isEmpty(sitePageDto.getPageTempCode())) {
				// 获取页面模版及模版配置信息
				PageTemplateDto pageTemplateDto = pageTemplateService.getPageTempInfo(sitePageDto.getPageTempCode());
				PageTemplateConfDto pageTemplateConfDto = pageTemplateService.getPageTempConfInfo(sitePageDto.getPageTempCode());
				
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
						profileConfEntity.setPageUuId(pageUuId);
						profileConfEntity.setJson(valObj.has("nestedPage") ? MathUtils.stringObj(valObj.get("nestedPage")) : MathUtils.stringObj(valObj));
						widgetConfList.add(profileConfEntity);
					}

					// 页面widget实例配置
					if (Assert.isNotEmpty(newJsonArr) && newJsonArr.length() > 0) {
						savePageWidgetInstances(pageUuId, newJsonArr);
					}

					// 页面widget实例系统配置信息
					if (Assert.isNotEmpty(widgetConfList) && widgetConfList.size() > 0) {
						profileConfPersistence.save(widgetConfList);
					}
				}
				
				sitePageDto.setLayoutCode(pageTemplateDto.getLayoutCode());
			}
			
			sitePageDto.setUuId(pageUuId);
			SitePageEntity entity = BeanUtils.convert(sitePageDto, SitePageEntity.class);
			reEntity = sitePagePersistence.save(entity);
			if (sitePageDto.getParentId() != 0) {
				// 更新父集节点为文件夹
				baseDao.update("update cos_portal_page set c_is_folder = 1 where c_id = ? ", sitePageDto.getParentId());
			}
		}

		return BeanUtils.convert(reEntity, SitePageDto.class);
	}

	@Override
	public void delSitePage(List<Long> ids, Long parentId) {
		baseDao.delete("cos_portal_page", "c_id", ids.toArray());

		// 父集节点下是否还有子集节点
		List<Map<String, Object>> childList = baseDao.query("select * from cos_portal_page where c_parent_id = ?",
				parentId);
		if (Assert.isEmpty(childList)) {
			baseDao.update("update cos_portal_page set c_is_folder = 0 where c_id = ? ", parentId);
		}
	}

	@Override
	public Boolean isExitPagePath(Long id, String path, String cascade, String type, String siteCode) {

		SitePageEntity entity = null;
		if (Assert.isEmpty(id)) {
			entity = sitePagePersistence.findByPaht(path, cascade, type, siteCode);
		} else {
			entity = sitePagePersistence.findByPath(path, id, cascade, siteCode, type);
		}

		if (entity != null) {
			return true;
		}

		return false;
	}

	@Override
	public Map<String, Object> getSitePageInfoByPath(Long personnelId, String path, String type, String siteCode) {

		StringBuffer sb = new StringBuffer();
		Map<String, Object> map = null;
		
		// 查询是否存在个性化站点页面
		if (!Assert.isEmpty(personnelId) && "0".equals(type)) {
			sb.append("SELECT p.*, s.c_public_theme,th.c_is_system as c_public_theme_system,s.c_private_theme,thh.c_is_system as c_private_theme_system, ");
			sb.append("l.c_name as c_layout_name,l.c_is_system as c_layout_system,l.c_project_code FROM cos_portal_diy_page p ");
			sb.append("INNER JOIN cos_portal_site s ON p.c_site_code = s.c_code ");
			sb.append("LEFT JOIN cos_portal_theme th ON s.c_public_theme = th.c_code ");
			sb.append("LEFT JOIN cos_portal_theme thh ON s.c_private_theme = thh.c_code ");
			sb.append("LEFT JOIN cos_portal_layout l ON p.c_layout_code = l.c_code where CONCAT(p.c_cascade,p.c_path) = ? and p.c_user_id = ? and p.c_site_code = ? and p.c_path != '' ");
			map = baseDao.load(sb.toString(), path, personnelId, siteCode);
		}
		
		if (Assert.isEmpty(map)) {
			sb = new StringBuffer();
			sb.append("SELECT p.*, s.c_public_theme,th.c_is_system as c_public_theme_system,s.c_private_theme,thh.c_is_system as c_private_theme_system, ");
			sb.append("l.c_name as c_layout_name,l.c_is_system as c_layout_system,l.c_project_code FROM cos_portal_page p ");
			sb.append("INNER JOIN cos_portal_site s ON p.c_site_code = s.c_code ");
			sb.append("LEFT JOIN cos_portal_theme th ON s.c_public_theme = th.c_code ");
			sb.append("LEFT JOIN cos_portal_theme thh ON s.c_private_theme = thh.c_code ");
			sb.append("LEFT JOIN cos_portal_layout l ON p.c_layout_code = l.c_code where CONCAT(p.c_cascade,p.c_path) = ? and p.c_type = ? and p.c_site_code = ? and p.c_path != '' ");
			map = baseDao.load(sb.toString(), path, type, siteCode);
		}
		return map;
	}

	@Override
	public Map<String, Object> getToPath(String siteCode, Long personnelId, String type, boolean isFolder, String vPath) {

		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String rtnPath = "";
		int status = ResultConstant.RESULT_RETURN_OK_STATUS;
		
		if (Assert.isNotEmpty(personnelId)) {// 用户未登录
			// 判断用户是否有个性化菜单，有即加载个性化菜单数据 
			List<DiySitePageDto> diySitePageList = diySitePageService.getDiyPageTree(siteCode, personnelId);
			
			// 1.站点权限校验
			if (personnelId.equals(1L)) {// 系统管理员，查询所有
				// 存在个性化菜单
				if (!Assert.isEmpty(diySitePageList) && diySitePageList.size() > 0) {
					sb.append("SELECT p1.* FROM cos_portal_diy_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code WHERE s1.c_code = ${siteCode} ");
					sb.append("AND p1.c_user_id = ${personnelId} and p1.c_path != '' ");
				} else {
					sb.append("SELECT * FROM cos_portal_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code WHERE s1.c_code = ${siteCode} and p1.c_path != '' ");
				}
			} else {
				// 存在个性化菜单
				if (!Assert.isEmpty(diySitePageList) && diySitePageList.size() > 0) {
					sb.append("SELECT p1.* FROM cos_portal_diy_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code ");
					sb.append("WHERE s1.c_code = ${siteCode} AND p1.c_user_id = ${personnelId} AND p1.c_is_system = 1 and p1.c_path != '' ");
					sb.append("AND EXISTS ( ");
					sb.append("SELECT p.* FROM cos_portal_permission pp, cos_sys_personnel p WHERE pp.c_grant_id = p.c_id ");
					sb.append("AND pp.c_yw_id = s1.c_id AND pp.c_yw_type = '0' AND pp.c_grant_type = '1' AND p.c_deleted = 0 AND p.c_id = ${personnelId} ");
					sb.append("UNION ALL ");
					sb.append("SELECT p.* FROM cos_sys_personnel p, cos_sys_role_personal rp, cos_sys_role r WHERE p.c_id = rp.c_personnel_id and rp.c_role_id = r.c_id ");
					sb.append("AND p.c_deleted = 0 AND EXISTS (SELECT rr.c_id FROM cos_portal_permission pp, cos_sys_role rr WHERE pp.c_grant_id = rr.c_id ");
					sb.append("AND pp.c_yw_id = s1.c_id AND pp.c_yw_type = '0' AND pp.c_grant_type = '0' AND rr.c_id = r.c_id ) AND p.c_id = ${personnelId} )");
					sb.append("UNION ALL ");
					sb.append("SELECT p2.* FROM cos_portal_diy_page p2 INNER JOIN cos_portal_site s1 ON p2.c_site_code = s1.c_code ");
					sb.append("WHERE s1.c_code = ${siteCode} AND p2.c_user_id = ${personnelId} AND p2.c_is_system = 0 and p2.c_path != '' ");
				}else {
					sb.append("SELECT * FROM cos_portal_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code WHERE s1.c_code = ${siteCode} and p1.c_path != '' ");
					sb.append("AND EXISTS ( ");
					sb.append("SELECT p.* FROM cos_portal_permission pp, cos_sys_personnel p WHERE pp.c_grant_id = p.c_id ");
					sb.append("AND pp.c_yw_id = s1.c_id AND pp.c_yw_type = '0' AND pp.c_grant_type = '1' AND p.c_deleted = 0 AND p.c_id = ${personnelId} ");
					sb.append("UNION ALL ");
					sb.append("SELECT p.* FROM cos_sys_personnel p, cos_sys_role_personal rp, cos_sys_role r WHERE p.c_id = rp.c_personnel_id and rp.c_role_id = r.c_id ");
					sb.append("AND p.c_deleted = 0 AND EXISTS (SELECT rr.c_id FROM cos_portal_permission pp, cos_sys_role rr WHERE pp.c_grant_id = rr.c_id ");
					sb.append("AND pp.c_yw_id = s1.c_id AND pp.c_yw_type = '0' AND pp.c_grant_type = '0' AND rr.c_id = r.c_id ) AND p.c_id = ${personnelId} )");
				}
			}

			params.put("siteCode", siteCode);
			params.put("personnelId", personnelId);

			List<Map<String, Object>> resList = baseDao.query(sb.toString(), params);
			if (!Assert.isEmpty(resList)) {
				sb = new StringBuffer();
				// 2.页面权限校验，返回第一个有权限的页面
				if (personnelId.equals(1L)) {// 系统管理员，查询所有
					// 存在个性化菜单
					if (!Assert.isEmpty(diySitePageList) && diySitePageList.size() > 0) {
						sb.append("select * from (SELECT p1.* FROM cos_portal_diy_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code WHERE s1.c_code = ${siteCode} AND p1.c_user_id = ${personnelId} and p1.c_path != '') as y ");
					} else {
						sb.append("select * from (SELECT p1.* FROM cos_portal_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code WHERE s1.c_code = ${siteCode} AND p1.c_type = '0' and p1.c_path != '') as y ");
					}
					
				} else {
					// 存在个性化菜单
					if (!Assert.isEmpty(diySitePageList) && diySitePageList.size() > 0) {
						sb.append("select * from (SELECT p1.* FROM cos_portal_diy_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code ");
						sb.append("WHERE s1.c_code = ${siteCode} AND p1.c_user_id = ${personnelId} AND p1.c_is_system = 1 and p1.c_path != '' ");
						sb.append("AND EXISTS ( ");
						sb.append("SELECT p.* FROM cos_portal_permission pp, cos_sys_personnel p WHERE pp.c_grant_id = p.c_id ");
						sb.append("AND pp.c_yw_id = p1.c_page_id AND pp.c_yw_type = '1' AND pp.c_grant_type = '1' AND p.c_deleted = 0 AND p.c_id = ${personnelId} ");
						sb.append("UNION ALL ");
						sb.append("SELECT p.* FROM cos_sys_personnel p, cos_sys_role_personal rp, cos_sys_role r WHERE p.c_id = rp.c_personnel_id and rp.c_role_id = r.c_id ");
						sb.append("AND p.c_deleted = 0 AND EXISTS (SELECT rr.c_id FROM cos_portal_permission pp, cos_sys_role rr WHERE pp.c_grant_id = rr.c_id ");
						sb.append("AND pp.c_yw_id = p1.c_page_id AND pp.c_yw_type = '1' AND pp.c_grant_type = '0' AND rr.c_id = r.c_id ) AND p.c_id = ${personnelId} )");
						sb.append("UNION ALL ");
						sb.append("SELECT p2.* FROM cos_portal_diy_page p2 INNER JOIN cos_portal_site s1 ON p2.c_site_code = s1.c_code ");
						sb.append("WHERE s1.c_code = ${siteCode} AND p2.c_user_id = ${personnelId} AND p2.c_is_system = 0 and p2.c_path != '') as y ");
						
					} else {
						sb.append("select * from (SELECT p1.* FROM cos_portal_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code WHERE s1.c_code = ${siteCode} AND p1.c_type = '0' and p1.c_path != '' ");
						sb.append("AND EXISTS ( ");
						sb.append("SELECT p.* FROM cos_portal_permission pp, cos_sys_personnel p WHERE pp.c_grant_id = p.c_id ");
						sb.append("AND pp.c_yw_id = p1.c_id AND pp.c_yw_type = '1' AND pp.c_grant_type = '1' AND p.c_deleted = 0 AND p.c_id = ${personnelId} ");
						sb.append("UNION ALL ");
						sb.append("SELECT p.* FROM cos_sys_personnel p, cos_sys_role_personal rp, cos_sys_role r WHERE p.c_id = rp.c_personnel_id and rp.c_role_id = r.c_id ");
						sb.append("AND p.c_deleted = 0 AND EXISTS (SELECT rr.c_id FROM cos_portal_permission pp, cos_sys_role rr WHERE pp.c_grant_id = rr.c_id ");
						sb.append("AND pp.c_yw_id = p1.c_id AND pp.c_yw_type = '1' AND pp.c_grant_type = '0' AND rr.c_id = r.c_id  ) AND p.c_id = ${personnelId} ");
						sb.append(")) as y ");
					}
				}

				if (isFolder) {
					sb.append(" where y.c_cascade LIKE ${vPath} ");
					sb.append("ORDER BY y.c_id ASC LIMIT 1 ");
					params.put("vPath", vPath + "%");
				} else {
					sb.append(" where CONCAT(y.c_cascade,y.c_path) = ${vPath} ");
					params.put("vPath", vPath);
				}

				params.put("type", type);
				List<Map<String, Object>> list = baseDao.query(sb.toString(), params);
				if (!Assert.isEmpty(list)) {
					Map<String, Object> rtnMap = list.get(0);
					rtnPath = MathUtils.stringObj(rtnMap.get("c_cascade")) + MathUtils.stringObj(rtnMap.get("c_path"));
				} else {
					status = ResultConstant.RESULT_RETURN_NO_EXIST_STATUS;
				}
			} else {
				// 站点无权限
				status = ResultConstant.RESULT_RETURN_NO_AUTH_STATUS;
			}
		} else {
			sb.append("SELECT p1.* FROM cos_portal_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code WHERE s1.c_code = ${siteCode} AND p1.c_type = ${type} and p1.c_path != '' ");
			if (isFolder) {
				sb.append(" and p1.c_cascade LIKE ${vPath} ");
				sb.append("ORDER BY p1.c_id ASC LIMIT 1 ");
				params.put("vPath", vPath + "%");
			} else {
				sb.append(" and CONCAT(p1.c_cascade,p1.c_path) = ${vPath} ");
				params.put("vPath", vPath);
			}
			params.put("siteCode", siteCode);
			params.put("type", type);

			List<Map<String, Object>> list = baseDao.query(sb.toString(), params);
			if (!Assert.isEmpty(list)) {
				Map<String, Object> rtnMap = list.get(0);
				rtnPath = MathUtils.stringObj(rtnMap.get("c_cascade")) + MathUtils.stringObj(rtnMap.get("c_path"));
			} else {
				status = ResultConstant.RESULT_RETURN_NO_EXIST_STATUS;
			}
		}
		
		resultMap.put("path", rtnPath);
		resultMap.put("status", status);
		return resultMap;
	}

//	@Override
//	public List<SitePageDto> getPages(String siteCode, Long personnelId) {
//
//		StringBuffer sb = new StringBuffer();
//		Map<String, Object> params = new HashMap<String, Object>();
//		List<SitePageDto> rtnList = new ArrayList<SitePageDto>();
//		
//		// 判断用户是否有个性化菜单，有即加载个性化菜单数据 
//		List<DiySitePageDto> diySitePageList = null;
//
//		if (Assert.isEmpty(personnelId)) {// 未登录，返回所有公开页面
//			List<SitePageEntity> sitePageEntityList = sitePagePersistence.getAllPublicPages(siteCode);
//			rtnList = BeanUtils.convert(sitePageEntityList, SitePageDto.class);
//		} else {// 所有有权限的私有页面
//			diySitePageList = diySitePageService.getDiyPageTree(siteCode, personnelId);
//			if (personnelId.equals(1L)) {// 系统管理员，查询所有
//				// 存在个性化菜单
//				if (!Assert.isEmpty(diySitePageList) && diySitePageList.size() > 0) {
//					sb.append("SELECT p1.* FROM cos_portal_diy_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code WHERE s1.c_code = ${siteCode} ");
//					sb.append("AND p1.c_user_id = ${personnelId} and p1.c_path != '' ");
//				} else {
//					sb.append("SELECT * FROM cos_portal_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code WHERE s1.c_code = ${siteCode} and p1.c_path != '' ");
//				}
//			} else {
//				// 存在个性化菜单
//				if (!Assert.isEmpty(diySitePageList) && diySitePageList.size() > 0) {
//					sb.append("SELECT p1.* FROM cos_portal_diy_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code ");
//					sb.append("WHERE s1.c_code = ${siteCode} AND p1.c_user_id = ${personnelId} AND p1.c_is_system = 1 and p1.c_path != '' ");
//					sb.append("AND EXISTS ( ");
//					sb.append("SELECT p.* FROM cos_portal_permission pp, cos_sys_personnel p WHERE pp.c_grant_id = p.c_id ");
//					sb.append("AND pp.c_yw_id = s1.c_id AND pp.c_yw_type = '0' AND pp.c_grant_type = '1' AND p.c_deleted = 0 AND p.c_id = ${personnelId} ");
//					sb.append("UNION ALL ");
//					sb.append("SELECT p.* FROM cos_sys_personnel p, cos_sys_role_personal rp, cos_sys_role r WHERE p.c_id = rp.c_personnel_id and rp.c_role_id = r.c_id ");
//					sb.append("AND p.c_deleted = 0 AND EXISTS (SELECT rr.c_id FROM cos_portal_permission pp, cos_sys_role rr WHERE pp.c_grant_id = rr.c_id ");
//					sb.append("AND pp.c_yw_id = s1.c_id AND pp.c_yw_type = '0' AND pp.c_grant_type = '0' AND rr.c_id = r.c_id ) AND p.c_id = ${personnelId} )");
//					sb.append("UNION ALL ");
//					sb.append("SELECT p2.* FROM cos_portal_diy_page p2 INNER JOIN cos_portal_site s1 ON p2.c_site_code = s1.c_code ");
//					sb.append("WHERE s1.c_code = ${siteCode} AND p2.c_user_id = ${personnelId} AND p2.c_is_system = 0 and p2.c_path != '' ");
//				}else {
//					sb.append("SELECT p1.* FROM cos_portal_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code WHERE s1.c_code = ${siteCode} and p1.c_path != '' ");
//					sb.append("AND EXISTS ( ");
//					sb.append("SELECT p.* FROM cos_portal_permission pp, cos_sys_personnel p WHERE pp.c_grant_id = p.c_id ");
//					sb.append("AND pp.c_yw_id = s1.c_id AND pp.c_yw_type = '0' AND pp.c_grant_type = '1' AND p.c_deleted = 0 AND p.c_id = ${personnelId} ");
//					sb.append("UNION ALL ");
//					sb.append("SELECT p.* FROM cos_sys_personnel p, cos_sys_role_personal rp, cos_sys_role r WHERE p.c_id = rp.c_personnel_id and rp.c_role_id = r.c_id ");
//					sb.append("AND p.c_deleted = 0 AND EXISTS (SELECT rr.c_id FROM cos_portal_permission pp, cos_sys_role rr WHERE pp.c_grant_id = rr.c_id ");
//					sb.append("AND pp.c_yw_id = s1.c_id AND pp.c_yw_type = '0' AND pp.c_grant_type = '0' AND rr.c_id = r.c_id ) AND p.c_id = ${personnelId} )");
//				}
//			}
//			params.put("siteCode", siteCode);
//			params.put("personnelId", personnelId);
//
//			List<Map<String, Object>> resList = baseDao.query(sb.toString(), params);
//			if (!Assert.isEmpty(resList)) {
//				sb = new StringBuffer();
//				// 2.页面权限校验，返回第一个有权限的页面
//				if (personnelId.equals(1L)) {// 系统管理员，查询所有
//					// 存在个性化菜单
//					if (!Assert.isEmpty(diySitePageList) && diySitePageList.size() > 0) {
//						sb.append("SELECT p1.* FROM cos_portal_diy_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code WHERE s1.c_code = ${siteCode} AND p1.c_user_id = ${personnelId} and p1.c_path != '' ");
//						sb.append(" ORDER BY p1.C_SORT_NO,p1.C_ID ASC");
//					} else {
//						sb.append("SELECT p1.* FROM cos_portal_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code WHERE s1.c_code = ${siteCode} AND p1.c_type = '0' and p1.c_path != '' ");
//						sb.append(" ORDER BY p1.C_SORT_NO,p1.C_ID ASC");
//					}
//				} else {
//					// 存在个性化菜单
//					if (!Assert.isEmpty(diySitePageList) && diySitePageList.size() > 0) {
//						sb.append(" select m.* from (");
//						sb.append("SELECT p1.* FROM cos_portal_diy_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code ");
//						sb.append("WHERE s1.c_code = ${siteCode} AND p1.c_user_id = ${personnelId} AND p1.c_is_system = 1 and p1.c_path != '' ");
//						sb.append("AND EXISTS ( ");
//						sb.append("SELECT p.* FROM cos_portal_permission pp, cos_sys_personnel p WHERE pp.c_grant_id = p.c_id ");
//						sb.append("AND pp.c_yw_id = p1.c_page_id AND pp.c_yw_type = '1' AND pp.c_grant_type = '1' AND p.c_deleted = 0 AND p.c_id = ${personnelId} ");
//						sb.append("UNION ALL ");
//						sb.append("SELECT p.* FROM cos_sys_personnel p, cos_sys_role_personal rp, cos_sys_role r WHERE p.c_id = rp.c_personnel_id and rp.c_role_id = r.c_id ");
//						sb.append("AND p.c_deleted = 0 AND EXISTS (SELECT rr.c_id FROM cos_portal_permission pp, cos_sys_role rr WHERE pp.c_grant_id = rr.c_id ");
//						sb.append("AND pp.c_yw_id = p1.c_page_id AND pp.c_yw_type = '1' AND pp.c_grant_type = '0' AND rr.c_id = r.c_id ) AND p.c_id = ${personnelId} )");
//						sb.append("UNION ALL ");
//						sb.append("SELECT p2.* FROM cos_portal_diy_page p2 INNER JOIN cos_portal_site s1 ON p2.c_site_code = s1.c_code ");
//						sb.append("WHERE s1.c_code = ${siteCode} AND p2.c_user_id = ${personnelId} AND p2.c_is_system = 0 and p2.c_path != '' ");
//						sb.append(" ) as m ORDER BY m.C_SORT_NO,m.C_ID ASC");
//						
//					} else {
//						sb.append("SELECT p1.* FROM cos_portal_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code WHERE s1.c_code = ${siteCode} AND p1.c_type = '0' and p1.c_path != '' ");
//						sb.append("AND EXISTS ( ");
//						sb.append("SELECT p.* FROM cos_portal_permission pp, cos_sys_personnel p WHERE pp.c_grant_id = p.c_id ");
//						sb.append("AND pp.c_yw_id = p1.c_id AND pp.c_yw_type = '1' AND pp.c_grant_type = '1' AND p.c_deleted = 0 AND p.c_id = ${personnelId} ");
//						sb.append("UNION ALL ");
//						sb.append("SELECT p.* FROM cos_sys_personnel p, cos_sys_role_personal rp, cos_sys_role r WHERE p.c_id = rp.c_personnel_id and rp.c_role_id = r.c_id ");
//						sb.append("AND p.c_deleted = 0 AND EXISTS (SELECT rr.c_id FROM cos_portal_permission pp, cos_sys_role rr WHERE pp.c_grant_id = rr.c_id ");
//						sb.append("AND pp.c_yw_id = p1.c_id AND pp.c_yw_type = '1' AND pp.c_grant_type = '0' AND rr.c_id = r.c_id  ) AND p.c_id = ${personnelId} ");
//						sb.append(") ORDER BY p1.C_SORT_NO,p1.C_ID ASC");
//					}
//				}
//
//				List<Map<String, Object>> list = baseDao.query(sb.toString(), params);
//				if (!Assert.isEmpty(list)) {
//					SitePageDto sitePageDto = null;
//					for (Map<String, Object> map : list) {
//						sitePageDto = new SitePageDto();
//						if (map.containsKey("C_IS_SYSTEM")) {
//							DiySitePageEntity diySitePageEntity = diySitePagePersistence.getOne(MathUtils.numObj2Long(map.get("C_ID")));
//							if (diySitePageEntity != null) {
//								sitePageDto.setId(diySitePageEntity.getId());
//								sitePageDto.setUuId(diySitePageEntity.getUuId());
//								sitePageDto.setName(diySitePageEntity.getName());
//								sitePageDto.setPath(diySitePageEntity.getPath());
//								sitePageDto.setCascade(diySitePageEntity.getCascade());
//								sitePageDto.setParentId(diySitePageEntity.getParentId());
//								sitePageDto.setSiteCode(diySitePageEntity.getSiteCode());
//								sitePageDto.setThemeCode(diySitePageEntity.getThemeCode());
//								sitePageDto.setType("0");
//								sitePageDto.setLayoutCode(diySitePageEntity.getLayoutCode());
//								sitePageDto.setOpenSelf(diySitePageEntity.getOpenSelf());
//								sitePageDto.setNavHidden(diySitePageEntity.getNavHidden());
//								sitePageDto.setAllowWidget(diySitePageEntity.getAllowWidget());
//								sitePageDto.setAllowLayout(diySitePageEntity.getAllowLayout());
//								sitePageDto.setIsMenu(diySitePageEntity.getIsMenu());
//								sitePageDto.setIsFolder(diySitePageEntity.getIsFolder());
//								sitePageDto.setThemeStyle(diySitePageEntity.getThemeStyle());
//								sitePageDto.setSortNo(diySitePageEntity.getSortNo());
//								sitePageDto.setLinkUrl(diySitePageEntity.getLinkUrl());
//								sitePageDto.setIsLink(diySitePageEntity.getIsLink());
//							}
//							
//						} else {
//							SitePageEntity sitePageEntity = sitePagePersistence
//									.findOne(MathUtils.numObj2Long(map.get("C_ID")));
//							sitePageDto = BeanUtils.convert(sitePageEntity, SitePageDto.class);
//						}
//						rtnList.add(sitePageDto);
//					}
//				}
//			}
//		}
//		return rtnList;
//	}
	
	@Override
	public List<SitePageDto> getPages(String siteCode, Long personnelId) {

		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		List<SitePageDto> rtnList = new ArrayList<SitePageDto>();
		
		// 判断用户是否有个性化菜单，有即加载个性化菜单数据 
		List<DiySitePageDto> diySitePageList = null;

		if (Assert.isEmpty(personnelId)) {// 未登录，返回所有公开页面
			List<SitePageEntity> sitePageEntityList = sitePagePersistence.getAllPublicPages(siteCode);
			rtnList = BeanUtils.convert(sitePageEntityList, SitePageDto.class);
		} else {// 所有有权限的私有页面
			diySitePageList = diySitePageService.getDiyPageTree(siteCode, personnelId);
			sb = new StringBuffer();
			
			if (personnelId.equals(1L)) {// 系统管理员，查询所有
				// 存在个性化菜单
				if (!Assert.isEmpty(diySitePageList) && diySitePageList.size() > 0) {
					sb.append("SELECT p1.* FROM cos_portal_diy_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code WHERE s1.c_code = ${siteCode} AND p1.c_user_id = ${personnelId} and p1.c_path != '' ");
					sb.append(" ORDER BY p1.C_SORT_NO,p1.C_ID ASC");
				} else {
					sb.append("SELECT p1.* FROM cos_portal_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code WHERE s1.c_code = ${siteCode} AND p1.c_type = '0' and p1.c_path != '' ");
					sb.append(" ORDER BY p1.C_SORT_NO,p1.C_ID ASC");
				}
			} else {
				// 存在个性化菜单
				if (!Assert.isEmpty(diySitePageList) && diySitePageList.size() > 0) {
					sb.append(" select m.* from (");
					sb.append("SELECT p1.* FROM cos_portal_diy_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code ");
					sb.append("WHERE s1.c_code = ${siteCode} AND p1.c_user_id = ${personnelId} and p1.c_path != '' ");
					sb.append(" ) as m ORDER BY m.C_SORT_NO,m.C_ID ASC");
					
				} else {
					sb.append("SELECT p1.* FROM cos_portal_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code WHERE s1.c_code = ${siteCode} AND p1.c_type = '0' and p1.c_path != '' ");
					sb.append(" ORDER BY p1.C_SORT_NO,p1.C_ID ASC");
				}
			}
			
			params.put("siteCode", siteCode);
			params.put("personnelId", personnelId);

			List<Map<String, Object>> list = baseDao.query(sb.toString(), params);
			if (!Assert.isEmpty(list)) {
				SitePageDto sitePageDto = null;
				for (Map<String, Object> map : list) {
					sitePageDto = new SitePageDto();
					if (map.containsKey("C_IS_SYSTEM")) {
						convertSitePageDto(sitePageDto, map);
						sitePageDto.setType("0");
					} else {
                        convertSitePageDto(sitePageDto, map);
					}
					rtnList.add(sitePageDto);
				}
			}
		}
		return rtnList;
	}
	
	/**
	 * map 对象转换为 sitePageDto
	 * @param sitePageDto
	 * @param map
	 */
	private void convertSitePageDto(SitePageDto sitePageDto, Map<String, Object> map) {
		sitePageDto.setId(MathUtils.numObj2Long(map.get("C_ID")));
		sitePageDto.setName(MathUtils.stringObj(map.get("C_NAME")));
		sitePageDto.setParentId(MathUtils.numObj2Long(map.get("C_PARENT_ID")));
		sitePageDto.setThemeCode(MathUtils.stringObj(map.get("C_THEME_CODE")));
		sitePageDto.setLayoutCode(MathUtils.stringObj(map.get("C_LAYOUT_CODE")));
		sitePageDto.setOpenSelf(MathUtils.stringObj(map.get("C_OPEN_SELF")));
		sitePageDto.setNavHidden(MathUtils.stringObj(map.get("C_NAV_HIDDEN")));
		sitePageDto.setAllowWidget(MathUtils.stringObj(map.get("C_ALLOW_WIDGET")));
		sitePageDto.setAllowLayout(MathUtils.stringObj(map.get("C_ALLOW_LAYOUT")));
		sitePageDto.setStatus(!Assert.isEmpty(map.get("C_STATUS")) ? MathUtils.numObj2Integer(map.get("C_STATUS")) : 0);
		sitePageDto.setType(MathUtils.stringObj(map.get("C_TYPE")));
		sitePageDto.setIsMenu(MathUtils.booleanValueOf(map.get("C_IS_MENU")));
		sitePageDto.setPath(MathUtils.stringObj(map.get("C_PATH")));
		sitePageDto.setCascade(MathUtils.stringObj(map.get("C_CASCADE")));
		sitePageDto.setIsFolder(MathUtils.booleanValueOf(map.get("C_IS_FOLDER")));
		sitePageDto.setThemeStyle(MathUtils.stringObj(map.get("C_THEME_STYLE")));
		sitePageDto.setSiteCode(MathUtils.stringObj(map.get("C_SITE_CODE")));
		sitePageDto.setUuId(MathUtils.stringObj(map.get("C_UU_ID")));
		sitePageDto.setSortNo(!Assert.isEmpty(map.get("C_SORT_NO")) ? MathUtils.numObj2Integer(map.get("C_SORT_NO")) : 0);
		sitePageDto.setLinkUrl(MathUtils.stringObj(map.get("C_LINK_URL")));
		sitePageDto.setIsLink(MathUtils.booleanValueOf(map.get("C_IS_LINK")));
		sitePageDto.setShow(MathUtils.stringObj(map.get("C_SHOW")));
		sitePageDto.setPageTempCode(MathUtils.stringObj(map.get("C_PAGE_TEMP_CODE")));
	}

	@Override
	@Transactional
	public void resetPage(String pageUuId, String pageWidgets, String siteCode, Long personnelId) {
		StringBuffer sb = new StringBuffer();
		// 1.重置用户自定义主题
		sb.append("DELETE FROM cos_custom_theme WHERE c_site_code = ? and c_user_id = ?");
		baseDao.update(sb.toString(), siteCode, personnelId);

		// 2.重置用户自定义布局
		sb = new StringBuffer();
		sb.append("DELETE FROM cos_custom_layout WHERE c_page_uu_id = ? and c_user_id = ?");
		baseDao.update(sb.toString(), pageUuId, personnelId);

		// 3.重置用户自定义widget示例
		sb = new StringBuffer();
		sb.append("DELETE FROM cos_custom_widget_instance WHERE c_page_uu_id = ? and c_user_id = ?");
		baseDao.update(sb.toString(), pageUuId, personnelId);
		
		// 4.重置系统偏好设置信息
		baseDao.update("delete from cos_custom_profile where c_page_uu_id = ? and c_user_id = ?", pageUuId, personnelId);
	}

	@Override
	public void changeLayout(String pageUuId, String layoutCode) {

		String sql = "UPDATE cos_portal_page SET c_layout_code = ? WHERE c_uu_id = ?";
		baseDao.update(sql, layoutCode, pageUuId);
	}

	@Override
	public ReturnDto saveSitePageTree(Long personnelId, String siteCode, String draggingNode, String dataTree, Long parentId) throws JSONException {

		JSONObject draggingJo = new JSONObject(draggingNode);

		// 根据ID查询父页面级联信息
		String cascade = "";
		String type = "";
		if (parentId == 1 || parentId == 2) {
			cascade = "/";
			if (parentId == 1) {
				type = "1";
			}else {
				type = "0";
			}
		} else {
			SitePageEntity entity = sitePagePersistence.getOne(parentId);
			cascade = entity.getCascade() + entity.getPath() + "/";
			type = entity.getType();
		}

		// 组装需要更新的数据，批量更新
		List<Map<String, Object>> needList = new ArrayList<Map<String, Object>>();
		
		long id = MathUtils.numObj2Long(draggingJo.get("id"));
		JSONObject attrJo = draggingJo.getJSONObject("attributes");
		String path = attrJo.getString("path");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("C_ID", id);
		map.put("C_PARENT_ID", parentId);
		map.put("C_UPDATE_BY", personnelId);
		map.put("C_UPDATE_TIME", new Date());
		
		//判断是否存在，若存在则重新生成新的path，避免每次都要做校验
		boolean isExitPath = this.isExitPagePath(id, path, cascade, type, siteCode);
		if (isExitPath) {
			path = MathUtils.stringObj(System.nanoTime());
		}
		map.put("C_CASCADE", cascade);
		map.put("C_PATH", path);
		map.put("C_TYPE", type);
		
		needList.add(map);
		JSONArray childArr = draggingJo.isNull("children") ? new JSONArray() : draggingJo.getJSONArray("children");
        if (childArr.length() > 0) {
        	cascade = cascade + path + "/";
			saveTreeData(childArr, id, personnelId, cascade, needList, type);
        }
		baseDao.modify("cos_portal_page", "C_ID", needList);
		
		//保存成功后重新自动更新排序号
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		JSONArray treeArr = new JSONArray(dataTree);
		JSONObject tmpJo = treeArr.getJSONObject(0);
		Long tmpParentId = MathUtils.numObj2Long(tmpJo.get("id"));
		updateTreeSortNo(treeArr, tmpParentId, dataList);
		baseDao.modify("cos_portal_page", "C_ID", dataList);
		
		return new ReturnDto("移动成功！");
	}

	/**
	 * 更新树顺序号
	 * @param treeArr
	 * @param tmpParentId
	 * @param dataList
	 * @throws JSONException 
	 */
	private void updateTreeSortNo(JSONArray treeArr, Long tmpParentId, List<Map<String, Object>> dataList) throws JSONException {
		int sortNo = 1;
		for (int i = 0;i<treeArr.length();i++) {
    		JSONObject tmpJo = treeArr.getJSONObject(i);
    		long id = MathUtils.numObj2Long(tmpJo.get("id"));
    		
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
    			updateTreeSortNo(childArr, id, dataList);
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
	private void saveTreeData(JSONArray joArr, Long parentId, Long personnelId, String cascade,
			List<Map<String, Object>> needList, String type) throws JSONException {
		for (int i = 0; i < joArr.length(); i++) {
			JSONObject tmpJo = joArr.getJSONObject(i);
			long id = MathUtils.numObj2Long(tmpJo.get("id"));
			JSONObject attrJo = tmpJo.getJSONObject("attributes");
			String path = attrJo.getString("path");
			String siteCode = attrJo.getString("siteCode");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("C_ID", id);
			map.put("C_PARENT_ID", parentId);
			map.put("C_UPDATE_BY", personnelId);
			map.put("C_UPDATE_TIME", new Date());
			
			//重新生成新的path，避免每次都要做校验
			boolean isExitPath = this.isExitPagePath(id, path, cascade, type, siteCode);
			if (isExitPath) {
				path = MathUtils.stringObj(System.nanoTime());
			}
			
			map.put("C_CASCADE", cascade);
			map.put("C_PATH", path);
			map.put("C_TYPE", type);
			
			needList.add(map);

			JSONArray childArr = tmpJo.isNull("children") ? new JSONArray() : tmpJo.getJSONArray("children");
			if (childArr.length() > 0) {
				cascade = cascade + path + "/";
				saveTreeData(childArr, id, personnelId, cascade, needList, type);
			}
		}
	}

	@Override
	@Transactional
	public ReturnDto copyPage(SitePageDto sitePageDto) throws Exception {
		
		// 页面widgets配置实例信息组织
		JSONArray pageWidgetInstanceJoArr = new JSONArray();
		String widgetInstanceJson = widgetConfService.getWidgetJson(sitePageDto.getUuId());
		if (!Assert.isEmpty(widgetInstanceJson)) {
			pageWidgetInstanceJoArr = new JSONArray(widgetInstanceJson);
		}
		
		// 保存页面信息
		sitePageDto.setId(null);
		sitePageDto.setUuId(UUID.randomUUID().toString().replace("-", ""));
		sitePageDto.setPath(MathUtils.stringObj(System.nanoTime()));
		SitePageDto saveDto = this.saveSitePage(sitePageDto);
		
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
			profileConfEntity.setPageUuId(saveDto.getUuId());
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
	public List<SitePageDto> getAuthSitePageTree(String siteCode, Long personnelId) {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		List<SitePageDto> rtnList = new ArrayList<SitePageDto>();

		if (!Assert.isEmpty(personnelId)){// 所有有权限的私有页面
			params.put("siteCode", siteCode);
			params.put("personnelId", personnelId);

			if (personnelId.equals(1L)) {// 系统管理员，查询所有
				sb.append("SELECT p1.* FROM cos_portal_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code WHERE s1.c_code = ${siteCode} AND p1.c_type = '0' and p1.c_path != '' ");
				sb.append(" ORDER BY p1.C_SORT_NO,p1.C_ID ASC");
			} else {
				sb.append("SELECT p1.* FROM cos_portal_page p1 INNER JOIN cos_portal_site s1 ON p1.c_site_code = s1.c_code WHERE s1.c_code = ${siteCode} AND p1.c_type = '0' and p1.c_path != '' ");
				sb.append("AND EXISTS ( ");
				sb.append("SELECT p.* FROM cos_portal_permission pp, cos_sys_personnel p WHERE pp.c_grant_id = p.c_id ");
				sb.append("AND pp.c_yw_id = p1.c_id AND pp.c_yw_type = '1' AND pp.c_grant_type = '1' AND p.c_deleted = 0 AND p.c_id = ${personnelId} ");
				sb.append("UNION ALL ");
				sb.append("SELECT p.* FROM cos_sys_personnel p, cos_sys_role_personal rp, cos_sys_role r WHERE p.c_id = rp.c_personnel_id and rp.c_role_id = r.c_id ");
				sb.append("AND p.c_deleted = 0 AND EXISTS (SELECT rr.c_id FROM cos_portal_permission pp, cos_sys_role rr WHERE pp.c_grant_id = rr.c_id ");
				sb.append("AND pp.c_yw_id = p1.c_id AND pp.c_yw_type = '1' AND pp.c_grant_type = '0' AND rr.c_id = r.c_id  ) AND p.c_id = ${personnelId} ");
				sb.append(") ORDER BY p1.C_SORT_NO,p1.C_ID ASC");
			}

			List<Map<String, Object>> list = baseDao.query(sb.toString(), params);
			if (!Assert.isEmpty(list)) {
				SitePageDto sitePageDto = null;
				for (Map<String, Object> map : list) {
					sitePageDto = new SitePageDto();
					SitePageEntity sitePageEntity = sitePagePersistence
							.findOne(MathUtils.numObj2Long(map.get("C_ID")));
					sitePageDto = BeanUtils.convert(sitePageEntity, SitePageDto.class);
					rtnList.add(sitePageDto);
				}
			}
		}
		return rtnList;
	}

}
