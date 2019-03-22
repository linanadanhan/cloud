package com.gsoft.portal.system.personnel.persistence;

import com.gsoft.portal.system.personnel.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


public interface RolePermissionPersistence extends JpaRepository<RolePermissionEntity, Long> {

	
	//删除角色时，删除关联
	@Query("delete from RolePermissionEntity where roleId=?1")
	@Modifying
	@Transactional
	void delByRoleId(Long roleId);
	
	
	//删除权限时，删除关联
	@Query("delete from RolePermissionEntity where permissionId=?1")
	@Modifying
	@Transactional
	void delByPermissionId(Long permissionId);

	//保存关联时，先把之前保存的关联删掉
	@Query("delete from RolePermissionEntity where roleId=?1")
	@Modifying
	@Transactional
    void deleteByRoleId(Long roleId);
	
	//保存关联时，先把之前保存的关联删掉
	@Query("delete from RolePermissionEntity where permissionId=?1")
	@Modifying
	@Transactional
    void deleteByPermissionId(Long permissionId);
	
	//保存关联时，先把之前保存的关联删掉
	@Query("delete from RolePermissionEntity where roleId=?1 and permissionId in (select id from PermissionEntity where type=?2)")
	@Modifying
	@Transactional
	void deleteByRoleId(Long roleId,String type);
	
	//保存关联时，先把之前保存的关联删掉
	@Query("delete from RolePermissionEntity where permissionId=?1 and roleId in (select id from RoleEntity where type=?2)")
	@Modifying
	@Transactional
	void deleteByPermissionId(Long permissionId,String type);
}
