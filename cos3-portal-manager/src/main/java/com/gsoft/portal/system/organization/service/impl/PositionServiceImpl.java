package com.gsoft.portal.system.organization.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.cos3.util.JPAUtil;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.system.basicdata.dto.DictionaryItemDto;
import com.gsoft.portal.system.basicdata.service.DictionaryService;
import com.gsoft.portal.system.organization.dto.OrganizationDto;
import com.gsoft.portal.system.organization.dto.PositionDto;
import com.gsoft.portal.system.organization.dto.PositionManageDto;
import com.gsoft.portal.system.organization.entity.IdentityInfoEntity;
import com.gsoft.portal.system.organization.entity.OrgGrantEntity;
import com.gsoft.portal.system.organization.entity.OrganizationEntity;
import com.gsoft.portal.system.organization.entity.PositionEntity;
import com.gsoft.portal.system.organization.entity.PositionManageEntity;
import com.gsoft.portal.system.organization.persistence.IdentityInfoPersistence;
import com.gsoft.portal.system.organization.persistence.OrgGrantPersistence;
import com.gsoft.portal.system.organization.persistence.OrganizationPersistence;
import com.gsoft.portal.system.organization.persistence.PositionManagePersistence;
import com.gsoft.portal.system.organization.persistence.PositionPersistence;
import com.gsoft.portal.system.organization.service.PositionService;
import com.gsoft.portal.system.personnel.entity.PersonnelEntity;
import com.gsoft.portal.system.personnel.persistence.PersonnelPersistence;

/**
 * 职位
 *
 * @author plsy
 */
@Service
public class PositionServiceImpl implements PositionService {

    @Autowired
    OrgGrantPersistence orgGrantPersistence;

    @Autowired
    OrganizationPersistence organizationPersistence;

    @Autowired
    IdentityInfoPersistence identityInfoPersistence;

    @Autowired
    PositionPersistence positionPersistence;

    @Autowired
    DictionaryService dictionaryService;

    @Autowired
    PositionManagePersistence positionManagePersistence;

    @Autowired
    PersonnelPersistence personnelPersistence;
    
    @Autowired
    BaseDao baseDao;

    @Override
    public List<OrganizationDto> getTreeListByAuthorization(String personnelId) {
        List<Long> longs = orgGrantPersistence.findByUserId(MathUtils.numObj2Long(personnelId)).stream()
                .map(OrgGrantEntity::getOrgId).distinct().collect(Collectors.toList());
        List<OrganizationEntity> collect = longs.stream().map(aLong -> organizationPersistence.findOne(aLong)).collect(Collectors.toList());
        return BeanUtils.convert(collect, OrganizationDto.class);
    }

    @Override
    public PageDto getPositionPageByOrgId(Long orgId, Integer page, Integer size) {
        Pageable pageable = JPAUtil.createPageRequest(page, size, "sortNo", "asc");
        Page<PositionEntity> dtoList = positionPersistence.findPageByOrgid(orgId, pageable);
        return new PageDto(dtoList);
    }

    @Override
    public List<PositionDto> getPositionListByOrgId(Long orgId) {
        List<PositionEntity> list = positionPersistence.findListByOrgId(orgId);
        return BeanUtils.convert(list, PositionDto.class);
    }


    @Override
    public void orgConnectPost(Long orgId, String postIds) {
        //如果新增数据不存在,但是数据库中存在此职位,删除
        List<PositionEntity> list = positionPersistence.findListByOrgId(orgId);
        list.stream().forEach(positionEntity -> {
            if (!postIds.contains(positionEntity.getPostId().toString())) {
                positionPersistence.deleteByPostId(positionEntity.getPostId());
            }
        });
        if (postIds.contains(",")) {
            String[] split = postIds.split(",");
            for (String id : split) {
                savePosition(orgId, id);
            }
        } else {
            savePosition(orgId, postIds);
        }
    }

    private void savePosition(Long orgId, String postValue) {
        //如果存在就不增加
        PositionEntity entity = positionPersistence.findByOrgIdAndPostId(orgId, postValue);
        if (Assert.isEmpty(entity)) {
            DictionaryItemDto post = dictionaryService.getDictionaryItemValue("org_post", postValue);
            PositionEntity positionEntity = new PositionEntity();
            positionEntity.setOrgId(orgId);
            positionEntity.setPostId(postValue);
            positionEntity.setPostName(post.getText());
            positionPersistence.save(positionEntity);
        }
    }

    @Override
    public List<DictionaryItemDto> getPostByOrg(Long orgId) {
        List<DictionaryItemDto> list = positionPersistence.findListByOrgId(orgId).stream()
                .map(positionEntity -> dictionaryService.getDictionaryItemValue("org_post", positionEntity.getPostId()))
                .collect(Collectors.toList());
        return BeanUtils.convert(list, DictionaryItemDto.class);
    }

    @Override
    public void modifyPositionSort(List<PositionEntity> list) {
        for (PositionEntity positionEntity : list) {
            positionPersistence.save(positionEntity);
        }
    }

    @Override
    public PageDto getIdentityInfoByPosition(Long positionId, Integer page, Integer size) {
        Pageable pageable = JPAUtil.createPageRequest(page, size, "sortNo", "asc");
        Page<IdentityInfoEntity> dtoList = identityInfoPersistence.findByPositionId(positionId, pageable);
        return new PageDto(dtoList);
    }

