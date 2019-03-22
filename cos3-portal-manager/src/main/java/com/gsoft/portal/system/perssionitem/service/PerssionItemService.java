package com.gsoft.portal.system.perssionitem.service;

import java.util.List;
import java.util.Map;

import com.gsoft.cos3.dto.PageDto;

/**
 * 权限项Service接口类
 * @author chenxx
 *
 */
public interface PerssionItemService {

	/**
	 * 分页查询权限项信息
	 * @param search
	 * @param page
	 * @param size
	 * @return
	 */
	PageDto queryPerssionItem(String search, Integer page, Integer size);

	/**
	 * 获取单笔权限项信息
	 * @param id
	 * @return
	 */
	Map<String, Object> getPermissionItemById(long id);

	/**
	 * 保存权限项信息
	 * @param map
	 * @param personnelId
	 */
	void savePermissionItem(Map<String, Object> map, String personnelId);

	/**
	 * 查询权限项未关联的角色信息
	 * @param itemId
	 * @param type
	 * @return
	 */
	List<Map<String, Object>> getPermissionHasNoConnectRole(Long itemId, String type);

	/**
	 * 查询权限项已关联的角色信息
	 * @param itemId
	 * @param type
	 * @return
	 */
	List<Map<String, Object>> getPermissionHasConnectRole(Long itemId, String type);

	/**
	 * 保存权限项已授权角色
	 * @param itemId
	 * @param roles
	 */
	void savePermissionRelRole(Long itemId, String roles);

	/**
	 * 查询权限项未授权的用户
	 * @param itemId
	 * @return
	 */
	List<Map<String, Object>> getPermissionHasNoConnectPerson(Long itemId);

	/**
	 * 查询权限项已授权的用户
	 * @param itemId
	 * @return
	 */
	List<Map<String, Object>> getPermissionHasConnectPerson(Long itemId);

	/**
	 * 保存权限项已授权的用户
	 * @param itemId
	 * @param userIds
	 */
	void savePermissionRelPerson(Long itemId, String userIds);

	/**
	 * 查询用户已授权的所有权限项
	 * @param personnelId
	 * @return
	 */
	List<Map<String, Object>> queryAllPermissionItems(long personnelId);

}
