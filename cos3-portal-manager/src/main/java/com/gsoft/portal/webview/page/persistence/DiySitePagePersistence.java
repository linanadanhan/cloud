package com.gsoft.portal.webview.page.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gsoft.portal.webview.page.entity.DiySitePageEntity;

/**
 * 个性化页面Persistence
 * @author SN
 *
 */
public interface DiySitePagePersistence extends JpaRepository<DiySitePageEntity, Long> {

	@Query("FROM DiySitePageEntity c WHERE c.siteCode=?1 and c.userId=?2 order by c.sortNo, c.id asc")
	List<DiySitePageEntity> getDiySitePageTree(String siteCode, long personnelId);

	@Query("FROM DiySitePageEntity c WHERE c.path=?1 and c.cascade=?2 and c.siteCode=?3 and c.userId=?4")
	DiySitePageEntity findPageByPath(String path, String cascade, String siteCode, Long personnelId);

	@Query("FROM DiySitePageEntity c WHERE c.path=?1 and id !=?2 and c.cascade=?3 and c.siteCode=?4 and c.userId=?5")
	DiySitePageEntity findPageByPath(String path, Long id, String cascade, String siteCode, Long personnelId);

	@Query("FROM DiySitePageEntity c WHERE c.sysPageId=?1 and c.userId=?2")
	DiySitePageEntity getSitePageInfoByPageId(Long parentId, Long personnelId);
	
	@Query("FROM DiySitePageEntity c WHERE c.userId=?1 and c.uuId=?2")
	DiySitePageEntity getDiySitePageInfoByUuId(long personnelId, String pageUuId);
}
