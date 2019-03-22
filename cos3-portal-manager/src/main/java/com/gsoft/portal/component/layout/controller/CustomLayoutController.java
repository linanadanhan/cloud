package com.gsoft.portal.component.layout.controller;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.common.constans.ResultConstant;
import com.gsoft.portal.component.layout.dto.CustomLayoutDto;
import com.gsoft.portal.component.layout.service.CustomLayoutService;
import com.gsoft.portal.webview.page.dto.DiySitePageDto;
import com.gsoft.portal.webview.page.service.DiySitePageService;
import com.gsoft.portal.webview.page.service.SitePageService;
import com.gsoft.portal.webview.widgetconf.dto.CustomProfileConfDto;
import com.gsoft.portal.webview.widgetconf.dto.ProfileConfDto;
import com.gsoft.portal.webview.widgetconf.service.CustomProfileConfService;
import com.gsoft.portal.webview.widgetconf.service.ProfileConfService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 自定义布局
 * 
 * @author SN
 *
 */
@Api(tags = "布局器管理", description = "自定义布局接口服务")
@RestController
@RequestMapping("/customLayout")
public class CustomLayoutController {

	@Resource
	CustomLayoutService customLayoutService;
	
	@Resource
	SitePageService sitePageService;
	
	@Resource
	ProfileConfService profileConfService;
	
	@Resource
	CustomProfileConfService customProfileConfService;
	
	@Resource
	DiySitePageService diySitePageService;

	@ApiOperation("保存用户自定义布局信息")
	@RequestMapping(value = "/changeLayout", method = RequestMethod.GET)
	public String changeLayout(@RequestParam String pageUuId, @RequestParam String layoutCode, @RequestParam boolean isDefault,
			ServletRequest servletRequest) throws JSONException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));
		JSONObject jo = new JSONObject();

		if (isDefault) {
			// 判断是否有自定义页面
			DiySitePageDto diySitePageDto = null;
			if (!Assert.isEmpty(personnelId)) {
				diySitePageDto = diySitePageService.getDiySitePageInfoByUuId(personnelId, pageUuId);
			}
			if (!Assert.isEmpty(diySitePageDto) && !Assert.isEmpty(diySitePageDto.getId())) {
				diySitePageService.changeLayout(pageUuId, layoutCode, personnelId);
			} else {
				sitePageService.changeLayout(pageUuId, layoutCode);
			}
			
		}else {
			CustomLayoutDto customLayoutDto = new CustomLayoutDto();
			customLayoutDto.setUserId(personnelId);
			customLayoutDto.setPageUuId(pageUuId);
			customLayoutDto.setLayoutCode(layoutCode);
			customLayoutService.saveCustomLayout(customLayoutDto);
		}
		jo.put("status", ResultConstant.RESULT_RETURN_OK_STATUS);
		
		return jo.toString();
	}
	
	@ApiOperation("面板切换布局信息")
	@RequestMapping(value = "/changeNestedLayout", method = RequestMethod.GET)
	public String changeNestedLayout(@RequestParam String widgetId, @RequestParam String layoutCode, @RequestParam boolean isDefault,
			ServletRequest servletRequest) throws Exception {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));
		JSONObject jo = new JSONObject();

		if (isDefault) {
			ProfileConfDto profileConfDto = profileConfService.getProfileConfInfo(widgetId);
			
			if (!Assert.isEmpty(profileConfDto) && !Assert.isEmpty(profileConfDto.getId())) {
				String json = profileConfDto.getJson();
				JSONObject jsonObj = new JSONObject(json);
				jsonObj.put("changeLayout", layoutCode);
				
				profileConfDto.setJson(jsonObj.toString());
				profileConfService.saveProfileConf(profileConfDto, null, null);
			}else {
				ProfileConfDto dto = new ProfileConfDto();
				dto.setWidgetUuId(widgetId);
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("changeLayout", layoutCode);
				dto.setJson(jsonObj.toString());
				profileConfService.saveProfileConf(dto, null, null);
			}
			
		}else {
			CustomProfileConfDto customProfileConfDto = customProfileConfService.getCustomProfileConfInfo(widgetId,personnelId);
			
			if (!Assert.isEmpty(customProfileConfDto) && !Assert.isEmpty(customProfileConfDto.getId())) {
				String json = customProfileConfDto.getJson();
				JSONObject jsonObj = new JSONObject(json);
				jsonObj.put("changeLayout", layoutCode);
				
				customProfileConfDto.setJson(jsonObj.toString());
				customProfileConfService.saveCustomProfileConf(customProfileConfDto, null);
			}else {
				CustomProfileConfDto dto = new CustomProfileConfDto();
				dto.setWidgetUuId(widgetId);
				dto.setUserId(personnelId);
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("changeLayout", layoutCode);
				dto.setJson(jsonObj.toString());
				customProfileConfService.saveCustomProfileConf(dto, null);
			}
		}
		jo.put("status", ResultConstant.RESULT_RETURN_OK_STATUS);
		
		return jo.toString();
	}

}
