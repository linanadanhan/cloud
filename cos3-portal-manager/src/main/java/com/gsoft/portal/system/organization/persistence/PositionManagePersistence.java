package com.gsoft.portal.system.organization.persistence;

import com.gsoft.portal.system.organization.entity.PositionManageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 职位管理关系持久类
 *
 * @author plsy
 */
public interface PositionManagePersistence extends JpaRepository<PositionManageEntity, Long> {

    @Query("FROM  PositionManageEntity c WHERE  c.positionId=?1")
    List<PositionManageEntity> getPositionManage(Long positionId);

    @Modifying
    @Transactional
    @Query("delete FROM PositionManageEntity p where p.positionId=?1")
    void deleteByPositionId(Long positionId);
}
