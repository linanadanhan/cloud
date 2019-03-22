package com.gsoft.portal.component.compmgr.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.component.compmgr.entity.BusinessCompConfEntity;
import com.gsoft.portal.component.compmgr.persistence.BusinessCompConfPersistence;
import com.gsoft.portal.component.compmgr.service.BusinessCompConfService;
import com.gsoft.portal.webview.widgetconf.entity.ProfileConfEntity;
import com.gsoft.portal.webview.widgetconf.persistence.ProfileConfPersistence;
import com.gsoft.portal.webview.widgetconf.service.WidgetConfService;

/**
 * 业务组件配置Service实现类
 * 
 * @author SN
 *
 */
@Service
public class BusinessCompConfServiceImpl implements BusinessCompConfService {

	@Resource
	BaseDao baseDao;
	
	@Resource
	BusinessCompConfPersistence businessCompConfPersistence;
	
	@Resource
	ProfileConfPersistence profileConfPersistence;
	
	@Resource
	WidgetConfService widgetConfService;
	
	@Override
	@Transactional
	public void saveBusinessCompConf(Long compId, String widgetIds) {
		
		// 查询之前是否已存在对应数据
		BusinessCompConfEntity businessCompConfEntity = businessCompConfPersistence.getBusinessCompConfByCompId(compId);
		if (!Assert.isEmpty(businessCompConfEntity)) {
			businessCompConfEntity.setJson(widgetIds);
		}else {
			businessCompConfEntity = new BusinessCompConfEntity();
			businessCompConfEntity.setCompId(compId);
			businessCompConfEntity.setJson(widgetIds);
		}
		businessCompConfPersistence.save(businessCompConfEntity);
	}

	@Override
	public ReturnDto getBusinessCompConfInfo(Long compId) throws JSONException {
		JSONObject rtnJo = new JSONObject();
		
		//1. 根据组件ID获取系统配置的嵌套widget实例ID
		BusinessCompConfEntity businessCompConfEntity = businessCompConfPersistence.getBusinessCompConfByCompId(compId);
		
		JSONObject dataJo = new JSONObject();
		// 设置默认页面模式和布局
		dataJo.put("mode", "standard");
		dataJo.put("layout", "default");
		
		if (!Assert.isEmpty(businessCompConfEntity)) {
			String json = businessCompConfEntity.getJson();
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
	@Transactional
	public ReturnDto copyBusinessCompConf(Long compId, String widgetUuId) throws JSONException {
		//1. 根据组件ID获取系统配置的嵌套widget实例ID
		BusinessCompConfEntity businessCompConfEntity = businessCompConfPersistence.getBusinessCompConfByCompId(compId);
		
		JSONObject dataJo = new JSONObject();
		JSONObject rtnJo = new JSONObject();
		// 设置默认页面模式和布局
		dataJo.put("mode", "standard");
		dataJo.put("layout", "default");
		
		if (!Assert.isEmpty(businessCompConfEntity)) {
			String json = businessCompConfEntity.getJson();
			if (!Assert.isEmpty(json)) {
				JSONArray jsonArr = new JSONArray(json);
    			
    			// 嵌套widget下的组件信息
    			JSONArray newJsonArr = new JSONArray();
    			// 嵌套widget的参数信息
    			JSONObject newWidgetParamJsonObj = new JSONObject();
    			// 保存新实例化的widgetId 与 组件模版中实例Id关系
    			JSONObject relWidgetObj = new JSONObject();
    			
    			widgetConfService.copyBusinessCompConf(jsonArr, newJsonArr, relWidgetObj, newWidgetParamJsonObj, true);
    			
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
					// 关联实例ID 用于修改配置时同步修改
					profileConfEntity.setTmpWidgetUuId(relWidgetObj.getString(key));
					widgetConfList.add(profileConfEntity);
				}
    			// 新增业务widget的配置数据
    			profileConfEntity = new ProfileConfEntity();
    			profileConfEntity.setWidgetUuId(widgetUuId);
    			
    			JSONObject jo = new JSONObject();
    			
    			jo.put("layout", "default");
    			jo.put("decorator", "none");
    			jo.put("widgetIco", "");
    			jo.put("transparency", 100);
    			jo.put("text", "面板");
    			jo.put("title", "面板");
    			jo.put("openMin", false);
    			jo.put("openMax", false);
    			jo.put("changeLayout", false);
    			jo.put("widgets", newJsonArr);
    			
    			profileConfEntity.setJson(jo.toString());
    			widgetConfList.add(profileConfEntity);
    			profileConfPersistence.save(widgetConfList);
    			
    			newWidgetParamJsonObj.put(widgetUuId, jo);
    			dataJo.put("widgets", newJsonArr);
        		dataJo.put("widgetParams", newWidgetParamJsonObj);
        		rtnJo.put("widgetParams", newWidgetParamJsonObj);
			}
		}
		rtnJo.put("nestedPage", dataJo);
		return new ReturnDto(rtnJo.toString());
	}
	
}
