package com.gsoft.portal.component.compmgr.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.portal.component.compmgr.service.ComponentService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 部件管理
 * 
 * @author SN
 *
 */
@Api(tags = "部件管理", description = "部件相关接口服务")
@RestController
@RequestMapping("/component")
public class ComponentController {

	@Resource
	ComponentService componentService;

	@ApiOperation("分页查找部件基本信息")
	@RequestMapping(value = "/queryComponentDataTable", method = RequestMethod.GET)
	public PageDto queryComponentDataTable(@RequestParam String search,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size,
			@RequestParam(value = "sortProp", defaultValue = "id") String sortProp,
			@RequestParam(value = "order", defaultValue = "desc") String order) {
		return componentService.queryComponentDataTable(search, page, size, sortProp, order);
	}

	@ApiOperation("启用/停用")
	@RequestMapping(value = "/updateStatus", method = RequestMethod.GET)
	public ReturnDto updateStatus(@RequestParam Long id, @RequestParam Boolean status) {
		componentService.updateStatus(id, status);
		return new ReturnDto("成功！");
	}

	@ApiOperation("卸载")
	@RequestMapping(value = "/uninstall", method = RequestMethod.GET)
	public ReturnDto uninstall(@RequestParam String code) {
		return new ReturnDto(componentService.uninstall(code));
	}

	@ApiOperation("查询部件清单明细信息")
	@RequestMapping(value = "/queryComponentDetailList", method = RequestMethod.GET)
	ReturnDto queryComponentDetailList(@RequestParam String compType, @RequestParam String compCode) {
		return new ReturnDto(componentService.queryComponentDetailList(compType, compCode));
	}
	
	@ApiOperation("分页查找云端部件包基本信息")
	@RequestMapping(value = "/getComponentPackageList", method = RequestMethod.GET)
	public ReturnDto getComponentPackageList(@RequestParam String search,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size,
			@RequestParam(value = "sortProp", defaultValue = "id") String sortProp,
			@RequestParam(value = "order", defaultValue = "desc") String order) {
		return new ReturnDto(componentService.queryComponentPackageDataTable(search, page, size, sortProp, order));
	}
	
    @ApiOperation("校验部件包是否已存在")
    @RequestMapping(value = "/isExistCompCode", method = RequestMethod.GET)
    public ReturnDto isExistCompCode(@RequestParam String compCode) {
        return new ReturnDto(componentService.isExistCompCode(compCode));
    }
    
	@ApiOperation("分页查找本地部件包基本信息")
	@RequestMapping(value = "/getPartPackageList", method = RequestMethod.GET)
	public ReturnDto getPartPackageList(HttpServletRequest request,
			@RequestParam String search,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size,
			@RequestParam(value = "sortProp", defaultValue = "id") String sortProp,
			@RequestParam(value = "order", defaultValue = "desc") String order) throws Exception {
		// 租户
		String customer = request.getHeader("Site-info");
		return new ReturnDto(componentService.getPartPackageList(customer, search, page, size, sortProp, order));
	}
	
    @ApiOperation("获取系统模组文件")
    @RequestMapping(value = "/getModuleFiles", method = RequestMethod.GET)
    public ReturnDto getModuleFiles() throws JSONException {
        return new ReturnDto(componentService.getModuleFiles());
    }
}
