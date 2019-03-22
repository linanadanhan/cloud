package com.gsoft.portal.system.sso.persistence;

import com.gsoft.portal.system.sso.entity.SsoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 外部接入的持久类
 *
 * @author plsy
 */
public interface SsoPersistence extends JpaRepository<SsoEntity, Long> {


}
