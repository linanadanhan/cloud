package com.gsoft.portal.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 门户业务权限表
 * @author SN
 *
 */
@Entity
@Table(name = "cos_portal_permission")
public class PortalAuthEntity  extends BaseEntity {

	private static final long serialVersionUID = -5494518685634330767L;
	
	/**
	 * 授权ID  用户ID or 角色ID
	 */
	@Column(name = "c_grant_id", length = 9)
	private Long grantId;
	
	/**
	 * 业务ID 站点ID 页面ID widgetID
	 */
	@Column(name = "c_yw_id", length = 9)
	private Long ywId;
	
	/**
	 * 授权类型
	 */
	@Column(name = "c_grant_type", length = 1)
	private String grantType;
	
	/**
	 * 业务类型
	 */
	@Column(name = "c_yw_type", length = 1)
	private String ywType;

	public Long getGrantId() {
		return grantId;
	}

	public void setGrantId(Long grantId) {
		this.grantId = grantId;
	}

	public Long getYwId() {
		return ywId;
	}

	public void setYwId(Long ywId) {
		this.ywId = ywId;
	}

	public String getGrantType() {
		return grantType;
	}

	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}

	public String getYwType() {
		return ywType;
	}

	public void setYwType(String ywType) {
		this.ywType = ywType;
	}

}
