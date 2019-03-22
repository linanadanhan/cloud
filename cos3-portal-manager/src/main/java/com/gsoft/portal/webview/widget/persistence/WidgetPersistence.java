package com.gsoft.portal.webview.widget.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gsoft.portal.webview.widget.entity.WidgetEntity;

/**
 * widget Persistence
 * @author SN
 *
 */
public interface WidgetPersistence extends JpaRepository<WidgetEntity, Long> {

	@Query("FROM  WidgetEntity c WHERE c.code=?1 ")
	WidgetEntity findByCode(String code);
	
	@Query("FROM  WidgetEntity c WHERE c.code=?1 and id !=?2")
	WidgetEntity findByCode(String code, Long id);
	
	@Query("FROM  WidgetEntity c ")
	List<WidgetEntity> getWidgetList();

}
