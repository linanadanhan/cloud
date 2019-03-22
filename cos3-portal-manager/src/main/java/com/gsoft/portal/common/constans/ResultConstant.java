package com.gsoft.portal.common.constans;

/**
 * 接口操作结果常量类
 * @author SN
 *
 */
public class ResultConstant 
{
    public static final int RESULT_RETURN_OK_STATUS = 200;
    public static final String RESULT_RETURN_OK_MSG = "成功";
    
    public static final int RESULT_RETURN_FAIL_STATUS = 500;
    public static final String RESULT_RETURN_FAIL_MSG = "系统错误";
    
    public static final int RESULT_RETURN_NO_AUTH_STATUS = 403;
    public static final String RESULT_RETURN_NO_AUTH_MSG = "无访问权限";
    
    public static final int RESULT_RETURN_NO_EXIST_STATUS = 404;
    public static final String RESULT_RETURN_NO_EXIST_MSG = "无此页面";
    
    public static final int RESULT_RETURN_NO_LOGIN_STATUS = 401;
    public static final String RESULT_RETURN_NO_LOGIN_MSG = "用户未登录";
}
