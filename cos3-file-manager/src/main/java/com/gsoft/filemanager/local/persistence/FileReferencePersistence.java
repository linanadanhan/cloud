package com.gsoft.filemanager.local.persistence;

import com.gsoft.filemanager.local.entity.FileReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文件引用持久层
 * @author wangfei
 * @Date 2015年8月18日 下午3:36:33
 *
 */
public interface FileReferencePersistence extends JpaRepository<FileReference, Long> {
	/**
	 * 修改文件应用信息
	 * @param referenceId
	 * @param businessKey
	 * @param businessType
	 */
	@Query("update FileReference f set f.businessKey = ?2,f.businessType = ?3 where f.referenceId = ?1")
	@Transactional
	@Modifying
	void updateFileAppInfo(String referenceId, String businessKey,
                           String businessType);

	@Query("from FileReference f where f.referenceId = ?1")
	FileReference findOneFileReference(String referenceId);

    @Query("from FileReference f where f.code = ?1")
    FileReference findOneByCode(String code);

    /**
     *
     * @Title updateFileReference
     * @Description TODO(根据referenceId更新FileReference信息)
     * @param referenceId
     * @param code 关联文件指纹Md5
     * @param name 文件名
     * @param type  文件类型
     * @param used 使用次数
     * @param appId   应用ID
     * @param businessKey 业务主键
     * @param businessType 业务类型
     * @param suffix 文件扩展名
     * @Return void   返回类型
     * @Throws
     * @Date  2016年4月28日
     * @修改历史
     *     1. [2016年4月28日]创建 by 秦奉春
     */
    @Transactional
    @Modifying
    @Query("update FileReference f set f.code=?2 ,f.name=?3 ,f.type=?4,f.used=?5,f.appId=?6 "
            + ",f.businessKey=?7,f.businessType=?8,f.suffix=?9 where f.referenceId = ?1")
    void updateObj(String referenceId, String code, String name, String type,
                   int used, String appId, String businessKey, String businessType, String suffix);
    
    
    /**
     * 根据CODE删除
     * @param codes
     */
    @Transactional
    @Modifying
    @Query("delete FileReference f where f.code in ?1")
    void deleteByCodes(List<String> codes);
    
    /**
     * 删除垃圾数据
     */
    @Transactional
    @Modifying
    @Query("delete FileReference f where f.used<=0")
    void deleteJunkData();

}
