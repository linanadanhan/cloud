package com.gsoft.portal.system.antpatterns.service;

import java.util.Map;

import com.gsoft.cos3.dto.PageDto;

/**
 * 白名单管理service接口类
 * @author chenxx
 *
 */
public interface AntPatternsService {

	/**
	 * 分页查询mapping列表信息
	 * @param search
	 * @param page
	 * @param size
	 * @param sortProp
	 * @param order
	 * @return
	 */
	public PageDto getMappingList(String search, Integer page, Integer size, String sortProp, String order);

	/**
	 * 根据主键获取单笔mapping信息
	 * @param id
	 * @return
	 */
	public Map<String, Object> getMappingById(Long id);

	/**
	 * 保存mapping信息
	 * @param map
	 * @return
	 */
	public long saveMapping(Map<String, Object> map);

	/**
	 * 删除mapping信息
	 * @param id
	 */
	public void deleteMapping(Long id);

	/**
	 * 校验mapping是否已存在
	 * @param id
	 * @param code
	 * @return
	 */
	public Boolean isUniqueMapping(Long id, String path, String server);

}
