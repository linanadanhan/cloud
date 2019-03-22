package com.gsoft.portal.system.personnel.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 角色关联人员表
 *
 * @author helx
 * @date 2017年8月7日 上午10:44:57
 */
@Entity
@Table(name = "COS_SYS_ROLE_PERSONAL")
public class RolePersonnelEntity implements java.io.Serializable{
	
	private static final long serialVersionUID = -8365029147413123827L;
	/**
	 * 主键Id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "C_ID", length = 9)
	private Long id;	
	
	/**
	 * 角色id
	 */
	@Column(name = "C_ROLE_ID")
	private Long roleId;
	
	/**
	 * 人员ID
	 */
	@Column(name = "C_PERSONNEL_ID")
	private Long personnelId;
	
	/**
	 * 允许授权:1允许，0不允许
	 * 默认不允许
	 */
	@Column(name = "C_IS_TURN_GRANT")
	private Boolean isTurnGrant=false;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}


	public Long getPersonnelId() {
		return personnelId;
	}

	public void setPersonnelId(Long personnelId) {
		this.personnelId = personnelId;
	}

	public Boolean getIsTurnGrant() {
		return isTurnGrant;
	}

	public void setIsTurnGrant(Boolean isTurnGrant) {
		this.isTurnGrant = isTurnGrant;
	}
	
	


	
	
}
