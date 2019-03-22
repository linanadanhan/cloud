/**
 * 
 */
package com.gsoft.cos3.tree;

/**
 * 
 * 树节点属性翻译器
 * @author shencq
 * 
 */
public interface TreeNodeAttributeTranslator {
	/**
	 * 翻译节点属性值
	 * @param source 原始节点数据对象
	 * @return	根据需要翻译的属性，完成对原始节点对应属性的值的翻译，并返回翻译结果
	 */
	public Object translate(Object source);
}
