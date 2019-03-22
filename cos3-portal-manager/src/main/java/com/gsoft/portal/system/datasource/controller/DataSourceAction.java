package com.gsoft.portal.system.datasource.controller;

import java.util.Map;

import javax.annotation.Resource;

import com.gsoft.cos3.dto.ReturnDto;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.portal.system.datasource.service.DataSourceService;
import io.swagger.annotations.ApiOperation;

@Api(tags = "数据源", description = "数据源业务类接口服务")
@RestController
@RequestMapping("/dataSource")
public class DataSourceAction {

	@Resource
	private DataSourceService service;

	@ApiOperation("测试并保存数据源")
	@RequestMapping(value = "/saveDataSource", method = RequestMethod.POST)
	public void saveDataSource(@RequestParam Map<String, Object> map) throws Exception {
		service.testAndSave(map);
	}

	@ApiOperation("查询数据源信息")
	@RequestMapping(value = "/queryDataSourceTable", method = RequestMethod.GET)
	public PageDto queryDataSourceTable(@RequestParam String search,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size) {
		return service.queryDataSourceTable(search, page, size);
	}

	@ApiOperation("获取所有数据源")
	@RequestMapping(value = "/getAll", method = RequestMethod.GET)
	public ReturnDto getAll() {
		return new ReturnDto(service.getAll());
	}
}
