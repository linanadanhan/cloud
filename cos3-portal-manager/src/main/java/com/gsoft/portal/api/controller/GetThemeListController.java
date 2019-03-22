package com.gsoft.portal.api.controller;

import java.util.List;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.util.Assert;
import com.gsoft.portal.common.constans.ResultConstant;
import com.gsoft.portal.component.theme.dto.ThemeDto;
import com.gsoft.portal.component.theme.service.ThemeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 获取主题list集合信息
 * 
 * @author SN
 *
 */
@Api(tags = "站点管理", description = "获取站点主题接口服务")
@RestController
@RequestMapping("/site")
public class GetThemeListController {

	@Resource
	ThemeService themeService;

	@ApiOperation("获取所有主题list")
	@RequestMapping(value = "/getThemeList", method = RequestMethod.GET)
	public String getThemeList(@RequestParam(required = false) String isOpen,
			@RequestParam(required = false) String siteCode) throws JSONException {

		JSONObject jo = new JSONObject();
		JSONArray joArr = new JSONArray();
		
		List<ThemeDto> themeList = themeService.getThemeList(isOpen, siteCode);

		if (Assert.isNotEmpty(themeList) && themeList.size() > 0) {
			for (ThemeDto dto : themeList) {
				JSONObject dtoJo = new JSONObject();
				dtoJo.put("name", dto.getCode());
				dtoJo.put("title", dto.getName());
				joArr.put(dtoJo);
			}
		}
		jo.put("status", ResultConstant.RESULT_RETURN_OK_STATUS);
		jo.put("data", joArr);

		return jo.toString();
	}
}
