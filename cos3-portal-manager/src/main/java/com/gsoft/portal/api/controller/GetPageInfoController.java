package com.gsoft.portal.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.auth.service.PortalAuthService;
import com.gsoft.portal.common.constans.ResultConstant;
import com.gsoft.portal.component.layout.service.CustomLayoutService;
import com.gsoft.portal.component.theme.service.CustomThemeService;
import com.gsoft.portal.webview.page.service.SitePageHelpService;
import com.gsoft.portal.webview.page.service.SitePageService;
import com.gsoft.portal.webview.site.service.SiteService;
import com.gsoft.portal.webview.widget.dto.WidgetDto;
import com.gsoft.portal.webview.widget.service.WidgetService;
import com.gsoft.portal.webview.widgetconf.dto.CustomWidgetConfDto;
import com.gsoft.portal.webview.widgetconf.service.CustomWidgetConfService;
import com.gsoft.portal.webview.widgetconf.service.WidgetConfService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 获取页面详情信息
 * 
 * @author SN
 *
 */
@Api(tags = "站点管理", description = "获取页面信息接口服务")
@RestController
@RequestMapping("/site")
public class GetPageInfoController {

	@Resource
	SitePageService sitePageService;

	@Resource
	SiteService siteService;

	@Resource
	WidgetConfService widgetConfService;

	@Resource
	CustomWidgetConfService customWidgetConfService;

	@Resource
	CustomThemeService customThemeService;

	@Resource
	CustomLayoutService customLayoutService;

	@Resource
	SitePageHelpService sitePageHelpService;
	
	@Resource
	PortalAuthService portalAuthService;
	
	@Resource
	WidgetService widgetService;

