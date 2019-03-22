/**
 * 
 */
package com.gsoft.cos3.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shencq 树对象
 * 
 */
public class Tree {

	/**
	 * 根节点
	 */
	private List<TreeNode> roots = new ArrayList<TreeNode>();
	
	/**
	 * 所有节点（顺序）
	 */
	private List<TreeNode> all = new ArrayList<TreeNode>();
	
	/**
	 * 转换好的TreeNode节点集合
	 */
	private Map<Object, TreeNode> nodes = new HashMap<Object, TreeNode>();
	

	/**
	 * 属性转换器
	 */
	private Map<String, TreeNodeAttributeTranslator> translators = new HashMap<String, TreeNodeAttributeTranslator>();
	
	/**
	 * 节点上需要增加的自定义属性
	 */
	private Map<String, String> attrs = new HashMap<String, String>();
	/**
	 * 原始数据集合
	 */
	private List<?> raws;

	/**
	 * @param raws
	 */
	public Tree(List<? extends Object> raws) {
		translators.put("id", new DefaultTreeNodeAttributeTranslator("id"));
		translators.put("parentId", new DefaultTreeNodeAttributeTranslator(
				"parentId"));
		translators.put("text", new DefaultTreeNodeAttributeTranslator("name"));
		translators.put("code", new DefaultTreeNodeAttributeTranslator("code"));
		translators.put("state", new StateTreeNodeAttributeTranslator("state"));
		translators.put("iconCls", new DefaultTreeNodeAttributeTranslator("iconCls"));
		translators.put("checked", new BooleanTreeNodeAttributeTranslator(
				"checked"));
		translators.put("cascade", new DefaultTreeNodeAttributeTranslator("cascade"));
		translators.put("attributes",
				new AttributesTreeNodeAttributeTranslator());
		this.raws = raws;
	}

	/**
	 * 更改属性名
	 * 
	 * @param name
	 *            属性名
	 * @param rawName
	 *            原始对象属性名
	 * @return
	 */
	public Tree change(String name, String rawName) {
		if ("state".equals(name)) {
			translators
					.put(name, new StateTreeNodeAttributeTranslator(rawName));
		} else if ("checked".equals(name)) {
			translators.put(name, new BooleanTreeNodeAttributeTranslator(
					rawName));
		} else if (Arrays.asList("id", "parentId", "text", "iconCls","code", "cascade").contains(name)) {
			translators.put(name, new DefaultTreeNodeAttributeTranslator(
					rawName));
		}
		return this;
	}

	/**
	 * 格式化属性
	 * 
	 * @param name
	 *            属性名
	 * @param format
	 *            格式化语句，如：%s_%s
	 * @param rawNames
	 *            原始对象属性名字符串，与格式化语句中的%s数量一致，如name,age
	 * @return
	 */
	public Tree format(String name, String format, String rawNames) {
		if (Arrays.asList("id", "parentId", "text", "iconCls","code", "cascade").contains(name)) {
			translators.put(name, new FormatTreeNodeAttributeTranslator(format,
					rawNames));
		}
		return this;
	}

	/**
	 * 属性增加前缀
	 * 
	 * @param name
	 *            属性名
	 * @param pre
	 *            前缀
	 * @param rawName
	 *            原始对象属性名
	 * @return
	 */
	public Tree prefix(String name, String pre, String rawName) {
		if (Arrays.asList("id", "parentId", "text", "iconCls","code", "cascade").contains(name)) {
			translators.put(name, new DefaultTreeNodeAttributeTranslator(rawName, pre));
		}
		return this;
	}

	/**
	 * 设置固定值
	 * 
	 * @param name
	 *            属性名
	 * @param value
	 *            值
	 * @return
	 */
	public Tree set(String name, Object value) {
		if (Arrays.asList("id", "parentId", "text", "state", "checked", "iconCls","code", "cascade")
				.contains(name)) {
			translators.put(name,
					new ConstantTreeNodeAttributeTranslator(value));
		}
		return this;
	}
	
	/**
	 * 设置固定值
	 * 
	 * @param name
	 *            属性名
	 * @param value
	 *            值
	 * @return
	 */
	public Tree setAttr(String name, String value) {
		attrs.put(name, value);
		return this;
	}

	/**
	 * 复制自定义属性
	 * 
	 * @param names
	 *            需要复制的原始对象属性名字符串
	 * @return
	 */
	public Tree attrs(String names) {
		translators.put("attributes",
				new AttributesTreeNodeAttributeTranslator(names));
		return this;
	}
	
	/**
	 * 复制自定义属性
	 * 
	 * @param names
	 *            需要复制的原始对象属性名字符串
	 * @return
	 */
	public Tree attrs(String[] names) {
		translators.put("attributes",
				new AttributesTreeNodeAttributeTranslator(names));
		return this;
	}

	/**
	 * 合并树
	 * 
	 * @param raws
	 * @return
	 */
	public Tree merge(List<?> raws) {
		for (Object raw : this.raws) {
			Object key = getValue(raw, "id");
			TreeNode node = createTreeNode(key, raw);
			nodes.put(key, node);
			all.add(node);
		}
		translators.put("id", new DefaultTreeNodeAttributeTranslator("id"));
		translators.put("parentId", new DefaultTreeNodeAttributeTranslator(
				"parentId"));
		translators.put("text", new DefaultTreeNodeAttributeTranslator("name"));
		translators.put("code", new DefaultTreeNodeAttributeTranslator("code"));
		translators.put("state", new StateTreeNodeAttributeTranslator("state"));
		translators.put("iconCls", new DefaultTreeNodeAttributeTranslator("iconCls"));
		translators.put("checked", new BooleanTreeNodeAttributeTranslator(
				"checked"));
		translators.put("cascade", new DefaultTreeNodeAttributeTranslator("cascade"));
		translators.put("attributes",
				new AttributesTreeNodeAttributeTranslator());
		this.raws = raws;
		this.attrs.clear();
		return this;
	}

	/**
	 * 转换成树结构
	 * 
	 * @return
	 */
	public List<TreeNode> tree() {

		for (Object raw : raws) {
			Object key = getValue(raw, "id");
			TreeNode node = createTreeNode(key, raw);
			nodes.put(key, node);
			all.add(node);
		}
		for (TreeNode node : all) {
			Object pid = node.getParentId();
			TreeNode parent = nodes.get(pid);
			if (parent == null) {
				roots.add(node);
			} else {
				parent.addChild(node);
			}
		}
		return roots;
	}

	/**
	 * 将原始对象转换成树节点
	 * 
	 * @param key
	 * @param raw
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private TreeNode createTreeNode(Object key, Object raw) {
		TreeNode node = new TreeNode();
		node.setId((String) key);
		node.setParentId((String) getValue(raw, "parentId"));
		node.setChecked((Boolean) getValue(raw, "checked"));
		node.setState((String) getValue(raw, "state"));
		node.setIconCls((String) getValue(raw, "iconCls"));
		node.setCode((String) getValue(raw, "code"));
		node.setText((String) getValue(raw, "text"));
		node.setCascade((String) getValue(raw, "cascade"));
		node.setAttributes((Map<String, String>) getValue(raw, "attributes"));
		node.addAttributes(attrs);
		return node;
	}

	/**
	 * 抓换属性值
	 * 
	 * @param raw
	 * @param name
	 * @return
	 */
	private Object getValue(Object raw, String name) {
		TreeNodeAttributeTranslator translator = translators.get(name);
		return translator.translate(raw);
	}

}
