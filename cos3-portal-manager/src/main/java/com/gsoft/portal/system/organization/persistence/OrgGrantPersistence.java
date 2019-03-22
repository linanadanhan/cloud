package com.gsoft.portal.system.organization.persistence;

import com.gsoft.portal.system.organization.entity.OrgGrantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 机构授权
 *
 * @author plsy
 */
public interface OrgGrantPersistence extends JpaRepository<OrgGrantEntity, Long> {


    /**
     * 根据identityId删除
     *
     * @param identityId
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM OrgGrantEntity c WHERE c.identityId=?1")
    void deleteByIdentityId(Long identityId);

    /**
     * 根据identityId和维度删除
     *
     * @param identityId
     * @param dimension
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM OrgGrantEntity c WHERE c.identityId=?1 and c.dimension=?2")
    void deleteByIdentityId(Long identityId, String dimension);

    /**
     * 根据identityId查找
     *
     * @param identityId
     * @return
     */
    @Query("FROM  OrgGrantEntity c WHERE  c.identityId=?1")
    List<OrgGrantEntity> findByIdentity(Long identityId);

    /**
     * 根据userId查找
     *
     * @param userId
     * @return
     */
    @Query("FROM  OrgGrantEntity c WHERE  c.userId=?1")
    List<OrgGrantEntity> findByUserId(Long userId);

    /**
     * 根据userId查找
     *
     * @param userId
     * @return
     */
    @Query("FROM  OrgGrantEntity c WHERE  c.userId=?1 and c.dimension=?2")
    List<OrgGrantEntity> findByUserId(Long userId, String dimension);

    /**
     * 根据identityId查找
     *
     * @param identityId
     * @return
     */
    @Query("FROM  OrgGrantEntity c WHERE  c.identityId=?1 and c.dimension=?2")
    List<OrgGrantEntity> findByIdentity(Long identityId, String dimension);


    /**
     * 根据Org查找
     *
     * @param orgId
     * @return
     */
    @Query("FROM  OrgGrantEntity c WHERE  c.orgId=?1 and c.dimension=?2")
    List<OrgGrantEntity> findByOrgId(Long orgId, String dimension);

    @Query("FROM  OrgGrantEntity c WHERE  c.orgId=?1 and c.userId=?2 and c.dimension=?3")
    OrgGrantEntity findByOrgAndUser(Long orgId, Long aLong, String dimension);

    @Modifying
    @Transactional
    @Query("DELETE FROM OrgGrantEntity c WHERE c.orgId=?1 and c.dimension=?2")
    void deleteByOrgAndDimension(Long orgId, String dimension);
}
