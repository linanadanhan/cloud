package com.gsoft.portal.system.basicdata.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 参数实体
 *
 * @author plsy
 * @Date 2017年8月11日 下午4:21:52
 */
@Entity
@Table(name = "cos_sys_param_configure")
public class Parameter extends BaseEntity {

    private static final long serialVersionUID = 217841174322053391L;

    /**
     * 参数名称
     */
    @Column(name = "c_name", length = 100, nullable = false)
    private String name;

    /**
     * 参数键
     */
    @Column(name = "c_key", length = 50, nullable = false)
    private String key;

    /**
     * 参数值
     */
    @Lob
    @Column(name = "c_value")
    private String value;

    /**
     * 参数分类
     */
    @Column(name = "c_type", length = 200)
    private String type;

    /**
     * 角色状态： true：启用，false：停用
     * 默认为true
     */
    @Column(name = "C_STATUS")
    private Boolean status = true;

    /**
     * 所属分类科目级联ID
     */
    @Column(name = "c_remark", length = 500)
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
