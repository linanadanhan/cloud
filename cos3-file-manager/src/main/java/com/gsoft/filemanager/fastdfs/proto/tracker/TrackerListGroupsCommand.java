package com.gsoft.filemanager.fastdfs.proto.tracker;

import com.gsoft.filemanager.fastdfs.domain.GroupState;
import com.gsoft.filemanager.fastdfs.proto.AbstractFdfsCommand;
import com.gsoft.filemanager.fastdfs.proto.tracker.internal.TrackerListGroupsRequest;
import com.gsoft.filemanager.fastdfs.proto.tracker.internal.TrackerListGroupsResponse;

import java.util.List;

/**
 * 列出组命令
 * 
 *
 *
 */
public class TrackerListGroupsCommand extends AbstractFdfsCommand<List<GroupState>> {

    public TrackerListGroupsCommand() {
        super.request = new TrackerListGroupsRequest();
        super.response = new TrackerListGroupsResponse();
    }

}
