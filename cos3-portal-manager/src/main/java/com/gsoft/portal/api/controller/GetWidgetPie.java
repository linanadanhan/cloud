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

@Api(tags = "站点管理", description = "获取饼图静态数据接口服务")
@RestController
@RequestMapping("/widget")
public class GetWidgetPie {
	@ApiOperation("widgetBar数据")
	@RequestMapping(value = "/widgetPie", method = RequestMethod.GET)
	public String getWidgetPie(@RequestParam String path) throws JSONException {
		JSONObject jo = new JSONObject();

		jo.put("status", ResultConstant.RESULT_RETURN_OK_STATUS);
		JSONObject dataJo = new JSONObject();

		JSONObject jo1 = new JSONObject();
		jo1.put("name", "访问来源");
		jo1.put("type", "pie");
		jo1.put("radius", "55%");

		JSONArray jsonArr = new JSONArray();

		JSONObject tmpJo1 = new JSONObject();
		tmpJo1.put("value", 235);
		tmpJo1.put("name", "视频广告");

		JSONObject tmpJo2 = new JSONObject();
		tmpJo2.put("value", 274);
		tmpJo2.put("name", "联盟广告");

		JSONObject tmpJo3 = new JSONObject();
		tmpJo3.put("value", 310);
		tmpJo3.put("name", "邮件营销");

		JSONObject tmpJo4 = new JSONObject();
		tmpJo4.put("value", 335);
		tmpJo4.put("name", "直接访问");

		JSONObject tmpJo5 = new JSONObject();
		tmpJo5.put("value", 400);
		tmpJo5.put("name", "搜索引擎");

		jsonArr.put(tmpJo1);
		jsonArr.put(tmpJo2);
		jsonArr.put(tmpJo3);
		jsonArr.put(tmpJo4);
		jsonArr.put(tmpJo5);

		jo1.put("data", jsonArr);
		dataJo.put("series", jo1);

		jo.put("data", dataJo);

		return jo.toString();
	}
}
