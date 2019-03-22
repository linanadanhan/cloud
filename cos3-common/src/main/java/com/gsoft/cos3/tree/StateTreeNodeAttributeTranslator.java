/**
 * 
 */
package com.gsoft.cos3.tree;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * 节点状态翻译器，将对应属性的布尔值翻译成open或closed
 * @author shencq
 *
 */
public class StateTreeNodeAttributeTranslator implements
		TreeNodeAttributeTranslator {
	
	private String name;
	
	public StateTreeNodeAttributeTranslator(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see com.gsoft.cos.core.tree.TreeNodeAttributeTranslator#translate(java.lang.Object)
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
		return Boolean.valueOf(value) ? "open" : "closed";
	}

}
