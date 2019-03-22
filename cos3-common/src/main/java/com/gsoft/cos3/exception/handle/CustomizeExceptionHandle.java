package com.gsoft.cos3.exception.handle;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.exception.BusinessException;
import com.gsoft.cos3.exception.BusinessMsgException;
import com.gsoft.cos3.exception.NoPermissionException;
import com.gsoft.cos3.exception.NotExistException;
import com.gsoft.cos3.exception.NotLoggedInException;
import com.gsoft.cos3.exception.TimeOutException;

/**
 * 自定义错误处理
 *
 * @author plsy
 */
public class CustomizeExceptionHandle implements ExceptionHandle {

    ExceptionHandle exceptionHandle;

    public CustomizeExceptionHandle(ExceptionHandle exceptionHandle) {
        this.exceptionHandle = exceptionHandle;
    }

    @Override
    public ReturnDto handleException(Exception e) {
        String message = null;
        int code = 500;
        //BusinessException必须放在最后
        if (e instanceof NotLoggedInException) {
            code = 401;
            message = "用户未登录！";
        } else if (e instanceof NoPermissionException) {
            code = 403;
            message = "用户无权限！";
        } else if (e instanceof NotExistException) {
            code = 404;
            message = "页面不存在！";
        } else if (e instanceof TimeOutException) {
            code = 408;
            message = "请求超时！";
        } else if (e instanceof BusinessException) {
        	System.out.println("handle error:" + e.getMessage());
//            message = "业务错误！";
        	message = e.getMessage();
        } else if (e instanceof BusinessMsgException) { 
        	message = e.getMessage();
        	return new ReturnDto(code, message);
        	
        } else {
            return exceptionHandle.handleException(e);
        }
        return new ReturnDto(code, message, getStackMsg(e));
    }
    
    /**
     * 将异常(getStackTrace)转化成String
     * @param e
     * @return
     */
    private static String getStackMsg(Exception e) {  
        StringBuffer sb = new StringBuffer();  
        StackTraceElement[] stackArray = e.getStackTrace();  
        for (int i = 0; i < stackArray.length; i++) {  
            StackTraceElement element = stackArray[i];  
            sb.append(element.toString() + "\n");  
        }  
        return sb.toString();  
    }
}
