/**
 * 
 */
package com.gsoft.cos3.tree;

/**
 * 
 * 常量翻译器，将属性翻译成给定的常量
 * @author shencq
 *
 */
public class ConstantTreeNodeAttributeTranslator implements
		TreeNodeAttributeTranslator {
	
	private Object value;
	
	/**
	 * @param value
	 */
	public ConstantTreeNodeAttributeTranslator(Object value) {
		super();
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see com.gsoft.cos.core.tree.TreeNodeAttributeTranslator#translate(java.lang.Object)
	 */
	@Override
	public Object translate(Object source) {
		return value;
	}

}
