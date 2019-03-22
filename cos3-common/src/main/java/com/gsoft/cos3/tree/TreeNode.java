/**
 * 
 */
package com.gsoft.cos3.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shencq
 * 
 *         树控件数据对象：树节点
 * 
 */
public class TreeNode {

	/**
	 * 主键，唯一
	 */
	private String id;

	/**
	 * 父节点
	 */
	private String parentId;
	/**
	 * 当前区划code
	 */
	private String code;
	/**
	 * 节点名称
	 */
	private String text;
	/**
	 * 显示状态：展开（open）、合拢（closed）
	 */
	private String state;

	/**
	 * 节点图标样式
	 */
	private String iconCls;

	/**
	 * 选中状态
	 */
	private boolean checked;
	/**
	 * 自定义属性
	 */
	private Map<String, String> attributes = new HashMap<String, String>();
	/**
	 * 子节点
	 */
	private List<TreeNode> children;
	/**
	 * 几层菜单 
	 */
	private int level;
	
	/**
	 * 级联属性
	 */
	private String cascade;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the parentId
	 */
	public String getParentId() {
		return parentId;
	}

	/**
	 * @param parentId
	 *            the parentId to set
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the iconCls
	 */
	public String getIconCls() {
		return iconCls;
	}

	/**
	 * @param iconCls
	 *            the iconCls to set
	 */
	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	/**
	 * @return the checked
	 */
	public boolean getChecked() {
		return checked;
	}

	/**
	 * @param checked
	 *            the checked to set
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	/**
	 * @return the attributes
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes
	 *            the attributes to set
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public void addAttributes(Map<String, String> attributes) {
		if (this.attributes != null) {
			this.attributes.putAll(attributes);
		} else {
			setAttributes(attributes);
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setAttribute(String key, String value) {
		if (this.attributes == null) {
			setAttributes(new HashMap<String, String>());
		}
		this.attributes.put(key, value);
	}

	/**
	 * @return the children
	 */
	public List<TreeNode> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}

	/**
	 * 获取自定义参数值
	 * 
	 * @param name
	 * @return
	 */
	public String getAttribute(String name) {
		return attributes.get(name);
	}

	public boolean isleaf() {
		return children == null || children.size() == 0;
	}
	
	public void addChild(TreeNode child) {
		if (children == null) {
			children = new ArrayList<TreeNode>();
		}
		children.add(child);
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getCascade() {
		return cascade;
	}

	public void setCascade(String cascade) {
		this.cascade = cascade;
	}
	
}
