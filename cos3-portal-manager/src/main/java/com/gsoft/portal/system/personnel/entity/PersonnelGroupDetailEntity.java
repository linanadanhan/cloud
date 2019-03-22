package com.gsoft.portal.system.personnel.entity;

import com.gsoft.cos3.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * 人员群组明细信息表
 * @author chenxx
 *
 */
@Entity
@Table(name = "COS_PERSONNEL_GROUP_DETAIL")
public class PersonnelGroupDetailEntity extends BaseEntity {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = -9022649337189740204L;
	
	/**
	 * 群组ID
	 */
	@Column(name = "C_GROUP_ID", length = 9)
	private Long groupId;
	
	/**
	 * 机构ID
	 */
	@Column(name = "C_ORG_ID", length = 9)
	private Long orgId;

	/**
	 * 用户ids--userIds
	 */
	@Lob
	@Column(name = "C_USER_IDS")
	private String userIds;

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getUserIds() {
		return userIds;
	}

	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}
}
