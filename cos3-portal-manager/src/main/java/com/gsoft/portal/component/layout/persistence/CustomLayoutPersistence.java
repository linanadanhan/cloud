package com.gsoft.portal.component.layout.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.gsoft.portal.component.layout.entity.CustomLayoutEntity;

/**
 * 用户自定义布局Persistence
 * @author SN
 *
 */
public interface CustomLayoutPersistence extends JpaRepository<CustomLayoutEntity, Long> {

	@Query("FROM  CustomLayoutEntity c WHERE c.userId=?1 and c.pageUuId=?2 ")
	CustomLayoutEntity existsCustomLayout(Long userId, String pageUuId);
}
