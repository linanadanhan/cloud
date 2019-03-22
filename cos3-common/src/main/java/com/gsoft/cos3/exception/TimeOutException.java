package com.gsoft.cos3.exception;

/**
 * 超时Exception
 * 状态码 408
 *
 * @author plsy
 */
public class TimeOutException extends BusinessException {

	private static final long serialVersionUID = -4453320368939727886L;

	public TimeOutException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeOutException(String message) {
        super(message);
    }
}
