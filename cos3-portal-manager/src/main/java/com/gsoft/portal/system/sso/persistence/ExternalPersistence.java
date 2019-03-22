package com.gsoft.portal.system.sso.persistence;

import com.gsoft.portal.system.sso.entity.ExternalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 外部接入关系的持久类
 *
 * @author plsy
 */
public interface ExternalPersistence extends JpaRepository<ExternalEntity, Long> {

    @Modifying
    @Transactional
    @Query("delete FROM ExternalEntity e where e.systemCode=?1")
    void deleteByServerCode(String serverCode);

    @Query("FROM ExternalEntity e where e.systemCode=?1")
    List<ExternalEntity> findByServerCode(String serverCode);

}
