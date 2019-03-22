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
import com.gsoft.portal.system.perssionitem.service.PerssionItemService;

import io.swagger.annotations.ApiOperation;

/**
 * 权限项管理Controller
 * @author chenxx
 *
 */
@Api(tags = "权限项管理", description = "权限项管理接口服务")
@RestController
@RequestMapping("/perssionItem")
public class PerssionItemController {
	
	@Resource
	private PerssionItemService perssionItemService;
	
	@ApiOperation("查询权限项信息")
	@RequestMapping(value = "/queryPerssionItem", method = RequestMethod.GET)
	public PageDto queryPerssionItem(@RequestParam String search,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size) {
		return perssionItemService.queryPerssionItem(search, page, size);
	}
	
	@ApiOperation("查询某一个权限项信息")
	@RequestMapping(value = "/getPermissionItemById", method = RequestMethod.GET)
	public Map<String, Object> getPermissionItemById(@RequestParam long id) {
		return perssionItemService.getPermissionItemById(id);
	}
	
	@ApiOperation("保存权限项数据表数据")
    @RequestMapping(value = "/savePermissionItem", method = RequestMethod.POST)
    public void savePermissionItem(@RequestParam Map<String, Object> map, HttpServletRequest request) throws Exception {
		String personnelId = request.getHeader("personnelId");
		perssionItemService.savePermissionItem(map, personnelId);
    }
	
	@ApiOperation("查询权限项未授权的角色")
	@RequestMapping(value = "/getPermissionHasNoConnectRole", method = RequestMethod.GET)
	public List<Map<String, Object>> getPermissionHasNoConnectRole(@RequestParam Long itemId, @RequestParam String type) {
		return perssionItemService.getPermissionHasNoConnectRole(itemId, type);
	}
	
	@ApiOperation("查询权限项已经授权的角色")
	@RequestMapping(value = "/getPermissionHasConnectRole", method = RequestMethod.GET)
	public List<Map<String, Object>> getPermissionHasConnectRole(@RequestParam Long itemId, @RequestParam String type) {
		return perssionItemService.getPermissionHasConnectRole(itemId, type);
	}
	
	@ApiOperation("保存权限项已经授权的角色")
	@RequestMapping(value = "/savePermissionRelRole", method = RequestMethod.GET)
	public void savePermissionRelRole(@RequestParam Long itemId, @RequestParam String roles) {
		perssionItemService.savePermissionRelRole(itemId, roles);
	}
	
	@ApiOperation("查询权限项未授权的用户")
	@RequestMapping(value = "/getPermissionHasNoConnectPerson", method = RequestMethod.GET)
	public List<Map<String, Object>> getPermissionHasNoConnectPerson(@RequestParam Long itemId) {
		return perssionItemService.getPermissionHasNoConnectPerson(itemId);
	}
	
	@ApiOperation("查询权限项已经授权的用户")
	@RequestMapping(value = "/getPermissionHasConnectPerson", method = RequestMethod.GET)
	public List<Map<String, Object>> getPermissionHasConnectPerson(@RequestParam Long itemId) {
		return perssionItemService.getPermissionHasConnectPerson(itemId);
	}
	
	@ApiOperation("保存权限项已经授权的用户")
	@RequestMapping(value = "/savePermissionRelPerson", method = RequestMethod.GET)
	public void savePermissionRelPerson(@RequestParam Long itemId, @RequestParam String userIds) {
		perssionItemService.savePermissionRelPerson(itemId, userIds);
	}
}