    @Override
    public List<PositionDto> getPositionListByOrg(Long orgId) {
        return BeanUtils.convert(positionPersistence.findListByOrgId(orgId), PositionDto.class);
    }

    @Override
    public void savePositionManage(PositionManageDto positionManageDto) {
        PositionManageEntity positionManageEntity = BeanUtils.convert(positionManageDto, PositionManageEntity.class);
        positionManagePersistence.deleteByPositionId(positionManageEntity.getPositionId());
        positionManagePersistence.save(positionManageEntity);
    }

    @Override
    public PositionManageDto getPositionManage(Long positionId) {
        List<PositionManageEntity> positionManage = positionManagePersistence.getPositionManage(positionId);
        if (positionManage.size() > 0) {
            return BeanUtils.convert(positionManage.get(0), PositionManageDto.class);
        }
        return new PositionManageDto();
    }

    @Override
    public void positionConnectPerson(Long positionId, String personIds) {
    	if(Assert.isNotEmpty(personIds)) {
    		if (personIds.contains(",")) {
                String[] split = personIds.split(",");
                for (String personId : split) {
                    saveIdentityInfo(positionId, MathUtils.numObj2Long(personId));
                }
            } else {
                saveIdentityInfo(positionId, MathUtils.numObj2Long(personIds));
            }
    	}
    }

    @Override
    public void personConnectPosition(Long personId, String positionIds) {
    	identityInfoPersistence.deleteByPersonId(personId);
    	if(Assert.isNotEmpty(positionIds)) {
    		if (positionIds.contains(",")) {
                String[] split = positionIds.split(",");
                for (String positionId : split) {
                    saveIdentityInfo(MathUtils.numObj2Long(positionId), personId);
                }
            } else {
                saveIdentityInfo(MathUtils.numObj2Long(positionIds), personId);
            }
    	}
    }

    private void saveIdentityInfo(Long positionId, Long personId) {
        identityInfoPersistence.deleteByPositionAndPerson(positionId, personId);
        IdentityInfoEntity identityInfoEntity = new IdentityInfoEntity();
        PersonnelEntity one = personnelPersistence.findOne(personId);
        identityInfoEntity.setUserId(personId);
        identityInfoEntity.setLoginName(one.getLoginName());
        identityInfoEntity.setMobilePhone(one.getMobilePhone());
        identityInfoEntity.setName(one.getName());
        identityInfoEntity.setSortNo(one.getSortNo());
        PositionEntity positionEntity = positionPersistence.findOne(positionId);
        identityInfoEntity.setPositionId(positionId);
        Long orgId = positionEntity.getOrgId();
        OrganizationEntity organizationEntity = organizationPersistence.findOne(orgId);
        identityInfoEntity.setPositionName(organizationEntity.getName() + "/" + positionEntity.getPostName());
        identityInfoPersistence.save(identityInfoEntity);
    }

    @Override
    public void deleteIdentityInfo(String identityIds) {
        if (identityIds.contains(",")) {
            String[] split = identityIds.split(",");
            for (String personId : split) {
                orgGrantPersistence.deleteByIdentityId(Long.valueOf(personId));
                identityInfoPersistence.delete(Long.valueOf(personId));
            }
        } else {
            orgGrantPersistence.deleteByIdentityId(Long.valueOf(identityIds));
            identityInfoPersistence.delete(Long.valueOf(identityIds));
        }

    }

    @Override
    public List<IdentityInfoEntity> getIdentityInfoByPersonAndOrg(Long personalId, Long orgId) {
//      List<Long> positionIds = positionPersistence.findListByOrgId(orgId).stream().map(BaseEntity::getId).collect(Collectors.toList());
//      return positionIds.stream()
//              .map(aLong -> identityInfoPersistence.findByUserAndPostion(personalId, aLong))
//              .filter(Objects::nonNull)
//              .collect(Collectors.toList());
    	return identityInfoPersistence.findByUserId(personalId);
    }

	@Override
	public List<PositionDto> getAllPostOpts() {
		List<PositionDto> rtnList = null;
		
		List<Map<String, Object>> qryList = baseDao.query("SELECT DISTINCT c_post_id, c_post_name FROM cos_sys_position");
		if (!Assert.isEmpty(qryList) && qryList.size() > 0) {
			rtnList = new ArrayList<PositionDto>();
			PositionDto dto = null;
			for (Map<String, Object> tmpMap : qryList) {
				dto = new PositionDto();
				dto.setPostId(MathUtils.stringObj(tmpMap.get("c_post_id")));
				dto.setPostName(MathUtils.stringObj(tmpMap.get("c_post_name")));
				rtnList.add(dto);
			}
		}
		return rtnList;
	}

	@Override
	public List<PositionDto> getPositionListByPostId(String postId) {
		List<PositionDto> rtnList = null;
		List<Map<String, Object>> qryList = baseDao.query("SELECT DISTINCT c_org_id FROM cos_sys_position WHERE c_post_id = ?", postId);
		if (!Assert.isEmpty(qryList) && qryList.size() > 0) {
			rtnList = new ArrayList<PositionDto>();
			PositionDto dto = null;
			for (Map<String, Object> tmpMap : qryList) {
				dto = new PositionDto();
				dto.setOrgId(MathUtils.numObj2Long(tmpMap.get("c_org_id")));
				rtnList.add(dto);
			}
		}
		return rtnList;
	}
}
