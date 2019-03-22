package com.gsoft.portal.webview.page.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gsoft.portal.webview.page.entity.SitePageEntity;

/**
 * 页面Persistence
 * @author SN
 *
 */
public interface SitePagePersistence extends JpaRepository<SitePageEntity, Long> {

	@Query("FROM  SitePageEntity c WHERE c.siteCode=?1 order by c.sortNo, c.id asc")
	List<SitePageEntity> getSitePageTree(String siteCode);
	
	@Query("delete FROM  SitePageEntity c WHERE c.id in (?1) ")
	void delSitePage(List<Long> ids);

	@Query("FROM  SitePageEntity c WHERE c.siteCode=?1 and type=?2 order by c.sortNo, c.id asc")
	List<SitePageEntity> getSitePageTree(String siteCode, String type);

	@Query("FROM  SitePageEntity c WHERE c.path=?1 and c.cascade=?2 and c.type=?3 and c.siteCode=?4")
	SitePageEntity findByPaht(String path, String cascade, String type, String siteCode);

	@Query("FROM  SitePageEntity c WHERE c.path=?1 and id !=?2 and c.cascade=?3 and c.siteCode=?4 and c.type=?5")
	SitePageEntity findByPath(String path, Long id, String cascade, String siteCode, String type);
	
	@Query("FROM  SitePageEntity c WHERE c.siteCode=?1 and c.type = '1' order by c.sortNo, c.id asc ")
	List<SitePageEntity> getAllPublicPages(String siteCode);

}
