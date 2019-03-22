package com.gsoft.portal.system.perssionitem.service;

import java.util.List;
import java.util.Map;

import com.gsoft.cos3.dto.PageDto;

/**
 * 权限组Service接口类
 * @author chenxx
 *
 */
public interface PerssionGroupService {

	/**
	 * 分页查询权限组信息
	 * @param search
	 * @param page
	 * @param size
	 * @return
	 */
	PageDto queryPerssionGroup(String search, Integer page, Integer size);

	/**
	 * 查询分组下待选权限项
	 * @param groupId
	 * @return
	 */
	List<Map<String, Object>> getHasNoAuthPermissionItem(Long groupId);

	/**
	 * 查询分组下已选权限项
	 * @param groupId
	 * @return
	 */
	List<Map<String, Object>> getHasAuthPermissionItem(Long groupId);

	/**
	 * 
	 * @param map
	 * @param personnelId
	 */
	void savePermissionGroup(Map<String, Object> map, String personnelId);

	/**
	 * 查询全部权限组信息
	 * @return
	 */
	List<Map<String, Object>> getAllPermissionGroup();

	/**
	 * 删除权限组信息
	 * @param id
	 */
	void delPerssionGroup(Long id);

}
