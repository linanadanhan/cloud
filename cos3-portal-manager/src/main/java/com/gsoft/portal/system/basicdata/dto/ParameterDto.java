package com.gsoft.portal.system.basicdata.dto;


import com.gsoft.cos3.dto.BaseDto;

/**
 * 参数管理数据传输对象
 *
 * @author plsy
 * @Date 2017年8月11日 下午4:26:15
 */
public class ParameterDto extends BaseDto {

    private static final long serialVersionUID = 6780376860107266053L;

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数键
     */
    private String key;

    /**
     * 参数值
     */
    private String value;

    /**
     * 参数分类
     */
    private String type;

    private Boolean status = true;

    /**
     * 备注
     */
    private String remark;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
