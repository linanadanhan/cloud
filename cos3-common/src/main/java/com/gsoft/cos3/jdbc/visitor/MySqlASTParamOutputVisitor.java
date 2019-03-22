/**
 * 
 */
package com.gsoft.cos3.jdbc.visitor;

import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.gsoft.cos3.jdbc.util.Constant;
import com.gsoft.cos3.util.CollectionUtils;
import com.gsoft.cos3.util.DateUtils;
import com.gsoft.cos3.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author shencq
 *
 */
public class MySqlASTParamOutputVisitor extends MySqlOutputVisitor {

	private List<Object> values;
	private Map<String, Object> params;

	public MySqlASTParamOutputVisitor(Appendable appender, List<Object> values, Map<String, Object> params) {
		super(appender);
		this.values = values;
		this.params = params;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor#visit(com.
	 * alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock)
	 */
	public boolean visit(MySqlSelectQueryBlock x) {
		if (x.getOrderBy() != null) {
			x.getOrderBy().setParent(x);
		}

		print("SELECT ");

		for (int i = 0, size = x.getHintsSize(); i < size; ++i) {
			SQLCommentHint hint = x.getHints().get(i);
			hint.accept(this);
			print(' ');
		}

		if (SQLSetQuantifier.ALL == x.getDistionOption()) {
			print("ALL ");
		} else if (SQLSetQuantifier.DISTINCT == x.getDistionOption()) {
			print("DISTINCT ");
		} else if (SQLSetQuantifier.DISTINCTROW == x.getDistionOption()) {
			print("DISTINCTROW ");
		}

		if (x.isHignPriority()) {
			print("HIGH_PRIORITY ");
		}

		if (x.isStraightJoin()) {
			print("STRAIGHT_JOIN ");
		}

		if (x.isSmallResult()) {
			print("SQL_SMALL_RESULT ");
		}

		if (x.isBigResult()) {
			print("SQL_BIG_RESULT ");
		}

		if (x.isBufferResult()) {
			print("SQL_BUFFER_RESULT ");
		}

		if (x.getCache() != null) {
			if (x.getCache().booleanValue()) {
				print("SQL_CACHE ");
			} else {
				print("SQL_NO_CACHE ");
			}
		}

		if (x.isCalcFoundRows()) {
			print("SQL_CALC_FOUND_ROWS ");
		}

		printSelectList(x.getSelectList());

		if (x.getInto() != null) {
			println();
			print("INTO ");
			x.getInto().accept(this);
		}

		if (x.getFrom() != null) {
			println();
			print("FROM ");
			x.getFrom().accept(this);
		}

		if (x.getWhere() != null && !Boolean.TRUE.equals(x.getWhere().getAttribute(Constant.DISABLED))) {
			println();
			print("WHERE ");
			x.getWhere().setParent(x);
			x.getWhere().accept(this);
		}

		if (x.getGroupBy() != null) {
			println();
			x.getGroupBy().accept(this);
		}

		if (x.getOrderBy() != null) {
			println();
			x.getOrderBy().accept(this);
		}

		if (x.getLimit() != null) {
			println();
			x.getLimit().accept(this);
		}

		if (x.getProcedureName() != null) {
			print(" PROCEDURE ");
			x.getProcedureName().accept(this);
			if (x.getProcedureArgumentList().size() > 0) {
				print("(");
				printAndAccept(x.getProcedureArgumentList(), ", ");
				print(")");
			}
		}

		if (x.isForUpdate()) {
			println();
			print("FOR UPDATE");
		}

		if (x.isLockInShareMode()) {
			println();
			print("LOCK IN SHARE MODE");
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor#visit(com.
	 * alibaba.druid.sql.ast.expr.SQLVariantRefExpr)
	 */
	@Override
	public boolean visit(SQLVariantRefExpr x) {
		if (Boolean.TRUE.equals(x.getAttribute(Constant.VARIABLE))
				&& !Boolean.TRUE.equals(x.getAttribute(Constant.DISABLED))) {
			Object value = params.get(x.getAttribute(Constant.VARIABLE_KEY));
			String type = (String) x.getAttribute(Constant.VARIABLE_TYPE);
			if ("date".equalsIgnoreCase(type) && value instanceof String) {// 日期、时间类型的变量
				value = DateUtils.parseDate((String) value);
			} else if ("array".equalsIgnoreCase(type) && value instanceof String) {// 数组字符串
				value = CollectionUtils.list(StringUtils.splitAndStrip((String) value, ','));
			}
			if (Boolean.TRUE.equals(x.getAttribute(Constant.IN_TARGET_LIST))) {
				if (value instanceof Collection) {
					Collection<?> c = (Collection<?>) value;
					print(StringUtils.repeat("?", ", ", c.size()));
					values.addAll(c);
				} else {
					print('?');
					values.add(value);
				}
			} else {
				print('?');
				values.add(value);
			}
			return false;
		}
		return super.visit(x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.alibaba.druid.sql.visitor.SQLASTOutputVisitor#visit(com.alibaba.druid
	 * .sql.ast.expr.SQLBetweenExpr)
	 */
	@Override
	public boolean visit(SQLBetweenExpr x) {
		if (Boolean.TRUE.equals(x.getAttribute(Constant.DISABLED))) {
			return false;
		}
		if (Constant.END.equals(x.getAttribute(Constant.TYPE))) {
			if (x.isNot()) {
				return visit(new SQLBinaryOpExpr(x.getTestExpr(), SQLBinaryOperator.GreaterThan, x.getEndExpr(),
						JdbcConstants.MYSQL));
			} else {
				return visit(new SQLBinaryOpExpr(x.getTestExpr(), SQLBinaryOperator.LessThanOrEqual, x.getEndExpr(),
						JdbcConstants.MYSQL));
			}
		}
		if (Constant.BEGIN.equals(x.getAttribute(Constant.TYPE))) {
			if (x.isNot()) {
				return visit(new SQLBinaryOpExpr(x.getTestExpr(), SQLBinaryOperator.LessThan, x.getBeginExpr(),
						JdbcConstants.MYSQL));
			} else {
				return visit(new SQLBinaryOpExpr(x.getTestExpr(), SQLBinaryOperator.GreaterThanOrEqual,
						x.getBeginExpr(), JdbcConstants.MYSQL));
			}
		}
		return super.visit(x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor#visit(com.
	 * alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPartitioningDef.
	 * InValues)
	 */
	@Override
	public boolean visit(SQLInListExpr x) {
		if (Boolean.TRUE.equals(x.getAttribute(Constant.DISABLED))) {
			return false;
		}
		return super.visit(x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.alibaba.druid.sql.visitor.SQLASTOutputVisitor#visit(com.alibaba.druid
	 * .sql.ast.expr.SQLBinaryOpExpr)
	 */
	@Override
	public boolean visit(SQLBinaryOpExpr x) {
		SQLObject parent = x.getParent();
		boolean isRoot = parent instanceof SQLSelectQueryBlock;
		boolean relational = x.getOperator() == SQLBinaryOperator.BooleanAnd
				|| x.getOperator() == SQLBinaryOperator.BooleanOr;

		if (isRoot && relational) {
			incrementIndent();
		}

		List<SQLExpr> groupList = new ArrayList<SQLExpr>();
		SQLExpr left = x.getLeft();
		for (;;) {
			if (left instanceof SQLBinaryOpExpr && ((SQLBinaryOpExpr) left).getOperator() == x.getOperator()) {
				SQLBinaryOpExpr binaryLeft = (SQLBinaryOpExpr) left;
				groupList.add(binaryLeft.getRight());
				left = binaryLeft.getLeft();
			} else {
				groupList.add(left);
				break;
			}
		}

		int j = 0;

		for (int i = groupList.size() - 1; i >= 0; --i) {
			SQLExpr item = groupList.get(i);
			if (Boolean.TRUE.equals(item.getAttribute(Constant.DISABLED))) {
				continue;
			}
			if (j > 0) {
				if (relational) {
					println();
				} else {
					print(" ");
				}
				print(x.getOperator().name);
				print(" ");
			}
			visitBinaryLeft(item, x.getOperator());
			j++;
		}
		if (!Boolean.TRUE.equals(x.getRight().getAttribute(Constant.DISABLED))) {
			if (j > 0) {
				if (relational) {
					println();
				} else {
					print(" ");
				}
				print(x.getOperator().name);
				print(" ");
			}
			visitorBinaryRight(x);
		}

		if (isRoot && relational) {
			decrementIndent();
		}

		return false;
	}

	private void visitorBinaryRight(SQLBinaryOpExpr x) {
		if (x.getRight() instanceof SQLBinaryOpExpr) {
			SQLBinaryOpExpr right = (SQLBinaryOpExpr) x.getRight();
			boolean rightRational = right.getOperator() == SQLBinaryOperator.BooleanAnd
					|| right.getOperator() == SQLBinaryOperator.BooleanOr;

			if (right.getOperator().priority >= x.getOperator().priority) {
				if (rightRational) {
					incrementIndent();
				}

				print('(');
				right.accept(this);
				print(')');

				if (rightRational) {
					decrementIndent();
				}
			} else {
				right.accept(this);
			}
		} else {
			x.getRight().accept(this);
		}
	}

	private void visitBinaryLeft(SQLExpr left, SQLBinaryOperator op) {
		if (left instanceof SQLBinaryOpExpr) {
			SQLBinaryOpExpr binaryLeft = (SQLBinaryOpExpr) left;
			boolean leftRational = binaryLeft.getOperator() == SQLBinaryOperator.BooleanAnd
					|| binaryLeft.getOperator() == SQLBinaryOperator.BooleanOr;

			if (binaryLeft.getOperator().priority > op.priority) {
				if (leftRational) {
					incrementIndent();
				}
				print('(');
				left.accept(this);
				print(')');

				if (leftRational) {
					decrementIndent();
				}
			} else {
				left.accept(this);
			}
		} else {
			left.accept(this);
		}

	}
}
