package com.gsoft.cos3.exception;

/**
 * 页面不存在Exception
 * 状态码 404
 *
 * @author plsy
 */
public class NotExistException extends BusinessException {
	
	private static final long serialVersionUID = -1828933200625549666L;

	public NotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistException(String message) {
        super(message);
    }
}
