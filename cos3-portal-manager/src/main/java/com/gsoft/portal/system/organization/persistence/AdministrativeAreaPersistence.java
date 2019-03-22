package com.gsoft.portal.system.organization.persistence;


import com.gsoft.portal.system.organization.entity.AdministrativeAreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdministrativeAreaPersistence extends JpaRepository<AdministrativeAreaEntity, Long> {

    @Query("FROM  AdministrativeAreaEntity c WHERE c.level<=3 ")
    List<Object[]> findByCondition();

    @Query("FROM  AdministrativeAreaEntity c WHERE  c.code=?1")
    AdministrativeAreaEntity findByCode(String areaCode);

}
