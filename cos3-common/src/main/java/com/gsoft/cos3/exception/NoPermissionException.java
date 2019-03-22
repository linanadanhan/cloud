package com.gsoft.cos3.exception;

/**
 * 无权限Exception
 * 状态码 403
 *
 * @author plsy
 */
public class NoPermissionException extends BusinessException {

	private static final long serialVersionUID = -7274571261801043204L;

	public NoPermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoPermissionException(String message) {
        super(message);
    }
}
