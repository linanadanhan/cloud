package com.gsoft.portal.component.theme.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.gsoft.portal.component.theme.entity.CustomThemeEntity;

/**
 * 用户自定义主题Persistence
 * @author SN
 *
 */
public interface CustomThemePersistence extends JpaRepository<CustomThemeEntity, Long> {

	@Query("FROM  CustomThemeEntity c WHERE c.userId=?1 and c.siteCode=?2")
	CustomThemeEntity existsCustomTheme(Long userId, String siteCode);

}
