package com.gsoft.cos3.jdbc;

/**
 * 
 * 字段定义类
 * @author shencq
 *
 */
public class ColumnDefinition {
	
	/**
	 * 字段名称
	 */
	private String name;
	
	/**
	 * 字段类型
	 */
	private int type;

	/**
	 * @param name	字段名称
	 * @param type	字段类型
	 */
	public ColumnDefinition(String name, int type) {
		super();
		this.name = name.toUpperCase();
		this.type = type;
	}

	/**
	 * 获取字段名称
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 获取字段类型
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	
	

}
