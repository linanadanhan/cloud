package com.gsoft.filemanager.local.persistence;

import com.gsoft.filemanager.local.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文件元数据持久层
 * @author wangfei
 * @Date 2015年8月18日 下午3:35:26
 *
 */
public interface FileMetadataPersistence extends JpaRepository<FileMetadata, Long> {
	
	@Query("from FileMetadata f where f.code = ?1")
	FileMetadata findOneByFileCode(String md5);
	  
	/**
	 * 更新数据库中FileMetadata对象信息
	 */
    @Transactional
    @Modifying
    @Query("update FileMetadata f set f.path=?2 ,f.usedNum=?3,f.size=?4 where f.code = ?1")
	void updateObj(String code, String path, int usedNum, long size);
    
    /**
     * 根据CODE删除
     * @param codes
     */
    @Transactional
    @Modifying
    @Query("delete FileMetadata f where f.code in ?1")
    void deleteByCodes(List<String> codes);

    @Query("from FileMetadata f where f.usedNum = ?1")
    List<FileMetadata> findAllByUsedNum(int i);

    @Query("from FileMetadata f where f.code = ?1")
    FileMetadata findMetadataByCode(String md5);
}
