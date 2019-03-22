package com.gsoft.filemanager.fastdfs.proto.tracker;

import com.gsoft.filemanager.fastdfs.domain.StorageNodeInfo;
import com.gsoft.filemanager.fastdfs.proto.AbstractFdfsCommand;
import com.gsoft.filemanager.fastdfs.proto.FdfsResponse;
import com.gsoft.filemanager.fastdfs.proto.tracker.internal.TrackerGetFetchStorageRequest;

/**
 * 获取源服务器
 * 
 *
 *
 */
public class TrackerGetFetchStorageCommand extends AbstractFdfsCommand<StorageNodeInfo> {

    public TrackerGetFetchStorageCommand(String groupName, String path, boolean toUpdate) {
        super.request = new TrackerGetFetchStorageRequest(groupName, path, toUpdate);
        super.response = new FdfsResponse<StorageNodeInfo>() {
            // default response
        };
    }

}
