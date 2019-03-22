package com.gsoft.portal.component.compmgr.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gsoft.portal.component.compmgr.entity.BusinessComponentEntity;

/**
 * 业务组件Persistence
 * @author SN
 *
 */
public interface BusinessComponentPersistence extends JpaRepository<BusinessComponentEntity, Long> {

}
