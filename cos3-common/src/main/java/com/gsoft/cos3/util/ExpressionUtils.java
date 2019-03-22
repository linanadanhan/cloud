/**
 * 
 */
package com.gsoft.cos3.util;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.jexl2.ObjectContext;

import java.util.Map;

/**
 * <p>
 * 表达式计算工具
 * </p>
 * <p>
 * 支持变量、Array、List、Map等，支持方法调用
 * </p>
 * <p>
 * 支持and、or、&&、||、?:、if else等
 * </p>
 * <p>
 * 支持大于、小于、等于等比较运算符
 * </p>
 * 
 * @author shencq
 *
 */
public class ExpressionUtils {

	private static final JexlEngine jexl = new JexlEngine();
	static {
		jexl.setCache(512);
		jexl.setLenient(true);
		jexl.setSilent(true);
	}

	/**
	 * 执行表达式
	 * 
	 * @param expression
	 *            表达式字符串
	 * @param context
	 *            上下文Map对象
	 * @return 执行结果
	 */
	public static Object execute(String expression, Map<String, Object> context) {
		Expression e = jexl.createExpression(expression);
		JexlContext ctx = new MapContext(context);
		return e.evaluate(ctx);
	}

	/**
	 * 执行表达式
	 * 
	 * @param expression
	 *            表达式字符串
	 * @param contextBean
	 *            上下文对象
	 * @return
	 */
	public static <T> Object execute(String expression, T contextBean) {
		Expression e = jexl.createExpression(expression);
		JexlContext ctx = new ObjectContext<T>(jexl, contextBean);
		return e.evaluate(ctx);
	}

	/**
	 * 确认表达式结果是否为真
	 * 
	 * @param expression
	 *            表达式字符串（计算结果应该为true、false）
	 * @param context
	 *            上下文对象
	 * @return
	 */
	public static Boolean confirm(String expression, Object contextBean) {
		Object val = execute(expression, contextBean);
		if(val == null) {
			return false;
		}
		return (Boolean) val;
	}
}
