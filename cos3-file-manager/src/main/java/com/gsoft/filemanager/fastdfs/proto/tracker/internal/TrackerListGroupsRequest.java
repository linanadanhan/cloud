package com.gsoft.filemanager.fastdfs.proto.tracker.internal;

import com.gsoft.filemanager.fastdfs.proto.CmdConstants;
import com.gsoft.filemanager.fastdfs.proto.FdfsRequest;
import com.gsoft.filemanager.fastdfs.proto.ProtoHead;

/**
 * 列出分组命令
 * 
 *
 *
 */
public class TrackerListGroupsRequest extends FdfsRequest {

    public TrackerListGroupsRequest() {
        head = new ProtoHead(CmdConstants.TRACKER_PROTO_CMD_SERVER_LIST_GROUP);
    }
}
