package com.gsoft.cos3.datasource;

import com.gsoft.cos3.util.Assert;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * DataSourceAspect
 *
 * @author plsy
 */
@Component
@Aspect
@Order(-1)
public class DataSourceAspect {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    private void cutController() {
    }

    @Before("cutController()")
    public void before(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 租户标识
        String sign = request.getHeader("Site-info");
        if (Assert.isNotEmpty(sign)) {
            DynamicDataSourceContextHolder.setDataSource(sign);
        } else {
        	DynamicDataSourceContextHolder.clearDataSource();
        }
    }
}
