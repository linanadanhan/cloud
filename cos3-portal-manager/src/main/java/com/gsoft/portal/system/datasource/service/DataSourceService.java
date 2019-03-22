package com.gsoft.portal.system.datasource.service;

import java.util.List;
import java.util.Map;

import com.gsoft.cos3.dto.PageDto;

/**
 * 数据源业务类接口
 * @author chenxx
 *
 */
public interface DataSourceService {
	
	/**
	 * 测试连接数据源
	 * @param map	数据源参数
	 * @throws Exception 
	 */
	void testAndSave(Map<String, Object> map) throws Exception;

	/**
	 * 分页查询数据源信息
	 * @param search
	 * @param page
	 * @param size
	 * @return
	 */
	PageDto queryDataSourceTable(String search, Integer page, Integer size);

	List<Map<String, Object>> getAll();

}
