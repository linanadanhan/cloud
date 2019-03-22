package com.gsoft.portal.system.personnel.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.gsoft.portal.system.personnel.entity.RolePersonnelEntity;


public interface RolePersonnelPersistence extends JpaRepository<RolePersonnelEntity, Long> {

	
	//删除人员时，删除关联
	@Query("delete from RolePersonnelEntity where personnelId=?1")
	@Modifying
	@Transactional
	void delByPersonnelId(Long personnelId);
	
	//删除角色时，删除关联
	@Query("delete from RolePersonnelEntity where roleId=?1")
	@Modifying
	@Transactional
	void delByRoleId(Long roleId);

	//保存关联时，先把之前保存的关联删掉
	@Query("delete from RolePersonnelEntity where roleId=?1 and personnelId in (select id from PersonnelEntity where areaCode=?2 )")
	@Modifying
	@Transactional
    void delHasConnectPersonnel(Long roleId,String areaCode);
	
	//保存关联时，先把之前保存的关联删掉--级联
	@Query("delete from RolePersonnelEntity where roleId=?1 and personnelId in (select id from PersonnelEntity where areaCascade like %?2% )")
	@Modifying
	@Transactional
	void delHasConnectPersonnelToCascade(Long roleId,String areaCode);
	
	//人员关联角色--删除某分类下的关联的角色
    @Transactional
    @Modifying
    @Query("delete from RolePersonnelEntity where personnelId=?1 and roleId in (select id from RoleEntity where type=?2)")
    void delHasConnectRole(Long personnelId,String type);

	@Query("from RolePersonnelEntity where roleId=?1")
	List<RolePersonnelEntity> getPersonsByRoleId(Long roleId);

//	//人员关联角色--删除所有分类下的关联的角色
//    @Transactional
//    @Modifying
//    @Query("delete from RolePersonnelEntity where personnelId=?1")
//    void delHasConnectRole(Long personnelId);


	@Query("from RolePersonnelEntity where roleId in ?1 ")
	@Modifying
	@Transactional
	List<RolePersonnelEntity> getPersonsByRoleIds(List<Long> roleIds);
}
