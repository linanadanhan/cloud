package com.gsoft.portal.system.basicdata.persistence;

import com.gsoft.portal.system.basicdata.entity.DictionaryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DictionaryItemPersistence extends JpaRepository<DictionaryItem, Long> {

	@Modifying
    @Query("DELETE FROM DictionaryItem di WHERE di.dicKey = ?1")
    @Transactional
    void deleteForDicId(String dicKey);

    List<DictionaryItem> findByDicId(Long id);

    @Query("FROM DictionaryItem di WHERE di.dicKey = ?1 and di.status = 1 ORDER BY di.sortNo")
    List<DictionaryItem> findDicItemsByKey(String key);

    @Query("from DictionaryItem d where d.value like %?1%")
    List<DictionaryItem> getDicItemByValue(String value);

    @Query("FROM DictionaryItem d WHERE d.dicKey=?1 and d.value = ?2 and d.deleted=0")
    DictionaryItem getParmByValue(String dicKey, String value);

    @Query("FROM DictionaryItem d WHERE d.id!=?1 and d.dicKey=?2 and d.value = ?3 and d.deleted=0")
    DictionaryItem getParmByValue(Long id, String dicKey, String value);
}
