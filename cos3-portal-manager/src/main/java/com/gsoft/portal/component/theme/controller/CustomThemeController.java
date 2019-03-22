package com.gsoft.portal.component.theme.controller;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.portal.common.constans.ResultConstant;
import com.gsoft.portal.component.theme.dto.CustomThemeDto;
import com.gsoft.portal.component.theme.service.CustomThemeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 用户自定义主题
 * 
 * @author SN
 *
 */
@Api(tags = "主题管理", description = "用户自定义主题接口服务")
@RestController
@RequestMapping("/customTheme")
public class CustomThemeController {

	@Resource
	CustomThemeService customThemeService;

	@ApiOperation("保存用户自定义主题信息")
	@RequestMapping(value = "/saveCustomTheme", method = RequestMethod.GET)
	public String saveCustomTheme(@RequestParam String siteCode, @RequestParam String themeCode,
			ServletRequest servletRequest) throws JSONException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;

		JSONObject jo = new JSONObject();

		CustomThemeDto customThemeDto = new CustomThemeDto();
		customThemeDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));
		customThemeDto.setUserId(Long.valueOf(request.getHeader("personnelId")));
		customThemeDto.setSiteCode(siteCode);
		customThemeDto.setThemeCode(themeCode);
		customThemeService.saveCustomTheme(customThemeDto);
		jo.put("status", ResultConstant.RESULT_RETURN_OK_STATUS);

		return jo.toString();
	}
}
