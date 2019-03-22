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

@Api(tags = "站点管理", description = "获取table widget静态数据接口服务")
@RestController
@RequestMapping("/widget")
public class GetWidgetTable {
	@ApiOperation("widgetTable数据")
	@RequestMapping(value = "/widgetTable", method = RequestMethod.GET)
	public String getWidgetTable(@RequestParam String path) throws JSONException {
		JSONObject jo = new JSONObject();
		
		jo.put("status", ResultConstant.RESULT_RETURN_OK_STATUS);
		JSONObject dataJo = new JSONObject();
		
		JSONArray jsonArr = new JSONArray();
		JSONObject jo1 = new JSONObject();
		jo1.put("type", "交办件");
		jo1.put("title", "浙江省财政厅");
		jo1.put("state", "办理中");
		jsonArr.put(jo1);
		jsonArr.put(jo1);
		jsonArr.put(jo1);
		dataJo.put("tableData", jsonArr);
		
		jo.put("data", dataJo);
		
		return jo.toString();
	}
}
