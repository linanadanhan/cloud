/**
 * 
 */
package com.gsoft.cos3.jdbc.dialect;

import com.alibaba.druid.util.JdbcConstants;

/**
 * @author shencq
 *
 */
public class MySqlDialect implements Dialect {

	@Override
	public boolean isMysql() {
		return true;
	}

	@Override
	public boolean isOracle() {
		return false;
	}

	@Override
	public String getDbType() {
		return JdbcConstants.MYSQL;
	}

}
