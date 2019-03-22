/**
 * 
 */
package com.gsoft.cos3.tree;

import org.apache.commons.beanutils.BeanUtils;

import com.gsoft.cos3.util.Assert;

import java.lang.reflect.InvocationTargetException;

/**
 * @author shencq
 * 
 */
public class FormatTreeNodeAttributeTranslator implements
		TreeNodeAttributeTranslator {

	private String format;
	private String[] names;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gsoft.cos.core.tree.TreeNodeAttributeTranslator#translate(java.lang
	 * .Object)
	 */
	/**
	 * @param format	文本格式化模版，如：我的名字叫%s，我是%s人.需要替换的参数用%s替代
	 * @param names	参数名，多个参数名用半角逗号隔开
	 */
	public FormatTreeNodeAttributeTranslator(String format, String names) {
		this.format = format;
		Assert.hasText(names, "属性名字符串不可以为空");
		this.names = names.split(",");
		for (int i = 0; i < this.names.length; i++) {
			this.names[i] = this.names[i].trim();
		}
	}

	@Override
	public Object translate(Object source) {
		Object[] values = new Object[names.length];
		for (int i = 0; i < names.length; i++) {
			try {
				values[i] = BeanUtils.getProperty(source, names[i]);
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			} catch (NoSuchMethodException e) {
			}
		}
		return String.format(format, values);
	}

}
