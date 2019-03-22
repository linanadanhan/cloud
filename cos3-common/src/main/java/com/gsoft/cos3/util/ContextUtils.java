/**
 * 
 */
package com.gsoft.cos3.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author zfk
 * 
 */
@Component
public class ContextUtils implements ApplicationContextAware{
	
	private static ApplicationContext context;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			if(ContextUtils.context == null) {
				ContextUtils.context = applicationContext;
	        }
		
	}
	
	 public static ApplicationContext getContext() {
	        return context;
	 }
}
