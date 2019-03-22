package com.gsoft.filemanager.fastdfs.exception;

/**
 * 封装fastdfs的异常，使用运行时异常
 * 
 * @author yuqih
 *
 * 
 */
public class FdfsException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    public FdfsException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public FdfsException(String message, Throwable cause) {
        super(message, cause);
    }

}
