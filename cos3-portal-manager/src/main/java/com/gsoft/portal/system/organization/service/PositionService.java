package com.gsoft.portal.system.organization.service;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.portal.system.basicdata.dto.DictionaryItemDto;
import com.gsoft.portal.system.organization.dto.OrganizationDto;
import com.gsoft.portal.system.organization.dto.PositionDto;
import com.gsoft.portal.system.organization.dto.PositionManageDto;
import com.gsoft.portal.system.organization.entity.IdentityInfoEntity;
import com.gsoft.portal.system.organization.entity.PositionEntity;

import java.util.List;

/**
 * 职位
 *
 * @author plsy
 */
public interface PositionService {

    /**
     * 根据权限得到授权的机构树
     *
     * @param personnelId
     * @return
     */
    List<OrganizationDto> getTreeListByAuthorization(String personnelId);

    /**
     * 根据机构得到职位
     *
     * @param orgId
     * @return
     */
    PageDto getPositionPageByOrgId(Long orgId, Integer page, Integer size);

    List<PositionDto> getPositionListByOrgId(Long orgId);

    /**
     * 机构关联岗位生成职位
     *
     * @param orgId
     * @param postValue
     */
    void orgConnectPost(Long orgId, String postValue);

    /**
     * 得到机构关联的岗位信息
     *
     * @param orgId
     * @return
     */
    List<DictionaryItemDto> getPostByOrg(Long orgId);

    /**
     * 修改排序号
     *
     * @param list
     */
    void modifyPositionSort(List<PositionEntity> list);

    /**
     * 根据职位得到身份信息
     *
     * @param positionId
     * @param page
     * @param size
     * @return
     */
    PageDto getIdentityInfoByPosition(Long positionId, Integer page, Integer size);

    /**
     * 根据机构得到职位
     *
     * @param orgId
     * @return
     */
    List<PositionDto> getPositionListByOrg(Long orgId);

    /**
     * 保存职位管理关系
     *
     * @param positionManageDto
     */
    void savePositionManage(PositionManageDto positionManageDto);

    /**
     * 根据职位得到职位管理关系
     *
     * @param positionId
     * @return
     */
    PositionManageDto getPositionManage(Long positionId);

    /**
     * 关联职位与人员
     *
     * @param positionId
     * @param personIds
     */
    void positionConnectPerson(Long positionId, String personIds);

    /**
     * 人员关联职位
     *
     * @param personId
     * @param positionIds
     */
    void personConnectPosition(Long personId, String positionIds);

    /**
     * 删除职位与人员的关联
     *
     * @param identityIds
     */
    void deleteIdentityInfo(String identityIds);

    List<IdentityInfoEntity> getIdentityInfoByPersonAndOrg(Long personalId, Long orgId);

	/**
	 * 查询所有职位信息
	 * @return
	 */
	List<PositionDto> getAllPostOpts();

	/**
	 * 获取岗位下的所有机构信息
	 * @param postId
	 * @return
	 */
	List<PositionDto> getPositionListByPostId(String postId);
}
