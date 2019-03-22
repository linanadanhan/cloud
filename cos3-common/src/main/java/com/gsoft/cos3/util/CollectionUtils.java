/**
 * 
 */
package com.gsoft.cos3.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author shencq
 * 
 */
public class CollectionUtils {
	/**
	 * 创建空HashMap
	 * 
	 * @return
	 */
	public static <K, T> Map<K, T> map() {
		return new HashMap<K, T>();
	}

	/**
	 * 创建HashMap
	 * 
	 * @param keys
	 * @param values
	 * @return
	 */
	public static <K, T> Map<K, T> map(K[] keys, T[] values) {
		Assert.notEmpty(keys, "主键不能为空");
		Assert.notEmpty(values, "值不能为空");
		Assert.equals(keys.length, values.length, "主键和值的数量必须相同");
		Map<K, T> map = map();
		for (int i = 0; i < keys.length; i++) {
			map.put(keys[i], values[i]);
		}
		return map;
	}

	/**
	 * 创建HashMap
	 * 
	 * @param keys
	 * @param values
	 * @return
	 */
	public static <K, T> Map<K, T> map(List<K> keys, List<T> values) {
		Assert.notEmpty(keys, "主键不能为空");
		Assert.notEmpty(values, "值不能为空");
		Assert.equals(keys.size(), values.size(), "主键和值的数量必须相同");
		Map<K, T> map = map();
		for (int i = 0; i < keys.size(); i++) {
			map.put(keys.get(i), values.get(i));
		}
		return map;
	}

	/**
	 * 创建HashMap
	 * 
	 * @param keys
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Map<String, T> map(String keys, T... values) {
		Assert.hasText(keys, "主键不能为空");
		Assert.notEmpty(values, "值不能为空");
		String[] names = StringUtils.split(keys, ',');
		Assert.equals(names.length, values.length, "主键和值的数量必须相同");
		Map<String, T> map = map();
		for (int i = 0; i < names.length; i++) {
			map.put(names[i].trim(), values[i]);
		}
		return map;
	}

	/**
	 * 创建空HashSet
	 * 
	 * @return
	 */
	public static <T> Set<T> set() {
		return new HashSet<T>();
	}

	/**
	 * 创建HashSet
	 * 
	 * @param ts
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Set<T> set(T... ts) {
		Set<T> set = set();
		if (ts != null) {
			for (T t : ts) {
				set.add(t);
			}
		}
		return set;
	}

	/**
	 * 创建HashSet
	 * 
	 * @param ts
	 * @return
	 */
	public static <T> Set<T> set(List<T> ts) {
		Set<T> set = set();
		if (ts != null) {
			for (T t : ts) {
				set.add(t);
			}
		}
		return set;
	}

	/**
	 * 创建空ArrayList
	 * 
	 * @return
	 */
	public static <T> List<T> list() {
		return new ArrayList<T>();
	}

	/**
	 * 创建ArrayList
	 * 
	 * @param ts
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> list(T... ts) {
		List<T> list = list();
		if (ts != null) {
			if (ts != null) {
				for (T t : ts) {
					list.add(t);
				}
			}
		}
		return list;
	}

	/**
	 * 创建ArrayList
	 * 
	 * @param ts
	 * @return
	 */
	public static <T> List<T> list(Set<T> ts) {
		List<T> list = list();
		if (ts != null) {
			for (T t : ts) {
				list.add(t);
			}
		}
		return list;
	}
}
