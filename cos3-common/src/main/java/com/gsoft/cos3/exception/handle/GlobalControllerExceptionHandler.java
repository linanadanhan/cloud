package com.gsoft.cos3.exception.handle;

import com.gsoft.cos3.exception.handle.CustomizeExceptionHandle;
import com.gsoft.cos3.exception.handle.DefaultExceptionHandle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局处理抛出controller异常的控制器
 *
 * @author plsy
 */
@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    protected Log logger = LogFactory.getLog(getClass());

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object defaultErrorHandler(HttpServletRequest request, Exception exception) {
        logger.error("访问请求: " + request.getRequestURL().toString() + " 出错,错误原因:" + exception.getMessage());
        exception.printStackTrace();
        //先匹配自定义错误处理,未找到自定义错误就调用默认错误兜底
        DefaultExceptionHandle defaultExceptionHandle = new DefaultExceptionHandle();
        CustomizeExceptionHandle customizeExceptionHandle = new CustomizeExceptionHandle(defaultExceptionHandle);
        return customizeExceptionHandle.handleException(exception);
    }

}
