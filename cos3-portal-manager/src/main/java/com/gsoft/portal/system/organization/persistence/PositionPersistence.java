package com.gsoft.portal.system.organization.persistence;

import com.gsoft.portal.system.organization.entity.PositionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 职位的持久类
 *
 * @author plsy
 */
public interface PositionPersistence extends JpaRepository<PositionEntity, Long> {

    @Query("FROM  PositionEntity c WHERE  c.orgId=?1 ")
    List<PositionEntity> findListByOrgId(Long id);

    @Query("FROM  PositionEntity c WHERE  c.orgId=?1")
    Page<PositionEntity> findPageByOrgid(Long id, Pageable pageable);

    @Modifying
    @Transactional
    @Query("delete FROM PositionEntity p where p.orgId=?1")
    void deleteByOrdId(Long orgId);

    @Query("FROM  PositionEntity c WHERE  c.orgId=?1 and c.postId=?2")
    PositionEntity findByOrgIdAndPostId(Long orgId, String postValue);

    @Modifying
    @Transactional
    @Query("delete FROM PositionEntity p where p.postId=?1")
    void deleteByPostId(String postValue);
}
