package com.gsoft.filemanager.local.persistence;

import com.gsoft.filemanager.local.entity.Chunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 分块持久层
 *
 * @author pilsy
 */
public interface FileChunkPersistence extends JpaRepository<Chunk, Long>, JpaSpecificationExecutor<Chunk> {

    @Query("from Chunk f where f.code = ?1")
    Chunk findOneByFileCode(String md5);

    @Query("from Chunk f where f.identifier = ?1")
    List<Chunk> findOneByIdentifier(String identifier);

    /**
     * 根据CODE删除
     *
     * @param codes
     */
    @Transactional
    @Modifying
    @Query("delete Chunk f where f.code in ?1")
    void deleteByCodes(List<String> codes);

    @Query("from Chunk f where f.code = ?1")
    Chunk findChunkByCode(String md5);

    @Query("from Chunk f where f.identifier = ?1 and f.chunkNumber = ?2")
    Chunk checkChunk(String identifier, Integer chunkNumber);

    @Query(value = "SELECT COUNT(*) FROM cos_file_chunk f WHERE f.c_identifier = ?1", nativeQuery = true)
    int getChunkNumber(String identifier);
}
