package com.gsoft.portal.system.basicdata.persistence;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.gsoft.portal.system.basicdata.entity.Parameter;

/**
 * 参数管理持久层
 * @author plsy
 * @Date 2017年8月11日 下午5:49:05
 *
 */
public interface ParameterPersistence extends JpaRepository<Parameter, Long> {
	@Query("FROM Parameter  WHERE key = ?1 and deleted=0")
	Parameter getParmByKey(String key);

    @Query("FROM Parameter  WHERE id!=?1 and key = ?2 and deleted=0")
    Parameter getParmByKey(Long id, String parmKey);

    //查询所有分类
    @Query("select type from Parameter group by type")
    List<String> getTypes();

    @Query("FROM Parameter r where r.deleted=0")
    Page<Parameter> getPage(Pageable pageable);

    @Query("FROM Parameter r where r.type=?1 and r.deleted=0")
    Page<Parameter> getPageByType(String type, Pageable pageable);

    //修改可用状态
    @Transactional
    @Modifying
    @Query("update Parameter set status=?2 where id=?1")
    void updateStatus(Long id, Boolean status);

}
