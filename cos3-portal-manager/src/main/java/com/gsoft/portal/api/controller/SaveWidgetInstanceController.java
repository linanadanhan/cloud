package com.gsoft.portal.api.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
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
import com.gsoft.portal.webview.page.service.SitePageService;
import com.gsoft.portal.webview.site.service.SiteService;
import com.gsoft.portal.webview.widgetconf.dto.CustomWidgetConfDto;
import com.gsoft.portal.webview.widgetconf.dto.WidgetConfDto;
import com.gsoft.portal.webview.widgetconf.service.CustomWidgetConfService;
import com.gsoft.portal.webview.widgetconf.service.WidgetConfService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 保存页面widget实例信息
 * 
 * @author SN
 *
 */
@Api(tags = "站点管理", description = "保存页面widget实例接口服务")
@RestController
@RequestMapping("/site")
public class SaveWidgetInstanceController {

	@Resource
	SitePageService sitePageService;

	@Resource
	SiteService siteService;

	@Resource
	WidgetConfService widgetConfService;

	@Resource
	CustomWidgetConfService customWidgetConfService;

	@ApiOperation("保存页面widget实例配置信息")
	@RequestMapping(value = "/saveWidgetInstance", method = RequestMethod.GET)
	public String saveWidgetInstance(@RequestParam String widgets, @RequestParam String pageUuId, @RequestParam boolean diyMode,
			@RequestParam String widgetIds, @RequestParam String delWidgetIds, HttpServletRequest request) throws JSONException {

		Long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));
		JSONObject jo = new JSONObject();
		List<String> dIds = new ArrayList<String>();

		if (diyMode) {
			//查询是否存在用户个性化widget实例配置信息
			CustomWidgetConfDto customWidgetConfDto = customWidgetConfService.getCustomWidgetConfInfo(personnelId,pageUuId);
			if (Assert.isEmpty(customWidgetConfDto.getId())) {
				customWidgetConfDto = new CustomWidgetConfDto();
				customWidgetConfDto.setPageUuId(pageUuId);
				customWidgetConfDto.setUserId(personnelId);
			}else {
				if (!Assert.isEmpty(delWidgetIds)) {
					String[] oldArr = delWidgetIds.split(",");
					for (String oldWidgetId : oldArr) {
						dIds.add(oldWidgetId);
					}
				}
			}
			customWidgetConfDto.setWidgetIds(widgetIds);
			if (Assert.isEmpty(widgetIds)) {
				customWidgetConfDto.setJson(null);
			}else {
				customWidgetConfDto.setJson(widgets);
			}
			
			customWidgetConfService.saveWidgetInstance(customWidgetConfDto, dIds);
		}else {
			//查询用户是否存在系统widget实例配置信息
			WidgetConfDto widgetConfDto = widgetConfService.getWidgetConfInfo(pageUuId);
			if (Assert.isEmpty(widgetConfDto.getId())) {
				widgetConfDto = new WidgetConfDto();
				widgetConfDto.setPageUuId(pageUuId);
			}else {
				if (!Assert.isEmpty(delWidgetIds)) {
					String[] oldArr = delWidgetIds.split(",");
					for (String oldWidgetId : oldArr) {
						dIds.add(oldWidgetId);
					}
				}
			}
			widgetConfDto.setWidgetIds(widgetIds);
			if (Assert.isEmpty(widgetIds)) {
				widgetConfDto.setJson(null);
			}else {
				widgetConfDto.setJson(widgets);
			}
			widgetConfService.saveWidgetConf(widgetConfDto, dIds);
		}
		jo.put("status", ResultConstant.RESULT_RETURN_OK_STATUS);

		return jo.toString();
	}

}
