package com.gsoft.portal.system.manualtables.controller;

import java.util.Map;

import javax.annotation.Resource;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.portal.system.manualtables.service.ManualTableService;

import io.swagger.annotations.ApiOperation;

/**
 * 手动建表实作类
 * @author chenxx
 *
 */
@Api(tags = "手动建表实作", description = "手动建表业务类接口")
@RestController
@RequestMapping("/manualTable")
public class ManualTableController {
	
	@Resource
	private ManualTableService manualTableService;
	
	@ApiOperation("查询动态表信息")
	@RequestMapping(value = "/queryManualTables", method = RequestMethod.GET)
	public PageDto queryManualTables(@RequestParam String search,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size) {
		return manualTableService.queryManualTables(search, page, size);
	}
	
	@ApiOperation("保存手动数据表数据")
    @RequestMapping(value = "/saveManualTable", method = RequestMethod.POST)
    public void saveManualTable(@RequestParam Map<String, Object> map) throws Exception {
		manualTableService.saveManualTable(map);
    }
	
	@ApiOperation("删除手动数据表数据")
    @RequestMapping(value = "/delManualTables", method = RequestMethod.GET)
    public void delManualTables(@RequestParam Long id,@RequestParam String dataSource) throws Exception {
		manualTableService.delManualTables(id,dataSource);
    }
	
	@ApiOperation("根据主键获取单笔手动数据表数据")
    @RequestMapping(value = "/getManualTableById", method = RequestMethod.GET)
    public Map<String, Object> getManualTableById(@RequestParam Long id, @RequestParam(required = false) String dataSource) throws Exception {
		return manualTableService.getManualTableById(id,dataSource);
    }
}
