/**
 * 
 */
package com.gsoft.cos3.tree;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * 
 * 布尔值翻译器：返回true或false
 * @author shencq
 * 
 */
public class BooleanTreeNodeAttributeTranslator implements
		TreeNodeAttributeTranslator {
	private String name;

	public BooleanTreeNodeAttributeTranslator(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gsoft.cos.core.tree.TreeNodeAttributeTranslator#translate(java.lang
	 * .Object)
	 */
	@Override
	public Object translate(Object source) {
		String value = "false";
		try {
			value = BeanUtils.getProperty(source, name);
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		} catch (NoSuchMethodException e) {
		}
		return Boolean.valueOf(value);
	}

}
