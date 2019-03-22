package com.gsoft.portal.component.compmgr.controller;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.util.Assert;
import com.gsoft.portal.component.compmgr.dto.BusinessComponentDto;
import com.gsoft.portal.component.compmgr.service.BusinessCompConfService;
import com.gsoft.portal.component.compmgr.service.BusinessComponentService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 业务组件管理
 * 
 * @author SN
 *
 */
@Api(tags = "业务组件管理", description = "业务组件相关接口服务")
@RestController
@RequestMapping("/businessComponent")
public class BusinessComponentController {

	@Resource
	BusinessComponentService businessComponentService;
	
	@Resource
	BusinessCompConfService businessCompConfService;

	@ApiOperation("分页查找业务组件信息")
	@RequestMapping(value = "/getBusinessComponentList", method = RequestMethod.GET)
	public ReturnDto getBusinessComponentList(@RequestParam String search,
			@RequestParam String category,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size,
			@RequestParam(value = "sortProp", defaultValue = "id") String sortProp,
			@RequestParam(value = "order", defaultValue = "desc") String order) {
		return new ReturnDto(businessComponentService.getBusinessComponentList(category, search, page, size, sortProp, order));
	}

	@ApiOperation("启用/停用")
	@RequestMapping(value = "/updateComponentStatus", method = RequestMethod.GET)
	public ReturnDto updateComponentStatus(@RequestParam Long ids, @RequestParam String status) {
		businessComponentService.updateComponentStatus(ids, status);
		return new ReturnDto("修改成功！");
	}

	@ApiOperation("保存业务组件数据")
	@RequestMapping(value = "/saveBusinessCompInfo", method = RequestMethod.POST)
	public ReturnDto saveBusinessCompInfo(HttpServletRequest request,
			@RequestBody BusinessComponentDto businessComponentDto) {
		businessComponentDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));
		if (Assert.isEmpty(businessComponentDto.getId())) {
			businessComponentDto.setCreateTime(new Date());
		} else {
			businessComponentDto.setUpdateTime(new Date());
		}
		return new ReturnDto(businessComponentService.saveBusinessCompInfo(businessComponentDto));
	}

	@ApiOperation("删除业务组件数据")
	@RequestMapping(value = "/delBusinessComp", method = RequestMethod.GET)
	public ReturnDto delBusinessComp(HttpServletRequest request, @RequestParam Long id) throws JSONException {
		return businessComponentService.delBusinessComp(id);
	}

	@ApiOperation("根据主键ID获取单笔业务组件数据")
	@RequestMapping(value = "/getBusinessCompById", method = RequestMethod.GET)
	public ReturnDto getBusinessCompById(@RequestParam Long id) {
		return new ReturnDto(businessComponentService.getBusinessCompById(id));
	}

	@ApiOperation("保存业务组件配置数据")
	@RequestMapping(value = "/saveBusinessCompConf", method = RequestMethod.GET)
	public ReturnDto saveBusinessCompConf(@RequestParam Long compId, @RequestParam String widgetIds) {
		businessCompConfService.saveBusinessCompConf(compId, widgetIds);
		return new ReturnDto("保存成功");
	}
	
	@ApiOperation("根据组件ID获取业务组件配置数据")
	@RequestMapping(value = "/getBusinessCompConfInfo", method = RequestMethod.GET)
	public ReturnDto getBusinessCompConfInfo(@RequestParam Long compId) throws JSONException {
		return businessCompConfService.getBusinessCompConfInfo(compId);
	}
	
	@ApiOperation("复制业务组件配置数据")
	@RequestMapping(value = "/copyBusinessCompConf", method = RequestMethod.GET)
	public ReturnDto copyBusinessCompConf(@RequestParam Long compId, @RequestParam String widgetUuId) throws JSONException {
		return businessCompConfService.copyBusinessCompConf(compId, widgetUuId);
	}
	
	@ApiOperation("获取业务部件级联信息")
	@RequestMapping(value = "/getAllCascadeBusCopm", method = RequestMethod.GET)
	public ReturnDto getAllCascadeBusCopm(HttpServletRequest request) {
		return new ReturnDto(businessComponentService.getAllCascadeBusCopm());
	}
}
