package com.gsoft.portal.system.personnel.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 角色关联权限表
 *
 * @author helx
 * @date 2017年8月7日 上午10:44:57
 */
@ApiModel("角色关联权限dto")
public class RolePermissionDto implements java.io.Serializable{
	
	
	private static final long serialVersionUID = -8365029147413123827L;
	/**
	 * 主键Id
	 */
	@ApiModelProperty("主键Id")
	private Long id;	
	
	/**
	 * 角色id
	 */
	@ApiModelProperty("角色id")
	private Long roleId;
	
	/**
	 * 权限ID
	 */
	@ApiModelProperty("权限ID")
	private Long permissionId;
	
	/**
	 * 权限分类--用来删除的时候用
	 */
	@ApiModelProperty("权限分类--用来删除的时候用")
	private String type;


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

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
