package com.gsoft.cos3.exception.handle;

import com.gsoft.cos3.dto.ReturnDto;

/**
 * 链式处理错误的接口
 *
 * @author plsy
 */
public interface ExceptionHandle {

    /**
     * 处理Exception
     *
     * @param e
     * @return
     */
    ReturnDto handleException(Exception e);

}
