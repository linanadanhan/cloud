/**
 * 
 */
package com.gsoft.cos3.util;

import java.util.Collection;

/**
 * @author shencq
 * 
 */
public class BooleanUtils extends org.apache.commons.lang3.BooleanUtils {
	/**
	 * 判断字符串是否为空.
	 * 
	 * @param str
	 * @return Boolean
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * 判断对象是否为空.
	 * 
	 * @param obj
	 * @return Boolean
	 */
	public static boolean isEmpty(Object obj) {
		if (obj instanceof String) {
			return obj == null || ("".equals(obj));
		} else if (obj instanceof Collection) {
			return obj == null || ((Collection<?>) obj).isEmpty();
		} else if (obj instanceof Object[]) {
			return obj == null || ((Object[]) obj).length == 0;
		} else {
			return obj == null;
		}
	}

	/**
	 * 判断字符串不为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * 判断对象不为空
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isNotEmpty(Object obj) {
		return !isEmpty(obj);
	}
}
