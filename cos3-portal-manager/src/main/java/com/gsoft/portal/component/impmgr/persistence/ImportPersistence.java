package com.gsoft.portal.component.impmgr.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gsoft.portal.component.impmgr.entity.ImportEntity;

/**
 * 导入Persistence
 * @author SN
 *
 */
public interface ImportPersistence extends JpaRepository<ImportEntity, Long> {

}
