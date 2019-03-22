package com.gsoft.portal.system.personnel.persistence;

import com.gsoft.portal.system.personnel.entity.BusinessUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BusinessUserPersistence extends JpaRepository<BusinessUserEntity, Long> {

    @Query("FROM BusinessUserEntity u where u.deleted=0 and u.status=1")
    List<BusinessUserEntity> findAllBusinessUsers();
}
