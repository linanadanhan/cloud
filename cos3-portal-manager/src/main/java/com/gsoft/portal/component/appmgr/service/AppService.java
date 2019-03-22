package com.gsoft.portal.component.appmgr.service;

import org.json.JSONException;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.portal.component.appmgr.dto.AppDto;

/**
 * 应用管理Service接口
 * @author SN
 *
 */
public interface AppService {

	/**
	 * 分页查询应用列表数据
	 * @param search
	 * @param page
	 * @param size
	 * @param sortProp
	 * @param order
	 * @return
	 */
	PageDto getAppList(String search, Integer page, Integer size, String sortProp, String order);

	/**
	 * 更新应用状态
	 * @param ids
	 * @param status
	 */
	void updateAppStatus(String ids, String status);

	/**
	 * 保存应用信息
	 * @param appDto
	 * @return
	 */
	AppDto saveAppInfo(AppDto appDto);

	/**
	 * 删除应用信息
	 * @param id
	 * @throws JSONException 
	 */
	ReturnDto delApp(Long id) throws JSONException;

	/**
	 * 根据主键获取单笔应用信息
	 * @param id
	 * @return
	 */
	AppDto getAppInfoById(Long id);

	/**
	 * 校验应用代码是否已存在
	 * @param id
	 * @param code
	 * @return
	 */
	ReturnDto isUniquAppCode(Long id, String code);

	/**
	 * 复制应用数据
	 * @param appDto
	 * @return
	 */
	AppDto copyAppInfo(AppDto appDto);
	
	/**
	 * 根据应用code查询应用信息
	 * @param code
	 * @return
	 */
	AppDto getAppInfoByCode(String code);

}
