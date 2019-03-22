package com.gsoft.portal.system.personnel.persistence;

import com.gsoft.portal.system.personnel.dto.PersonnelDto;
import com.gsoft.portal.system.personnel.entity.PersonnelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface PersonnelPersistence extends JpaRepository<PersonnelEntity, Long> {

    //新增，验证登录名唯一
    @Query("from PersonnelEntity where loginName=?1 and deleted=0")
    PersonnelEntity isExitByLoginName(String loginName);

    //修改，验证登录名唯一,排除自己
    @Query("from PersonnelEntity where loginName=?2 and id!=?1")
    PersonnelEntity isExitByLoginName(Long id, String loginName);

    //新增，验证手机号唯一
    @Query("from PersonnelEntity where mobilePhone=?1 ")
    PersonnelEntity isExitByPhone(String phone);

    //修改，验证手机号唯一,排除自己
    @Query("from PersonnelEntity where mobilePhone=?2 and id!=?1 ")
    PersonnelEntity isExitByPhone(Long id, String phone);

    //修改可用状态
    @Transactional
    @Modifying
    @Query("update PersonnelEntity set status=?2 where id=?1")
    void updateStatus(Long id, Boolean status);

    //逻辑删除
    @Transactional
    @Modifying
    @Query("update PersonnelEntity set deleted=1 where id=?1")
    void delById(Long id);

    //根据手机号得到唯一对象
    @Query("from PersonnelEntity where mobilePhone=?1")
    PersonnelEntity getOneByPhone(String phone);

    //根据登录名和行政区划得到唯一对象
    @Query("from PersonnelEntity where loginName=?1 and deleted=0")
    PersonnelEntity getOneByloginNameAndAreaCode(String loginName);

    //--------------------------角色关联人员------------------
    @Query("select p.id,p.name from PersonnelEntity p,OrganizationEntity o,UserOrganizationEntity po where p.id=po.personId and o.id=po.orgId and p.deleted=0 and o.code=?1 ")
    List<PersonnelEntity> getHasNoConnectPerson(String orgCode);

    //查询未授权的角色--级联
    @Query("select p.id,p.name from PersonnelEntity  p,OrganizationEntity o,UserOrganizationEntity po where p.id=po.personId and o.id=po.orgId and p.deleted=0 and o.cascade like %?1% ")
    List<PersonnelEntity> getHasNoConnectPersonToCascade(String orgCode);

    //查询已经授权的角色
    @Query("select new  com.gsoft.portal.system.personnel.dto.PersonnelDto(p.id, p.name, p.loginName, rp.isTurnGrant) from PersonnelEntity  p ,RolePersonnelEntity rp where p.id=rp.personnelId and rp.roleId=?1 and p.deleted=0")
    List<PersonnelDto> getHasConnectPerson(Long roleId);

    /**
     * 获取指定机构id下的所有用户（未删除用户）
     *
     * @param orgId
     * @return
     */
    @Query(" FROM PersonnelEntity p WHERE p.id IN (SELECT po.personId FROM UserOrganizationEntity po WHERE po.orgId = ?1) AND p.deleted=0")
    List<PersonnelEntity> getPersons(Long orgId);

    //-----------------移动--------------------
    //查询移动的人员
    @Query("from PersonnelEntity where id in (?1)")
    List<PersonnelEntity> getMovePersonnel(List<Long> idList);

    //逻辑修改失效时间和逻辑删除
    @Transactional
    @Modifying
    @Query(value = "update cos_sys_personnel set c_deleted=1,c_aead_time=now() where c_id in (?1)", nativeQuery = true)
    void updateAeadTime(List<Long> idList);

    /**
     * 根据手机号修改密码
     *
     * @param mobile
     * @param password1
     */
    @Transactional
    @Modifying
    @Query("update PersonnelEntity set passWord = ?2 where mobilePhone = ?1 and deleted=0")
    void updatePassword(String mobile, String password1);

    @Query("from PersonnelEntity where name like %:condition%  or loginName like %:condition% or mobilePhone like %:condition% ")
    List<PersonnelEntity> vagueQueryPerson(@Param("condition") String condition);

    @Query(value = "SELECT p.* FROM cos_sys_personnel p , cos_sys_user_org uo , cos_organization_org o WHERE p.c_id = uo.c_personnel_id AND o.c_id = uo.c_org_id AND o.c_dimension = ?1 ", nativeQuery = true)
    List<PersonnelEntity> getAllPersonByDimension(String dimension);

    @Query(value = "SELECT p.* FROM cos_organization_org o, cos_sys_personnel p  ,cos_sys_user_org uo WHERE o.c_id = uo.c_org_id AND p.c_id = uo.c_personnel_id AND o.c_id = ?1", nativeQuery = true)
    List<PersonnelEntity> findPersonByOrgId(Long id);


    //根据纬度，机构，查询本级和下级所有人员,如果是根目录--排除系统管理员
    @Query("SELECT new  com.gsoft.portal.system.personnel.dto.PersonnelDto(p.id, p.name, p.loginName, o.code, o.id, o.dimension) FROM OrganizationEntity o, UserOrganizationEntity uo , PersonnelEntity p  WHERE  o.dimension = ?1 AND ( o.id=?2 OR o.cascade like ?3 ) AND o.code!='0000' AND o.id = uo.orgId AND uo.personId= p.id ")
    List<PersonnelDto> getAllPersons(String dimension,Long orgId,String cascade);

    //根据纬度，机构，查询本级和下级所有人员,如果是根目录--排除系统管理员
    @Query("SELECT new  com.gsoft.portal.system.personnel.dto.PersonnelDto(p.id, p.name, p.loginName, o.code, o.id, o.dimension) FROM OrganizationEntity o, UserOrganizationEntity uo , PersonnelEntity p  WHERE  o.dimension = ?1 AND  o.id=?2 AND o.code!='0000' AND o.id = uo.orgId AND uo.personId= p.id ")
    List<PersonnelDto> getAllPersons(String dimension,Long orgId);

    @Transactional
    @Modifying
    @Query("update PersonnelEntity set passWord = ?2 where id = ?1 and deleted=0")
	void resetPassword(Long id, String password);

//    @Query("from PersonnelEntity where name like %:condition% and deleted = 0")
//    List<PersonnelEntity> vagueQueryPersonList(@Param("condition") String condition);
}
