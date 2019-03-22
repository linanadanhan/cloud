/**
 * 
 */
package com.gsoft.cos3.jdbc.visitor;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;
import com.gsoft.cos3.jdbc.util.Constant;
import com.gsoft.cos3.util.BooleanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shencq
 *
 */
public class OracleASTParamVisitor extends OracleASTVisitorAdapter {

	private Map<String, Object> params;

	public OracleASTParamVisitor(Map<String, Object> params) {
		super();
		this.params = params != null ? params : new HashMap<String, Object>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter#endVisit(com.alibaba.
	 * druid.sql.ast.expr.SQLBinaryOpExpr)
	 */
	@Override
	public void endVisit(SQLBinaryOpExpr x) {
		if (x.getOperator().isRelational()) {
			if (Boolean.TRUE.equals(x.getRight().getAttribute(Constant.DISABLED))
					|| Boolean.TRUE.equals(x.getLeft().getAttribute(Constant.DISABLED))) {
				x.putAttribute(Constant.DISABLED, true);
			}
		} else {
			if (Boolean.TRUE.equals(x.getRight().getAttribute(Constant.DISABLED))
					&& Boolean.TRUE.equals(x.getLeft().getAttribute(Constant.DISABLED))) {
				x.putAttribute(Constant.DISABLED, true);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter#endVisit(com.alibaba.
	 * druid.sql.ast.expr.SQLBetweenExpr)
	 */
	@Override
	public void endVisit(SQLBetweenExpr x) {
		if (Boolean.TRUE.equals(x.getBeginExpr().getAttribute(Constant.DISABLED))) {
			if (Boolean.TRUE.equals(x.getEndExpr().getAttribute(Constant.DISABLED))) {
				x.putAttribute(Constant.DISABLED, true);
			} else {
				x.putAttribute(Constant.TYPE, "end");
			}
		} else if (Boolean.TRUE.equals(x.getEndExpr().getAttribute(Constant.DISABLED))) {
			x.putAttribute(Constant.TYPE, "begin");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter#endVisit(com.alibaba.
	 * druid.sql.ast.expr.SQLVariantRefExpr)
	 */
	@Override
	public void endVisit(SQLVariantRefExpr x) {
		if (x.getName().startsWith("${") && x.getName().endsWith("}")) {
			String key = x.getName().substring(2, x.getName().length() - 1).trim();
			int index = key.indexOf('#');
			if (index > 0) {
				x.putAttribute(Constant.VARIABLE_TYPE, key.substring(index + 1).trim());
				key = key.substring(0, index);
			}
			x.putAttribute(Constant.DISABLED, BooleanUtils.isEmpty(params.get(key)));
			x.putAttribute(Constant.VARIABLE, true);
			x.putAttribute(Constant.VARIABLE_KEY, key);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter#endVisit(com.alibaba.
	 * druid.sql.ast.expr.SQLMethodInvokeExpr)
	 */
	@Override
	public void endVisit(SQLMethodInvokeExpr x) {
		List<SQLExpr> items = x.getParameters();
		if (items != null) {
			for (SQLExpr item : items) {
				if (Boolean.TRUE.equals(item.getAttribute(Constant.DISABLED))) {
					x.putAttribute(Constant.DISABLED, true);
					return;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter#endVisit(com.alibaba.
	 * druid.sql.ast.expr.SQLInListExpr)
	 */
	@Override
	public void endVisit(SQLInListExpr x) {
		boolean allDisabled = true;
		for (SQLExpr item : x.getTargetList()) {
			if (!Boolean.TRUE.equals(item.getAttribute(Constant.DISABLED))) {
				allDisabled = false;
			}
			item.putAttribute(Constant.IN_TARGET_LIST, true);
		}
		x.putAttribute(Constant.DISABLED, allDisabled);
	}

}
