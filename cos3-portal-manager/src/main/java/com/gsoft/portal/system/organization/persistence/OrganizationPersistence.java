package com.gsoft.portal.system.organization.persistence;

import com.gsoft.portal.system.organization.entity.OrganizationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 组织机构Persistence
 *
 * @author SN
 */
public interface OrganizationPersistence extends JpaRepository<OrganizationEntity, Long> {

    @Query("FROM  OrganizationEntity c WHERE c.deleted=0 AND c.code=?1 ")
    OrganizationEntity findByCode(String code);

    @Modifying
    @Transactional
    @Query("update OrganizationEntity set deleted=true where id=?1 or cascade like %?2%")
    Integer deleteById(Long id, String code);

    @Query("FROM OrganizationEntity c WHERE c.deleted=0 AND c.cascade like %?1% order by c.sortNo asc")
    Page<OrganizationEntity> findByCascade(String code, Pageable pageable);

    @Query("FROM  OrganizationEntity c WHERE c.deleted=0 AND c.parentId=?1 order by c.sortNo asc")
    Page<OrganizationEntity> findByPid(Long id, Pageable pageable);
    
    @Query("FROM OrganizationEntity c WHERE c.deleted=0 AND (c.code=?1 OR c.cascade like %?2% )  order by c.sortNo asc ")
    List<OrganizationEntity> getTreeList(String orgCode, String casCade);

    @Query("FROM OrganizationEntity c WHERE c.deleted=0 AND (c.code=?1 OR c.cascade like %?2% ) AND c.dimension=?3 OR c.parentId = 0 order by c.sortNo asc ")
    List<OrganizationEntity> getTreeList(String orgCode, String casCade,String dimension);
    
    @Query("FROM OrganizationEntity c WHERE c.deleted=0  AND (c.code=?1 OR c.cascade like %?2% )  AND (c.orgType=?3 OR c.parentId = 0) AND c.dimension=?4  order by c.sortNo asc ")
    List<OrganizationEntity> getTreeList(String orgCode, String casCade,String orgType,String dimension);

    @Query("FROM OrganizationEntity c WHERE c.deleted=0  AND (c.code=?1 OR c.cascade like %?2% )  AND c.orgType=?3 AND c.dimension=?4  order by c.sortNo asc ")
    List<OrganizationEntity> getList(String orgCode, String casCade,String orgType,String dimension);

    @Query("FROM OrganizationEntity c WHERE c.deleted=0 AND (c.code=?1 OR c.cascade like %?2% ) AND c.dimension=?3")
    List<OrganizationEntity> getTreeListByCondition(String orgCode, String casCade, String dimension);

    @Query("FROM  OrganizationEntity c WHERE c.deleted=0 AND (c.dimension = ?1 or c.dimension = '') order by c.sortNo asc ")
    List<OrganizationEntity> findByDimension(String dimension);

    @Query("FROM OrganizationEntity c WHERE c.deleted=0 AND c.cascade like %?1% ")
    List<OrganizationEntity> findByCascade(String code);

    @Query("FROM  OrganizationEntity c WHERE c.deleted=0 AND c.parentId=?1")
    List<OrganizationEntity> findByPid(Long id);

    @Query(value = "SELECT g.* FROM cos_sys_personnel p, cos_sys_user_org ug, cos_organization_org g WHERE p.c_id = ug.c_personnel_id AND g.c_id = ug.c_org_id AND p.c_id = ?1 AND (g.c_dimension = ?2 or g.c_dimension = '')", nativeQuery = true)
    List<OrganizationEntity> findByPersonAndDimension(Long personnelId, String dimension);

    @Query("FROM OrganizationEntity c WHERE c.deleted=0 AND c.cascade like %?1% AND (c.dimension = ?2 or c.dimension = '')")
    List<OrganizationEntity> findByCascadeAndDimension(String orgCode, String dimension);

    @Query("FROM  OrganizationEntity c WHERE c.deleted=0 AND c.code=?1 AND c.dimension = ?2")
    OrganizationEntity findByCodeAndDimension(String orgCode, String dimension);

    @Query("FROM OrganizationEntity c WHERE c.deleted=0 AND c.createBy=?1 AND (c.dimension=?2 or c.dimension = '') or c.parentId = 0")
    List<OrganizationEntity> findbyCreateAndDimension(Long personnelId, String dimension);
    
    @Query("FROM OrganizationEntity c WHERE c.deleted=0 AND c.createBy=?1  AND (c.dimension=?2 or c.dimension = '') AND c.orgType=?3 or c.parentId = 0")
    List<OrganizationEntity> findbyCreateAndDimension(Long personnelId, String dimension,String orgType);
    
    @Query(value = "SELECT o.* FROM cos_organization_org o, cos_sys_personnel p  ,cos_sys_user_org uo WHERE o.c_id = uo.c_org_id AND p.c_id = uo.c_personnel_id AND p.c_id = ?", nativeQuery = true)
    List<OrganizationEntity> findByPersonnelId(Long personnelId);

    @Query("FROM  OrganizationEntity c WHERE c.deleted=0 AND c.id in ?1 ")
    List<OrganizationEntity> findOrgsByIds(List<Long> ids);

    @Query("FROM OrganizationEntity c WHERE c.deleted=0 AND c.code in ?1 ")
    List<OrganizationEntity> getOrgsByMulCode(List<String> code);

    @Query("FROM OrganizationEntity c WHERE c.name like %?1% AND c.dimension=?2")
    List<OrganizationEntity> getOrgInfoByNameAndDimension(String departmentName,String dimension);

}
