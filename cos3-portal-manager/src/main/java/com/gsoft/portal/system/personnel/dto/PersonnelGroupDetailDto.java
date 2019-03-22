package com.gsoft.portal.system.personnel.dto;

import com.gsoft.cos3.dto.BaseDto;

/**
 * 人员群组明细信息表
 * @author chenxx
 *
 */
public class PersonnelGroupDetailDto extends BaseDto {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = -364249724627372468L;
	
	/**
	 * 群组ID
	 */
	private Long groupId;
	
	/**
	 * 机构ID
	 */
	private Long orgId;

	/**
	 * 保存用户json数据
	 */
	private String userIds;


	//机构Label,映射的机构层级的名称
	private String orgLabel;

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

	public String getOrgLabel() {
		return orgLabel;
	}

	public void setOrgLabel(String orgLabel) {
		this.orgLabel = orgLabel;
	}
}
