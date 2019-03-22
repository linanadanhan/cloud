package com.gsoft.portal.system.basicdata.entity;


import com.gsoft.cos3.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 数据字典项
 *
 * @author plsy
 * @date 2017年8月11日 上午10:51:46
 */
@Entity
@Table(name = "cos_sys_dictionary_item")
public class DictionaryItem extends BaseEntity {

    private static final long serialVersionUID = -8096350236920238974L;
    /**
     * 所属字典ID
     */
    @Column(name = "c_dic_id", length = 9, nullable = false)
    private Long dicId;

    /**
     * 字典标识
     */
    @Column(name = "c_dic_key", length = 50, nullable = false)
    private String dicKey;

    /**
     * 字典项值
     */
    @Column(name = "c_value", length = 500, nullable = false)
    private String value;

    /**
     * 字典项文本
     */
    @Column(name = "c_text", length = 200)
    private String text;

    /**
     * 备注
     */
    @Column(name = "c_remark", length = 500)
    private String remark;

    /**
     * 排序
     */
    @Column(name = "c_sortno", length = 2)
    private Integer sortNo;

    /**
     * 可用状态 1可用，0停用
     */
    @Column(name = "c_status", length = 1, nullable = false)
    private Integer status;

    public Long getDicId() {
        return dicId;
    }

    public void setDicId(Long dicId) {
        this.dicId = dicId;
    }

    public String getDicKey() {
        return dicKey;
    }

    public void setDicKey(String dicKey) {
        this.dicKey = dicKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
