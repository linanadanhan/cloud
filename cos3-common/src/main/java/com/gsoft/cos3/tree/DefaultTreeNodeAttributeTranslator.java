/**
 * 
 */
package com.gsoft.cos3.tree;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.gsoft.cos3.util.Assert;

/**
 * 
 * 默认翻译器：不做任何转化，直接返回对应属性值
 * 
 * @author shencq
 * 
 */
public class DefaultTreeNodeAttributeTranslator implements
		TreeNodeAttributeTranslator {
	private String name;
	private String pre = "";

	/**
	 * 用于转换属性名字
	 * 
	 * @param name
	 */
	public DefaultTreeNodeAttributeTranslator(String name) {
		this.name = name;
	}

	/**
	 * 用于转换属性名字
	 * 
	 * @param name
	 */
	public DefaultTreeNodeAttributeTranslator(String name, String pre) {
		this.name = name;
		if (Assert.isNotEmpty(name)) {
			this.pre = pre;
		}
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
		try {
			return pre + BeanUtils.getProperty(source, name);
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		} catch (NoSuchMethodException e) {
		}
		return "";
	}
}