package com.gsoft.portal.component.compmgr.persistence;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.gsoft.portal.component.compmgr.entity.ComponentEntity;

/**
 * 部件Persistence
 * @author SN
 *
 */
public interface ComponentPersistence extends JpaRepository<ComponentEntity, Long> {

    //启用、停用
    @Transactional
    @Modifying
    @Query("update ComponentEntity set status=?2 where id=?1")
	void updateStatus(Long id, Boolean status);

    @Query("from ComponentEntity where code=?1")
	ComponentEntity getComponentByCode(String compCode);
}
