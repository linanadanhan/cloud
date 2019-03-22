package com.gsoft.portal.component.compmgr.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * 业务组件widget配置信息表
 * @author SN
 *
 */
@Entity
@Table(name = "cos_yw_component_instance")
public class BusinessCompConfEntity  extends BaseEntity {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = -5494518685634330767L;
	
	/**
	 * 组件ID
	 */
	@Column(name = "c_comp_id", length = 9)
	private Long compId;	
	
	/**
	 * 实例json数据
	 */
	@Lob
	@Column(name = "c_json")
	private String json;

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public Long getCompId() {
		return compId;
	}

	public void setCompId(Long compId) {
		this.compId = compId;
	}
}
