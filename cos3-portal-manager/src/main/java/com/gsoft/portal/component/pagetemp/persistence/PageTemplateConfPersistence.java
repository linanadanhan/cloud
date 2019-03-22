package com.gsoft.portal.component.pagetemp.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gsoft.portal.component.pagetemp.entity.PageTemplateConfEntity;

/**
 * 页面模版配置信息Persistence
 * @author SN
 *
 */
public interface PageTemplateConfPersistence extends JpaRepository<PageTemplateConfEntity, Long> {
	
	@Query("from PageTemplateConfEntity where code=?1")
	PageTemplateConfEntity findByCode(String code);

}
