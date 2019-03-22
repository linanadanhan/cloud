/**
 * 
 */
package com.gsoft.cos3.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gsoft.cos3.dto.TreeBaseDto;
import com.gsoft.cos3.tree.Tree;

/**
 * @author shencq
 * 
 */
public class TreeUtils {

	public static Tree convert(List<?> raws) {
		return new Tree(raws);
	}
	
	
	/**
	 * 从list到tree
	 * 
	 * @param beanlist
	 * @return
	 * @deprecated 使用com.gsoft.cos.core.util.TreeUtils.convert().tree()替代
	 */
	public static <T extends TreeBaseDto> List<T> convertBeanList(
			List<T> beanlist) {
		List<T> roots = new ArrayList<T>();
		Map<Object, T> tempMap = new HashMap<Object, T>();
		for (T t : beanlist) {
			Object key = t.getId();
			if (t.getNodeType() != null) {
				key = t.getNodeType() + key;
			}
			tempMap.put(key, t);
		}
		for (T t : beanlist) {
			Object key = t.getParentId();
			if (t.getParentNodeType() != null) {
				key = t.getParentNodeType() + key;

			}
			T parent = tempMap.get(key);
			if (parent == null) {
				roots.add(t);
			} else {
				parent.addChildren(t);
			}
		}
		return roots;
	}

	/**
	 * 将Map集合转换成树结构
	 * 
	 * @param maplist
	 * @return
	 * @deprecated 使用com.gsoft.cos.core.util.TreeUtils.convert().tree()替代
	 */
	public static List<Map<Object, Object>> convertMapList(
			List<Map<Object, Object>> maplist) {
		List<Map<Object, Object>> roots = new ArrayList<Map<Object, Object>>();
		Map<Object, Map<Object, Object>> tempMap = new HashMap<Object, Map<Object, Object>>();
		for (Map<Object, Object> t : maplist) {
			Object key = t.get("id");
			if (t.get("nodeType") != null) {
				key = t.get("nodeType").toString() + key;
			}
			tempMap.put(key, t);
		}
		for (Map<Object, Object> t : maplist) {
			Object key = t.get("parentId");
			if (t.get("parentNodeType") != null) {
				key = t.get("parentNodeType").toString() + key;
			}
			Map<Object, Object> parent = tempMap.get(key);
			if (parent == null) {
				roots.add(t);
			} else {
				@SuppressWarnings("unchecked")
				List<Map<Object, Object>> childs = (List<Map<Object, Object>>) Utils
						.getValue(parent.get("children"),
								new ArrayList<Map<Object, Object>>());
				childs.add(t);
				parent.put("children", childs);
			}
		}
		return roots;
	}

	
}
