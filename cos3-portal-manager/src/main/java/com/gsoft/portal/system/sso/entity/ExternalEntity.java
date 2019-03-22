package com.gsoft.portal.system.sso.entity;

import com.gsoft.cos3.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 外部接入api关系
 * @author plsy
 */
@Entity
@Table(name = "cos_sys_sso_external")
public class ExternalEntity extends BaseEntity {

    private static final long serialVersionUID = 7888522877619730122L;

    /**
     * 系统代码
     */
    @Column(name = "c_system_code", length = 50)
    private String systemCode;

    /**
     * 服务名
     */
    @Column(name = "c_server_name", length = 50)
    private String serverName;

    /**
     * uri
     */
    @Column(name = "c_controller_path", length = 200)
    private String controllerPath;

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getControllerPath() {
        return controllerPath;
    }

    public void setControllerPath(String controllerPath) {
        this.controllerPath = controllerPath;
    }
}
