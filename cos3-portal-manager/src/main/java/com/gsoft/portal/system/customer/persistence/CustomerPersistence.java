package com.gsoft.portal.system.customer.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.gsoft.portal.system.customer.entity.Customer;

/**
 * 租户管理持久层
 * @author chenxx
 *
 */
public interface CustomerPersistence extends JpaRepository<Customer, Long>{
	
	@Query("FROM  Customer where code=?1")
	Customer isUniqueCustomerCode(String formFlag);

	@Query("FROM  Customer where code=?2 and id !=?1")
	Customer isUniqueCustomerCode(Long id, String formFlag);
	
	@Query("FROM  Customer where domain=?1")
	Customer isUniqueDomain(String domain);
	
	@Query("FROM  Customer where domain=?2 and id !=?1")
	Customer isUniqueDomain(Long id, String domain);
	
}
