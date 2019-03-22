package com.gsoft.portal.system.personnel.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import com.gsoft.portal.system.personnel.entity.UserOrganizationEntity;

/**
 * 用户组织机构Persistence
 * @author SN
 *
 */
public interface UserOrganizationPersistence extends JpaRepository<UserOrganizationEntity, Long> {

	//删除人员时，删除关联
	@Query("delete from UserOrganizationEntity where personId=?1")
	@Modifying
	@Transactional
	void delByPersonnelId(Long id);

	@Query("from UserOrganizationEntity where personId=?1 and orgId=?2")
	UserOrganizationEntity findOneByPIdAndOrgId(Long personId, Long orgId);

	@Query("from UserOrganizationEntity where orgId=?1")
	List<UserOrganizationEntity> findByOrgId(Long orgId);
	
	//批量删除人员时，删除关联
	@Query("delete from UserOrganizationEntity where personId in (?1)")
	@Modifying
	@Transactional
	void delByPersonIds(List<Long> idList);

}
