package com.gsoft.portal.system.organization.entity;

import com.gsoft.cos3.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 职位管理关系表
 *
 * @author plsy
 */
@Entity
@Table(name = "cos_sys_position_manage")
public class PositionManageEntity extends BaseEntity{

	private static final long serialVersionUID = -155555696178501596L;

	/**
     * 职位id
     */
    @Column(name = "c_position_id", length = 9)
    private Long positionId;

    /**
     * 上级id
     */
    @Column(name = "c_leader_id", length = 9)
    private Long leaderId;

    /**
     * 上级职位类型
     */
    @Column(name = "c_leader_type", length = 20)
    private String leaderType;

    /**
     * 上级职位所属机构id
     */
    @Column(name = "c_leader_org_id", length = 9)
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
