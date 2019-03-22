package com.gsoft.portal.system.personnel.persistence;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gsoft.portal.system.personnel.entity.PersonnelGroupEntity;

/**
 * 人员群组Persistence
 * @author chenxx
 *
 */
public interface PersonnelGroupPersistence extends JpaRepository<PersonnelGroupEntity, Long> {
    
    @Query("from PersonnelGroupEntity where createBy=?1 and groupType=2")
	List<PersonnelGroupEntity> getPersonGroupOpts(Long personId);

    @Query("from PersonnelGroupEntity where groupType=1")
    List<PersonnelGroupEntity> getPersonGroupOpts();

    @Query("from PersonnelGroupEntity where groupType=1 and groupName like ?1")
    List<PersonnelGroupEntity> getPersonGroupOpts(String name);


}
