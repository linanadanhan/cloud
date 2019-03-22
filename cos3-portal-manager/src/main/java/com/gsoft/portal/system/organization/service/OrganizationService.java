package com.gsoft.portal.system.organization.service;

import java.util.List;
import java.util.Map;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ResponseMessageDto;
import com.gsoft.cos3.tree.TreeNode;
import com.gsoft.portal.system.organization.dto.IdentityInfoDto;
import com.gsoft.portal.system.organization.dto.OrgGrantDto;
import com.gsoft.portal.system.organization.dto.OrganizationDto;

/**
 * 组织机构Service接口
 *
 * @author SN
 */
public interface OrganizationService {

    /**
     * 根据Id获取组织机构信息
     *
     * @param id
     * @return
     */
    OrganizationDto getOneById(Long id);

    /**
     * 判断组织机构代码是否存在
     *
     * @param id
     * @param code
     * @param dimension
     * @return
     */
    Boolean isExitCode(Long id, String code, String dimension);

    /**
     * 保存组织机构
     *
     * @param organizationDto
     * @return
     */
    OrganizationDto save(OrganizationDto organizationDto);

    /**
     * 删除组织机构
     *
     * @param id
     * @param code
     */
    Boolean deleteById(Long id, String code);

    /**
     * 组织机构导入
     *
     * @param dto
     * @return
     */
    OrganizationDto orgImport(OrganizationDto dto);

    /**
     * 通过组织机构代码查询下属组织机构
     *
     * @param code
     * @param isCascade
     * @param id
     * @param page
     * @param size
     * @return
     */
    PageDto getListByCode(String code, Boolean isCascade, Long id, Integer page, Integer size);


    /**
     * 获取组织机构tree
     *
     * @return
     */
    List<OrganizationDto> getTreeList(String personnelId, String dimension);
    
    /**
     * 根据机构类型获取组织机构tree
     * orgType 机构类型
     * @return
     */
    List<OrganizationDto> getTreeList(String personnelId, String dimension,String orgType);

    List<OrganizationDto> getTreeList(String personnelId);

    /**
     * 根据选择维度得到所有授权机构树
     *
     * @param dimension
     * @return
     */
    List<OrganizationDto> getTreeListByDimension(String dimension);

    /**
     * 通过组织机构代码查询下属组织关联的身份信息
     *
     * @param code
     * @param isCascade
     * @param id
     * @param page
     * @param size
     * @return
     */
    PageDto getIdentityInfoListByOrgCode(String code, Boolean isCascade, Long id, Integer page, Integer size);

    /**
     * 保存身份与机构之间的授权关系
     *
     * @param identityId
     * @param orgIds
     * @param dimension
     * @return
     */
    ResponseMessageDto saveDataGrant(Long identityId, String orgIds, String dimension);

    /**
     * 根据身份查找关联机构的ids
     *
     * @param identityId
     * @param dimension
     * @return
     */
    List<Long> getOrgIdsByIdentity(Long identityId, String dimension);

    /**
     * 通过组织机构查询关联的身份信息
     *
     * @param orgId
     * @return
     */
    List<IdentityInfoDto> getIdentityListByOrg(Long orgId);


    /**
     * 通过ParentId得到Cascade
     *
     * @param parentId
     * @return
     */
    String getCascadeByParentId(Long parentId);

    /**
     * 新增机构时添加权限
     *
     * @param orgId
     * @param personnelId
     * @return
     */
    ResponseMessageDto addGrant(String orgId, String dimension, String personnelId);

    /**
     * 查询已经授权的身份
     *
     * @param orgId
     * @param isCascade
     * @param orgCode
     * @param dimension
     * @return
     */
    List<IdentityInfoDto> getHasConnectIdentity(String orgId, Boolean isCascade, String orgCode, String dimension);

    /**
     * 查询未授权的身份
     *
     * @param orgId
     * @param isCascade
     * @param orgCode
     * @param dimension
     * @return
     */
    List<IdentityInfoDto> getHasNoConnectIdentity(String orgId, Boolean isCascade, String orgCode, String dimension);

    /**
     * 保存机构授权信息
     *
     * @param list
     */
    void saveOrgIdentityInfo(List<OrgGrantDto> list);

    /**
     * 获取人员所属机构信息
     *
     * @param userId
     * @param dimension
     * @return
     */
    Map<String, Object> getOrgInfoByUserId(Long userId, String dimension);

    List<OrganizationDto> getOrgDtoByUserId(Long userId, String dimension);

    List<OrganizationDto> getOrgDtoByUserId(Long personnelId);

	/**
	 * 根据搜索人员拼音获取机构信息
	 * @param selLetters
	 * @return
	 */
	List<Map<String, Object>> getOrgInfoByLetter(String selLetters,String dimension);

    List<Map<String, Object>> getAllOrgListByRelPerson(String search);

    /**
     * 选人左侧的机构树
     * @param dimension
     * @param orgIds
     * @param isCascade
     * @return
     */
    List<TreeNode> getOrgTreeBySelPerson(String dimension, String orgIds, Boolean isCascade);

    /**
     * 根据机构id得到所有上级机构的名称，按-拼接
     * @param id  机构id
     * @param re  连接符,默认按-
     * @return
     */
    String getCascadeNameById(Long id,String re);


    /**
     * 查询所有机构
     * @return
     */
    List<OrganizationDto> getAllOrgList();

    /**
     * 根据机构名模糊查询机构
     * @param departmentName
     * @return
     */
    List<Map<String, Object>> getOrgInfoByNameAndDimension(String departmentName,String dimension);
    
    /**
     * 根据维度、机构类型获取机构（不包含”根目录“）
     * @param personnelId
     * @param dimension
     * @param orgType
     * @return
     */
    public List<OrganizationDto> getList(String personnelId, String dimension,String orgType);
}
