package com.gsoft.portal.system.sso.dto;

import com.gsoft.cos3.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 外部接入
 * @author plsy
 */
@ApiModel("外部接入dto")
public class SsoDto extends BaseDto {

	private static final long serialVersionUID = -4394085533587911989L;

	/**
     * 系统名称
     */
    @ApiModelProperty("系统名称")
    private String systemName;

    /**
     * 系统代码
     */
    @ApiModelProperty("系统代码")
    private String systemCode;

    /**
     * 访问地址
     */
    @ApiModelProperty("访问地址")
    private String accessUrl;


    /**
     * 认证适配器
     */
    @ApiModelProperty("认证适配器")
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
