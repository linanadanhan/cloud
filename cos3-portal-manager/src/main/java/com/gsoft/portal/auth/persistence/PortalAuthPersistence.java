package com.gsoft.portal.auth.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gsoft.portal.auth.entity.PortalAuthEntity;

/**
 * 门户授权Persistence
 * @author SN
 *
 */
public interface PortalAuthPersistence extends JpaRepository<PortalAuthEntity, Long> {

}
