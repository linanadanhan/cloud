package com.gsoft.portal.system.personnel.service;

import java.util.List;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.portal.system.personnel.dto.PermissionDto;


/**
 * 权限管理
 *
 * @author Administrator
 * @date 2017年8月7日 下午2:41:49
 */
public interface PermissionService {
	
	/**
	 * 查询所有权限分类
	 * @return
	 */
	List<String> getTypes();
	
	/**
	 * 2.根据分类查询列表，分页
	 * @param type
	 * @param page
	 * @param size
	 * @return
	 */
	PageDto getPageByType(String type, Integer page, Integer size);
	
	/**
	 * 根据ID得到对象
	 * @param id
	 * @return
	 */
	PermissionDto getOneById(Long id);
	
	/**
	 * 验证权限代码唯一
	 * @param id
	 * @param code
	 * @return
	 */
	Boolean isExitCode(Long id, String code);
	
	/**
	 * 保存
	 * @param permissionDto
	 * @return
	 */
	PermissionDto save(PermissionDto permissionDto);
	
	/**
	 * 删除权限，同时删除角色与权限
	 * @param id
	 * @return
	 */
	void deleteById(Long id);

	/**
	 * 得到包含的资源集合
	 * @return
	 */
	List<String> getIncludeResources();
	
	/**
	 * 得到排斥的资源集合
	 * @return
	 */
	List<String> getExcludeResources();
	
	//---------------------角色关联权限
	
	/**
	 * 2.1根据分类查询未授权列表--平台管理员
	 * @param type为空的话，查询所有
	 * @return
	 */
	List<PermissionDto> getHasNoConnectListByType(String type);
	/**
	 * 2.3 查询已经授权的权限--行政区划人员
	 * @param roleId  选中的角色
	 * @param type  可为空
	 * @return
	 */
	List<PermissionDto> getHasConnectPermission(Long roleId,String type);

	/**
	 * 查询所有权限项分类
	 * @param personnelId
	 * @return
	 */
	List<String> getTypesByLoginPersonnel(Long personnelId);

	/**
	 * 查询所有未授权的权限项
	 * @param personnelId
	 * @param type
	 * @param roleId
	 * @return
	 */
	List<PermissionDto> getHasNoConnectPermission(Long personnelId, String type, Long roleId);

    List<PermissionDto> getAllPermission();

}
