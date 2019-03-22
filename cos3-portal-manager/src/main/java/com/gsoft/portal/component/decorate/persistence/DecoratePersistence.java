package com.gsoft.portal.component.decorate.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gsoft.portal.component.decorate.entity.DecorateEntity;

/**
 * 修饰器Persistence
 * @author SN
 *
 */
public interface DecoratePersistence extends JpaRepository<DecorateEntity, Long> {

	@Query("FROM  DecorateEntity c WHERE c.code=?1")
	DecorateEntity findByCode(String code);
	
	@Query("FROM  DecorateEntity c WHERE c.code=?1 and c.id !=?2")
	DecorateEntity findByCode(String code, Long id);

	@Query("FROM  DecorateEntity c ")
	List<DecorateEntity> getDecorateList();

}
