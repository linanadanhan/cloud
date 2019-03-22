package com.gsoft.portal.api.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.common.constans.ResultConstant;
import com.gsoft.portal.webview.page.service.SitePageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 重置页面
 * 
 * @author SN
 *
 */
@Api(tags = "站点管理", description = "重置页面接口服务")
@RestController
@RequestMapping("/site")
public class ResetPageController {

	@Resource
	SitePageService sitePageService;

	@ApiOperation("重置页面")
	@RequestMapping(value = "/resetPage", method = RequestMethod.GET)
	public String resetPage(@RequestParam String pageUuId, @RequestParam String siteCode, @RequestParam String pageWidgets,
			HttpServletRequest request) throws JSONException {

		Long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));
		JSONObject jo = new JSONObject();

		sitePageService.resetPage(pageUuId, pageWidgets, siteCode, personnelId);
		jo.put("status", ResultConstant.RESULT_RETURN_OK_STATUS);
		
		return jo.toString();
	}

}
