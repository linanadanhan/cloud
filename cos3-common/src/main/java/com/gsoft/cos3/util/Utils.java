/**
 * 
 */
package com.gsoft.cos3.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shencq
 * 
 */
public class Utils {

	/**
	 * 获取对象，如果为空则使用默认值
	 * 
	 * @param obj
	 * @param defualt
	 * @return
	 */
	public static <T> T getValue(T obj, T defualt) {
		if (Assert.isEmpty(obj)) {
			return defualt;
		}
		return obj;
	}

	/**
	 * 获取日志对象
	 * 
	 * @param clazz
	 * @return
	 */
	public static Logger getLogger(Class<?> clazz) {
		return LoggerFactory.getLogger(clazz);
	}

}
