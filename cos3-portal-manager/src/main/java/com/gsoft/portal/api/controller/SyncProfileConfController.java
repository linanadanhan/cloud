package com.gsoft.portal.api.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.portal.api.service.SyncProfileConfService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "站点管理", description = "同步系统配置信息")
@RestController
@RequestMapping("/site")
public class SyncProfileConfController {
	
	@Resource
	SyncProfileConfService syncProfileConfService;

	@ApiOperation("同步系统配置信息")
	@RequestMapping(value = "/syncProfileConf", method = RequestMethod.GET)
	public ReturnDto syncProfileConf() throws Exception {
		// 查询平台所有系统页面
		syncProfileConfService.syncProfileConf();
		return new ReturnDto("同步成功");
	}

}
