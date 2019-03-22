package com.gsoft.portal.webview.widgetconf.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.gsoft.portal.webview.widgetconf.entity.CustomWidgetConfEntity;

/**
 * 个性化widget配置 Persistence
 * @author SN
 *
 */
public interface CustomWidgetConfPersistence extends JpaRepository<CustomWidgetConfEntity, Long> {

	@Query("FROM  CustomWidgetConfEntity c WHERE c.pageUuId=?1 and c.userId=?2 ")
	CustomWidgetConfEntity getWidgetJson(String pageUuId, Long personnelId);
}
