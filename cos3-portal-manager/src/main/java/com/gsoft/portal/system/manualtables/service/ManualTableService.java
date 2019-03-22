package com.gsoft.portal.system.manualtables.service;

import java.util.Map;

import org.json.JSONException;

import com.gsoft.cos3.dto.PageDto;

/**
 * 手动建表业务类接口
 * @author chenxx
 *
 */
public interface ManualTableService {

	/**
	 * 保存手动数据表
	 * @param map
	 * @throws JSONException 
	 */
	void saveManualTable(Map<String, Object> map) throws JSONException;

	/**
	 * 删除手动创建的数据表
	 * @param id
	 * @param dataSource
	 */
	void delManualTables(Long id,String dataSource);

	/**
	 * 根据主键获取单笔手动创建的数据表数据
	 * @param id
	 * @param dataSource
	 * @return
	 */
	Map<String, Object> getManualTableById(Long id,String dataSource);

	/**
	 * 查询手动建表数据信息
	 * @param search
	 * @param page
	 * @param size
	 * @return
	 */
	PageDto queryManualTables(String search, Integer page, Integer size);
	
}
