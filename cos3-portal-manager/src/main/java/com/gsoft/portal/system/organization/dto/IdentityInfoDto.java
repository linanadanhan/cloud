package com.gsoft.portal.system.organization.dto;


import com.gsoft.cos3.dto.BaseDto;

/**
 * 身份信息表
 *
 * @author plsy
 */
public class IdentityInfoDto extends BaseDto {

	private static final long serialVersionUID = -4024343020271203297L;

	/**
     * 职位id
     */
    private Long positionId;

    /**
     * 登录名
     */
    private String loginName;

    /**
     * 中文名
     */
    private String name;

    /**
     * 职位名
     */
    private String positionName;

    /**
     * 移动电话
     */
    private String mobilePhone;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 排序号
     */
    private Integer sortNo;

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

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
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
