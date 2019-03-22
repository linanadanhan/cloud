package com.gsoft.portal.webview.badge.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gsoft.portal.webview.badge.entity.PageBadgeEntity;

/**
 * 页面badge Persistence
 * @author SN
 *
 */
public interface PageBadgePersistence extends JpaRepository<PageBadgeEntity, Long> {
	
	@Query("FROM  PageBadgeEntity where pageUuId=?1 and widgetUuId=?2")
	PageBadgeEntity getPageBadgeInfo(String pageUuId, String widgetUuId);
	
	@Query("select badgeName FROM  PageBadgeEntity where pageUuId in (?1)")
	List<String> getBadgeNamesByPageUuId(List<String> pageUuIdList);
}
