package com.gsoft.filemanager.fastdfs.proto.mapper;

/**
 * 动态属性类型
 * 
 * <pre>
 * 可以为空的属性-不发送该报文
 * 剩余的所有byte-将该字段全部写入到最后的byte当中
 * </pre>
 * 
 *
 *
 */
public enum DynamicFieldType {
    /** 非动态属性 */
    NULL,
    /** 剩余的所有Byte */
    allRestByte,
    /** 可空的属性 */
    nullable,
    /** 文件元数据Set */
    matedata

}
