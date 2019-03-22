package com.gsoft.portal.api.controller;

import javax.annotation.Resource;

import io.swagger.annotations.Api;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.portal.api.service.SyncDataService;

import io.swagger.annotations.ApiOperation;

/**
 * 第三方同步数据处理接口服务
 * @author chenxx
 *
 */
@Api(tags = "第三方数据接口", description = "第三方同步数据处理接口服务")
@RestController
@RequestMapping("/syncData")
public class SyncDataController {
	
	@Resource
	SyncDataService syncDataService;

	@ApiOperation("获取对应用户widget的badge数据信息")
	@RequestMapping(value = "/getBadgeInfoJson", method = RequestMethod.GET)
	public String getBadgeInfoJson(@RequestParam String badge, @RequestParam String userId) {
		return syncDataService.getBadgeInfoJson(badge, userId);
	}
	
	@ApiOperation("获取对应用户widget的badge数据信息")
	@RequestMapping(value = "/getBadgeInfo", method = RequestMethod.GET)
	public String getBadgeInfo(@RequestParam String badge, @RequestParam String userId,@RequestParam boolean isDto) throws JSONException {
		JSONObject jo = syncDataService.getBadgeInfo(badge, userId,isDto);
		return jo.toString();
	}
	
	@ApiOperation("保存badge数据信息")
	@RequestMapping(value = "/saveBadgeInfo", method = RequestMethod.GET)
	public String saveBadgeInfo(@RequestParam String badge, @RequestParam String userId, @RequestParam String json) throws JSONException {
		JSONObject jo = syncDataService.saveBadgeInfo(badge, userId, json);
		return jo.toString();
	}
	
	@ApiOperation("更新badge数据信息")
	@RequestMapping(value = "/modifyBadgeInfo", method = RequestMethod.GET)
	public String modifyBadgeInfo(@RequestParam String badge, @RequestParam String userId, @RequestParam String ids) throws JSONException {
		return syncDataService.modifyBadgeInfo(badge, userId, ids);
	}
	
	@ApiOperation("覆盖badge数据信息")
	@RequestMapping(value = "/coverBadgeInfo", method = RequestMethod.GET)
	public String coverBadgeInfo(@RequestParam String badge, @RequestParam String userId, @RequestParam String json) throws JSONException {
		return syncDataService.coverBadgeInfo(badge, userId, json);
	}
}
