package com.gsoft.portal.system.organization.dto;

import com.gsoft.cos3.dto.BaseDto;

/**
 * 职位表
 *
 * @author plsy
 */
public class PositionDto extends BaseDto {

	private static final long serialVersionUID = 8685686983389422746L;

	/**
     * 机构id
     */
    private Long orgId;

    /**
     * 岗位id
     */
    private String postId;

    /**
     * 岗位名称
     */
    private String postName;

    /**
     * 排序号
     */
    private Integer sortNo;

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }
}
