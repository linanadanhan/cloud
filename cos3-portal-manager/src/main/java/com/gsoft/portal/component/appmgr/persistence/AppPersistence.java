package com.gsoft.portal.component.appmgr.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.gsoft.portal.component.appmgr.entity.AppEntity;

/**
 * 应用信息Persistence
 * @author SN
 *
 */
public interface AppPersistence extends JpaRepository<AppEntity, Long> {
	
	@Query("from AppEntity where code=?1")
	AppEntity findByCode(String code);
	
	@Query("from AppEntity where code=?1 and id !=?2")
	AppEntity findByCode(String code, Long id);

}
