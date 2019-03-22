package com.gsoft.cos3.dto;

import java.util.ArrayList;
import java.util.List;

import com.gsoft.cos3.dto.BaseDto;

/**
 * tree结构的基础dto
 * @author wangfei
 * @Date 2015年7月29日 下午6:28:56
 *
 */
public abstract class TreeBaseDto extends BaseDto {

	private static final long serialVersionUID = -3644937903795801378L;
	
	private List<TreeBaseDto> children = new ArrayList<TreeBaseDto>();
	/**
	 * 标识上下级的ID
	 */
	private Long parentId;
	
	/**
	 * 父节点类型标识，用于租转不同类型资源的综合树
	 */
	private String parentNodeType;
	
	/**
	 * 节点类型标识，用于租转不同类型资源的综合树
	 */
	private String nodeType;
	/**
	 * 是否选中
	 */
	private Boolean checked;
	
	public abstract String getText();
	
	public abstract String getState();
	
	public List<TreeBaseDto> getChildren() {
		return children;
	}

	public void setChildren(List<TreeBaseDto> children) {
		this.children = children;
	}
	
	public void addChildren(TreeBaseDto dto) {
		this.children.add(dto);
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	/**
	 * @return the parentNodeType
	 */
	public String getParentNodeType() {
		return parentNodeType;
	}

	/**
	 * @param parentNodeType the parentNodeType to set
	 */
	public void setparentNodeType(String parentNodeType) {
		this.parentNodeType = parentNodeType;
	}

	/**
	 * @return the nodeType
	 */
	public String getNodeType() {
		return nodeType;
	}

	/**
	 * @param nodeType the nodeType to set
	 */
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	
}
