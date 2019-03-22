package com.gsoft.portal.system.personnel.entity;

import com.gsoft.cos3.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 人员群组信息表
 * @author chenxx
 *
 */
@Entity
@Table(name = "COS_SYS_PERSONNEL_GROUP")
public class PersonnelGroupEntity extends BaseEntity {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = -9022649337189740204L;
	
	/**
	 * 群组名称
	 */
	@Column(name = "C_GROUP_NAME", length = 50)
	private String groupName;

	/**
	 * 群组类型： 1系统群组，2个人群组
	 */
	@Column(name = "C_GROUP_type", length = 2)
	private Integer groupType;

	/**
	 * 描述
	 */
	@Column(name = "C_DESCRIBE",length=200)
	private String describe;


	//createBy

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
}
