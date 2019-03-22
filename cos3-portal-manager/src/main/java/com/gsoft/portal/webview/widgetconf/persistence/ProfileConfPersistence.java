package com.gsoft.portal.webview.widgetconf.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gsoft.portal.webview.widgetconf.entity.ProfileConfEntity;

/**
 * 系统偏好配置 Persistence
 * 
 * @author SN
 *
 */
public interface ProfileConfPersistence extends JpaRepository<ProfileConfEntity, Long> {

	@Query("FROM  ProfileConfEntity c WHERE c.widgetUuId=?1 ")
	ProfileConfEntity findProfileConfInfo(String widgetId);

	@Query("FROM  ProfileConfEntity c WHERE c.tmpWidgetUuId=?1 ")
	List<ProfileConfEntity> getRelInstanceList(String widgetUuId);
	
	@Query("FROM  ProfileConfEntity c WHERE c.pageUuId=?1 ")
	List<ProfileConfEntity> getSysConfListByPageUuId(String pageUuId);

}
