package com.gsoft.portal.system.personnel.dto;

import com.gsoft.cos3.dto.BaseDto;


/**
 * 权限表
 *
 * @author helx
 * @date 2017年8月7日 上午11:30:02
 */
public class PermissionDto extends BaseDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 权限名称
	 */
	private String name;
	
	/**
	 * 权限代码
	 */
	private String code;
	
	/**
	 * 权限描述
	 */
	private String describe;
	
	/**
	 * 分类名,直接输入，不用数据字典维护
	 */
	private String type;
	
	
	/**
	 * 包含资源url
	 * 按/user/add/**规则写,多个用逗号分隔
	 */
	private String includeResourceRul; 
	
	/**
	 * 排斥资源url
	 * 按/user/add/**规则写,多个用逗号分隔
	 */
	private String excludeResourceRul;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIncludeResourceRul() {
		return includeResourceRul;
	}

	public void setIncludeResourceRul(String includeResourceRul) {
		this.includeResourceRul = includeResourceRul;
	}

	public String getExcludeResourceRul() {
		return excludeResourceRul;
	}

	public void setExcludeResourceRul(String excludeResourceRul) {
		this.excludeResourceRul = excludeResourceRul;
	}
	

}
