package com.gsoft.portal.webview.widget.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
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
import com.gsoft.portal.common.constans.ParameterConstant;
import com.gsoft.portal.system.basicdata.service.ParameterService;
import com.gsoft.portal.webview.widget.dto.WidgetDto;
import com.gsoft.portal.webview.widget.entity.WidgetEntity;
import com.gsoft.portal.webview.widget.persistence.WidgetPersistence;
import com.gsoft.portal.webview.widget.service.WidgetService;

/**
 * widget管理Service实现类
 * 
 * @author SN
 *
 */
@Service
public class WidgetServiceImpl implements WidgetService {
	
	@Resource
	WidgetPersistence widgetPersistence;

	@Resource
	BaseDao baseDao;
	
	@Resource
	ParameterService parameterService;

	@Override
	public PageDto queryWidgetDataTable(String search, Integer page, Integer size, String sortProp, String order) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM cos_portal_widget where 1=1 ");

		Map<String, Object> params = new HashMap<String, Object>();

		if (Assert.isNotEmpty(search)) {
			sb.append(" AND (c_code like ${search} or c_name like ${search} or c_title like ${search}) ");
			params.put("search", "%" + search + "%");
		}
		sb.append(" ORDER BY c_id DESC ");

		PageDto pageDto = baseDao.query(page, size, sb.toString(), params);
		return pageDto;
	}

	@Override
	public WidgetDto getWidgetInfoById(Long id) {
		WidgetEntity entity = widgetPersistence.findOne(id);
		WidgetDto dto = BeanUtils.convert(entity, WidgetDto.class);
		return dto;
	}

	@Override
	public Boolean isExitWidgetCode(Long id, String code, String projectCode) {
		
		WidgetEntity entity = null;
		
		if (Assert.isEmpty(id)) {
			entity = widgetPersistence.findByCode(code);
		}else {
			entity = widgetPersistence.findByCode(code,id);
		}
		
		if (entity != null) {
			return true;
		}

		return false;
	}

	@Override
	public WidgetDto saveWidget(WidgetDto widgetDto) {
		WidgetEntity entity = BeanUtils.convert(widgetDto, WidgetEntity.class);
		WidgetEntity reEntity = widgetPersistence.save(entity);
		return BeanUtils.convert(reEntity, WidgetDto.class);
	}

	@Override
	public void delWidget(Long id, String code) {
		
		WidgetEntity entity = widgetPersistence.findOne(id);
		baseDao.delete("cos_portal_widget", "c_id", id);
		
		//删除成功成功后，移除服务器上对应目录包文件
		if (Assert.isNotEmpty(entity.getIsImp()) && "1".equals(entity.getIsImp())) {
			String destDir = parameterService.getParmValueByKey(ParameterConstant.PORTAL_MODULES_PATH[0], ParameterConstant.PORTAL_MODULES_PATH[1]);
			
			if (!destDir.endsWith("/")) {
				destDir = destDir + "/";
			}
			
			destDir = destDir + entity.getProjectCode() + "/widgets/"+ code + "/";
			File toFile = new File(destDir);
			FileUtils.deleteQuietly(toFile);
		}
	}

	@Override
	public List<WidgetDto> getWidgetList() {
		
		List<WidgetEntity> entityList = widgetPersistence.getWidgetList();
		
		return BeanUtils.convert(entityList, WidgetDto.class);
	}

	@Override
	public WidgetDto getWidgetInfoByCode(String widgetCode) {
		WidgetEntity entity = widgetPersistence.findByCode(widgetCode);
		return BeanUtils.convert(entity, WidgetDto.class);
	}

	@Override
	public String getCatlogWidgetList() throws JSONException {
		List<WidgetEntity> entityList = widgetPersistence.getWidgetList();
		if (!Assert.isEmpty(entityList) && entityList.size() > 0) {
			Map<String, Object> moduleMap = new HashMap<String, Object>();
			for(WidgetEntity entity : entityList) {
				moduleMap.put(Assert.isEmpty(entity.getProjectCode()) ? "" : entity.getProjectCode(), entity.getCategory());
			}
			
			if (moduleMap.size() > 0) {
				// 先组装系统widget分类
				JSONArray joArr = new JSONArray();
				
				for(Map.Entry<String, Object> entry:moduleMap.entrySet())
				{
					catlogWidgetList(joArr, entityList, entry.getKey(), MathUtils.stringObj(entry.getValue()));
				}
				return joArr.toString();
			}
		}
		
		return null;
	}

	/**
	 * 组装分类widget集合信息
	 * @param joArr
	 * @param entityList
	 * @param projectCode
	 * @throws JSONException 
	 */
	private void catlogWidgetList(JSONArray joArr, List<WidgetEntity> entityList, String projectCode, String moduleName) throws JSONException {
		
		JSONObject pJson = new JSONObject();
		pJson.put("value", projectCode);
		pJson.put("label", moduleName);
		JSONArray childMenu = new JSONArray();
		for (WidgetEntity entity : entityList) {
			JSONObject jsonMenu = new JSONObject();
			if (!entity.getIsNested() && projectCode.equals(Assert.isEmpty(entity.getProjectCode()) ? "" : entity.getProjectCode())) {
				jsonMenu.put("value", entity.getCode());
				jsonMenu.put("label", entity.getName());
				childMenu.put(jsonMenu);
			}
		}
		pJson.put("children", childMenu);
		joArr.put(pJson);
	}
	
	
	/**
	 *  修改widget code 信息
	 */
	@Override
	public void udpWidgetCode() {
		String tableNames[] = {"cos_widget_instance","cos_custom_widget_instance","cos_sys_profile","cos_custom_profile"};
		String sql = "";
		String newJson = "";
		String json = "";
		Map<String, Object> map =new HashMap<String, Object>();
		for(int i = 0 ;i < tableNames.length;i++) {
			sql = "select c_id, c_json from "+tableNames[i]+"";
			List<Map<String, Object>> values = baseDao.query(sql, map);
			for(int j = 0; j< values.size();j++) {
				Map<String, Object> value = values.get(j);
				if(Assert.isNotEmpty(value.get("c_json"))) {
				json = value.get("c_json").toString();
				if(json.contains("\"name\":\"table-browser\"") || json.contains("\"name\":\"form-browser\"") || json.contains("\"name\":\"tree-browser\"") || json.contains("\"name\":\"widget-form\"")) {
					newJson = json.replaceAll("\"name\":\"table-browser\"", "\"name\":\"appFactory/widgets/table-browser\"")
						.replaceAll("\"name\":\"form-browser\"", "\"name\":\"appFactory/widgets/form-browser\"")
						.replaceAll("\"name\":\"tree-browser\"", "\"name\":\"appFactory/widgets/tree-browser\"")
						.replaceAll("\"name\":\"widget-form\"", "\"name\":\"appFactory/widgets/widget-form\"");
					
					sql="update "+tableNames[i]+" set c_json = '"+newJson+"' where c_id = "+value.get("c_id");
					baseDao.update(sql);
				}
			}
			}
		}
	}

	@Override
	public ReturnDto getCategoryWidgetTree(String model) throws JSONException {
		
		StringBuffer sb = new StringBuffer();
		JSONArray joArr = new JSONArray();// 组装返回数据
		
		if (Assert.isEmpty(model)) { // 查询系统技术类+业务类
			sb.append("SELECT w.c_category FROM cos_portal_widget w ");
			sb.append("WHERE EXISTS (SELECT c.* FROM cos_portal_component c ");
			sb.append("WHERE c.c_code = w.c_project_code AND c.c_status = 1) ");
			sb.append("GROUP BY w.c_category ");
			sb.append("UNION ");
			sb.append("SELECT c.c_category FROM cos_portal_yw_component c WHERE c.c_status = 1 GROUP BY c.c_category ");
			
		} else if ("sys".equals(model)) { // 查询技术类
			sb.append("SELECT w.c_category FROM cos_portal_widget w ");
			sb.append("WHERE EXISTS (SELECT c.* FROM cos_portal_component c ");
			sb.append("WHERE c.c_code = w.c_project_code AND c.c_status = 1) and w.c_is_business = 0 ");
			sb.append("GROUP BY w.c_category ");
			
		} else { // 查询业务类
			sb.append("SELECT c.c_category FROM cos_portal_yw_component c WHERE c.c_status = 1 GROUP BY c.c_category ");
		}
		
		List<Map<String, Object>> categoryList = baseDao.query(sb.toString());
		
		if (!Assert.isEmpty(categoryList) && categoryList.size() > 0) {
			treeMenuList(categoryList, joArr);
		}
		return new ReturnDto(joArr.toString());
	}

	/**
	 * 解析列表数据转为树
	 * @param categoryList
	 * @param parentId
	 * @return
	 * @throws JSONException 
	 */
	private void treeMenuList(List<Map<String, Object>> categoryList, JSONArray treeArr) throws JSONException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = new HashMap<String, Object>();
		
		for (Map<String, Object> map : categoryList) {
			String category = MathUtils.stringObj(map.get("C_CATEGORY"));
			String[] categoryArr = category.split("/");
			
			for (int i = 0; i < categoryArr.length; i++) {
				item = new HashMap<String, Object>();
				if (i == 0) {
					item.put("category", Assert.isEmpty(categoryArr[i]) ? "" : categoryArr[i]);
					item.put("parent", "0");
				} else {
					item.put("category", categoryArr[i]);
					item.put("parent", categoryArr[i - 1]);
				}
				// 判断是否已经存在值相同的
				boolean isAdd = true;
				for (Map<String, Object> itemT : list) {
					if (itemT.get("category").equals(item.get("category")) && itemT.get("parent").equals(item.get("parent"))) {
						isAdd = false;
					}
				}
				if (isAdd) {
					list.add(item);
				}
			}
		}
		
		for (Map<String, Object> itemT : list) {
			if ("0".equals(itemT.get("parent"))) {
				JSONObject jsonMenu = new JSONObject();
				jsonMenu.put("category", itemT.get("category"));
				jsonMenu.put("parent", itemT.get("parent"));
				treeArr.put(jsonMenu);
				iteratorMenuTree(jsonMenu, list);
			}
		}
	}

	/**
	 * 遍历已有菜单节点并组装
	 * @param categoryName
	 * @param treeArr
	 * @throws JSONException 
	 */
	private void iteratorMenuTree(JSONObject jsonMenu, List<Map<String, Object>> list) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		jsonMenu.put("children", jsonArray);
		for (Map<String, Object> itemT : list) {
			if (jsonMenu.get("category").equals(itemT.get("parent"))) {
				JSONObject jsonMenuT = new JSONObject();
				jsonMenuT.put("category", itemT.get("category"));
				jsonMenuT.put("parent", itemT.get("parent"));
				jsonArray.put(jsonMenuT);
				iteratorMenuTree(jsonMenuT, list);
			}
		}
	}

	@Override
	public ReturnDto getWidgetListByCategory(String model, String category, Long personnelId) {
		StringBuffer sb = new StringBuffer();
		List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();
		Map<String, Object> params = new HashMap<String, Object>();
		
		if (Assert.isEmpty(model)) { // 查询系统技术类+业务类
			sb.append("SELECT w.c_id, w.c_code, w.c_name, w.c_desc, w.c_params FROM cos_portal_widget w ");
			sb.append("WHERE EXISTS (SELECT c.* FROM cos_portal_component c ");
			sb.append("WHERE c.c_code = w.c_project_code AND c.c_status = 1) and w.c_is_business = 0 ");
			if (Assert.isEmpty(category)) {
				sb.append("and w.c_category is NULL ");
			} else {
				sb.append("and w.c_category = ${category} ");
				params.put("category", category);
			}
			sb.append(" UNION ALL ");
			getYwCompSqlString(personnelId, sb, category);
			params.put("personnelId", personnelId);
			
		} else if ("sys".equals(model)) { // 查询技术类
			sb.append("SELECT w.c_id, w.c_code, w.c_name, w.c_desc, w.c_params FROM cos_portal_widget w ");
			sb.append("WHERE EXISTS (SELECT c.* FROM cos_portal_component c ");
			sb.append("WHERE c.c_code = w.c_project_code AND c.c_status = 1) and w.c_is_business = 0 ");
			if (Assert.isEmpty(category)) {
				sb.append("and w.c_category is NULL ");
			} else {
				sb.append("and w.c_category = ${category} ");
				params.put("category", category);
			}
			
		} else { // 查询业务类
			// 匹配查找授权了的业务组件信息 授权
			getYwCompSqlString(personnelId, sb, category);
			params.put("personnelId", personnelId);
			params.put("category", category);
		}
		List<Map<String, Object>> resList = baseDao.query(sb.toString(), params);

		if (!Assert.isEmpty(resList) && resList.size() > 0) {
			Map<String, Object> map = null;
			// 查询嵌套widget信息
			WidgetDto widgetDto = this.getWidgetInfoByCode("nested");
			
			for (Map<String, Object> tmpMap : resList) {
				map = new HashMap<String, Object>();
				map.put("id", System.nanoTime());
				if (Assert.isEmpty(tmpMap.get("C_CODE"))) {
					map.put("compId", tmpMap.get("C_ID"));
				}
				map.put("name", Assert.isEmpty(tmpMap.get("C_CODE")) ? "nested" : tmpMap.get("C_CODE"));
				map.put("title", tmpMap.get("C_NAME"));
				map.put("description", tmpMap.get("C_DESC"));
				map.put("singleton", false);
				map.put("params", Assert.isEmpty(tmpMap.get("C_PARAMS")) ? widgetDto.getParams() : tmpMap.get("C_PARAMS"));
				rtnList.add(map);
			}
		}
		return new ReturnDto(rtnList);
	}

	private void getYwCompSqlString(Long personnelId, StringBuffer sb, String category) {
		sb.append("SELECT c.c_id, '' as c_code, c.c_comp_name as c_name, c.c_comp_desc as c_desc, '' as c_params FROM cos_portal_yw_component c ");
		sb.append("WHERE c.c_status = 1 ");
		if (Assert.isEmpty(category)) {
			sb.append("and c.c_category is NULL ");
		} else {
			sb.append("and c.c_category = ${category} ");
		}
		
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
		if (Assert.isEmpty(category)) {
			sb.append("and w.c_category is NULL ");
		} else {
			sb.append("and w.c_category = ${category} ");
		}
	}
}
