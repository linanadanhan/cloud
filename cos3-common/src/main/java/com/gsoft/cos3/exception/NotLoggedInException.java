package com.gsoft.cos3.exception;

/**
 * 未登录Exception
 * 状态码 401
 *
 * @author plsy
 */
public class NotLoggedInException extends BusinessException {

	private static final long serialVersionUID = 6450361121695599552L;

	public NotLoggedInException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotLoggedInException(String message) {
        super(message);
    }
}
