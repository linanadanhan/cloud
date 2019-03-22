package com.gsoft.portal.webview.page.persistence;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gsoft.portal.webview.page.entity.SitePageHelpHideEntity;

public interface SitePageHelpHidePersistence  extends JpaRepository<SitePageHelpHideEntity, Long>{
	@Query("FROM  SitePageHelpHideEntity c WHERE c.siteCode=?1 and c.owner=?2 and c.deleted = 0")
	List<SitePageHelpHideEntity> getSiteOwnner(String siteCode, String owner);
}
