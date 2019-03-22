package com.gsoft.portal.system.organization.dto;

import com.gsoft.cos3.dto.BaseDto;

/**
 * 职位管理关系表
 *
 * @author plsy
 */
public class PositionManageDto extends BaseDto {

	private static final long serialVersionUID = 5222922946032440390L;

	/**
     * 职位id
     */
    private Long positionId;

    /**
     * 上级id
     */
    private Long leaderId;

    /**
     * 上级职位类型
     */
    private String leaderType;

    /**
     * 上级职位所属机构
     */
    private Long leaderOrgId;

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public Long getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Long leaderId) {
        this.leaderId = leaderId;
    }

    public String getLeaderType() {
        return leaderType;
    }

    public void setLeaderType(String leaderType) {
        this.leaderType = leaderType;
    }

    public Long getLeaderOrgId() {
        return leaderOrgId;
    }

    public void setLeaderOrgId(Long leaderOrgId) {
        this.leaderOrgId = leaderOrgId;
    }
}
