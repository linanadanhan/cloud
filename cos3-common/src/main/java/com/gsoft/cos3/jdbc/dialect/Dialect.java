package com.gsoft.cos3.jdbc.dialect;

public interface Dialect {
	
	/**
	 * 判断是否使用Mysql数据库
	 * @return
	 */
	public boolean isMysql();
	
	/**
	 * 判断是否使用Oracle数据库
	 * @return
	 */
	public boolean isOracle();
	
	/**
	 * 获取数据库类型
	 * @return
	 */
	public String getDbType();
}
