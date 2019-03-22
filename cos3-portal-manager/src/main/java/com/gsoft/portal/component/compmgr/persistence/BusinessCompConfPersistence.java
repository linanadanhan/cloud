package com.gsoft.portal.component.compmgr.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.gsoft.portal.component.compmgr.entity.BusinessCompConfEntity;

/**
 * 业务组件配置Persistence
 * @author SN
 *
 */
public interface BusinessCompConfPersistence extends JpaRepository<BusinessCompConfEntity, Long> {

	@Query("from BusinessCompConfEntity where compId=?1")
	BusinessCompConfEntity getBusinessCompConfByCompId(Long compId);

}
