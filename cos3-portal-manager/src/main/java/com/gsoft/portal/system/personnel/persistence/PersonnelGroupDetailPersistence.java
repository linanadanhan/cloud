package com.gsoft.portal.system.personnel.persistence;

import com.gsoft.portal.system.personnel.entity.PersonnelGroupDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 人员群组明细Persistence
 * @author chenxx
 *
 */
public interface PersonnelGroupDetailPersistence extends JpaRepository<PersonnelGroupDetailEntity, Long> {


    @Transactional
    @Modifying
    @Query("DELETE FROM PersonnelGroupDetailEntity  WHERE groupId = ?1")
	void delGroupDetailByGroupId(Long groupId);

    @Transactional
    @Modifying
    @Query("DELETE FROM PersonnelGroupDetailEntity  WHERE groupId = ?1 and  orgId = ?2")
    void delGroupDetail(Long groupId,Long orgId);

    @Transactional
    @Modifying
    @Query("DELETE FROM PersonnelGroupDetailEntity  WHERE groupId = ?1 and  orgId in ?2")
    void delGroupDetail(Long groupId,List<Long> orgIds);
    
    @Query("FROM PersonnelGroupDetailEntity di WHERE di.groupId in ?1")
	List<PersonnelGroupDetailEntity> findGroupDetailByGroupId(List<Long> groupIdList);

    @Query("FROM PersonnelGroupDetailEntity di WHERE di.groupId = ?1")
    List<PersonnelGroupDetailEntity> getOneById(Long groupId);
}
