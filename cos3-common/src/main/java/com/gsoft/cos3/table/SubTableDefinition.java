/**
 * 
 */
package com.gsoft.cos3.table;

import java.util.Map;

import com.gsoft.cos3.util.Assert;


/**
 * 主从表操作中对从表的配置信息对象
 * 
 * @author shencq
 *
 */
public class SubTableDefinition implements java.io.Serializable {

	private static final long serialVersionUID = 6662600185077956084L;

	/**
	 * 从表名称
	 */
	private String tableName;

	/**
	 * 从表关联业务外键字段名
	 */
	private String relatedForeignKey;

	/**
	 * 与主表主键关联的外键字段名
	 */
	private String primaryForeignKey;
	
	/**
	 * 其他关联属性
	 */
	private Map<String, String> additionalForeignKeys;
	
	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName
	 *            the tableName to set
	 */
	public void setTableName(String tableName) {
		Assert.hasText(tableName, "从表配置中必须定义从表的名称：tableName");
		this.tableName = tableName;
	}

	/**
	 * @return the relatedForeignKey
	 */
	public String getRelatedForeignKey() {
		return relatedForeignKey;
	}

	/**
	 * @param relatedForeignKey
	 *            the relatedForeignKey to set
	 */
	public void setRelatedForeignKey(String relatedForeignKey) {
		this.relatedForeignKey = relatedForeignKey;
	}

	/**
	 * @return the additionalForeignKeys
	 */
	public Map<String, String> getAdditionalForeignKeys() {
		return additionalForeignKeys;
	}

	/**
	 * @param additionalForeignKeys
	 *            the additionalForeignKeys to set
	 */
	public void setAdditionalForeignKeys(Map<String, String> foreignKeys) {
		this.additionalForeignKeys = foreignKeys;
	}

	/**
	 * @return the primaryForeignKey
	 */
	public String getPrimaryForeignKey() {
		return primaryForeignKey;
	}

	/**
	 * @param primaryForeignKey the primaryForeignKey to set
	 */
	public void setPrimaryForeignKey(String primaryForeignKey) {
		Assert.hasText(tableName, "从表配置中必须定义主表关联外键：primaryForeignKey");
		this.primaryForeignKey = primaryForeignKey;
	}

}
