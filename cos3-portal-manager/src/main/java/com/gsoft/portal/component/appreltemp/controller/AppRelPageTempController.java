package com.gsoft.portal.component.appreltemp.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.portal.component.appreltemp.dto.AppRelPageTempDto;
import com.gsoft.portal.component.appreltemp.service.AppRelPageTempService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 应用与页面模版管理关联实作类
 * 
 * @author SN
 *
 */
@Api(tags = "应用关联页面模版管理", description = "应用关联页面模版相关接口服务")
@RestController
@RequestMapping("/appRelTemp")
public class AppRelPageTempController {

	@Resource
	AppRelPageTempService appRelPageTempService;
	
	@ApiOperation("查询应用未关联的页面模版列表")
	@RequestMapping(value = "/getNoSelectedPageTemp", method = RequestMethod.GET)
	public ReturnDto getNoSelectedPageTemp(@RequestParam String appCode) {
		return new ReturnDto(appRelPageTempService.getNoSelectedPageTemp(appCode));
	}
	
	@ApiOperation("查询应用已关联的页面模版列表")
	@RequestMapping(value = "/getHasSelectedPageTemp", method = RequestMethod.GET)
	public ReturnDto getHasSelectedPageTemp(@RequestParam String appCode) {
		return new ReturnDto(appRelPageTempService.getHasSelectedPageTemp(appCode));
	}
	
	@ApiOperation("保存应用关联页面模版信息")
	@RequestMapping(value = "/saveAppRelPageTemp", method = RequestMethod.POST)
	public ReturnDto saveAppRelPageTemp(@RequestBody List<AppRelPageTempDto> list) {
		appRelPageTempService.saveAppRelPageTemp(list);
		return new ReturnDto("保存成功！");
	}
}
