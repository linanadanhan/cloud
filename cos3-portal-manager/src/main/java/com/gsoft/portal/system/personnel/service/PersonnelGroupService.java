package com.gsoft.portal.system.personnel.service;

import com.gsoft.portal.system.personnel.dto.PersonnelGroupDetailDto;
import com.gsoft.portal.system.personnel.dto.PersonnelGroupDto;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 人员群组Service接口类
 * @author chenxx
 *
 */
public interface PersonnelGroupService {

	/**
	 * 获取用户自定义群组集合
	 * @param personId
	 * @param groupType 分组类型：1系统，2个人，为空或0就查所有
	 * @return
	 */
	List<PersonnelGroupDto> getPersonGroupOpts(Long personId,Integer groupType);

	/**
	 * 保存用户自定义分组信息
	 * @param personId
	 * @param map
	 * @return
	 */
	Long savePersonGroup(Long personId, Map<String, Object> map) throws IOException;

	/**
	 * 根据groupId获取人员明细信息
	 * @param groupIds  多个分组
	 * @return
	 */
	List<PersonnelGroupDetailDto> getPersonDetailByGroupIds(String groupIds);

	/**
	 * 删除用户自定义群组信息
	 * @param groupId
	 */
	void delPersonGroup(Long groupId);

	//-===========================系统群组
	/**
	 * 保存
	 * @param personnelGroupDto
	 * @return
	 */
	Long saveSysGroup(PersonnelGroupDto personnelGroupDto) throws IOException;

	/**
	 * 根据ID得到对象
	 * @param id
	 * @return
	 */
	PersonnelGroupDto getOneById(Long id);


	/**
	 * 查询系统群组列表
	 * @param name
	 * @return
	 */
	List<PersonnelGroupDto> getSysGroupList(String name);


}
