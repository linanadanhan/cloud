package com.gsoft.portal.webview.widgetconf.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gsoft.portal.webview.widgetconf.entity.WidgetConfEntity;

/**
 * widget配置 Persistence
 * @author SN
 *
 */
public interface WidgetConfPersistence extends JpaRepository<WidgetConfEntity, Long> {
	
	@Query("FROM  WidgetConfEntity c WHERE c.pageUuId=?1 ")
	WidgetConfEntity getWidgetJson(String pageUuId);
}
