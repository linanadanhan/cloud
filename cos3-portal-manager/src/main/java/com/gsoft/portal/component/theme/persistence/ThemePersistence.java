package com.gsoft.portal.component.theme.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gsoft.portal.component.theme.entity.ThemeEntity;

/**
 * 主题Persistence
 * @author SN
 *
 */
public interface ThemePersistence extends JpaRepository<ThemeEntity, Long> {

	@Query("FROM  ThemeEntity c WHERE c.code=?1 ")
	ThemeEntity findByCode(String code);
	
	@Query("FROM  ThemeEntity c WHERE c.code=?1 and c.id !=?2 ")
	ThemeEntity findByCode(String code, Long id);

	@Query("FROM ThemeEntity p ")
	List<ThemeEntity> getThemeList();

	@Query("FROM ThemeEntity p where p.isOpen = ?1 ")
	List<ThemeEntity> getThemeList(String isOpen);

	@Query("FROM ThemeEntity p where p.code = ?1 and p.isOpen =?2 ")
	ThemeEntity findByCodeAndOpen(String themeCode, String isOpen);

}
