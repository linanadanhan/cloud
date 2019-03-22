package com.gsoft.portal.webview.site.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gsoft.portal.webview.site.entity.SiteEntity;

/**
 * 站点Persistence
 * @author SN
 *
 */
public interface SitePersistence extends JpaRepository<SiteEntity, Long> {

	@Query("FROM  SiteEntity c WHERE c.code=?1 ")
	SiteEntity findByCode(String code);
	
	@Query("FROM  SiteEntity c WHERE c.code=?1 and id !=?2")
	SiteEntity findByCode(String code, Long id);

	@Query("FROM  SiteEntity c ")
	List<SiteEntity> getAllSiteList();

}
