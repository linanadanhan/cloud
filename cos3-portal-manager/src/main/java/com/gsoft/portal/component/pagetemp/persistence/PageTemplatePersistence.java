package com.gsoft.portal.component.pagetemp.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gsoft.portal.component.pagetemp.entity.PageTemplateEntity;

/**
 * 页面模版信息Persistence
 * @author SN
 *
 */
public interface PageTemplatePersistence extends JpaRepository<PageTemplateEntity, Long> {
	
	@Query("from PageTemplateEntity where code=?1")
	PageTemplateEntity findByCode(String code);
	
	@Query("from PageTemplateEntity where code=?1 and id !=?2")
	PageTemplateEntity findByCode(String code, Long id);
	
	@Query("from PageTemplateEntity")
	List<PageTemplateEntity> getAllPageTempList();

}
