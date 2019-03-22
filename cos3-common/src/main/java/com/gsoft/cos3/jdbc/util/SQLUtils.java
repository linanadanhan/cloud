package com.gsoft.cos3.jdbc.util;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;
import com.gsoft.cos3.jdbc.visitor.MySqlASTParamOutputVisitor;
import com.gsoft.cos3.jdbc.visitor.MySqlASTParamVisitor;
import com.gsoft.cos3.jdbc.visitor.OracleASTParamOutputVisitor;
import com.gsoft.cos3.jdbc.visitor.OracleASTParamVisitor;

import java.util.List;
import java.util.Map;

public class SQLUtils {
	/**
	 * 根据参数提供情况，重组sql和参数列表
	 * <p>
	 * 重组规则举例，如：select * from test t where t.age > {min} and t.age < {max}，
	 * 参数集中提供min参数，但不提供max参数的话，将重组为：select * from test t where t.age > ?
	 * </p>
	 * 
	 * @param sql
	 *            需要重组的sql语句
	 * @param params
	 *            提供的参数集合
	 * @param out
	 *            重组后sql的输出目标
	 * @param values
	 *            重组后sql对应的变量值数组
	 * @param dbType
	 *            数据库类型
	 */
	public static void recombine(String sql, Map<String, Object> params, StringBuilder out, List<Object> values,
			String dbType) {
		SQLSelect select = parse(sql, dbType);
		if (JdbcConstants.MYSQL.equals(dbType)) {
			select.accept(new MySqlASTParamVisitor(params));
			select.accept(new MySqlASTParamOutputVisitor(out, values, params));
			return;
		}
		if (JdbcConstants.ORACLE.equals(dbType)) {
			select.accept(new OracleASTParamVisitor(params));
			select.accept(new OracleASTParamOutputVisitor(out, values, params));
			return;
		}
		throw new UnsupportedOperationException();
	}

    /**
     * 将SQL语句解析成SQLSelect对象
     * @param sql	需要解析的sql
     * @param dbType	数据库类型
     * @return
     */
    public static SQLSelect parse(String sql, String dbType) {
        List<SQLStatement> stmtList = com.alibaba.druid.sql.SQLUtils.parseStatements(sql, dbType);

        if (stmtList.size() != 1) {
            throw new IllegalArgumentException("sql not support count : " + sql);
        }

        SQLStatement stmt = stmtList.get(0);

        if (!(stmt instanceof SQLSelectStatement)) {
            throw new IllegalArgumentException("sql not support count : " + sql);
        }

        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
        return selectStmt.getSelect();
    }
}
