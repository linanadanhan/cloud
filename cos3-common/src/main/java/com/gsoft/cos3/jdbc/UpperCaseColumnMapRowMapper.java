/**
 * 
 */
package com.gsoft.cos3.jdbc;

import org.springframework.jdbc.core.ColumnMapRowMapper;

/**
 * @author shencq
 *
 */
public class UpperCaseColumnMapRowMapper extends ColumnMapRowMapper {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.jdbc.core.ColumnMapRowMapper#getColumnKey(java.lang.
	 * String)
	 */
	@Override
	protected String getColumnKey(String columnName) {
		return columnName.toUpperCase();
	}

}
