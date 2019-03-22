package com.gsoft.portal.component.compmgr.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.table.Constant;
import com.gsoft.cos3.tree.TreeNode;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.cos3.util.StringUtils;
import com.gsoft.cos3.util.TreeUtils;
import com.gsoft.portal.component.compmgr.dto.BusinessComponentDto;
import com.gsoft.portal.component.compmgr.entity.BusinessCompConfEntity;
import com.gsoft.portal.component.compmgr.entity.BusinessComponentEntity;
import com.gsoft.portal.component.compmgr.persistence.BusinessCompConfPersistence;
import com.gsoft.portal.component.compmgr.persistence.BusinessComponentPersistence;
import com.gsoft.portal.component.compmgr.service.BusinessComponentService;
import com.gsoft.portal.webview.widget.dto.WidgetDto;
import com.gsoft.portal.webview.widget.service.WidgetService;
import com.gsoft.portal.webview.widgetconf.service.WidgetConfService;

/**
 * 业务组件管理Service实现类
 * 
 * @author SN
 *
 */
@Service
public class BusinessComponentServiceImpl implements BusinessComponentService {

	@Resource
	BaseDao baseDao;

	@Resource
	BusinessComponentPersistence businessComponentPersistence;

	@Resource
	BusinessCompConfPersistence businessCompConfPersistence;

	@Resource
	WidgetConfService widgetConfService;
	
	@Resource
	WidgetService widgetService;

	@Override
	public PageDto getBusinessComponentList(String category, String search, Integer page, Integer size, String sortProp, String order) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT c.* FROM cos_portal_yw_component c where 1=1 ");
		Map<String, Object> params = new HashMap<String, Object>();

		if (Assert.isNotEmpty(search)) {
			sb.append(" AND c.c_comp_name like ${search} ");
			params.put("search", "%" + search + "%");
		}
		
		if (Assert.isEmpty(category)) {
			sb.append(" and c.c_category is null ");
		} else {
			sb.append(" and c.c_category = ${category} ");
			params.put("category", category);
		}
		
