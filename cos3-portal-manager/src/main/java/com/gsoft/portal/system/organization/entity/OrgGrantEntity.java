package com.gsoft.portal.system.organization.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 机构授权表
 *
 * @author plsy
 */
@Entity
@Table(name = "cos_sys_org_grant")
public class OrgGrantEntity implements java.io.Serializable {

    private static final long serialVersionUID = -5494518685634330767L;

    /**
     * 主键Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "c_id", length = 9)
    private Long id;

    /**
     * 机构id
     */
    @Column(name = "c_org_id", length = 9)
    private Long orgId;

    /**
     * 身份id
     */
    @Column(name = "c_identity_id", length = 9)
    private Long identityId;

    /**
     * 人员id
     */
    @Column(name = "c_user_id", length = 9)
    private Long userId;

    /**
     * 授权的机构维度
     */
    @Column(name = "c_dimension", length = 50)
    private String dimension;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getIdentityId() {
        return identityId;
    }

    public void setIdentityId(Long identityId) {
        this.identityId = identityId;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }
}
