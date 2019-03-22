package com.gsoft.portal.system.personnel.service;

import java.util.List;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ResponseMessageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.portal.system.personnel.dto.PasswordInfoDto;
import com.gsoft.portal.system.personnel.dto.PersonnelDto;
import com.gsoft.portal.system.personnel.dto.RolePersonnelDto;


/**
 * 人员管理
 *
 * @author helx
 * @date 2017年8月7日 下午2:41:41
 */
public interface PersonnelService {

	/**
	 * 验证登录名在一个行政区划下是否唯一
	 * @param id
	 * @param loginName
	 * @return 存在就返回true,不存在返回false,注意action那层取反
	 */
	Boolean isExitByLoginName(Long id, String loginName);

	/**
	 * 验证手机号唯一
	 * @param id
	 * @param phone
	 * @return
	 */
	Boolean isExitByPhone(Long id, String phone);
	
	/**
	 * 得到单条对象
	 * @param id
	 * @return
	 */
	PersonnelDto getOneById(Long id);
	
	/**
	 * 保存对象
	 * @param personnelDto
	 * @return
	 */
	PersonnelDto save(PersonnelDto personnelDto);

	/**
	 * 删除人员，另外还要删除人员与角色的关系
	 * @param id
	 * @return
	 */
	void deleteById(Long id);

	/**
	 * 根据主键修改可用状态
	 * @param id
	 * @return
	 */
	void updateStatus(Long id, Boolean status);

	/**
	 * 根据手机号得到单个对象
	 * @param phone
	 * @return
	 */
	PersonnelDto getOneByPhone(String phone);

	/**
	 * 根据登录名和行政区划Code得到单个对象
	 * @param loginName
	 * @return
	 */
	PersonnelDto getOneByloginName(String loginName);
	
	
	/**
	 * 人员关联角色
	 * @param list
	 */
	void connectRolePersonnel(List<RolePersonnelDto> list);
	
	/**
	 * 角色关联人员--查询某角色已经授权的人员
	 * @param roleId
	 */
	List<PersonnelDto> getHasConnectPerson(String orgCode, Boolean isCascade, Long roleId, String personnelId);
	
	/**
	 * 移动人员
	 * @param ids
	 * @param orgId
	 */
	void movePersonnel(String ids, Long orgId);
	
	/**
	 * 修改密码
	 * @param mobile
	 * @param password1
	 */
	void resetPassword(String mobile, String password1);

	/**
	 * 修改密码
	 * @param passwordInfoDto 密码实体
	 * @return
	 */
	ReturnDto modifyPassword(PasswordInfoDto passwordInfoDto);

	/**
	 * 绑定手机号
	 * @param id 人员id
 	 * @param mobile 手机号
	 * @return
	 */
	ResponseMessageDto bindPhone(String id, String mobile);

	/**
	 * 根据机构代码查询用户list
	 * @param orgCode
	 * @param isCascade
	 * @param page
	 * @param size
	 * @param sortProp
	 * @param order
	 * @return
	 */
	PageDto getListByOrgCode(String dimension, String orgCode, Boolean isCascade, String search, Integer page, Integer size, String sortProp,
			String order, String personnelId);

	/**
	 * 用户导入
	 * @param dto
	 * @return
	 */
	PersonnelDto importPersonnel(PersonnelDto dto);

	/**
	 * 查询未授权的人员
	 * @param orgCode
	 * @param isCascade
	 * @return
	 */
	List<PersonnelDto> getHasNoConnectPerson(String orgCode, Boolean isCascade, String personnelId);

	/**
	 * 模糊查询用户
	 * @param condition
	 * @param positionId
     * @return
	 */
    List<PersonnelDto> vagueQueryPerson(String condition, String positionId);

	/**
	 * 批量删除人员信息
	 * @param ids
	 */
	void batchDelPersonnel(String ids);

	/**
	 * 修改用户头像信息
	 * @param id
	 * @param referenceId
	 */
	void modifyHeadImg(Long id, String referenceId);

	/**
	 * 根据roleCode得到人员
	 * @param code
	 * @return
	 */
    List<PersonnelDto> getPersonsByRoleCode(String code);


    List<PersonnelDto> getAllPersonByDimension(String dimension);
    
    /**
     * 获取机构下的用户
     * @param orgId
     * @return
     */
    List<PersonnelDto> getPersonsByOrgId(Long orgId);

    void connectRolePersonnel(String personalId, String roleIds);

	/**
	 * 根据机构ID和用户首字母查询用户信息
	 * @param orgId
	 * @param selLetters
	 * @return
	 */
	List<PersonnelDto> getPersonsByOrgIdAndLetters(Long orgId, String selLetters);

    List<PersonnelDto> getCascadePersonnelByOrg(Long orgId);

	/**
	 * 模糊查询用户信息
	 * @param search
	 * @return
	 */
//	List<PersonnelDto> vagueQueryPersonList(String search);

	/**
	 * 映射所有的人
	 * @return
	 */
	List<PersonnelDto> getAllPerson();

	//----------------------选人
	/**
	 * 根据纬度得到所有人员，并且里面带机构,纬度
	 * @param dimension 纬度
	 */
	List<PersonnelDto> getDimensionPersonsByIds(String ids,String dimension);

	/**
	 * 根据多个id得到对象，里面带纬度，机构，人员只有id,name,如果需要更多属性的，不要用此方法
	 * @param ids
	 * @return
	 */
	List<PersonnelDto> getDimensionPersonsByIds(String ids);

	/**
	 * 根据多个机构id得到人员
	 * @param orgIds
	 * @return
	 */
	List<PersonnelDto> getDimensionPersonsByOrgIds(String orgIds);

	/**
	 * 根据名字模糊匹配
	 * @param personName 人员名称
	 * @return
	 */
	List<PersonnelDto> getDimensionPersonsByName(String personName);

	/**
	 * 根据多个人员id得到多个人员对象
	 * @param ids 人员id,多个用逗号分割
	 * @return
	 */
	List<PersonnelDto> getPersonsByIds(String ids);

	/**
	 * 根据角色查询授权人员
	 * @param roleIds
	 * @return
	 */
	List<PersonnelDto> getPersonsByRoleIds(String roleIds);

	/**
	 * 根据纬度，机构，是否级联得到用户信息
	 * @param dimension  纬度
	 * @param orgId      机构id
	 * @param isCascade    是否级联
	 * @return
	 */
	List<PersonnelDto> getAllPersons(String dimension,Long orgId,Boolean isCascade);

	/**
	 * 重置密码为系统默认
	 * @param id
	 * @return
	 */
	ReturnDto resetPassword(Long id);

}
