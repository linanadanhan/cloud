package com.gsoft.cos3.exception;

/**
 * 
 * @author zhangfk
 *
 */
public class ReturnCode {
	
	/**
	 * OK_STATUS：200
	 */
    public static final int OK_STATUS = 200;
    /**
     * OK_MSG：成功
     */
    public static final String OK_MSG = "成功";
    /**
     * FAIL_STATUS:500
     */
    public static final int FAIL_STATUS = 500;
    /**
     * FAIL_MSG:系统错误
     */
    public static final String FAIL_MSG = "系统错误";
    /**
     * NOT_AUTH_STATUS:403
     */
    public static final int NOT_AUTH_STATUS = 403;
    /**
     * NOT_AUTH_MSG:无访问权限
     */
    public static final String NOT_AUTH_MSG = "无访问权限";
    /**
     * NOT_EXIST_STATUS:404
     */
    public static final int NOT_EXIST_STATUS = 404;
    /**
     * NOT_EXIST_MSG:无此页面
     */
    public static final String NOT_EXIST_MSG = "无此页面";


}
