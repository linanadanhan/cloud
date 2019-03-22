package com.gsoft.portal.system.basicdata.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.gsoft.portal.system.basicdata.entity.Catalog;

/**
 * 分类科目 Persistence
 * @author SN
 *
 */
public interface CatalogPersistence extends JpaRepository<Catalog, Long>{
	
	//查询所有分类科目信息
	@Query(" FROM Catalog c ")
	List<Catalog> getCatalogTree();

	@Query("FROM  Catalog c WHERE c.rootkey=?1 ")
	Catalog findByRootKey(String rootkey);

	@Query("FROM  Catalog c WHERE c.rootkey=?1 and id !=?2")
	Catalog findByRootKey(String rootkey, Long id);

	@Query("FROM Catalog c WHERE c.deleted=0 AND (c.id=?1 OR c.cascadeid like %?2% ) " )
	List<Catalog> getCatalogTree(Long id, Long id2);
	
}
