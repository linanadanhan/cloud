package com.gsoft.cos3.exception.handle;

import com.gsoft.cos3.dto.ReturnDto;
import org.springframework.dao.DataAccessException;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 默认系统错误处理
 *
 * @author plsy
 */
public class DefaultExceptionHandle implements ExceptionHandle {

    @Override
    public ReturnDto handleException(Exception e) {
        String message = null;
        if (e instanceof DataAccessException) {
            message = "数据库操作失败！";
        } else if (e instanceof NullPointerException) {
            message = "调用了未经初始化的对象或者是不存在的对象！";
        } else if (e instanceof IOException) {
            message = "IO异常！";
        } else if (e instanceof ClassNotFoundException) {
            message = "指定的类不存在！";
        } else if (e instanceof ArithmeticException) {
            message = "数学运算异常！";
        } else if (e instanceof ArrayIndexOutOfBoundsException) {
            message = "数组下标越界!";
        } else if (e instanceof IllegalArgumentException) {
            message = "方法的参数错误！";
        } else if (e instanceof NoSuchMethodException) {
            message = "方法未找到异常！";
        } else if (e instanceof ClassCastException) {
            message = "类型强制转换错误！";
        } else if (e instanceof SecurityException) {
            message = "违背安全原则异常！";
        } else if (e instanceof SQLException) {
            message = "操作数据库异常！";
        } else if (e instanceof Exception) {
            message = "程序内部错误，操作失败！";
        }
        return new ReturnDto(500, message, getStackMsg(e));
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
