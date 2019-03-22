package com.gsoft.portal.system.personnel.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gsoft.portal.system.personnel.entity.PermissionEntity;


public interface PermissionPersistence extends JpaRepository<PermissionEntity, Long> {
	
	//查询所有分类
    @Query("select type from PermissionEntity group by type")
    List<String> getTypes();

    //查询当前行政区划的角色
    @Query("FROM PermissionEntity r where r.type=?1 ")
    Page<PermissionEntity> getPageByType(String type, Pageable pageable);

    @Query("FROM PermissionEntity r")
    Page<PermissionEntity> getPages(Pageable pageable);

	//根据分类查询权限--平台管理员用
	@Query("FROM PermissionEntity p where p.type=?1 ")
	List<PermissionEntity> getListByType(String type);

	//得到所有的权限
	@Query("FROM PermissionEntity p")
	List<PermissionEntity> getList();

	//新增，验证角色代码唯一
	@Query("from PermissionEntity where code=?1 ")
	PermissionEntity isExitByCode(String code);
			
	//修改，验证登录名唯一,排除自己
	@Query("from PermissionEntity where code=?2 and id!=?1")
	PermissionEntity isExitByCode(Long id, String code);
	
	//查询未授权的权限--1.查询出当前行政区划的角色，2.查询出登录人可授权的角色,3.剔掉已经关联的角色的权限
	@Query("from PermissionEntity where id in(select rm.permissionId from  RoleEntity r,RolePersonnelEntity rp,RolePermissionEntity rm where r.id=rp.roleId and ((rp.personnelId=?2 and rp.isTurnGrant=1)) and r.id=rm.roleId)")
	List<PermissionEntity> getHasNoConnectPermission(String loginAreaCode,Long loginPersonId);

    @Query("from PermissionEntity p where p.id in(select rm.permissionId from  RoleEntity r,RolePersonnelEntity rp,RolePermissionEntity rm where r.id=rp.roleId and ((rp.personnelId=?2 and rp.isTurnGrant=1)) and r.id=rm.roleId) and p.type=?3")
    List<PermissionEntity> getHasNoConnectPermission(String currentAreaCode, Long loginPersonId,String type);
    
	//查询已经授权的权限
	@Query("from PermissionEntity where id in (select permissionId from RolePermissionEntity where roleId=?1)")
	List<PermissionEntity> getHasConnectPermission(Long roleId);
	
	//查询已经授权的权限
	@Query("from PermissionEntity where type=?2 and id in (select permissionId from RolePermissionEntity where roleId=?1)")
	List<PermissionEntity> getHasConnectPermission(Long roleId,String type);

	@Query("select p.type from PermissionEntity p where p.id in(select rm.permissionId from  RoleEntity r,RolePersonnelEntity rp,RolePermissionEntity rm  where r.id=rp.roleId and ((rp.personnelId=?1 and rp.isTurnGrant=1)) and r.id=rm.roleId)")
	List<String> getTypesByLoginPersonnel(Long personnelId);
	
	@Query("from PermissionEntity where id in(select rm.permissionId from  RoleEntity r,RolePersonnelEntity rp,RolePermissionEntity rm where r.id=rp.roleId and ((rp.personnelId=?1 and rp.isTurnGrant=1)) and r.id=rm.roleId)")
	List<PermissionEntity> getHasNoConnectPermission(Long personnelId);
	
	@Query("from PermissionEntity p where p.id in(select rm.permissionId from  RoleEntity r,RolePersonnelEntity rp,RolePermissionEntity rm where r.id=rp.roleId and ((rp.personnelId=?1 and rp.isTurnGrant=1)) and r.id=rm.roleId) and p.type=?2")
	List<PermissionEntity> getHasNoConnectPermission(Long personnelId, String type);
	
}
