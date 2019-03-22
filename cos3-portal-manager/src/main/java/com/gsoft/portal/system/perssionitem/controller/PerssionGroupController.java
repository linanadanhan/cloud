package com.gsoft.portal.system.perssionitem.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.portal.system.perssionitem.service.PerssionGroupService;

import io.swagger.annotations.ApiOperation;

/**
 * 权限组管理Controller
 * @author chenxx
 *
 */
@Api(tags = "权限组管理", description = "权限组管理接口服务")
@RestController
@RequestMapping("/perssionGroup")
public class PerssionGroupController {
	
	@Resource
	private PerssionGroupService perssionGroupService;
	
	@ApiOperation("查询权限组信息")
	@RequestMapping(value = "/queryPerssionGroup", method = RequestMethod.GET)
	public PageDto queryPerssionGroup(@RequestParam String search,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size) {
		return perssionGroupService.queryPerssionGroup(search, page, size);
	}
	
	@ApiOperation("查询所有权限组信息")
	@RequestMapping(value = "/getAllPermissionGroup", method = RequestMethod.GET)
	public ReturnDto getAllPermissionGroup() {
		return new ReturnDto(perssionGroupService.getAllPermissionGroup());
	}
	
	@ApiOperation("查询分组待选权限项")
	@RequestMapping(value = "/getHasNoAuthPermissionItem", method = RequestMethod.GET)
	public List<Map<String, Object>> getHasNoAuthPermissionItem(@RequestParam Long groupId) {
		return perssionGroupService.getHasNoAuthPermissionItem(groupId);
	}
	
	@ApiOperation("查询分组已选权限项")
	@RequestMapping(value = "/getHasAuthPermissionItem", method = RequestMethod.GET)
	public List<Map<String, Object>> getHasAuthPermissionItem(@RequestParam Long groupId) {
		return perssionGroupService.getHasAuthPermissionItem(groupId);
	}
	
	@ApiOperation("保存权限组数据表数据")
    @RequestMapping(value = "/savePermissionGroup", method = RequestMethod.POST)
    public void savePermissionGroup(@RequestParam Map<String, Object> map, HttpServletRequest request) throws Exception {
		String personnelId = request.getHeader("personnelId");
		perssionGroupService.savePermissionGroup(map, personnelId);
    }
	
    @ApiOperation("删除权限组信息")
    @RequestMapping(value = "/delPerssionGroup", method = RequestMethod.GET)
    public void delPerssionGroup(@RequestParam Long id) {
        perssionGroupService.delPerssionGroup(id);
    }
}
