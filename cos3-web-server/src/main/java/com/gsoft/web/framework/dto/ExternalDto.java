package com.gsoft.web.framework.dto;

import com.gsoft.cos3.dto.BaseDto;

/**
 * 外部接入api
 *
 * @author plsy
 */
public class ExternalDto extends BaseDto {


    private static final long serialVersionUID = 7888522877619730122L;

    /**
     * 系统代码
     */
    private String systemCode;

    /**
     * 服务名
     */
    private String serverName;

    /**
     * uri
     */
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
