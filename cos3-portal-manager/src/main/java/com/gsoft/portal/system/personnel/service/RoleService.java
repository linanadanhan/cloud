package com.gsoft.portal.system.personnel.service;

import java.util.List;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.portal.system.personnel.dto.RoleDto;
import com.gsoft.portal.system.personnel.dto.RolePermissionDto;
import com.gsoft.portal.system.personnel.dto.RolePersonnelDto;

/**
 * 角色管理
 *
 * @author helx
 * @date 2017年8月7日 下午2:41:24
 */
public interface RoleService {
    /**
     * 查询当前行政区划的角色分类
     * @return
     */
    List<String> getRoleTypes();

    /**
     * 平台管理员查询自己创建的角色 
     * @param createBy
     * @param type
     * @param page
     * @param size
     * @return
     */
    PageDto getListByCreateBy(Long createBy,String type, Integer page, Integer size);

    /**
     * 验证角色代码唯一
     *
     * @param id
     * @param code
     * @return
     */
    Boolean isExitCode(Long id, String code);

    /**
     * 保存
     *
     * @param roleDto
     * @return
     */
    RoleDto save(RoleDto roleDto);

    /**
     * 删除角色，同时删除角色与人员，角色与权限关联关系
     *
     * @param id
     */
    void deleteById(Long id);

    /**
     * 修改可用状态
     *
     * @param id
     */
    void updateStatus(Long id, Boolean status);

    /**
     * 保存--关联角色与人员
     * @param list
     */
    void saveConnectRolePersonnel(List<RolePersonnelDto> list);

    /**
     * 关联角色与权限
     *
     * @param list
     */
    void connectRolePermission(List<RolePermissionDto> list);

    void connectRolePermission(Long roleId, String permissionIds);
    /**
     * 根据登录人ID得到关联角色的范围
     * @param loginPersonnelId
     * @return
     */
    List<String> getHasNoConnectRoleTypes(Long loginPersonnelId);

    /**
     * 人员管理角色--根据类型查询未关联的角色
     * @param loginPersonnelId
     * @param type
     * @return
     */
    List<RoleDto> getHasNoConnectRole(Long loginPersonnelId,String type);

    /**
     * 人员管理角色，根据类型查询已经关联的角色
     * @param personnelId
     * @param type
     * @return
     */
    List<RoleDto> getHasConnectRole(Long personnelId,String type);

    /**
     * 根据类型查询角色
     * @param type
     * @return
     */
	List<RoleDto> getHasNoConnectListByType(String type);

	/**
     * 根据权限查询没关联的角色
     * @param loginPersonId
     * @param type
     * @param permissionId
     * @return
     */
	List<RoleDto> getHasNoConnectRole(Long loginPersonId, String type, Long permissionId);

	/**
     * 根据权限查询已经关联的角色
     * @param permissionId
     * @param type
     * @return
     */
	List<RoleDto> getHasConnectRoles(Long permissionId, String type);

	void connectRolePermission1(List<RolePermissionDto> list);

    void deleteConnectRole(Long personnelId);

    List<RoleDto> getAllRoles();

    void batchDeleteByIds(String ids);

    /**
     * 查询指定的多个角色
     * @return
     */
    List<RoleDto> getRolesByIds(String ids);

    /**
     * 根据类型查询角色
     * @param type
     * @return
     */
    List<RoleDto> getRolesByType(String type);

    /**
     * 根据维度查询角色
     * @param role_dimension
     * @return
     */
    List<RoleDto> getRolesByDimension(String role_dimension);

    /**
     * 根据类型、维度和角色名模糊查询角色
     * @param type
     * @param role_dimension
     * @param roleName
     * @return
     */
    List<RoleDto> getRolesByTypeAndDemensionAndName(String type,String role_dimension,String roleName);
}
