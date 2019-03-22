package com.gsoft.portal.system.sso.entity;

import com.gsoft.cos3.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 外部接入
 * @author plsy
 */
@Entity
@Table(name = "cos_sys_sso")
public class SsoEntity extends BaseEntity {

	private static final long serialVersionUID = 7888522877619730122L;

	/**
     * 系统名称
     */
    @Column(name = "c_system_name", length = 50)
    private String systemName;

    /**
     * 系统代码
     */
    @Column(name = "c_system_code", length = 50)
    private String systemCode;

    /**
     * 访问地址
     */
    @Column(name = "c_access_url", length = 500)
    private String accessUrl;


    /**
     * 认证适配器
     */
    @Column(name = "c_sso_adapter", length = 50)
    private String ssoAdapter;

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public String getSsoAdapter() {
        return ssoAdapter;
    }

    public void setSsoAdapter(String ssoAdapter) {
        this.ssoAdapter = ssoAdapter;
    }
}
