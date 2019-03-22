package com.gsoft.portal.webview.page.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.gsoft.portal.webview.page.entity.SitePageConfEntity;

/**
 * 页面配置Persistence
 * @author SN
 *
 */
public interface SitePageConfPersistence extends JpaRepository<SitePageConfEntity, Long> {

	@Query("FROM  SitePageConfEntity c WHERE c.pageUuId=?1 ")
	List<SitePageConfEntity> getgetPageWidgets(String pageUuId);
	
	@Query("FROM  SitePageConfEntity c WHERE c.uuId=?1 ")
	SitePageConfEntity getPageConfInfoByUuId(String uuId);

}
