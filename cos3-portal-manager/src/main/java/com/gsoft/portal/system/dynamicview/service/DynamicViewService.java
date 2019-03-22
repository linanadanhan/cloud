package com.gsoft.portal.system.dynamicview.service;

import java.util.List;
import java.util.Map;

import com.gsoft.cos3.dto.PageDto;

/**
 * 动态视图业务类接口
 * @author chenxx
 *
 */
public interface DynamicViewService {

	/**
	 * 查询动态视图信息
	 * @param search
	 * @param page
	 * @param size
	 * @return
	 */
	PageDto queryDynamicView(String search, Integer page, Integer size);

	/**
	 * 获取指定视图的所有记录
	 * @param viewName
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> queryAll(String viewName, Map<String, Object> map);

	/**
	 * 分页获取指定视图的所有记录
	 * @param viewName
	 * @param map
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageDto queryPage(String viewName, Map<String, Object> map, int pageNum, int pageSize);

	/**
	 * 根据参数拼接sql
	 * @param viewName
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> queryAllByParams(String viewName, Map<String, Object> map);
	
}
