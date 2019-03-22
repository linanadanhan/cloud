package com.gsoft.portal.system.personnel.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户组织机构表
 * @author SN
 *
 */
@Entity
@Table(name = "cos_sys_user_org")
public class UserOrganizationEntity {
	
	/**
	 * 主键Id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "C_ID", length = 9)
	private Long id;	
	
	/**
	 * 用户ID
	 */
	@Column(name = "c_personnel_id", length = 9)
	private Long personId;
	
	/**
	 * 组织机构ID
	 */
	@Column(name = "c_org_id", length = 9)
	private Long orgId;

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	
}
