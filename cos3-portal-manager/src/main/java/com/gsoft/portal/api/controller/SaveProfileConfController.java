package com.gsoft.portal.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.api.dto.ProfileConfigDto;
import com.gsoft.portal.common.constans.ResultConstant;
import com.gsoft.portal.webview.widgetconf.dto.CustomProfileConfDto;
import com.gsoft.portal.webview.widgetconf.dto.ProfileConfDto;
import com.gsoft.portal.webview.widgetconf.service.CustomProfileConfService;
import com.gsoft.portal.webview.widgetconf.service.ProfileConfService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 保存页面偏好设置信息
 * 
 * @author SN
 *
 */
@Api(tags = "站点管理", description = "保存widget系统设置或偏好接口服务")
@RestController
@RequestMapping("/site")
public class SaveProfileConfController {

	@Resource
	ProfileConfService profileConfService;

	@Resource
	CustomProfileConfService customProfileConfService;

	@ApiOperation("保存页面widget偏好设置信息")
	@RequestMapping(value = "/saveProfileConf", method = RequestMethod.GET)
	public ReturnDto saveProfileConf(@RequestParam String json, @RequestParam String widgetUuId, HttpServletRequest request,
			@RequestParam boolean diyMode, @RequestParam(required = false) String delWidgetIds,
			@RequestParam String pageUuId) throws Exception {

		Long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));

		if (diyMode) {
			// 查询是否存在用户个性化widget实例配置信息
			CustomProfileConfDto customProfileConfDto = customProfileConfService.getCustomProfileConfInfo(widgetUuId,
					personnelId);
			if (Assert.isEmpty(customProfileConfDto.getId())) {
				customProfileConfDto = new CustomProfileConfDto();
				customProfileConfDto.setWidgetUuId(widgetUuId);
				customProfileConfDto.setUserId(personnelId);
				customProfileConfDto.setPageUuId(pageUuId);
			}
			customProfileConfDto.setJson(json);
			customProfileConfService.saveCustomProfileConf(customProfileConfDto, delWidgetIds);
		} else {
			// 查询用户是否存在系统widget实例配置信息
			ProfileConfDto profileConfDto = profileConfService.getProfileConfInfo(widgetUuId);
			if (Assert.isEmpty(profileConfDto.getId())) {
				profileConfDto = new ProfileConfDto();
				profileConfDto.setWidgetUuId(widgetUuId);
				profileConfDto.setPageUuId(pageUuId);
			}
			profileConfDto.setJson(json);
			// 查询该widget实例模版是否有被其他widget引用，若有的话需要更新对应的配置信息
			List<ProfileConfDto> relInstanceList = profileConfService
					.getRelInstanceList(profileConfDto.getWidgetUuId());
			profileConfService.saveProfileConf(profileConfDto, delWidgetIds, pageUuId);

			if (!Assert.isEmpty(relInstanceList) && relInstanceList.size() > 0) {
				List<Map<String, Object>> syncList = new ArrayList<Map<String, Object>>();
				Map<String, Object> tmpMap = null;
				for (ProfileConfDto profileDto : relInstanceList) {
					String configJson = profileDto.getJson();
					JSONObject confJo = new JSONObject(configJson);
					JSONObject newConfJo = new JSONObject(json);

					if (confJo.has("widgets")) {
						newConfJo.put("widgets", confJo.get("widgets"));
					}
					profileDto.setJson(newConfJo.toString());

					tmpMap = new HashMap<String, Object>();
					tmpMap.put("C_ID", profileDto.getId());
					tmpMap.put("C_JSON", profileDto.getJson());
					syncList.add(tmpMap);
				}
				if (!Assert.isEmpty(syncList) && syncList.size() > 0) {
					profileConfService.handleBusinessCompInstanceConf(syncList);
				}
			}
		}

		return new ReturnDto("保存成功");
	}

	@ApiOperation("保存页面widget偏好设置信息")
	@RequestMapping(value = "/saveProfileConfByPost", method = RequestMethod.POST)
	public String saveProfileConf(HttpServletRequest request, @RequestBody ProfileConfigDto profileConfigDto)
			throws Exception {

		Long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));
		JSONObject jo = new JSONObject();

		if (profileConfigDto.isDiyMode()) {
			// 查询是否存在用户个性化widget实例配置信息
			CustomProfileConfDto customProfileConfDto = customProfileConfService
					.getCustomProfileConfInfo(profileConfigDto.getWidgetUuId(), personnelId);
			if (Assert.isEmpty(customProfileConfDto.getId())) {
				customProfileConfDto = new CustomProfileConfDto();
				customProfileConfDto.setWidgetUuId(profileConfigDto.getWidgetUuId());
				customProfileConfDto.setUserId(personnelId);
				customProfileConfDto.setPageUuId(profileConfigDto.getPageUuId());
			}
			customProfileConfDto.setJson(profileConfigDto.getJson());
			customProfileConfService.saveCustomProfileConf(customProfileConfDto, profileConfigDto.getDelWidgetIds());
		} else {
			// 查询用户是否存在系统widget实例配置信息
			ProfileConfDto profileConfDto = profileConfService.getProfileConfInfo(profileConfigDto.getWidgetUuId());
			if (Assert.isEmpty(profileConfDto.getId())) {
				profileConfDto = new ProfileConfDto();
				profileConfDto.setWidgetUuId(profileConfigDto.getWidgetUuId());
				profileConfDto.setPageUuId(profileConfigDto.getPageUuId());
			}
			profileConfDto.setJson(profileConfigDto.getJson());
			// 查询该widget实例模版是否有被其他widget引用，若有的话需要更新对应的配置信息
			List<ProfileConfDto> relInstanceList = profileConfService
					.getRelInstanceList(profileConfDto.getWidgetUuId());
			profileConfService.saveProfileConf(profileConfDto, profileConfigDto.getDelWidgetIds(),
					profileConfigDto.getPageUuId());

			if (!Assert.isEmpty(relInstanceList) && relInstanceList.size() > 0) {
				List<Map<String, Object>> syncList = new ArrayList<Map<String, Object>>();
				Map<String, Object> tmpMap = null;
				for (ProfileConfDto profileDto : relInstanceList) {
					String configJson = profileDto.getJson();
					JSONObject confJo = new JSONObject(configJson);
					JSONObject newConfJo = new JSONObject(profileConfigDto.getJson());

					if (confJo.has("widgets")) {
						newConfJo.put("widgets", confJo.get("widgets"));
					}
					profileDto.setJson(newConfJo.toString());

					tmpMap = new HashMap<String, Object>();
					tmpMap.put("C_ID", profileDto.getId());
					tmpMap.put("C_JSON", profileDto.getJson());
					syncList.add(tmpMap);
				}
				if (!Assert.isEmpty(syncList) && syncList.size() > 0) {
					profileConfService.handleBusinessCompInstanceConf(syncList);
				}
			}
		}
		jo.put("status", ResultConstant.RESULT_RETURN_OK_STATUS);

		return jo.toString();
	}
}
