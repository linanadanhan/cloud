package com.gsoft.portal.webview.page.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gsoft.portal.webview.page.entity.SitePageHelpEntity;

public interface SitePageHelpPersistence extends JpaRepository<SitePageHelpEntity, Long>{
	@Query("FROM  SitePageHelpEntity c WHERE c.siteCode=?1 and c.pageUuId=?2 and c.type=?3 and c.deleted = 0")
	List<SitePageHelpEntity> getSitePgeHelp(String siteCode, String pageUuId, String Type);
	
	@Query("FROM  SitePageHelpEntity c WHERE c.siteCode=?1 and c.type=?2 and c.pageUuId = '' and c.deleted = 0")
	List<SitePageHelpEntity> getSitePgeHelp(String siteCode, String type);
}
