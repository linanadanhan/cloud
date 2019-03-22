package com.gsoft.portal.api.service;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 同步第三方数据Service接口类
 * @author chenxx
 *
 */
public interface SyncDataService {

	/**
	 * 获取widge badge数据信息
	 * @param badge
	 * @param userId
	 * @return
	 * @throws JSONException 
	 */
	JSONObject getBadgeInfo(String badge, String userId,boolean isDto) throws JSONException;

	/**
	 *
	 * @param badge
	 * @param userId
	 * @return
	 */
	String getBadgeInfoJson(String badge, String userId);

	/**
	 * 保存用户badge数据信息
	 * @param badge
	 * @param userId
	 * @param json
	 * @return
	 * @throws JSONException 
	 */
	JSONObject saveBadgeInfo(String badge, String userId, String json) throws JSONException;

	/**
	 * 更新用户badge数据信息
	 * @param badge
	 * @param userId
	 * @param ids
	 * @return
	 * @throws JSONException 
	 */
	String modifyBadgeInfo(String badge, String userId, String ids) throws JSONException;

	/**
	 * 覆盖用户badge数据信息
	 * @param badge
	 * @param userId
	 * @param json
	 * @return
	 * @throws JSONException 
	 */
	String coverBadgeInfo(String badge, String userId, String json) throws JSONException;
	
}
