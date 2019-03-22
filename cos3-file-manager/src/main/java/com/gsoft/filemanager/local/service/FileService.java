package com.gsoft.filemanager.local.service;

import com.gsoft.filemanager.local.dto.ChunkDto;
import com.gsoft.filemanager.local.dto.FileNode;
import com.gsoft.filemanager.local.dto.FileReferenceDto;
import com.gsoft.filemanager.local.entity.Chunk;
import com.gsoft.filemanager.local.entity.FileReference;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * 文件上传下载服务接口
 */
public interface FileService {

    /**
     * 上传文件
     * @param file webuploader上传的文件片断或整个文件
     * @return 文件对象
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    FileNode uploadWeb(MultipartFile file,String currentUserId) throws IOException, NoSuchAlgorithmException;

    /**
     * 上传文件
     *
     * @param is       文件流
     * @param fileName 文件名称
     * @param size     文件大小
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    FileNode uploadInputStream(InputStream is, String fileName, long size, String currentUserId) throws IOException, NoSuchAlgorithmException;


    /**
     * 下载文件
     */
    InputStream getFileDataByReferenceId(String referenceId) throws FileNotFoundException;

    /**
     * 获取文件引用信息
     */
    FileReferenceDto referenceInfo(String referenceId);

    /**
     * 修改fileReference对象信息
     */
    void updateFileReference(FileReference fileReference);

    /**
     * 定时清理垃圾文件
     */
    void cleanUpJunkFiles();

	/**
	 * 根据referenceId 获取文件信息
	 * @param referenceId
	 * @return
	 */
	List<FileNode> getFileNodesById(String referenceIds);


    /**
     * 分块上传
     * @param chunkDto
     * @param personnelId
     * @return
     */
    FileNode chunkUploadWeb(ChunkDto chunkDto, String personnelId) throws IOException, NoSuchAlgorithmException;


    /**
     * 将分块文件合并后返回
     * @param identifier
     * @return
     */
    SequenceInputStream getMergeFileDataByIdentifier(String identifier) throws IOException;

    List<ChunkDto> identifierInfo(String identifier);

    List<FileNode> getFileNodesByIdentifier(String identifier);

    InputStream getFileChunkByNumber(String identifier, Integer chunkNumber) throws FileNotFoundException;

    ChunkDto getChunkByNumber(String identifier, Integer chunkNumber);

    int getChunkNumber(String identifier);
}
