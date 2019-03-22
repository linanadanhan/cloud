package com.gsoft.filemanager.fastdfs.proto;

import com.gsoft.filemanager.fastdfs.conn.Connection;

/**
 * Fdfs交易命令抽象
 * 
 *
 *
 */
public interface FdfsCommand<T> {

    /** 执行交易 */
    public T execute(Connection conn);

}
