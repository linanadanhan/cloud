package com.gsoft.portal.component.layout.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gsoft.portal.component.layout.entity.LayoutEntity;

/**
 * 布局Persistence
 * @author SN
 *
 */
public interface LayoutPersistence extends JpaRepository<LayoutEntity, Long> {

	@Query("FROM  LayoutEntity c WHERE c.code=?1 ")
	LayoutEntity findByCode(String code);
	
	@Query("FROM  LayoutEntity c WHERE c.code=?1 and c.id !=?2 ")
	LayoutEntity findByCode(String code, Long id);

	@Query("FROM  LayoutEntity c ")
	List<LayoutEntity> getLayoutList();
}
