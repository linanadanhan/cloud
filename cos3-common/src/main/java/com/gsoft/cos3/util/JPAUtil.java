/**
 * 
 */
package com.gsoft.cos3.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

/**
 * @author shencq
 * 
 */
public class JPAUtil {

	/**
	 * 创建分页对象
	 * 
	 * @param page
	 *            当前页，首页页码为1
	 * @param size
	 *            每页显示条数
	 * @param sort
	 *            排序字段名
	 * @param order
	 *            顺序（asc）、倒序（desc）
	 * @return
	 */
	public static Pageable createPageRequest(int page, int size, String sort, String order) {
		return new PageRequest(page - 1, size, Direction.fromStringOrNull(order), sort);
	}

	/**
	 * 创建分页对象（多属性排序）
	 * 
	 * @param page
	 *            当前页，首页页码为1
	 * @param size
	 *            每页显示条数
	 * @param orders
	 *            多个Order{@link Order}对象
	 * @return
	 */
	public static Pageable createPageRequest(int page, int size, Order... orders) {
		return new PageRequest(page - 1, size, new Sort(orders));
	}

	/**
	 * 创建排序对象
	 * 
	 * @param sort
	 * @return
	 */
	public static Sort createSort(String... sort) {
		return new Sort(sort);
	}

	/**
	 * 创建排序对象
	 * 
	 * @param orders
	 * @return
	 */
	public static Sort createSort(Order... orders) {
		return new Sort(orders);
	}

	/**
	 * 创建分页对象
	 * 
	 * @param page
	 *            实际页码
	 * @param size
	 *            每页查询条数
	 * @param order
	 *            排序方式
	 * @param sort
	 *            排序字段
	 * @return
	 */
	public static Pageable createPageRequest(int page, int size, String order, String... sort) {
		return new PageRequest(page - 1, size, Direction.fromStringOrNull(order), sort);
	}
}
