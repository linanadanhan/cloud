package com.gsoft.portal.system.personnel.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 选人辅助dto
 * @author helx
 *
 */
public class PersonnelGroupSelDto implements java.io.Serializable {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = -364249724627372468L;

	//机构id
	private Long orgId;

	//机构的层级名称: 如：中科天翔-研发二部
	private String label;

	private Boolean isIndeterminate=false;

	//是否展开
	private Boolean expand=true;

	//是否勾选所有
	private Boolean checkAll=false;

	//勾选的人(多选时)
	private List<String> checkedUsers=new ArrayList<>();

	//用户jsonList
	private List<PersonnelDto> userList;



	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Boolean getIndeterminate() {
		return isIndeterminate;
	}

	public void setIndeterminate(Boolean indeterminate) {
		isIndeterminate = indeterminate;
	}

	public Boolean getExpand() {
		return expand;
	}

	public void setExpand(Boolean expand) {
		this.expand = expand;
	}

	public Boolean getCheckAll() {
		return checkAll;
	}

	public void setCheckAll(Boolean checkAll) {
		this.checkAll = checkAll;
	}

	public List<String> getCheckedUsers() {
		return checkedUsers;
	}

	public void setCheckedUsers(List<String> checkedUsers) {
		this.checkedUsers = checkedUsers;
	}

	public List<PersonnelDto> getUserList() {
		return userList;
	}

	public void setUserList(List<PersonnelDto> userList) {
		this.userList = userList;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}


}
