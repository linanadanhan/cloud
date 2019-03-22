package com.gsoft.portal.system.mobile.controller;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.gsoft.cos3.util.JsonMapper;
import com.gsoft.portal.system.basicdata.service.ParameterService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "移动端相关请求(不需要权限验证的)", description = "处理移动端相关服务的请求")
@RestController
@RequestMapping("/mobile/noauthor")
public class MobileController {

	@Resource
	ParameterService parameterService;

	@ApiOperation("获取移动端APK版本信息")
	@RequestMapping(value = "/getAppPackVersion")
	public Map<String, Object> getAppPackVersion() throws JsonParseException, JsonMappingException, IOException {
		String val = parameterService.getParmValueByKey("version_info", "{}");
		return JsonMapper.fromJsonMap(val, String.class, Object.class);
	}
}