	@ApiOperation("获取页面明细数据")
	@RequestMapping(value = "/pageInfo", method = RequestMethod.GET)
	public String getPageInfo(@RequestParam String path, @RequestParam(required = false) boolean isDefault,
			HttpServletRequest request) throws Exception {

		Long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));
		JSONObject jo = new JSONObject();// 返回结果
		jo.put("status", ResultConstant.RESULT_RETURN_OK_STATUS);

		// path 解析
		String[] paramArr = StringUtils.split(path, "/");
		String siteCode = paramArr[0];
		String group = paramArr[1];
		siteCode = paramArr[0];
		String type = "public".equals(group) ? "1" : "0";
		String toPath = path.substring(path.indexOf(group) + group.length());
		
		// 规避zuul传过来的用户ID为空，但又是访问私有页面问题
		if ("private".equals(group) && Assert.isEmpty(personnelId)) {
			jo.put("status", ResultConstant.RESULT_RETURN_NO_LOGIN_STATUS);
			jo.put("data", ResultConstant.RESULT_RETURN_NO_LOGIN_MSG);
			return jo.toString();
		}

		// 页面信息(布局、主题)
		Map<String, Object> sitePageInfo = sitePageService.getSitePageInfoByPath(personnelId, toPath, type,
				siteCode);
		
		JSONObject dataJo = new JSONObject();
		if (Assert.isNotEmpty(sitePageInfo)) {
			
			// 页面主题
			Map<String, Object> themeMap = new HashMap<String, Object>();
			if (Assert.isEmpty(personnelId)) {
				themeMap.put("name", sitePageInfo.get("C_PUBLIC_THEME"));
				themeMap.put("system", sitePageInfo.get("C_PUBLIC_THEME_SYSTEM"));
				themeMap.put("mode", sitePageInfo.get("C_THEME_STYLE"));

			} else {
				// 查询用户自定义主题信息
				Map<String, Object> customThemeMap = customThemeService
						.getCustomThemeInfo(MathUtils.stringObj(sitePageInfo.get("C_SITE_CODE")), personnelId);

				if (Assert.isEmpty(customThemeMap) || isDefault) {
					themeMap.put("name", sitePageInfo.get("C_PRIVATE_THEME"));
					themeMap.put("system", sitePageInfo.get("C_PRIVATE_THEME_SYSTEM"));
				} else {
					themeMap.put("name", customThemeMap.get("C_THEME_CODE"));
					themeMap.put("system", customThemeMap.get("C_IS_SYSTEM"));
				}

				themeMap.put("mode", sitePageInfo.get("C_THEME_STYLE"));
			}
			dataJo.put("mode", themeMap.get("mode"));

			// 页面名称
			dataJo.put("title", sitePageInfo.get("C_NAME"));
			// 页面UuId
			dataJo.put("pageUuId", sitePageInfo.get("C_UU_ID"));
			// widget个性配置
			dataJo.put("allowWidget", sitePageInfo.get("C_ALLOW_WIDGET"));
			dataJo.put("allowLayout", sitePageInfo.get("C_ALLOW_LAYOUT"));

			// 查询页面的帮助信息
			List<Map<String, Object>> pageHelp = sitePageHelpService.queryPageHelp(siteCode,
					(String) sitePageInfo.get("C_UU_ID"));
			dataJo.put("pageHelp", pageHelp);
			
			// widgets 数据集合
			JSONArray jsonArr = new JSONArray();
			
			// widgetParams widget实例参数信息
			JSONObject widgetParamJsonObj = new JSONObject();
			
			// 页面权限校验 私有页面
			if ("private".equals(group) && !Assert.isEmpty(personnelId)) {
				boolean isShow = portalAuthService.checkPermission(MathUtils.stringObj(sitePageInfo.get("C_SHOW")), personnelId);
				
				if (!isShow) {
					// 页面无权限时展示无权限widget
					long randomWidgetId = System.nanoTime();
					
					JSONArray noPermissionAr = new JSONArray();
					JSONObject noPermissionJo = new JSONObject();
					noPermissionJo.put("id", randomWidgetId);
					noPermissionJo.put("name", "no-permission");// 无权限widget
					noPermissionAr.put(noPermissionJo);
					jsonArr.put(noPermissionAr);
					
					WidgetDto widgetDto = widgetService.getWidgetInfoByCode("no-permission");
					
					dataJo.put("layout", "default");// 默认布局
					dataJo.put("widgets", jsonArr);
					
					widgetParamJsonObj.put(randomWidgetId+"", new JSONObject(widgetDto.getParams()));
					dataJo.put("widgetParams", widgetParamJsonObj);
					jo.put("data", dataJo);
					return jo.toString();
				}
			}
			
			// 布局个性配置
			// 页面布局
			Map<String, Object> layoutMap = new HashMap<String, Object>();

			// 用户非空时查询用户是否有自定义的布局
			Map<String, Object> customLayoutMap = null;
			if (Assert.isNotEmpty(personnelId)) {
				customLayoutMap = customLayoutService
						.getCustomLayoutInfo(MathUtils.stringObj(sitePageInfo.get("C_UU_ID")), personnelId);
			}

			if (Assert.isEmpty(customLayoutMap) || isDefault) {
				layoutMap.put("name", sitePageInfo.get("C_LAYOUT_CODE"));
				layoutMap.put("system", sitePageInfo.get("C_LAYOUT_SYSTEM"));
			} else {
				layoutMap.put("name", customLayoutMap.get("C_LAYOUT_CODE"));
				layoutMap.put("system", customLayoutMap.get("C_IS_SYSTEM"));
			}
			dataJo.put("layout", layoutMap.get("name"));

			// 读取页面widget实例json数据
			String widgetJson = "";
			if (Assert.isNotEmpty(personnelId) && !isDefault) {// 登录用户返回用户自定义widget配置实例
				CustomWidgetConfDto customWidgetConfDto = customWidgetConfService
						.getCustomWidgetConfInfo(personnelId, MathUtils.stringObj(sitePageInfo.get("C_UU_ID")));
				if (Assert.isNotEmpty(customWidgetConfDto.getId())) {
					widgetJson = customWidgetConfDto.getJson();
				}
			}

			if (Assert.isEmpty(widgetJson)) {
				widgetJson = widgetConfService.getWidgetJson(MathUtils.stringObj(sitePageInfo.get("C_UU_ID")));
			}

			if (Assert.isNotEmpty(widgetJson) && widgetJson.length() > 0) {
				jsonArr = new JSONArray(widgetJson);
			}
			dataJo.put("widgets", jsonArr);

			widgetConfService.getWidgetInstanceParams(MathUtils.stringObj(dataJo.get("pageUuId")), widgetParamJsonObj, isDefault, personnelId);
			
			dataJo.put("widgetParams", widgetParamJsonObj);
		} else {
			jo.put("status", ResultConstant.RESULT_RETURN_NO_EXIST_STATUS);
		}
		jo.put("data", dataJo);
		return jo.toString();
	}
}
