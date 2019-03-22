package com.gsoft.cos3.scan;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.annotation.Permission;
import com.gsoft.cos3.table.service.SingleTableService;
import com.gsoft.cos3.util.Assert;

import io.swagger.annotations.ApiOperation;

/**
 * 扫描有@RestConroller注解类下的所有method
 *
 * @author plsy
 */
@Component
public class ScanUtil {

    @Autowired
    private ApplicationContext context;

    @Autowired
    SingleTableService singleTableService;

    @Value("${spring.application.name}")
    String application;

    private static final HashMap<String, Object> map = new HashMap<String, Object>();

    @Async
    public void scanMethod() throws Exception {
        Map<String, Object> beans = context.getBeansWithAnnotation(RestController.class);
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            if (!Assert.isEmpty(entry.getValue())) {
                Object value = getCglibProxyTargetObject(entry.getValue());
                Class<?> targetClass = value.getClass();
                RequestMapping mapping = targetClass.getAnnotation(RequestMapping.class);
                String controllerPath = "";
                if (Objects.nonNull(mapping)) {
                    controllerPath = mapping.value()[0];
                }
                Method[] methods = targetClass.getMethods();
                for (Method method : methods) {
                    RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                    if (Objects.nonNull(annotation)) {
                        map.put("C_PATH", controllerPath + annotation.value()[0]);
                        map.put("C_SERVER", application);
                        Boolean unique = singleTableService.unique("cos_scan_controller", null, map);
                        Permission annotationPermission = method.getAnnotation(Permission.class);
                        if (unique) {
                            ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
                            map.put("C_DETAILS", apiOperation.value());
                            map.put("C_ID", null);
                            map.put("C_ALLOW", Assert.isEmpty(annotationPermission) ? false : annotationPermission.value());
                            singleTableService.save("cos_scan_controller", map);
                        }
                    }
                }
            }
        }
    }


    /**
     * 从cglib代理中得到object
     *
     * @param proxy
     * @return
     * @throws Exception
     */
    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        Object target = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
        return target;
    }


}
