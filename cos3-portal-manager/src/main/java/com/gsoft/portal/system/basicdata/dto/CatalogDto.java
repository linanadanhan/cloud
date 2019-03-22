package com.gsoft.portal.system.basicdata.dto;

import com.gsoft.cos3.dto.TreeBaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 分类科目DTO
 * @author SN
 *
 */
@ApiModel("分类科目dto")
public class CatalogDto extends TreeBaseDto {

	private static final long serialVersionUID = -4827230445388795954L;
	/**
	 * 级联ID，如/a/b/c/
	 */
	@ApiModelProperty("级联ID")
	private String cascadeid;
	/**
	 * 根目录key
	 */
	@ApiModelProperty("根目录key")
	private String rootkey;
	/**
	 * 根目录名称
	 */
	@ApiModelProperty("根目录名称")
	private String rootName;
	/**
	 * 科目名称
	 */
	@ApiModelProperty("科目名称")
	private String name;
	/**
	 * 热键，预留
	 */
	@ApiModelProperty("热键，预留")
	private String hotkey;
	/**
	 * 是否叶子节点
	 */
	@ApiModelProperty("是否叶子节点")
	private Boolean isLeaf;
	/**
	 * 是否自动展开
	 */
	@ApiModelProperty("是否自动展开")
	private Boolean isAutoExpend;
	/**
	 * 图标名称
	 */
	@ApiModelProperty("图标名称")
	private String iconCls;
	/**
	 * 排序号
	 */
	@ApiModelProperty("排序号")
	private Integer sortNo;
	/**
	 * 科目分类
	 */
	@ApiModelProperty("科目分类")
	private String type;
	/**
	 * 状态
	 */
	@ApiModelProperty("状态")
	private Boolean status;
	/**
	 * 科目值
	 */
	@ApiModelProperty("科目值")
	private String value;
	
	public Long get_parentId() {
		return this.getParentId();
	}

	public CatalogDto() {

	}

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

	@Override
	public String getText() {
		return name;
	}

	@Override
	public String getState() {
		return "open";
	}

	@Override
	public Boolean getChecked() {
		return Boolean.FALSE;
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
