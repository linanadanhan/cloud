/**
 * 
 */
package com.gsoft.cos3.table;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.gsoft.cos3.exception.BusinessException;
import com.gsoft.cos3.util.BooleanUtils;
import com.gsoft.cos3.util.CollectionUtils;
import com.gsoft.cos3.util.JsonMapper;
import com.gsoft.cos3.util.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * 数据表定义类
 * 
 * @author shencq
 *
 */
public class TableDefinition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -542216370296724392L;
	private List<String> dateColumns;
	private List<String> fileColumns;
	private Map<String, SubTableDefinition> subtables;
	private List<String> columns;
	private String extTable;

	public TableDefinition() {

	}

	public TableDefinition(String fileColumns, String subtables) {
		this.fileColumns = CollectionUtils.list(StringUtils.splitAndStrip(fileColumns, ","));
		if (BooleanUtils.isNotEmpty(subtables)) {
			try {
				this.subtables = JsonMapper.fromJsonMap(subtables, String.class, SubTableDefinition.class);
			} catch (JsonParseException e) {
				throw new BusinessException("从表配置信息解析失败", e);
			} catch (JsonMappingException e) {
				throw new BusinessException("从表配置信息转换为SubTableDefinition对象失败", e);
			} catch (IOException e) {
				throw new BusinessException("读取从表配置信息失败", e);
			}
		}
	}
	
	public TableDefinition(String fileColumns, String subtables,String extTable) {
		this.extTable = extTable;
		this.fileColumns = CollectionUtils.list(StringUtils.splitAndStrip(fileColumns, ","));
		if (BooleanUtils.isNotEmpty(subtables)) {
			try {
				this.subtables = JsonMapper.fromJsonMap(subtables, String.class, SubTableDefinition.class);
			} catch (JsonParseException e) {
				throw new BusinessException("从表配置信息解析失败", e);
			} catch (JsonMappingException e) {
				throw new BusinessException("从表配置信息转换为SubTableDefinition对象失败", e);
			} catch (IOException e) {
				throw new BusinessException("读取从表配置信息失败", e);
			}
		}
	}

	/**
	 * 判断字段是否为Date类型
	 * 
	 * @param columnName
	 * @return
	 */
	public boolean dateColumn(Object columnName) {
		return dateColumns == null ? false : dateColumns.contains(columnName);
	}

	/**
	 * 判断字段是否为文件类型
	 * 
	 * @param columnName
	 * @return
	 */
	public boolean fileColumn(Object columnName) {
		return fileColumns == null ? false : fileColumns.contains(columnName);
	}

	/**
	 * 获取存放文件ReferenceId的字段
	 * 
	 * @return
	 */
	public List<String> getFileColumns() {
		return fileColumns == null ? new ArrayList<String>() : fileColumns;
	}

	/**
	 * 获取存放文件ReferenceId的字段
	 * 
	 * @return
	 */
	public List<String> getFileColumns(Map<String, Object> params) {
		List<String> fileCols = new ArrayList<String>();
		for (String name : getFileColumns()) {
			if (params.containsKey(name)) {
				fileCols.add(name);
			}
		}
		return fileCols;
	}

	/**
	 * 判断是否为从表
	 * 
	 * @param columnName
	 * @return
	 */
	public boolean subtable(Object columnName) {
		return subtables == null ? false : subtables.containsKey(columnName);
	}

	/**
	 * 获取对应属性的从表配置信息
	 * @param columnName
	 * @return
	 */
	public Map<String, SubTableDefinition> getSubTableDefinitions() {
		return subtables;
	}

	/**
	 * 判断是否包含列名
	 * 
	 * @param columnName
	 * @return
	 */
	public boolean column(Object columnName) {
		return columns == null ? false : columns.contains(columnName);
	}

	/**
	 * 判断是否已经初始化所有字段信息
	 * 
	 * @return
	 */
	public boolean columnsInitialized() {
		return columns != null;
	}

	/**
	 * @param columns
	 *            the columns to set
	 */
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	/**
	 * @return
	 */
	public List<String> getColumns() {
		return columns;
	}

	/**
	 * @return the dateColumns
	 */
	public List<String> getDateColumns() {
		return dateColumns;
	}

	/**
	 * @param dateColumns
	 *            the dateColumns to set
	 */
	public void setDateColumns(List<String> dateColumns) {
		this.dateColumns = dateColumns;
	}

	public String getExtTable() {
		return extTable;
	}

	public void setExtTable(String extTable) {
		this.extTable = extTable;
	}
	

}
