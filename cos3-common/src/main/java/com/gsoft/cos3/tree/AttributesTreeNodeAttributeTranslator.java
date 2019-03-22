/**
 * 
 */
package com.gsoft.cos3.tree;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;


/**
 * 自定义属性翻译器，将返回指定属性作为树节点的自定义属性
 * @author shencq
 * 
 */
public class AttributesTreeNodeAttributeTranslator implements
		TreeNodeAttributeTranslator {

	private String[] names;

	/**
	 * @param names	需要作为自定义属性的属性名，多个属性名用半角逗号隔开
	 */
	public AttributesTreeNodeAttributeTranslator(String names) {
		if (Assert.isNotEmpty(names)) {
			this.names = names.split(",");
			for (int i = 0; i < this.names.length; i++) {
				this.names[i] = this.names[i].trim();
			}
		}
	}
	
	/**
	 * @param names	需要作为自定义属性的属性名
	 */
	public AttributesTreeNodeAttributeTranslator(String[] names){
		this.names = names;
	}
	
	/**
	 * 
	 */
	public AttributesTreeNodeAttributeTranslator(){
		
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
		if (names != null) {
			Map<String, String> map = new HashMap<String, String>();
			for (String name : names) {
				try {
					map.put(name, BeanUtils.getProperty(source, name));
				} catch (IllegalAccessException e) {
				} catch (InvocationTargetException e) {
				} catch (NoSuchMethodException e) {
				}
			}
			return map;
		}
		return null;
	}

}
