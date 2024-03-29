package com.gsoft.filemanager.fastdfs.service;

import com.gsoft.filemanager.fastdfs.conn.TrackerConnectionManager;
import com.gsoft.filemanager.fastdfs.domain.GroupState;
import com.gsoft.filemanager.fastdfs.domain.StorageNode;
import com.gsoft.filemanager.fastdfs.domain.StorageNodeInfo;
import com.gsoft.filemanager.fastdfs.domain.StorageState;
import com.gsoft.filemanager.fastdfs.proto.tracker.TrackerDeleteStorageCommand;
import com.gsoft.filemanager.fastdfs.proto.tracker.TrackerGetFetchStorageCommand;
import com.gsoft.filemanager.fastdfs.proto.tracker.TrackerGetStoreStorageCommand;
import com.gsoft.filemanager.fastdfs.proto.tracker.TrackerListGroupsCommand;
import com.gsoft.filemanager.fastdfs.proto.tracker.TrackerListStoragesCommand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 目录服务客户端默认实现
 * 
 *
 *
 */
@Service
public class DefaultTrackerClient implements TrackerClient {

    @Resource
    private TrackerConnectionManager trackerConnectionManager;

    /**
     * 获取存储节点
     */
    @Override
    public StorageNode getStoreStorage() {
        TrackerGetStoreStorageCommand command = new TrackerGetStoreStorageCommand();
        return trackerConnectionManager.executeFdfsTrackerCmd(command);
    }

    /**
     * 按组获取存储节点
     */
    @Override
    public StorageNode getStoreStorage(String groupName) {
        TrackerGetStoreStorageCommand command;
        if (StringUtils.isBlank(groupName)) {
            command = new TrackerGetStoreStorageCommand();
        } else {
            command = new TrackerGetStoreStorageCommand(groupName);
        }

        return trackerConnectionManager.executeFdfsTrackerCmd(command);
    }

    /**
     * 获取源服务器
     */
    @Override
    public StorageNodeInfo getFetchStorage(String groupName, String filename) {
        TrackerGetFetchStorageCommand command = new TrackerGetFetchStorageCommand(groupName, filename, false);
        return trackerConnectionManager.executeFdfsTrackerCmd(command);
    }

    /**
     * 获取更新服务器
     */
    @Override
    public StorageNodeInfo getUpdateStorage(String groupName, String filename) {
        TrackerGetFetchStorageCommand command = new TrackerGetFetchStorageCommand(groupName, filename, true);
        return trackerConnectionManager.executeFdfsTrackerCmd(command);
    }

    /**
     * 列出组
     */
    @Override
    public List<GroupState> listGroups() {
        TrackerListGroupsCommand command = new TrackerListGroupsCommand();
        return trackerConnectionManager.executeFdfsTrackerCmd(command);
    }

    /**
     * 按组列出存储状态
     */
    @Override
    public List<StorageState> listStorages(String groupName) {
        TrackerListStoragesCommand command = new TrackerListStoragesCommand(groupName);
        return trackerConnectionManager.executeFdfsTrackerCmd(command);
    }

    /**
     * 按ip列出存储状态
     */
    @Override
    public List<StorageState> listStorages(String groupName, String storageIpAddr) {
        TrackerListStoragesCommand command = new TrackerListStoragesCommand(groupName, storageIpAddr);
        return trackerConnectionManager.executeFdfsTrackerCmd(command);
    }

    /**
     * 删除存储节点
     */
    @Override
    public void deleteStorage(String groupName, String storageIpAddr) {
        TrackerDeleteStorageCommand command = new TrackerDeleteStorageCommand(groupName, storageIpAddr);
        trackerConnectionManager.executeFdfsTrackerCmd(command);
    }

}
