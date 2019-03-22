package com.gsoft.portal.system.basicdata.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

@Entity
@Table(name = "cos_basicdata_catalog")
public class Catalog extends BaseEntity {
	
	private static final long serialVersionUID = 8652338869569180763L;
	/**
	 * 级联ID，如/1/2/3/
	 */
	@Column(name = "C_CASCADE_ID", length = 50)
	private String cascadeid;
	/**
	 * 根目录key
	 */
	@Column(name = "C_ROOT_KEY", length = 50)
	private String rootkey;
	/**
	 * 根目录名称
	 */
	@Column(name = "C_ROOT_NAME", length = 50)
	private String rootName;
	/**
	 * 科目名称
	 */
	@Column(name = "C_NAME", length = 50)
	private String name;
	/**
	 * 热键，预留
	 */
	@Column(name = "C_HOT_KEY", length = 50)
	private String hotkey;
	/**
	 * 父id
	 */
	@Column(name = "C_PARENT_ID")
	private Long parentId;
	/**
	 * 是否叶子节点
	 */
	@Column(name = "C_IS_LEAF")
	private Boolean isLeaf;
	/**
	 * 是否自动展开
	 */
	@Column(name = "C_IS_AUTO_EXPEND")
	private Boolean isAutoExpend;
	/**
	 * 图标名称
	 */
	@Column(name = "C_ICON_CLS", length = 50)
	private String iconCls;
	/**
	 * 排序号
	 */
	@Column(name = "C_SORT_NO")
	private Integer sortNo;
	/**
	 * 科目类型
	 */
	@Column(name = "C_TYPE")
	private String type;
	/**
	 * 科目状态
	 */
	@Column(name = "C_STATUS")
	private Boolean status;
	/**
	 * 科目值
	 */
	@Column(name = "C_VALUE")
	private String value;
	
	public String getCascadeid() {
		return cascadeid;
	}
	public void setCascadeid(String cascadeid) {
		this.cascadeid = cascadeid;
	}
	public String getRootkey() {
		return rootkey;
	}
	public void setRootkey(String rootkey) {
		this.rootkey = rootkey;
	}
	public String getRootName() {
		return rootName;
	}
	public void setRootName(String rootName) {
		this.rootName = rootName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHotkey() {
		return hotkey;
	}
	public void setHotkey(String hotkey) {
		this.hotkey = hotkey;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public Boolean getIsLeaf() {
		return isLeaf;
	}
	public void setIsLeaf(Boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	public Boolean getIsAutoExpend() {
		return isAutoExpend;
	}
	public void setIsAutoExpend(Boolean isAutoExpend) {
		this.isAutoExpend = isAutoExpend;
	}
	public String getIconCls() {
		return iconCls;
	}
	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}
	public Integer getSortNo() {
		return sortNo;
	}
	public void setSortNo(Integer sortNo) {
		this.sortNo = sortNo;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
