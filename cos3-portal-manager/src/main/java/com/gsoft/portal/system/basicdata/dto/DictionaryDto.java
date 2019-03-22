package com.gsoft.portal.system.basicdata.dto;


import com.gsoft.cos3.dto.BaseDto;

/**
 * 数据字典
 *
 * @author plsy
 * @date 2017年8月11日 上午10:48:46
 */
public class DictionaryDto extends BaseDto {

    private static final long serialVersionUID = -8096350236920238974L;

    /**
     * 字典名称
     */
    private String name;

    /**
     * 字典标识
     */
    private String key;

    /**
     * 字典分类
     */
    private String type;

    /**
     * 备注
     */
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
