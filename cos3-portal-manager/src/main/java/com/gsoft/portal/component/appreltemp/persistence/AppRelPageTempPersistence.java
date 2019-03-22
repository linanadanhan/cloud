package com.gsoft.portal.component.appreltemp.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.gsoft.portal.component.appreltemp.entity.AppRelPageTempEntity;

/**
 * 门户授权Persistence
 * @author SN
 *
 */
public interface AppRelPageTempPersistence extends JpaRepository<AppRelPageTempEntity, Long> {
	
	@Query("from AppRelPageTempEntity where appCode=?1")
	List<AppRelPageTempEntity> getRelPageTempList(String appCode);

}
