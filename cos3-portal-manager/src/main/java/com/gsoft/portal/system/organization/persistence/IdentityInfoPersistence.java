package com.gsoft.portal.system.organization.persistence;

import com.gsoft.portal.system.organization.entity.IdentityInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 身份信息持久类
 *
 * @author plsy
 */
public interface IdentityInfoPersistence extends JpaRepository<IdentityInfoEntity, Long> {

    @Query("FROM IdentityInfoEntity c WHERE c.positionId in (?1) ")
    Page<IdentityInfoEntity> findPage(List<Long> join, Pageable pageable);

    @Query("FROM IdentityInfoEntity c WHERE c.positionId in (?1) ")
    List<IdentityInfoEntity> findList(List<Long> join);

    @Query("FROM IdentityInfoEntity c WHERE c.userId = ?1")
    List<IdentityInfoEntity> findByUserId(Long personnelId);

    @Query("FROM IdentityInfoEntity c WHERE c.positionId = ?1")
    Page<IdentityInfoEntity> findByPositionId(Long positionId, Pageable pageable);

    @Query("FROM IdentityInfoEntity c WHERE c.positionId = ?1")
    List<IdentityInfoEntity> findListByPositionId(Long positionId);

    @Modifying
    @Transactional
    @Query("delete FROM IdentityInfoEntity p where p.positionId=?1 and p.userId=?2")
    void deleteByPositionAndPerson(Long positionId, Long personId);

    @Query("FROM IdentityInfoEntity c WHERE c.userId = ?1 and c.positionId = ?2 ")
    IdentityInfoEntity findByUserAndPostion(Long personId, Long postionId);

    @Modifying
    @Transactional
    @Query("delete FROM IdentityInfoEntity p where p.userId=?1")
    void deleteByPersonId(Long personId);
}
