package com.gsoft.portal.system.personnel.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 角色关联权限表
 *
 * @author helx
 * @date 2017年8月7日 上午10:44:57
 */
@Entity
@Table(name = "COS_SYS_ROLE_PERMISSION")
public class RolePermissionEntity implements java.io.Serializable{
	
	
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
	 * 权限ID
	 */
	@Column(name = "C_PERMISSION_ID")
	private Long permissionId;

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

	public Long getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(Long permissionId) {
		this.permissionId = permissionId;
	}
	
	
}
