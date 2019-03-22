package com.gsoft.portal.system.basicdata.persistence;


import com.gsoft.portal.system.basicdata.entity.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DictionaryPersistence extends JpaRepository<Dictionary, Long> {

    //查询所有分类
    @Query("select type from Dictionary group by type")
    List<String> getTypes();

    @Query("FROM Dictionary d where d.deleted=0")
    List<Dictionary> getList();

    @Query("FROM Dictionary d where d.type=?1 and d.deleted=0")
    List<Dictionary> getListByType(String type);

    @Query("from Dictionary d where d.name like %?1%")
    List<Dictionary> getListByName(String name);

    @Query("FROM Dictionary d WHERE d.key = ?1 and d.deleted=0")
    Dictionary getParmByKey(String key);

    @Query("FROM Dictionary d WHERE d.id != ?1 and d.key = ?2 and d.deleted=0")
    Dictionary getParmByKey(Long id, String key);
}
