package com.gsoft.portal.system.organization.entity;

import com.gsoft.cos3.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 职位表
 *
 * @author plsy
 */
@Entity
@Table(name = "cos_sys_position")
public class PositionEntity extends BaseEntity {

	private static final long serialVersionUID = -9186603708660793920L;

	/**
     * 机构id
     */
    @Column(name = "c_org_id", length = 9)
    private Long orgId;

    /**
     * 岗位id
     */
    @Column(name = "c_post_id", length = 50)
    private String postId;

    /**
     * 岗位名称
     */
    @Column(name = "c_post_name", length = 50)
    private String postName;

    /**
     * 排序号
     */
    @Column(name = "c_sort_no", length = 9)
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
