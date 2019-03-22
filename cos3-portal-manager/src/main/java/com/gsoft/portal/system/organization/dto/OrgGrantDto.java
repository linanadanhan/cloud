package com.gsoft.portal.system.organization.dto;

/**
 * 机构授权表
 *
 * @author plsy
 */
public class OrgGrantDto implements java.io.Serializable {

    private static final long serialVersionUID = -5494518685634330767L;

    /**
     * 主键Id
     */
    private Long id;

    /**
     * 机构id
     */
    private Long orgId;

    /**
     * 身份id
     */
    private Long identityId;

    /**
     * 人员id
     */
    private Long userId;

    /**
     * 授权的机构维度
     */
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

    public Long getIdentityId() {
        return identityId;
    }

    public void setIdentityId(Long identityId) {
        this.identityId = identityId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }
}
