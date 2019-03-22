package com.gsoft.portal.system.organization.entity;

import com.gsoft.cos3.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 行政区划表
 *
 * @author yzz
 * @date 2017年8月8日 上午10:23:56
 */
@Entity
@Table(name = "cos_sys_administrative_area")
public class AdministrativeAreaEntity extends BaseEntity {

    private static final long serialVersionUID = -5494518685634330767L;

    /**
     * 姓名
     */
    @Column(name = "c_name", length = 20)
    private String name;

    /**
     * 编码
     */
    @Column(name = "c_code", length = 30)
    private String code;

    /**
     * 级别（1=省，2=地市，3=区县，4=乡镇，5=村）
     */
    @Column(name = "c_level")
    private Integer level;

    /**
     * 级联关系
     */
    @Column(name = "c_cascade", length = 100)
    private String cascade;

    /**
     * 父Code
     */
    @Column(name = "c_parent_code", length = 100)
    private Long parentCode;

    /**
     * 行政区划版本年度标识
     * 用于区分爬取数据与之前数据
     */
    @Column(name = "c_year", length = 20)
    private String year;

    public AdministrativeAreaEntity(String name, String code, Integer level, String cascade, Long parentCode, String year) {
        this.name = name;
        this.code = code;
        this.level = level;
        this.cascade = cascade;
        this.parentCode = parentCode;
        this.year = year;
    }

    public AdministrativeAreaEntity() {
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Long getParentCode() {
        return parentCode;
    }

    public void setParentCode(Long parentCode) {
        this.parentCode = parentCode;
    }

    public String getCascade() {
        return cascade;
    }

    public void setCascade(String cascade) {
        this.cascade = cascade;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
