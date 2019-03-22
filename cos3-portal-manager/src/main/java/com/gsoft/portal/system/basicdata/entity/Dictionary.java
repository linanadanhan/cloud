package com.gsoft.portal.system.basicdata.entity;


import com.gsoft.cos3.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 数据字典
 *
 * @author plsy
 * @date 2017年8月11日 上午10:48:46
 */
@Entity
@Table(name = "cos_sys_dictionary")
public class Dictionary extends BaseEntity {

    private static final long serialVersionUID = -8096350236920238974L;

    /**
     * 字典名称
     */
    @Column(name = "c_name", length = 100, nullable = false)
    private String name;

    /**
     * 字典标识
     */
    @Column(name = "c_key", length = 50, nullable = false)
    private String key;

    /**
     * 字典分类
     */
    @Column(name = "c_type", length = 200)
    private String type;

    /**
     * 备注
     */
    @Column(name = "C_REMARK", length = 500)
    private String remark;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
