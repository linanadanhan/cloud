package com.gsoft.filemanager.fastdfs.proto.storage;

import com.gsoft.filemanager.fastdfs.domain.MateData;
import com.gsoft.filemanager.fastdfs.proto.AbstractFdfsCommand;
import com.gsoft.filemanager.fastdfs.proto.FdfsResponse;
import com.gsoft.filemanager.fastdfs.proto.storage.enums.StorageMetdataSetType;
import com.gsoft.filemanager.fastdfs.proto.storage.internal.StorageSetMetadataRequest;

import java.util.Set;

/**
 * 设置文件标签
 * 
 *
 *
 */
public class StorageSetMetadataCommand extends AbstractFdfsCommand<Void> {

    /**
     * 设置文件标签(元数据)
     * 
     * @param groupName
     * @param path
     * @param metaDataSet
     * @param type
     */
    public StorageSetMetadataCommand(String groupName, String path, Set<MateData> metaDataSet,
            StorageMetdataSetType type) {
        this.request = new StorageSetMetadataRequest(groupName, path, metaDataSet, type);
        // 输出响应
        this.response = new FdfsResponse<Void>() {
            // default response
        };
    }

}
