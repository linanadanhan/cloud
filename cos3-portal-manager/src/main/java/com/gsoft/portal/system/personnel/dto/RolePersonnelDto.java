package com.gsoft.portal.system.personnel.dto;


/**
 * 角色关联人员表
 *
 * @author helx
 * @date 2017年8月7日 上午10:44:57
 */
public class RolePersonnelDto implements java.io.Serializable{
	
	private static final long serialVersionUID = -8365029147413123827L;
	private Long id;	
	
	/**
	 * 角色id
	 */
	private Long roleId;
	
	/**
	 * 人员ID
	 */
	private Long personnelId;
	
	/**
	 * 允许授权:1允许，0不允许
	 * 默认不允许
	 */
	private Boolean isTurnGrant;
	
	/**
	 * 角色关联人员--选中的行政区划code
	 */
	private String orgCode;
	
	/**
	 * 角色关联人员--是否级联 
	 */
	private Boolean isCascade;
	
	/**
	 * 人员关联角色--选中的角色类型
	 */
	private String type;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getIsCascade() {
		return isCascade;
	}

	public void setIsCascade(Boolean isCascade) {
		this.isCascade = isCascade;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	
	
}
