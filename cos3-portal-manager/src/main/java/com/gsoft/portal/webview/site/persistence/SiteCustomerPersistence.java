package com.gsoft.portal.webview.site.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gsoft.portal.webview.site.entity.SiteCustomerEntity;

/**
 * 站点Persistence
 * @author SN
 *
 */
public interface SiteCustomerPersistence extends JpaRepository<SiteCustomerEntity, Long> {
	
	@Query("FROM  SiteCustomerEntity c WHERE c.siteCode=?1 and c.domain=?2 and c.customer=?3 ")
	SiteCustomerEntity getSiteCustomerInfo(String siteCode, String domainName, String customer);

}
