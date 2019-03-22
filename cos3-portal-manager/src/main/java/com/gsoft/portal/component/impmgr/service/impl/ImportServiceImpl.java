package com.gsoft.portal.component.impmgr.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

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
import com.gsoft.portal.component.impmgr.dto.ImportDto;
import com.gsoft.portal.component.impmgr.entity.ImportEntity;
import com.gsoft.portal.component.impmgr.persistence.ImportPersistence;
import com.gsoft.portal.component.impmgr.service.ImportService;
import com.gsoft.portal.component.layout.dto.LayoutDto;
import com.gsoft.portal.component.layout.service.LayoutService;
import com.gsoft.portal.component.theme.dto.ThemeDto;
import com.gsoft.portal.component.theme.service.ThemeService;
import com.gsoft.portal.system.basicdata.service.ParameterService;
import com.gsoft.portal.webview.widget.dto.WidgetDto;
import com.gsoft.portal.webview.widget.service.WidgetService;

/**
 * 导入管理Service实现类
 * 
 * @author SN
 *
 */
@Service
public class ImportServiceImpl implements ImportService {

	@Resource
	BaseDao baseDao;
	
	@Resource
	ParameterService parameterService;
	
	@Resource
	ImportPersistence importPersistence;
	
	@Resource
	ThemeService themeService;
	
	@Resource
	LayoutService layoutService;
	
	@Resource
	DecorateService decorateService;
	
	@Resource
	WidgetService widgetService;

	@Override
	public PageDto queryImportData(String search, Integer page, Integer size, String sortProp, String order) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT r.c_id,r.c_file_name,r.c_file_alias,r.c_fail_reason,DATE_FORMAT(r.c_create_time,'%Y-%m-%d %H:%i:%S') as c_create_time,r.c_result,p.c_name ");
		sb.append("FROM cos_import_record r LEFT JOIN cos_sys_personnel p ON r.c_create_by = p.c_id where 1=1");

		Map<String, Object> params = new HashMap<String, Object>();

		if (Assert.isNotEmpty(search)) {
			sb.append(" AND r.c_file_name like ${search} ");
			params.put("search", "%" + search + "%");
		}
		sb.append(" ORDER BY r.c_id DESC ");

		PageDto pageDto = baseDao.query(page, size, sb.toString(), params);
		return pageDto;
	}

	@Override
	public ImportDto saveImportRecord(ImportDto importDto) {
		ImportEntity entity = BeanUtils.convert(importDto, ImportEntity.class);
		ImportEntity reEntity = importPersistence.save(entity);
		return BeanUtils.convert(reEntity, ImportDto.class);
	}

	@Override
	public boolean handleImportData(JSONObject jsonObj) throws JSONException {
		
		//主题信息
		JSONArray themeArray = jsonObj.getJSONArray("themes");
		String projectCode = jsonObj.getString("projectCode");
		for(int i=0;i<themeArray.length();i++){
			JSONObject obj = themeArray.getJSONObject(i);
			if (!obj.has("isCover") ||(obj.has("isCover") && "1".equals(obj.get("isCover")))) {
				String themeCode = obj.getString("name");
				// 兼容V2.0 处理
				if (jsonObj.has("main")) {
					themeCode = projectCode + "." + themeCode;
				}
				baseDao.update("DELETE FROM cos_portal_theme WHERE c_code = ? ",themeCode);
				
				ThemeDto themeDto = new ThemeDto();
				themeDto.setCode(themeCode);
				themeDto.setName(obj.getString("title"));
				themeDto.setProjectCode(projectCode);
				themeDto.setIsOpen(obj.has("isOpen") ? obj.getString("isOpen") : "1");
				themeDto.setIsSystem(false);
				themeDto.setIsImp("1");
				themeService.saveTheme(themeDto);
			}
		}
		
		//布局信息
		JSONArray layoutArray = jsonObj.getJSONArray("layouts");
		for(int i=0;i<layoutArray.length();i++){
			JSONObject obj = layoutArray.getJSONObject(i);
			if (!obj.has("isCover") ||(obj.has("isCover") && "1".equals(obj.get("isCover")))) {
				String layoutCode = obj.getString("name");
				// 兼容V2.0 处理
				if (jsonObj.has("main")) {
					layoutCode = projectCode + "." + layoutCode;
				}
				baseDao.update("DELETE FROM cos_portal_layout WHERE c_code = ? ",layoutCode);
				
				LayoutDto layoutDto = new LayoutDto();
				layoutDto.setCode(layoutCode);
				layoutDto.setName(obj.getString("title"));
				layoutDto.setProjectCode(projectCode);
				layoutDto.setIsSystem(false);
				layoutDto.setIsImp("1");
				layoutService.saveLayout(layoutDto);
			}
		}
		
		//修饰器
		JSONArray decorateArray = jsonObj.getJSONArray("decorators");
		for(int i=0;i<decorateArray.length();i++){
			JSONObject obj = decorateArray.getJSONObject(i);
			if (!obj.has("isCover") ||(obj.has("isCover") && "1".equals(obj.get("isCover")))) {
				String decorateCode = obj.getString("name");
				// 兼容V2.0 处理
				if (jsonObj.has("main")) {
					decorateCode = projectCode + "." + decorateCode;
				}
				baseDao.update("DELETE FROM cos_portal_decorate WHERE c_code = ? ",decorateCode);
				
				DecorateDto decorateDto = new DecorateDto();
				decorateDto.setCode(decorateCode);
				decorateDto.setName(obj.getString("title"));
				decorateDto.setProjectCode(projectCode);
				decorateDto.setIsSystem(false);
				decorateDto.setIsImp("1");
				decorateService.saveDecorate(decorateDto);
			}
		}
		
		//widget
		JSONArray widgetArray = jsonObj.getJSONArray("widgets");
		for(int i=0;i<widgetArray.length();i++){
			JSONObject obj = widgetArray.getJSONObject(i);
			if (!obj.has("isCover") ||(obj.has("isCover") && "1".equals(obj.get("isCover")))) {
				String widgetCode = obj.getString("name");
				// 兼容V2.0 处理
				if (jsonObj.has("main")) {
					widgetCode = projectCode + "." + widgetCode;
				}
				baseDao.update("DELETE FROM cos_portal_widget WHERE c_code = ? ",widgetCode);
				
				WidgetDto widgetDto = new WidgetDto();
				widgetDto.setCode(widgetCode);
				widgetDto.setName(obj.getString("title"));
				widgetDto.setProjectCode(projectCode);
				widgetDto.setIsSystem(false);
				widgetDto.setIsNested((obj.has("isNested") ? obj.getBoolean("isNested") : false));
				widgetDto.setIsImp("1");
				widgetDto.setDesc((obj.has("description") ? obj.getString("description") : ""));
				widgetDto.setIsBusiness((obj.has("business") ? ("1".equals(obj.getString("business")) ? true : false) : false));
				widgetDto.setTitle(obj.has("title") ? obj.getString("title") : "");
				widgetDto.setParams((obj.has("params") ? MathUtils.stringObj(obj.get("params")) : ""));
				
				String category = "";
				if (obj.has("category") && !Assert.isEmpty(obj.get("category"))) {
					category = MathUtils.stringObj(obj.get("category"));
				}
				widgetDto.setCategory(category);
				widgetService.saveWidget(widgetDto);
			}
		}

		return true;
	}

}
