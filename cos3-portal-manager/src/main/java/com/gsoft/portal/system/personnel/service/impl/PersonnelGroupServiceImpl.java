package com.gsoft.portal.system.personnel.service.impl;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.cos3.util.JsonMapper;
import com.gsoft.cos3.util.JsonUtils;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.system.organization.entity.OrganizationEntity;
import com.gsoft.portal.system.organization.persistence.OrganizationPersistence;
import com.gsoft.portal.system.personnel.dto.PersonnelDto;
import com.gsoft.portal.system.personnel.dto.PersonnelGroupDetailDto;
import com.gsoft.portal.system.personnel.dto.PersonnelGroupDto;
import com.gsoft.portal.system.personnel.dto.PersonnelGroupSelDto;
import com.gsoft.portal.system.personnel.entity.PersonnelGroupDetailEntity;
import com.gsoft.portal.system.personnel.entity.PersonnelGroupEntity;
import com.gsoft.portal.system.personnel.persistence.PersonnelGroupDetailPersistence;
import com.gsoft.portal.system.personnel.persistence.PersonnelGroupPersistence;
import com.gsoft.portal.system.personnel.service.PersonnelGroupService;
import com.gsoft.portal.system.personnel.service.PersonnelService;

/**
 * 人员群组Service实现类
 *
 * @author chenxx
 */
@Service
public class PersonnelGroupServiceImpl implements PersonnelGroupService {

    /**
     * personnelGroupPersistence
     */
    @Resource
    private PersonnelGroupPersistence personnelGroupPersistence;

    /**
     * personnelGroupDetailPersistence
     */
    @Resource
    private PersonnelGroupDetailPersistence personnelGroupDetailPersistence;

    @Resource
    private OrganizationPersistence organizationPersistence;


    @Resource
    private PersonnelService personnelService;
    @Resource
    private BaseDao baseDao;

    @Override
    public List<PersonnelGroupDto> getPersonGroupOpts(Long personId, Integer groupType) {
        List<PersonnelGroupEntity> result = new ArrayList<>();
        if (Assert.isEmpty(groupType) || groupType == 0) {
            result = personnelGroupPersistence.findAll();
        } else if (groupType == 1) {
            result = personnelGroupPersistence.getPersonGroupOpts(); //系统
        } else if (groupType == 2) {
            result = personnelGroupPersistence.getPersonGroupOpts(personId);//个人
        }
        return BeanUtils.convert(result, PersonnelGroupDto.class);
    }

    @Override
    @Transactional
    public Long savePersonGroup(Long personId, Map<String, Object> map) throws IOException {

        String groupName = MathUtils.stringObj(map.get("groupName"));
        Integer groupType = MathUtils.numObj2Integer(map.get("groupType"));
        String userJson = MathUtils.stringObj(map.get("userJson"));
        if (Assert.isEmpty(groupType)) {
            groupType = 1; //默认系统
        }
        List<PersonnelGroupSelDto> userJsonList = JsonUtils.fromJsonList(userJson, PersonnelGroupSelDto.class);
//			JSONArray jsonArr = new JSONArray(userJson);

        Long groupId = null;
        // 判断是新增还是修改
        if (Assert.isEmpty(map.get("groupId"))) {
            PersonnelGroupEntity groupEntity = new PersonnelGroupEntity();
            groupEntity.setGroupType(groupType);
            groupEntity.setCreateBy(personId);
            groupEntity.setCreateTime(new Date());
            groupEntity.setGroupName(groupName);
            PersonnelGroupEntity rtnEntity = personnelGroupPersistence.save(groupEntity);
            groupId = rtnEntity.getId();
        } else {
            groupId = MathUtils.numObj2Long(map.get("groupId"));
        }

        // 删除原分组下的数据--没有必要查询
//			List<PersonnelGroupDetailEntity> qryEntityList = personnelGroupDetailPersistence.findGroupDetailByGroupId(groupId);
//			if (!Assert.isEmpty(qryEntityList) && qryEntityList.size() > 0) {
        personnelGroupDetailPersistence.delGroupDetailByGroupId(groupId);
//			}

        // 新增群成员信息表
        if (!Assert.isEmpty(userJsonList) && userJsonList.size() > 0) {
            List<PersonnelGroupDetailEntity> groupDetailList = new ArrayList<PersonnelGroupDetailEntity>();
            PersonnelGroupDetailEntity tmpEntity = null;
            for (PersonnelGroupSelDto ps : userJsonList) {
                tmpEntity = new PersonnelGroupDetailEntity();
                tmpEntity.setGroupId(groupId);
                tmpEntity.setOrgId(MathUtils.numObj2Long(ps.getOrgId()));
                tmpEntity.setUserIds(ps.getUserList().stream().map(p -> p.getId().toString()).collect(joining(",")));
                groupDetailList.add(tmpEntity);
            }
            personnelGroupDetailPersistence.save(groupDetailList);
        }
        return groupId;

    }

