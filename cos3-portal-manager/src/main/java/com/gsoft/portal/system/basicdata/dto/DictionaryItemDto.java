package com.gsoft.portal.system.basicdata.dto;


import com.gsoft.cos3.dto.BaseDto;

/**
 * 数据字典项
 *
 * @author plsy
 * @date 2017年8月11日 上午10:51:46
 */
public class DictionaryItemDto extends BaseDto {

    private static final long serialVersionUID = -8096350236920238974L;
    /**
     * 所属字典ID
     */
    private Long dicId;

    /**
     * 字典标识
     */
    private String dicKey;

    /**
     * 字典项值
     */
    private String value;

    /**
     * 字典项文本
     */
    private String text;

    /**
     * 备注
     */
    private String remark;

    /**
     * 排序
     */
    private Integer sortNo;

    /**
     * 可用状态 1可用，0停用
     */
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
