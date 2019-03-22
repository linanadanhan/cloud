package com.gsoft.portal.system.personnel.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.gsoft.portal.system.personnel.dto.RoleDto;
import com.gsoft.portal.system.personnel.entity.RoleEntity;

public interface RolePersistence extends JpaRepository<RoleEntity, Long> {

    //查询所有角色
    @Query("select type from RoleEntity where deleted=0 group by type")
    List<String> getRoleTypes();
    
    @Query("FROM RoleEntity r where r.createBy=?1 and r.deleted=0")
    Page<RoleEntity> getAllListByCreateBy(Long createBy, Pageable pageable);
    
    @Query("FROM RoleEntity r where r.createBy=?1 and r.type=?2 and r.deleted=0")
    Page<RoleEntity> getAllListByCreateBy(Long createBy,String type, Pageable pageable);

    //新增，验证角色代码唯一
    @Query("from RoleEntity where code=?1 and deleted=0")
    RoleEntity isExitByCode(String code);

    //修改，验证登录名唯一,排除自己
    @Query("from RoleEntity where code=?2 and id!=?1 and deleted=0")
    RoleEntity isExitByCode(Long id, String code);

    //修改可用状态
    @Transactional
    @Modifying
    @Query("update RoleEntity set status=?2 where id=?1")
    void updateStatus(Long id, Boolean status);

    //删除
    @Transactional
    @Modifying
    @Query("update RoleEntity set deleted=1 where id=?1")
    void delById(Long id);

    //查询未授权的角色--踢掉已经授权的(后台踢掉)-------已废弃，先放着
    @Query("from RoleEntity where (createBy=?2 or id in (select roleId from RolePersonnelEntity where personnelId=?1 and isTurnGrant=1)) and id not in (select roleId from RolePersonnelEntity where personnelId=?3)")
    List<RoleEntity> getHasNoConnectRole(Long loginPersonId, Long loginPersonId2, Long personnelId);
    //已废弃，先放着
    @Query("from RoleEntity where type=?4 and (createBy=?2 or id in (select roleId from RolePersonnelEntity where personnelId=?1 and isTurnGrant=1)) and id not in (select roleId from RolePersonnelEntity where personnelId=?3)")
    List<RoleEntity> getHasNoConnectRole(Long loginPersonId,  Long loginPersonId2, Long personnelId,String type);
    
    //查询未授权的角色--踢掉已经授权的(前台剔除)
    @Query("from RoleEntity where (createBy=?2 or id in (select roleId from RolePersonnelEntity where personnelId=?1 and isTurnGrant=1)) and deleted=0")
    List<RoleEntity> getHasNoConnectRole(Long loginPersonId,  Long loginPersonId2);

    @Query("from RoleEntity where type=?3 and (createBy=?2 or id in (select roleId from RolePersonnelEntity where personnelId=?1 and isTurnGrant=1)) and deleted=0 ")
    List<RoleEntity> getHasNoConnectRole(Long loginPersonId, Long loginPersonId2,String type);
    /**
     * 人员管理角色--查询角色分类
     * @param loginPersonId
     * @param loginPersonNum
     * @return
     */
    @Query("select distinct type from RoleEntity where (createBy=?2 or id in (select roleId from RolePersonnelEntity where personnelId=?1 and isTurnGrant=1)) and deleted=0 ")
    List<String> getHasNoConnectRoleTypes(Long loginPersonId, Long loginPersonId2);
    //查询已经授权的角色
    @Query("select distinct new com.gsoft.portal.system.personnel.dto.RoleDto(r.id,r.name,r.code,rp.isTurnGrant) from RoleEntity  r ,RolePersonnelEntity rp where r.id=rp.roleId and rp.personnelId=?1 and r.deleted=0)")
    List<RoleDto> getHasConnectRole(Long personnelId);
    
    @Query("select distinct new com.gsoft.portal.system.personnel.dto.RoleDto(r.id,r.name,r.code,rp.isTurnGrant) from RoleEntity  r ,RolePersonnelEntity rp where r.id=rp.roleId and rp.personnelId=?1 and r.type=?2 and r.deleted=0)")
    List<RoleDto> getHasConnectRole(Long personnelId,String type);
    
    //根据分类查询权限--平台管理员用
  	@Query("FROM RoleEntity p where p.type=?1 ")
  	List<RoleEntity> getListByType(String type);

  	//得到所有的权限
  	@Query("FROM RoleEntity p")
  	List<RoleEntity> getList();
  	
	//查询未授权的角色--1.查询出当前行政区划的角色，2.查询出登录人可授权的角色,3.剔掉已经关联的角色的权限
	@Query("from RoleEntity where id in(select rm.roleId from  RoleEntity r,RolePersonnelEntity rp,RolePermissionEntity rm where r.id=rp.roleId and ((rp.personnelId=?2 and rp.isTurnGrant=1)) and r.id=rm.roleId)")
	List<RoleEntity> getHasNoConnectRole(String loginAreaCode,Long loginPersonId);
	
    @Query("from RoleEntity p where p.id in(select rm.roleId from  RoleEntity r,PermissionEntity rp,RolePermissionEntity rm where rp.id=rm.permissionId and r.id=rm.roleId) and p.type=?3")
    List<RoleEntity> getHasNoConnectRole(String currentAreaCode, Long loginPersonId,String type);
    
	//查询已经授权的权限
	@Query("from RoleEntity where id in (select roleId from RolePermissionEntity where permissionId=?1)")
	List<RoleEntity> getHasConnectPermission(Long permissionId);
	
	//查询已经授权的权限
	@Query("from RoleEntity where type=?2 and id in (select roleId from RolePermissionEntity where permissionId=?1)")
	List<RoleEntity> getHasConnectPermission(Long permissionId,String type);

    @Query("FROM RoleEntity p where p.code=?1 ")
    List<RoleEntity> getListByCode(String code);

    @Query("FROM RoleEntity where id in ?1 and deleted=0 ")
    List<RoleEntity> getRolesByIds(List<Long> ids);

    @Query("FROM RoleEntity p where p.dimension=?1")
    List<RoleEntity> getRolesByDimension(String role_dimension);

}
