package com.gsoft.portal.webview.widgetconf.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.gsoft.portal.webview.widgetconf.entity.CustomProfileConfEntity;

/**
 * 个性化偏好配置 Persistence
 * 
 * @author SN
 *
 */
public interface CustomProfileConfPersistence extends JpaRepository<CustomProfileConfEntity, Long> {

	@Query("FROM CustomProfileConfEntity c WHERE c.widgetUuId=?1 and c.userId=?2 ")
	CustomProfileConfEntity findCustomProfileConfInfo(String widgetId, long personnelId);
	
	@Query("FROM CustomProfileConfEntity c WHERE c.pageUuId=?1 and c.userId=?2 ")
	List<CustomProfileConfEntity> getCusConfByPageUuId(String pageUuId, Long personnelId);
}