		sb.append(" ORDER BY c.c_id DESC ");
		PageDto pageDto = baseDao.query(page, size, sb.toString(), params);
		return pageDto;
	}

	@Override
	public void updateComponentStatus(Long ids, String status) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("C_STATUS", status);
		baseDao.modify("cos_portal_yw_component", Constant.COLUMN_NAME_ID,
				StringUtils.splitAndStrip(MathUtils.stringObj(ids), ","), map);
	}

	@Override
	public BusinessComponentDto saveBusinessCompInfo(BusinessComponentDto businessComponentDto) {
		BusinessComponentEntity entity = BeanUtils.convert(businessComponentDto, BusinessComponentEntity.class);
		BusinessComponentEntity reEntity = businessComponentPersistence.save(entity);
		return BeanUtils.convert(reEntity, BusinessComponentDto.class);
	}

	@Override
	@Transactional
	public ReturnDto delBusinessComp(Long id) throws JSONException {

		businessComponentPersistence.delete(id);

		// 根据组件ID获取系统配置的嵌套widget实例ID
		BusinessCompConfEntity businessCompConfEntity = businessCompConfPersistence.getBusinessCompConfByCompId(id);

		if (!Assert.isEmpty(businessCompConfEntity)) {
			if (!Assert.isEmpty(businessCompConfEntity.getId())) {
				String json = businessCompConfEntity.getJson();
				if (!Assert.isEmpty(json)) {
					JSONArray jsonArr = new JSONArray(json);
					JSONObject widgetParamJsonObj = new JSONObject();
					widgetConfService.getBusinessWidgetInstanceParams(jsonArr, widgetParamJsonObj);

					// 先删除配置实例信息再删除模版配置信息
					List<String> delUuIds = new ArrayList<String>();
					@SuppressWarnings("rawtypes")
					Iterator iterator = widgetParamJsonObj.keys();
					while (iterator.hasNext()) {
						String key = (String) iterator.next();
						delUuIds.add(MathUtils.stringObj(key));
					}
					
					if (delUuIds.size() > 0) {
						for (String widgetId : delUuIds) {
							baseDao.update("DELETE FROM cos_sys_profile WHERE c_widget_uu_id = ? ", widgetId);
						}
					}
					businessCompConfPersistence.delete(businessCompConfEntity.getId());
				}
			}
		}
		return new ReturnDto("删除成功!");
	}

	@Override
	public BusinessComponentDto getBusinessCompById(Long id) {
		return BeanUtils.convert(businessComponentPersistence.getOne(id), BusinessComponentDto.class);
	}

	@Override
	public ReturnDto getAllBusinessCompList(Long personnelId) {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		// 匹配查找授权了的业务组件信息 授权
		sb.append("SELECT c.c_id, '' as c_code, c.c_comp_name, c.c_comp_desc, '' as c_params FROM cos_portal_yw_component c ");
		sb.append("WHERE c.c_status = 1 ");
		
		if (!personnelId.equals(1L)) {// 非系统管理员，只查询有权限的
			sb.append("AND EXISTS ( ");
			sb.append("SELECT p.* FROM cos_portal_permission pp, cos_sys_personnel p WHERE pp.c_grant_id = p.c_id ");
			sb.append("AND pp.c_yw_id = c.c_id AND pp.c_yw_type = '2' AND pp.c_grant_type = '1' AND p.c_deleted = 0 AND p.c_id = ${personnelId} ");
			sb.append("UNION ALL ");
			sb.append("SELECT p.* FROM cos_sys_personnel p, cos_sys_role_personal rp, cos_sys_role r WHERE p.c_id = rp.c_personnel_id and rp.c_role_id = r.c_id ");
			sb.append("AND p.c_deleted = 0 AND EXISTS (SELECT rr.c_id FROM cos_portal_permission pp, cos_sys_role rr WHERE pp.c_grant_id = rr.c_id ");
			sb.append("AND pp.c_yw_id = c.c_id AND pp.c_yw_type = '2' AND pp.c_grant_type = '0' AND rr.c_id = r.c_id ) AND p.c_id = ${personnelId} )");
		}
		
		sb.append(" UNION ALL ");
		sb.append("SELECT w.c_id, w.c_code, w.c_name as c_comp_name, w.c_desc as c_comp_desc, w.c_params FROM cos_portal_widget w ");
		sb.append("WHERE EXISTS (SELECT c.* FROM cos_portal_component c ");
		sb.append("WHERE c.c_code = w.c_project_code AND c.c_status = 1) and w.c_is_business = 1 ");

		params.put("personnelId", personnelId);
		List<Map<String, Object>> resList = baseDao.query(sb.toString(), params);
		List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();

		if (!Assert.isEmpty(resList) && resList.size() > 0) {
			Map<String, Object> map = null;
			// 查询嵌套widget信息
			WidgetDto widgetDto = widgetService.getWidgetInfoByCode("nested");
			
			for (Map<String, Object> tmpMap : resList) {
				map = new HashMap<String, Object>();
				map.put("id", System.nanoTime());
				if (Assert.isEmpty(tmpMap.get("C_CODE"))) {
					map.put("compId", tmpMap.get("C_ID"));
				}
				map.put("name", Assert.isEmpty(tmpMap.get("C_CODE")) ? "nested" : tmpMap.get("C_CODE"));
				map.put("title", tmpMap.get("C_COMP_NAME"));
				map.put("description", tmpMap.get("C_COMP_DESC"));
				map.put("singleton", false);
				map.put("params", Assert.isEmpty(tmpMap.get("C_PARAMS")) ? widgetDto.getParams() : tmpMap.get("C_PARAMS"));
				rtnList.add(map);
			}
		}
		return new ReturnDto(rtnList);
	}
	
	@Override
	public List<TreeNode> getAllCascadeBusCopm() {
		List<Map<String, Object>> listRes = new ArrayList<Map<String, Object>>();
		
		String sqlCategory = "SELECT c.c_category FROM cos_portal_yw_component c WHERE c_deleted = 0 and c.c_status = 1 GROUP BY c.c_category ";
		List<Map<String, Object>> categoryList = baseDao.query(sqlCategory);
		
		String sqlComp = "select c_id id,c_comp_name text, c_category parentId from cos_portal_yw_component where c_deleted = 0 and c_status = 1";
		List<Map<String, Object>> compList = baseDao.query(sqlComp);
		
		if (Assert.isNotEmpty(categoryList) && categoryList.size() > 0) {
			List<Map<String, Object>> listCategory = categoryCascade(categoryList);
			listRes.addAll(listCategory);
		}
		if(Assert.isNotEmpty(compList) && compList.size() > 0) {
			List<Map<String, Object>> listcomp = compCascade(compList);
			listRes.addAll(listcomp);
		}
		
		List<TreeNode> tree = TreeUtils.convert(listRes).change(Constant.TREE_NODE_ID, "ID")
				.change(Constant.TREE_NODE_PARENT_ID, "PARENTID")
				.change(Constant.TREE_NODE_TEXT, "TEXT").tree();
		
		return tree;
	}
	private List<Map<String, Object>> compCascade(List<Map<String, Object>> compList) {
		for (Map<String, Object> comp : compList) {
			String parent = "";
			if(Assert.isEmpty(comp.get("PARENTID"))) {
				parent = "category_未分类";
			}else {
				String[] split = comp.get("PARENTID").toString().split("/");
				parent = "category_"+split[split.length-1];
			}
			comp.put("PARENTID", parent );
		}
		return compList;
	}
	private List<Map<String, Object>> categoryCascade(List<Map<String, Object>> categoryList) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = new HashMap<String, Object>();
		
		for (Map<String, Object> map : categoryList) {
			String category = MathUtils.stringObj(map.get("C_CATEGORY"));
			String[] categoryArr = category.split("/");
			
			for (int i = 0; i < categoryArr.length; i++) {
				item = new HashMap<String, Object>();
				if (i == 0) {
					item.put("TEXT", Assert.isEmpty(categoryArr[i]) ? "未分类" : categoryArr[i]);
					item.put("PARENTID", "0");
					item.put("ID", "category_"+item.get("TEXT"));
				} else {
					item.put("TEXT", categoryArr[i]);
					item.put("PARENTID", "category_"+categoryArr[i - 1]);
					item.put("ID", "category_"+item.get("TEXT"));
				}
				// 判断是否已经存在值相同的
				boolean isAdd = true;
				for (Map<String, Object> itemT : list) {
					if (itemT.get("TEXT").equals(item.get("TEXT")) && itemT.get("PARENTID").equals(item.get("PARENTID"))) {
						isAdd = false;
					}
				}
				if (isAdd) {
					list.add(item);
				}
			}
		}
		return list;
	}
}
