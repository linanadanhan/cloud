package com.gsoft.portal.system.personnel.dto;

import com.gsoft.cos3.dto.BaseDto;


/**
 * 角色实体
 *
 * @author helx
 * @date 2017年8月7日 上午10:44:57
 */
public class RoleDto extends BaseDto {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色代码
     */
    private String code;

    /**
     * 角色描述
     */
    private String describe;

    /**
     * 维度
     */
    private String dimension;
    /**
     * 角色状态： true：启用，false：停用
     * 默认为true
     */
    private Boolean status;

    /**
     * 分类名,直接输入，不用数据字典维护
     */
    private String type;

    /**
     * 所属行政区划
     */
    private String areaCode;


    /**
     * 允许转授
     */
    private Boolean isTurnGrant;

    public RoleDto() {
        super();
    }


    public RoleDto(Long id, String name, String code, Boolean isTurnGrant) {
        super();
        this.setId(id);
        this.name = name;
        this.code = code;
        this.isTurnGrant = isTurnGrant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public Boolean getIsTurnGrant() {
        return isTurnGrant;
    }

    public void setIsTurnGrant(Boolean isTurnGrant) {
        this.isTurnGrant = isTurnGrant;
    }


}
