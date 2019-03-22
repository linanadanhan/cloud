/**
 * 
 */
package com.gsoft.cos3.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shencq
 * 
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

	/**
	 * 分解，去空格
	 * 
	 * @param str
	 * @param c
	 * @return
	 */
	public static String[] splitAndStrip(String str, char c) {
		return stripAll(split(str, c));
	}

	/**
	 * 分解，去空格
	 * 
	 * @param str
	 * @param c
	 * @return
	 */
	public static String[] splitAndStrip(String str, String c) {
		return stripAll(split(str == null ? "" : str, c));
	}

	/**
	 * 替换掉字符串中所有的制表符、空格等
	 * 
	 * @param str
	 * @return
	 */
	public static String repaceAllTabAndSpace(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 获取特定字符在字符串中第N次出现的下标
	 * @param str
	 * @param modelStr
	 * @param count
	 * @return
	 */
	public static int getFromIndex(String str, String modelStr, Integer count) {
		// 对子字符串进行匹配
		Matcher slashMatcher = Pattern.compile(modelStr).matcher(str);
		int index = 0;
		// matcher.find();尝试查找与该模式匹配的输入序列的下一个子序列
		while (slashMatcher.find()) {
			index++;
			// 当modelStr字符第count次出现的位置
			if (index == count) {
				break;
			}
		}
		// matcher.start();返回以前匹配的初始索引。
		return slashMatcher.start();
	}
}
