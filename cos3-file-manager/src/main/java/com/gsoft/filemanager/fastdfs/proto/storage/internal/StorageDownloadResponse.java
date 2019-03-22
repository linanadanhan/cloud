package com.gsoft.filemanager.fastdfs.proto.storage.internal;

import com.gsoft.filemanager.fastdfs.proto.FdfsResponse;
import com.gsoft.filemanager.fastdfs.proto.storage.DownloadCallback;
import com.gsoft.filemanager.fastdfs.proto.storage.FdfsInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 文件下载结果
 * 
 *
 * @param <T>
 *
 */
public class StorageDownloadResponse<T> extends FdfsResponse<T> {

    private DownloadCallback<T> callback;

    public StorageDownloadResponse(DownloadCallback<T> callback) {
        super();
        this.callback = callback;
    }

    /**
     * 解析反馈内容
     */
    @Override
    public T decodeContent(InputStream in, Charset charset) throws IOException {
        // 解析报文内容
        FdfsInputStream input = new FdfsInputStream(in, getContentLength());
        return callback.recv(input);
    }

}
