package com.gsoft.portal.component.appreltemp.service;

import java.util.List;
import java.util.Map;

import com.gsoft.portal.component.appreltemp.dto.AppRelPageTempDto;

/**
 * 应用关联页面模版管理Service接口
 * @author SN
 *
 */
public interface AppRelPageTempService {

	/**
	 * 查询应用未关联的页面模版信息
	 * @param appCode
	 * @return
	 */
	List<Map<String, Object>> getNoSelectedPageTemp(String appCode);

	/**
	 * 查询应用已关联的页面模版信息
	 * @param appCode
	 * @return
	 */
	List<Map<String, Object>> getHasSelectedPageTemp(String appCode);

	/**
	 * 保存应用关联页面模版信息
	 * @param list
	 */
	void saveAppRelPageTemp(List<AppRelPageTempDto> list);
	
	/**
	 * 根据应用code查询关联的页面模版信息
	 * @param appCode
	 * @return
	 */
	List<AppRelPageTempDto> getRelPageTempList(String appCode);
}
