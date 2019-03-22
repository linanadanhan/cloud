package com.gsoft.portal.system.personnel.dto;

import com.gsoft.cos3.dto.BaseDto;

/**
 * 人员群组信息表
 * @author chenxx
 *
 */
public class PersonnelGroupDto extends BaseDto {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = -364249724627372468L;
	
	/**
	 * 群组名称
	 */
	private String groupName;
	/**
	 * 群组类型： 1系统群组，2个人群组
	 */
	private Integer groupType;

	/**
	 * 描述
	 */
	private String describe;


	//当前机构：如果是清空某机构的数据，下面的json是空的，只有把当前机构传过来，才知道
	private Long currentOrgId;

	//json数据
	private String jsonPersonDtoList;  //多条，里面包含org,userIds


	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getGroupType() {
		return groupType;
	}

	public void setGroupType(Integer groupType) {
		this.groupType = groupType;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getJsonPersonDtoList() {
		return jsonPersonDtoList;
	}

	public void setJsonPersonDtoList(String jsonPersonDtoList) {
		this.jsonPersonDtoList = jsonPersonDtoList;
	}

	public Long getCurrentOrgId() {
		return currentOrgId;
	}

	public void setCurrentOrgId(Long currentOrgId) {
		this.currentOrgId = currentOrgId;
	}
}
