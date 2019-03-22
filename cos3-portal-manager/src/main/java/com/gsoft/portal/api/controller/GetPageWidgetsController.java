package com.gsoft.portal.api.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.component.compmgr.service.BusinessComponentService;
import com.gsoft.portal.component.compmgr.service.ComponentService;
import com.gsoft.portal.webview.widget.service.WidgetService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 获取页面widgets配置
 * 
 * @author SN
 *
 */
@Api(tags = "站点管理", description = "获取页面可新增部件接口服务")
@RestController
@RequestMapping("/site")
public class GetPageWidgetsController {
	
	@Resource
	ComponentService componentService;
	
	@Resource
	BusinessComponentService businessComponentService;
	
	@Resource
	WidgetService widgetService;
	
	@ApiOperation("获取页面下的所有widget")
	@RequestMapping(value = "/pagewidges", method = RequestMethod.GET)
	public ReturnDto getPageWidgets(@RequestParam boolean isDefault, HttpServletRequest request) {
		Long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));
		
		if (isDefault) {// 编辑模式下 ==> 查询所有启用了的系统技术组件
			return componentService.getAllSysCompList();
			
		}else {// 个性化模式下 ==> 查询所有授权了的业务组件
			return businessComponentService.getAllBusinessCompList(personnelId);
			
		}
	}
	
	@ApiOperation("获取widget分类树信息")
	@RequestMapping(value = "/getCategoryWidgetTree", method = RequestMethod.GET)
	public ReturnDto getCategoryWidgetTree(@RequestParam String model) throws JSONException {
		return widgetService.getCategoryWidgetTree(model);
	}
	
	@ApiOperation("获取页面对应分类下的所有widget")
	@RequestMapping(value = "/getWidgetListByCategory", method = RequestMethod.GET)
	public ReturnDto getWidgetListByCategory(@RequestParam String model, @RequestParam String category, HttpServletRequest request) {
		Long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));
		return widgetService.getWidgetListByCategory(model, category, personnelId);
	}
}
