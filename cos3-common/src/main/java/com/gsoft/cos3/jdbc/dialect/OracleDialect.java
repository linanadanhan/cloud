/**
 * 
 */
package com.gsoft.cos3.jdbc.dialect;

import com.alibaba.druid.util.JdbcConstants;

/**
 * @author shencq
 *
 */
public class OracleDialect implements Dialect {

	@Override
	public boolean isMysql() {
		return false;
	}

	@Override
	public boolean isOracle() {
		return true;
	}

	@Override
	public String getDbType() {
		return JdbcConstants.ORACLE;
	}

	
}
