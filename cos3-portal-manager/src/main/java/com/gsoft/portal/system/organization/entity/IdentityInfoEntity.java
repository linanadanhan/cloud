package com.gsoft.portal.system.organization.entity;


import com.gsoft.cos3.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 身份信息表
 *
 * @author plsy
 */
@Entity
@Table(name = "cos_sys_identity_info")
public class IdentityInfoEntity extends BaseEntity {

	private static final long serialVersionUID = -4226117069422762264L;

	/**
     * 职位id
     */
    @Column(name = "c_position_id", length = 9)
    private Long positionId;

    /**
     * 登录名
     */
    @Column(name = "c_login_name", length = 32)
    private String loginName;

    /**
     * 中文名
     */
    @Column(name = "c_name", length = 50)
    private String name;

    /**
     * 职位名
     */
    @Column(name = "c_position_name", length = 50)
    private String positionName;

    /**
     * 移动电话
     */
    @Column(name = "c_mobile_phone", length = 11)
    private String mobilePhone;

    /**
     * 用户id
     */
    @Column(name = "c_user_id", length = 9)
    private Long userId;

    /**
     * 排序号
     */
    @Column(name = "c_sort_no", length = 9)
    private Integer sortNo;

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }
}