    @Override
    public List<PersonnelGroupDetailDto> getPersonDetailByGroupIds(String groupIds) {
        List<Long> idList = Arrays.stream(groupIds.split(",")).map(Long::new).collect(toList());
        List<PersonnelGroupDetailEntity> entityList = personnelGroupDetailPersistence.findGroupDetailByGroupId(idList);
        return BeanUtils.convert(entityList, PersonnelGroupDetailDto.class);
    }

    @Override
    @Transactional
    public void delPersonGroup(Long groupId) {
        //删除系统群组
        personnelGroupDetailPersistence.delGroupDetailByGroupId(groupId);
        personnelGroupPersistence.delete(groupId);
    }


    //===================================================系统群组
    @Override
    public Long saveSysGroup(PersonnelGroupDto personnelGroupDto) throws IOException {
        PersonnelGroupEntity pg = personnelGroupPersistence.save(BeanUtils.convert(personnelGroupDto, PersonnelGroupEntity.class));

        //删除当前机构（包括子机构），再重新关联
        if (Assert.isNotEmpty(personnelGroupDto.getCurrentOrgId())) {
            //查询所有子机构
            OrganizationEntity org = organizationPersistence.findOne(personnelGroupDto.getCurrentOrgId());
            List<OrganizationEntity> orgList = organizationPersistence.getTreeList(org.getCode(), "%/" + org.getCode() + "/%");
            if (Assert.isNotEmpty(orgList) && orgList.size() > 0) {
                List<Long> orgIds = orgList.stream().map(OrganizationEntity::getId).collect(toList());
                personnelGroupDetailPersistence.delGroupDetail(pg.getId(),orgIds);
            }
        }

        String jsonPersonDtoList = personnelGroupDto.getJsonPersonDtoList();
        if (Assert.isNotEmpty(jsonPersonDtoList)) {
            List<PersonnelDto> pList = JsonMapper.fromJsonList(jsonPersonDtoList, PersonnelDto.class);
            Map<Long, List<PersonnelDto>> pMap = pList.stream().collect(groupingBy(PersonnelDto::getOrgId));
            pMap.forEach((key, value) -> {
                PersonnelGroupDetailEntity detail = new PersonnelGroupDetailEntity();
                detail.setGroupId(pg.getId());
                detail.setOrgId(key);
                detail.setUserIds(value.stream().map(p -> p.getId().toString()).collect(joining(",")));
                personnelGroupDetailPersistence.save(detail);
            });
        }
        return pg.getId();

    }

    @Override
    public PersonnelGroupDto getOneById(Long id) {
        return BeanUtils.convert(personnelGroupPersistence.findOne(id), PersonnelGroupDto.class);
    }


    @Override
    public List<PersonnelGroupDto> getSysGroupList(String name) {
        List<PersonnelGroupEntity> list;
        if (Assert.isNotEmpty(name)) {
            list = personnelGroupPersistence.getPersonGroupOpts("%" + name + "%");
        } else {
            list = personnelGroupPersistence.getPersonGroupOpts();
        }
        return BeanUtils.convert(list, PersonnelGroupDto.class);
    }

}
