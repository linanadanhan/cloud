package com.gsoft.filemanager.fastdfs.proto.tracker.internal;

import com.gsoft.filemanager.fastdfs.domain.StorageState;
import com.gsoft.filemanager.fastdfs.proto.FdfsResponse;
import com.gsoft.filemanager.fastdfs.proto.mapper.FdfsParamMapper;
import com.gsoft.filemanager.fastdfs.proto.mapper.ObjectMateData;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 列出分组信息执行结果
 * 
 *
 *
 */
public class TrackerListStoragesResponse extends FdfsResponse<List<StorageState>> {

    /**
     * 解析反馈内容
     */
    @Override
    public List<StorageState> decodeContent(InputStream in, Charset charset) throws IOException {
        // 解析报文内容
        byte[] bytes = new byte[(int) getContentLength()];
        int contentSize = in.read(bytes);
        if (contentSize != getContentLength()) {
            throw new IOException("读取到的数据长度与协议长度不符");
        }
        return decode(bytes, charset);

    }

    /**
     * 解析Group
     * 
     * @param bs
     * @param charset
     * @return
     * @throws IOException
     */
    private List<StorageState> decode(byte[] bs, Charset charset) throws IOException {
        // 获取对象转换定义
        ObjectMateData objectMateData = FdfsParamMapper.getObjectMap(StorageState.class);
        int fixFieldsTotalSize = objectMateData.getFieldsFixTotalSize();
        if (bs.length % fixFieldsTotalSize != 0) {
            throw new IOException("fixFieldsTotalSize=" + fixFieldsTotalSize + "but byte array length: " + bs.length
                    + " is invalid!");
        }
        // 计算反馈对象数量
        int count = bs.length / fixFieldsTotalSize;
        int offset = 0;
        List<StorageState> results = new ArrayList<StorageState>(count);

        for (int i = 0; i < count; i++) {
            byte[] one = new byte[fixFieldsTotalSize];
            System.arraycopy(bs, offset, one, 0, fixFieldsTotalSize);
            results.add(FdfsParamMapper.map(one, StorageState.class, charset));
            offset += fixFieldsTotalSize;
        }

        return results;
    }
}
