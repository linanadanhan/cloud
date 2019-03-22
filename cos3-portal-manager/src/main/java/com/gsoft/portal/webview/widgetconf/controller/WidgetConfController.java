package com.gsoft.portal.webview.widgetconf.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.portal.webview.widgetconf.service.WidgetConfService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * widget配置管理
 * 
 * @author SN
 *
 */
@Api(tags = "widget管理", description = "widget配置接口服务")
@RestController
@RequestMapping("/widgetConf")
public class WidgetConfController {

	@Resource
	WidgetConfService widgetConfService;

	@ApiOperation("查询页面widget配置信息")
	@RequestMapping(value = "/queryWidgetConfig", method = RequestMethod.GET)
	public List<Map<String, Object>> queryWidgetConfig(@RequestParam String search, @RequestParam String pageUuId,
			@RequestParam String layoutCode, @RequestParam String position, @RequestParam String nestUuId,
			@RequestParam String ywType) {
		return widgetConfService.queryWidgetConfig(search, pageUuId, layoutCode, position, ywType, nestUuId);
	}

	@ApiOperation("判断页面布局位置中widget是否存在")
	@RequestMapping(value = "/isExitWidgetCode", method = RequestMethod.GET)
	public Boolean isExitWidgetCode(@RequestParam(required = false) Long id, @RequestParam String code,
			@RequestParam Long pageId, @RequestParam String layoutCode, @RequestParam String position) {
		return widgetConfService.isExitWidgetCode(id, code, pageId, layoutCode, position);
	}

	@ApiOperation("删除widget配置信息")
	@RequestMapping(value = "/delWidgetConf", method = RequestMethod.GET)
	public void delWidgetConf(@RequestParam Long id) {
		widgetConfService.delWidgetConf(id);
	}

}
