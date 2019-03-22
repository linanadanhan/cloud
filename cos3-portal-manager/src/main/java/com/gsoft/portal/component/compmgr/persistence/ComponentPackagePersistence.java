package com.gsoft.portal.component.compmgr.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.gsoft.portal.component.compmgr.entity.ComponentPackageEntity;

/**
 * 部件包Persistence
 * @author SN
 *
 */
public interface ComponentPackagePersistence extends JpaRepository<ComponentPackageEntity, Long> {
	
	@Query("from ComponentPackageEntity where componentName=?1 and version=?2")
	ComponentPackageEntity getComponentPackage(String componentName, String version);
}
