package com.gsoft.portal.api.service.impl;

import java.util.Arrays;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.gsoft.cos3.util.Assert;
import com.gsoft.portal.api.service.SyncDataService;

/**
 * 同步第三方接口数据Service实现类
 * @author chenxx
 *
 */
@Service
public class SyncDataServiceImpl implements SyncDataService {
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Override
	public JSONObject getBadgeInfo(String badge, String userId,boolean isDto) throws JSONException {
		String json = stringRedisTemplate.opsForValue().get(getRedisKey(userId, badge));
		JSONObject jo = getRtnJo(badge, json,isDto);
		return jo;
	}

	@Override
	public String getBadgeInfoJson(String badge, String userId) {
		return stringRedisTemplate.opsForValue().get(getRedisKey(userId, badge));
	}

	/**
	 * 获取返回结果
	 * @param badge
	 * @param json
	 * @return
	 * @throws JSONException 
	 */
	private JSONObject getRtnJo(String badge, String json, boolean isDto) throws JSONException {
		int num = 0;
		JSONObject rtnJo = new JSONObject();
		JSONObject jo = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		
		if (!Assert.isEmpty(json) && json.length() > 0) {
			JSONObject rJo = new JSONObject(json);
			num = rJo.length();
			@SuppressWarnings("rawtypes")
			Iterator iterator = rJo.keys();
			while(iterator.hasNext()){
			   String key = (String) iterator.next();
			   Object value = rJo.get(key);
			   jsonArr.put(value);
			}
		}
		JSONObject dataJo = new JSONObject();
		dataJo.put("isDot", isDto);
		dataJo.put("value", num);
		dataJo.put("data", jsonArr);
		jo.put("key", badge);
		jo.put("badge", dataJo);
		rtnJo.put("status", 200);
		rtnJo.put("data", jo);
		return rtnJo;
	}

	@Override
	public JSONObject saveBadgeInfo(String badge, String userId, String json) throws JSONException {
		
		String oldJson = stringRedisTemplate.opsForValue().get(getRedisKey(userId, badge));
		JSONObject rtnJo = new JSONObject();
		
		if (!Assert.isEmpty(oldJson) && oldJson.length() > 0) {
			JSONObject rJo = new JSONObject(oldJson);
			
			JSONObject oJo = new JSONObject(json);
			@SuppressWarnings("rawtypes")
			Iterator iterator = oJo.keys();
			while(iterator.hasNext()){
			   String key = (String) iterator.next();
			   Object value = oJo.get(key);
			   if (!rJo.has(key)) {
				   rJo.put(key, value);
			   } 
			}
			stringRedisTemplate.opsForValue().set(getRedisKey(userId, badge), rJo.toString());
		}else {
			stringRedisTemplate.opsForValue().set(getRedisKey(userId, badge), json);
		}
		rtnJo.put("status", 200);
		rtnJo.put("data", new JSONObject());
		
		return rtnJo;
	}

	@Override
	public String modifyBadgeInfo(String badge, String userId, String ids) throws JSONException {
		String json = stringRedisTemplate.opsForValue().get(getRedisKey(userId, badge));
		JSONObject rtnJo = new JSONObject();
		
		if (!Assert.isEmpty(json) && json.length() > 0) {
			JSONObject rJo = new JSONObject(json);
			String[] idArr = ids.split(",");
			JSONObject newJo = new JSONObject();
			
			@SuppressWarnings("rawtypes")
			Iterator iterator = rJo.keys();
			while(iterator.hasNext()){
			   String key = (String) iterator.next();
			   Object value = rJo.get(key);
			   if (!Arrays.asList(idArr).contains(key)) {
				   newJo.put(key, value);
			   } 
			}
			stringRedisTemplate.opsForValue().set(getRedisKey(userId, badge), newJo.toString());
		}
		rtnJo.put("status", 200);
		rtnJo.put("data", new JSONObject());
		
		return rtnJo.toString();
	}
	
	public String getRedisKey(String userId, String badge) {
		return userId + "_" + badge;
	}

	@Override
	public String coverBadgeInfo(String badge, String userId, String json) throws JSONException {
		String oldJson = stringRedisTemplate.opsForValue().get(getRedisKey(userId, badge));
		JSONObject rtnJo = new JSONObject();
		
		if (!Assert.isEmpty(oldJson) && oldJson.length() > 0) {
			JSONObject rJo = new JSONObject(oldJson);

			JSONObject oJo = new JSONObject(json);
			@SuppressWarnings("rawtypes")
			Iterator iterator = oJo.keys();
			while(iterator.hasNext()){
			   String key = (String) iterator.next();
			   Object value = oJo.get(key);
			   rJo.put(key, value);
			}
			stringRedisTemplate.opsForValue().set(getRedisKey(userId, badge), rJo.toString());
		}else {
			stringRedisTemplate.opsForValue().set(getRedisKey(userId, badge), json);
		}
		rtnJo.put("status", 200);
		rtnJo.put("data", new JSONObject());
		
		return rtnJo.toString();
	}
}
