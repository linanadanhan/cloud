package com.gsoft.portal.api.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.portal.common.constans.ResultConstant;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "站点管理", description = "获取bar图静态数据接口服务")
@RestController
@RequestMapping("/widget")
public class GetWidgetBar {
	@ApiOperation("widgetBar数据")
	@RequestMapping(value = "/widgetBar", method = RequestMethod.GET)
	public String getWidgetBar(@RequestParam String path) throws JSONException {
		JSONObject jo = new JSONObject();
		
		jo.put("status", ResultConstant.RESULT_RETURN_OK_STATUS);
		JSONObject dataJo = new JSONObject();
		
		JSONArray jsonArr = new JSONArray();
		jsonArr.put("衬衫");
		jsonArr.put("羊毛衫");
		jsonArr.put("雪纺衫");
		jsonArr.put("裤子");
		jsonArr.put("高跟鞋");
		jsonArr.put("袜子");
		
		dataJo.put("xAxis", jsonArr);
		dataJo.put("yAxis", new JSONObject());
		
		JSONObject jo1 = new JSONObject();
		jo1.put("name", "销量");
		jo1.put("type", "bar");
		
		JSONArray jo1Arr = new JSONArray();
		jo1Arr.put(5);
		jo1Arr.put(20);
		jo1Arr.put(36);
		jo1Arr.put(10);
		jo1Arr.put(10);
		jo1Arr.put(20);
		
		jo1.put("data", jo1Arr);
		dataJo.put("series", jo1);
		jo.put("data", dataJo);
		
		return jo.toString();
	}
}
