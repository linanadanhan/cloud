package com.gsoft.filemanager.fastdfs.service;

import com.gsoft.filemanager.fastdfs.domain.FileInfo;
import com.gsoft.filemanager.fastdfs.domain.MateData;
import com.gsoft.filemanager.fastdfs.domain.StorePath;
import com.gsoft.filemanager.fastdfs.proto.storage.DownloadCallback;

import java.io.InputStream;
import java.util.Set;

/**
 * 基本文件存储客户端操作
 * 
 *
 *
 */
public interface GenerateStorageClient {

    /**
     * 上传文件(文件不可修改)
     * 
     * <pre>
     * 文件上传后不可以修改，如果要修改则删除以后重新上传
     * </pre>
     * 
     * @param groupName
     * @param inputStream
     * @param fileSize
     * @param fileExtName
     * @return
     */
    StorePath uploadFile(String groupName, InputStream inputStream, long fileSize, String fileExtName);

    /**
     * 上传从文件
     * 
     * @param groupName
     * @param masterFilename
     * @param inputStream
     * @param fileSize
     * @param prefixName
     * @param fileExtName
     * @return
     */
    StorePath uploadSlaveFile(String groupName, String masterFilename, InputStream inputStream, long fileSize,
                              String prefixName, String fileExtName);

    /**
     * 获取文件元信息
     * 
     * @param groupName
     * @param path
     * @return
     */
    Set<MateData> getMetadata(String groupName, String path);

    /**
     * 修改文件元信息（覆盖）
     * 
     * @param groupName
     * @param path
     * @param metaDataSet
     */
    void overwriteMetadata(String groupName, String path, Set<MateData> metaDataSet);

    /**
     * 修改文件元信息（合并）
     * 
     * @param groupName
     * @param path
     * @param metaDataSet
     */
    void mergeMetadata(String groupName, String path, Set<MateData> metaDataSet);

    /**
     * 查看文件的信息
     * 
     * @param groupName
     * @param path
     * @return
     */
    FileInfo queryFileInfo(String groupName, String path);

    /**
     * 删除文件
     * 
     * @param groupName
     * @param path
     */
    void deleteFile(String groupName, String path);

    /**
     * 下载整个文件
     * 
     * @param groupName
     * @param path
     * @param callback
     * @return
     */
    <T> T downloadFile(String groupName, String path, DownloadCallback<T> callback);

    /**
     * 下载文件片段
     * 
     * @param groupName
     * @param path
     * @param fileOffset
     * @param fileSize
     * @param callback
     * @return
     */
    <T> T downloadFile(String groupName, String path, long fileOffset, long fileSize, DownloadCallback<T> callback);

}
