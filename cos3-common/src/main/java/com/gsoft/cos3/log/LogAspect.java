package com.gsoft.cos3.log;

import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * ApiOperation切面设置log
 *
 * @author plsy
 */
@Component
@Aspect
public class LogAspect {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${spring.application.name}")
	String application;

	@Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
	private void cutController() {
	}

	@After("cutController()")
	public void after(JoinPoint joinPoint) throws ClassNotFoundException {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		// 记录下请求内容
		String url = request.getRequestURI();
		String httpMethod = request.getMethod();
		String remoteAddr = request.getRemoteAddr();
		String personnelId = request.getHeader("personnelid");
		String methodName = joinPoint.getSignature().getName();
		String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + methodName;
		String args = Arrays.toString(joinPoint.getArgs());
		String targetName = joinPoint.getTarget().getClass().getName();
		Object[] arguments = joinPoint.getArgs();
		Class<?> targetClass = Class.forName(targetName);
		Method[] methods = targetClass.getDeclaredMethods();
		AtomicReference<String> value = new AtomicReference<>();
		Arrays.stream(methods).parallel().filter(method -> method.getName().equals(methodName)).findAny()
				.ifPresent(method -> {
					Class<?>[] classes = method.getParameterTypes();
					if (classes.length == arguments.length) {
						// 设置注解值
						ApiOperation annotation = method.getAnnotation(ApiOperation.class);
						if (Objects.nonNull(annotation)) {
							value.set(annotation.value());
						}
					}
				});

		String format = String.format(
				"personnelId=%s application=%s uri=%s HTTP_METHOD=%s IP=%s CLASS_METHOD=%s args=%s detailed=%s",
				personnelId, application, url, httpMethod, remoteAddr, classMethod, args, value.get());

		logger.info(format);
	}

}
