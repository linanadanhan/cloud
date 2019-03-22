package com.gsoft.portal.system.organization.service.impl;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ResponseMessageDto;
import com.gsoft.cos3.entity.BaseEntity;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.tree.TreeNode;
import com.gsoft.cos3.util.*;
import com.gsoft.portal.system.organization.dto.IdentityInfoDto;
import com.gsoft.portal.system.organization.dto.OrgGrantDto;
import com.gsoft.portal.system.organization.dto.OrganizationDto;
import com.gsoft.portal.system.organization.entity.IdentityInfoEntity;
import com.gsoft.portal.system.organization.entity.OrgGrantEntity;
import com.gsoft.portal.system.organization.entity.OrganizationEntity;
import com.gsoft.portal.system.organization.entity.PositionEntity;
import com.gsoft.portal.system.organization.persistence.IdentityInfoPersistence;
import com.gsoft.portal.system.organization.persistence.OrgGrantPersistence;
import com.gsoft.portal.system.organization.persistence.OrganizationPersistence;
import com.gsoft.portal.system.organization.persistence.PositionPersistence;
import com.gsoft.portal.system.organization.service.OrganizationService;
import com.gsoft.portal.system.personnel.entity.UserOrganizationEntity;
import com.gsoft.portal.system.personnel.persistence.UserOrganizationPersistence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 组织机构Service实现类
 *
 * @author SN
 */
@Service
public class OrganizationServiceImpl implements OrganizationService {

    @Resource
    OrganizationPersistence organizationPersistence;

    @Resource
    IdentityInfoPersistence identityInfoPersistence;

    @Resource
    OrgGrantPersistence orgGrantPersistence;

    @Resource
    PositionPersistence positionPersistence;

    @Resource
    UserOrganizationPersistence userOrganizationPersistence;

    @Resource
    BaseDao baseDao;

    @Override
    public PageDto getListByCode(String code, Boolean isCascade, Long id, Integer page, Integer size) {
        Pageable pageable = JPAUtil.createPageRequest(page, size, "code", "asc");
        Page<OrganizationEntity> dtoList;
        if (Assert.isNotEmpty(isCascade) && isCascade) {
            dtoList = organizationPersistence.findByCascade(code, pageable);
        } else {
            dtoList = organizationPersistence.findByPid(id, pageable);
        }
        return new PageDto(dtoList);
    }

    @Override
    public OrganizationDto getOneById(Long id) {
        OrganizationEntity entity = organizationPersistence.findOne(id);
        OrganizationDto dto = BeanUtils.convert(entity, OrganizationDto.class);
        return dto;
    }

    @Override
    public Boolean isExitCode(Long id, String code, String dimension) {
        OrganizationEntity entity = organizationPersistence.findByCodeAndDimension(code, dimension);
        if (entity != null) {
            return true;
        }
        if (id == null) {
            return false;
        }
        return organizationPersistence.exists(id);
    }

    @Override
    public OrganizationDto save(OrganizationDto organizationDto) {
        OrganizationEntity entity = null;
        if (Assert.isEmpty(organizationDto.getId())) {
            entity = BeanUtils.convert(organizationDto, OrganizationEntity.class);

            OrganizationEntity parentOrg = organizationPersistence.findOne(entity
                    .getParentId());// 找到上一级组织
            entity.setCascade(parentOrg.getCascade() + parentOrg.getCode()
                    + "/");
        } else {
            entity = BeanUtils.convert(organizationDto, OrganizationEntity.class);
        }
        OrganizationEntity reEntity = organizationPersistence.save(entity);
        return BeanUtils.convert(reEntity, OrganizationDto.class);
    }

    @Override
    public Boolean deleteById(Long id, String code) {
        Integer rows = organizationPersistence.deleteById(id, code);
        return rows > 0;
    }

    @Override
    public OrganizationDto orgImport(OrganizationDto dto) {
        try {
            OrganizationEntity entity = new OrganizationEntity();
            entity.setName(dto.getName());
            entity.setCode(dto.getCode());
            entity.setDimension(dto.getDimension());

            //查询上级机构是否已存在
            OrganizationEntity pEntity = organizationPersistence.findByCode(dto.getParentCode());

            if (Assert.isEmpty(pEntity)) {
                return null;
            } else {
                entity.setParentId(pEntity.getId());
                entity.setCascade(pEntity.getCascade() + pEntity.getCode() + "/");
                entity.setLevel(pEntity.getLevel() + 1);
                entity.setOrgType(dto.getOrgType());
                entity.setDimension(dto.getDimension());
            }

            OrganizationEntity oEntity = organizationPersistence.findByCode(entity.getCode());
            if (Assert.isEmpty(oEntity)) {
                // 创建时间和创建人
                organizationPersistence.save(entity);
            } else {
            	 Date date = new Date();
                 oEntity.setUpdateBy(dto.getCreateBy());
                 oEntity.setUpdateTime(date);
                 oEntity.setDimension(dto.getDimension());
                 oEntity.setOrgType(dto.getOrgType());
                 oEntity.setName(dto.getName());
                 organizationPersistence.save(oEntity);
            }
            return dto;

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<OrganizationDto> getTreeList(String personnelId, String dimension) {
        List<OrganizationEntity> list = orgGrantPersistence.findByUserId(MathUtils.numObj2Long(personnelId), dimension).stream()
                .map(OrgGrantEntity::getOrgId).map(aLong -> organizationPersistence.findOne(aLong)).collect(Collectors.toList());
        List<OrganizationEntity> createList = organizationPersistence.findbyCreateAndDimension(MathUtils.numObj2Long(personnelId), dimension);
        createList.stream().forEach(organizationEntity -> {
            boolean noneMatch = list.stream().noneMatch(org -> org.getId().equals(organizationEntity.getId()));
            if (noneMatch) {
                list.add(organizationEntity);
            }
        });
        List<OrganizationEntity> rtnList = new ArrayList<OrganizationEntity>();
        if (!Assert.isEmpty(list) && list.size() > 0) {
        	for (OrganizationEntity entity : list) {
        		List<OrganizationEntity> tmpList = organizationPersistence.getTreeList(entity.getCode(), entity.getCode(),dimension);
        		if (!Assert.isEmpty(tmpList)) {
        			rtnList.addAll(tmpList);
        		}
        	}
        }
        return BeanUtils.convert(rtnList.stream().distinct().collect(Collectors.toList()), OrganizationDto.class);
    }
    
    @Override
    public List<OrganizationDto> getTreeList(String personnelId, String dimension,String orgType) {
        List<OrganizationEntity> list = orgGrantPersistence.findByUserId(MathUtils.numObj2Long(personnelId), dimension).stream()
                .map(OrgGrantEntity::getOrgId).map(aLong -> organizationPersistence.findOne(aLong)).collect(Collectors.toList());
        List<OrganizationEntity> createList = organizationPersistence.findbyCreateAndDimension(MathUtils.numObj2Long(personnelId), dimension,orgType);
        list.addAll(createList);
        List<OrganizationEntity> rtnList = new ArrayList<OrganizationEntity>();
        if (!Assert.isEmpty(list) && list.size() > 0) {
        	for (OrganizationEntity entity : list) {
        		List<OrganizationEntity> tmpList = organizationPersistence.getTreeList(entity.getCode(), entity.getCode(),orgType,dimension);
        		if (!Assert.isEmpty(tmpList)) {
        			rtnList.addAll(tmpList);
        		}
        	}
        }
        return BeanUtils.convert(rtnList.stream().distinct().collect(Collectors.toList()), OrganizationDto.class);
    }

    @Override
    public List<OrganizationDto> getTreeList(String personnelId) {
        //查询登录人员所属组织机构信息
        Map<String, Object> params = new HashMap<String, Object>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT o.* FROM cos_organization_org o,cos_sys_user_org po,cos_sys_personnel p ");
        sb.append("WHERE o.c_id = po.c_org_id AND po.c_personnel_id = p.c_id ");
        sb.append("AND p.c_id = ${personnelId} order by o.c_sort_no asc");

        params.put("personnelId", personnelId);

        List<Map<String, Object>> orgList = baseDao.query(sb.toString(), params);

        List<OrganizationEntity> resList = null;

        if (!Assert.isEmpty(orgList)) {

            resList = new ArrayList<OrganizationEntity>();

            for (Map<String, Object> map : orgList) {
                List<OrganizationEntity> list = organizationPersistence.getTreeList(MathUtils.stringObj(map.get("C_CODE")), MathUtils.stringObj(map.get("C_CODE")));

                resList.addAll(list);
            }
        }

        return BeanUtils.convert(resList, OrganizationDto.class);
    }

    @Override
    public List<OrganizationDto> getTreeListByDimension(String dimension) {
        List<OrganizationEntity> resList = organizationPersistence.findByDimension(dimension);
        return BeanUtils.convert(resList, OrganizationDto.class);
    }

    @Override
    public PageDto getIdentityInfoListByOrgCode(String code, Boolean isCascade, Long id, Integer page, Integer size) {
        List<OrganizationEntity> organizations = new ArrayList<>();
        List<Long> longs = new ArrayList<>();
        if (Assert.isNotEmpty(isCascade) && isCascade) {
            organizations = organizationPersistence.findByCascade(code);
        } else {
            OrganizationEntity organizationEntity = organizationPersistence.findOne(id);
            organizations.add(organizationEntity);
        }
        for (OrganizationEntity organization : organizations) {
            List<PositionEntity> list = positionPersistence.findListByOrgId(organization.getId());
            for (PositionEntity positionEntity : list) {
                longs.add(positionEntity.getId());
            }
        }
        if (longs.size() > 0) {
            Pageable pageable = JPAUtil.createPageRequest(page, size, "sortNo", "asc");
            Page<IdentityInfoEntity> dtoList = identityInfoPersistence.findPage(longs, pageable);
            return new PageDto(dtoList);
        } else {
            return new PageDto(new ArrayList<>(), 0);
        }

    }

    @Override
    public ResponseMessageDto saveDataGrant(Long identityId, String orgIds, String dimension) {
        //先将表中身份关联的信息清空
        orgGrantPersistence.deleteByIdentityId(identityId, dimension);
        //根据身份查找personalId
        IdentityInfoEntity identityInfoEntity = identityInfoPersistence.findOne(identityId);
        if (Assert.isNotEmpty(orgIds)) {
            String[] split = orgIds.split(",");
            Arrays.stream(split).forEach(str -> {
                OrgGrantEntity orgGrantEntity = new OrgGrantEntity();
                orgGrantEntity.setIdentityId(identityId);
                orgGrantEntity.setOrgId(MathUtils.numObj2Long(str));
                orgGrantEntity.setUserId(identityInfoEntity.getUserId());
                orgGrantEntity.setDimension(dimension);
                orgGrantPersistence.save(orgGrantEntity);
            });
        }
        return ResponseMessageDto.SUCCESS;
    }

    @Override
    public List<Long> getOrgIdsByIdentity(Long identityId, String dimension) {
        List<OrgGrantEntity> orgGrantEntities = orgGrantPersistence.findByIdentity(identityId, dimension);
        return orgGrantEntities.stream().map(OrgGrantEntity::getOrgId).collect(Collectors.toList());
    }

    @Override
    public List<IdentityInfoDto> getIdentityListByOrg(Long orgId) {
        ArrayList<IdentityInfoEntity> identityInfoEntities = new ArrayList<>();
        List<PositionEntity> listByOrgid = positionPersistence.findListByOrgId(orgId);
        for (PositionEntity positionEntity : listByOrgid) {
            List<IdentityInfoEntity> list = identityInfoPersistence.findListByPositionId(positionEntity.getId());
            identityInfoEntities.addAll(list);
        }
        return BeanUtils.convert(identityInfoEntities, IdentityInfoDto.class);
    }

    @Override
    public String getCascadeByParentId(Long parentId) {
        OrganizationEntity parentOrg = organizationPersistence.findOne(parentId);
        String cascade = parentOrg.getCascade() + parentOrg.getCode() + "/";
        return cascade;
    }

    @Override
    public ResponseMessageDto addGrant(String orgId, String dimension, String personnelId) {
        //todo 必须以某个身份来添加权限 待处理
//        if (Assert.isNotEmpty(orgId)) {
//            OrgGrantEntity orgGrantEntity = new OrgGrantEntity();
//            orgGrantEntity.setOrgId(Long.valueOf(orgId));
//            orgGrantEntity.setUserId(Long.valueOf(personnelId));
//            orgGrantEntity.setDimension(dimension);
//            orgGrantPersistence.save(orgGrantEntity);
//        }
        return ResponseMessageDto.SUCCESS;
    }

    @Override
    public List<IdentityInfoDto> getHasConnectIdentity(String orgId, Boolean isCascade, String orgCode, String dimension) {
        List<OrganizationEntity> organizations = new ArrayList<>();
        if (Assert.isNotEmpty(isCascade) && isCascade) {
            organizations = organizationPersistence.findByCascadeAndDimension(orgCode, dimension);
        }
        OrganizationEntity byCodeAndDimension = organizationPersistence.findByCodeAndDimension(orgCode, dimension);
        if (Assert.isNotEmpty(byCodeAndDimension)) {
            organizations.add(byCodeAndDimension);
        }

        List<Long> personIds = organizations.stream().flatMap(organizationEntity -> {
            List<UserOrganizationEntity> list = userOrganizationPersistence.findByOrgId(organizationEntity.getId());
            return list.stream().map(UserOrganizationEntity::getPersonId).distinct();
        }).collect(Collectors.toList());
        //得到IdentityInfoId
        List<Long> longList = personIds.stream()
                .map(aLong -> {
                    OrgGrantEntity grantEntity = orgGrantPersistence.findByOrgAndUser(MathUtils.numObj2Long(orgId), aLong, dimension);
                    if (Assert.isNotEmpty(grantEntity)) {
                        return grantEntity.getIdentityId();
                    } else {
                        return null;
                    }
                }).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        //剔除超级管理员
        List<IdentityInfoEntity> collect = longList.stream().map(aLong -> identityInfoPersistence.findOne(aLong)).filter(identityInfoEntity -> identityInfoEntity.getId() != 1).distinct().collect(Collectors.toList());
        return BeanUtils.convert(collect, IdentityInfoDto.class);
    }


    @Override
    public List<IdentityInfoDto> getHasNoConnectIdentity(String orgId, Boolean isCascade, String orgCode, String dimension) {
        List<OrganizationEntity> organizations = new ArrayList<>();
        if (Assert.isNotEmpty(isCascade) && isCascade) {
            organizations = organizationPersistence.findByCascadeAndDimension(orgCode, dimension);
        }
        OrganizationEntity byCodeAndDimension = organizationPersistence.findByCodeAndDimension(orgCode, dimension);
        if (Assert.isNotEmpty(byCodeAndDimension)) {
            organizations.add(byCodeAndDimension);
        }

        //得到机构下关联的人员
        List<Long> personIds = organizations.stream().flatMap(organizationEntity -> {
            List<UserOrganizationEntity> list = userOrganizationPersistence.findByOrgId(organizationEntity.getId());
            return list.stream().map(UserOrganizationEntity::getPersonId).distinct();
        }).collect(Collectors.toList());

        //得到对orgId授权的身份
        List<Long> grantIndentityInfoIds = personIds.stream()
                .map(aLong -> {
                    OrgGrantEntity grantEntity = orgGrantPersistence.findByOrgAndUser(MathUtils.numObj2Long(orgId), aLong, dimension);
                    if (Assert.isNotEmpty(grantEntity)) {
                        return grantEntity.getIdentityId();
                    } else {
                        return null;
                    }
                }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        //得到机构下关联的职位
        List<Long> postionIds = organizations.stream().flatMap(organizationEntity -> {
            List<PositionEntity> listByOrgId = positionPersistence.findListByOrgId(organizationEntity.getId());
            return listByOrgId.stream().map(BaseEntity::getId).distinct();
        }).collect(Collectors.toList());
        //得到所有符合的身份
        List<Long> allIdentityInfoIds = postionIds.stream().flatMap(postionId ->
                personIds.stream().map(personId -> {
                    IdentityInfoEntity entity = identityInfoPersistence.findByUserAndPostion(personId, postionId);
                    if (Assert.isNotEmpty(entity)) {
                        return entity.getId();
                    } else {
                        return null;
                    }
                }).filter(Objects::nonNull)).collect(Collectors.toList());

        //从全部身份中剔除授权过的身份
        List<Long> longList = allIdentityInfoIds.stream()
                .filter(aLong -> grantIndentityInfoIds.stream().noneMatch(grant -> grant.equals(aLong))).collect(Collectors.toList());
        //剔除超级管理员
        List<IdentityInfoEntity> collect = longList.stream().map(aLong -> identityInfoPersistence.findOne(aLong)).filter(identityInfoEntity -> identityInfoEntity.getId() != 1).distinct().collect(Collectors.toList());
        return BeanUtils.convert(collect, IdentityInfoDto.class);
    }

    @Override
    public void saveOrgIdentityInfo(List<OrgGrantDto> list) {
        if (Assert.isNotEmpty(list) && list.size() > 0) {
            OrgGrantDto orgGrantDto = list.get(0);
            //删除之前的授权信息
            orgGrantPersistence.deleteByOrgAndDimension(orgGrantDto.getOrgId(), orgGrantDto.getDimension());
            //判断是否存在身份id和用户id,存在则存储在授权表中
            list.stream().filter(orgGrant -> Assert.isNotEmpty(orgGrant.getIdentityId()) && Assert.isNotEmpty(orgGrant.getUserId()))
                    .forEach(dto -> orgGrantPersistence.save(BeanUtils.convert(dto, OrgGrantEntity.class)));

        }
    }

    @Override
    public Map<String, Object> getOrgInfoByUserId(Long userId, String dimension) {

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT g.* FROM cos_sys_personnel p, cos_sys_user_org ug, cos_organization_org g ");
        sb.append("WHERE p.c_id = ug.c_personnel_id AND g.c_id = ug.c_org_id AND p.c_id = ? AND g.c_dimension = ?");

        return baseDao.load(sb.toString(), userId, dimension);
    }

    @Override
    public List<OrganizationDto> getOrgDtoByUserId(Long userId, String dimension) {
        List<OrganizationEntity> byPersonAndDimension = organizationPersistence.findByPersonAndDimension(userId, dimension);
        ArrayList<OrganizationDto> list = new ArrayList<>();
        if (byPersonAndDimension.size() > 0) {
            for (OrganizationEntity organizationEntity : byPersonAndDimension) {
                OrganizationDto dto = BeanUtils.convert(organizationEntity, OrganizationDto.class);
                OrganizationEntity entity = findSuperiorOrg(organizationEntity);
                if (Assert.isNotEmpty(entity)){
                    dto.setAttributionOrgId(entity.getId());
                }
                list.add(dto);
            }
        }
        return list;
    }

    public OrganizationEntity findSuperiorOrg(OrganizationEntity organizationEntity) {
        if (Assert.isEmpty(organizationEntity)) {
            return organizationEntity;
        }
        OrganizationEntity entity = organizationPersistence.findOne(organizationEntity.getParentId());
        if (Assert.isNotEmpty(entity)) {
            if (!"1".equals(entity.getOrgType())) {
                return entity;
            }
        }
        return findSuperiorOrg(entity);
    }

    @Override
    public List<OrganizationDto> getOrgDtoByUserId(Long personnelId) {
        List<OrganizationEntity> entities = organizationPersistence.findByPersonnelId(personnelId);
        if (entities.size() > 0) {
            return BeanUtils.convert(entities, OrganizationDto.class);
        }
        return new ArrayList<OrganizationDto>();
    }

	@Override
	public List<Map<String, Object>> getOrgInfoByLetter(String selLetters,String dimension) {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<>();
		sb.append(" SELECT DISTINCT o.c_id FROM cos_organization_org o,cos_sys_user_org po,cos_sys_personnel p ");
		sb.append(" WHERE o.c_dimension=${dimension} and  o.c_id = po.c_org_id AND po.c_personnel_id = p.c_id and o.c_code !='0000' ");
		 if(selLetters.length()==1){
            sb.append(" and GET_FIRST_PINYIN_CHAR(p.c_name) = ${selLetters} ");
        }else if(selLetters.length()==2){
            sb.append(" and GET_TWO_PINYIN_CHAR(p.c_name) = ${selLetters} ");
        }else{
             sb.append(" and GET_PINYIN_CHAR(p.c_name) = ${selLetters} ");
        }
		params.put("selLetters", selLetters);
        params.put("dimension",dimension);
		return baseDao.query(sb.toString(), params);
	}


	//------------------------------------------------选人开始
    @Override
    public List<TreeNode> getOrgTreeBySelPerson(String dimension,String orgIds, Boolean isCascade){

        StringBuffer sb=new StringBuffer();
        sb.append(" select * from cos_organization_org where 1=1 and c_deleted = 0 ");
        sb.append(" and c_dimension=${dimension} ");

        Map<String,Object> params=new HashMap<>();
        params.put("dimension",dimension);
        if(Assert.isNotEmpty(orgIds)){
            //orgIds不为空，才会判断是否有级联

            sb.append(" and ( c_id in ${orgIds}  ");
            if(isCascade){
                List<OrganizationDto> orgsByIds = this.getOrgsByIds(orgIds);
                //根据dimension,orgIds得到对象
                orgsByIds.forEach(org->sb.append(" or c_cascade like '%/"+org.getCode()+"/%' "));
            }
            sb.append(" )");
            List<Long> idList = Arrays.asList(orgIds.split(",")).stream().map(Long::new).collect(Collectors.toList());
            params.put("orgIds",idList);
        }
        List<Map<String, Object>> list = baseDao.query(sb.toString(), params);
        List<OrganizationDto> resultList = list.stream().filter(m->!("0000".equals(m.get("C_CODE").toString()))).map(m -> {  //排除根节点
            OrganizationDto dto = new OrganizationDto();
            dto.setId(MathUtils.numObj2Long(m.get("C_ID")));
            dto.setCascade(m.get("C_CASCADE").toString());
            dto.setLevel(MathUtils.numObj2Integer(m.get("C_LEVEL")));
            dto.setCode(m.get("C_CODE").toString());
            dto.setName(m.get("C_NAME").toString());
            dto.setParentId(MathUtils.numObj2Long(m.get("C_PARENT_ID")));
            return dto;
        }).collect(Collectors.toList());

        List<TreeNode> tree =TreeUtils.convert(resultList).attrs("cascade,code,level,id").tree();

        return tree;
    }


    @Override
    public String getCascadeNameById(Long id,String re){
        String result="";
        if(Assert.isEmpty(re)){
            re="-";
        }
        OrganizationEntity org = organizationPersistence.findOne(id);
        if(Assert.isEmpty(org)){
            return result;
        }
        String cascade=org.getCascade()+org.getCode(); //加上自己
        List<String> codeList = Arrays.stream(cascade.split("/")).collect(Collectors.toList());
        result = organizationPersistence.getOrgsByMulCode(codeList).stream().filter(o->!"0000".equals(o.getCode())).map(OrganizationEntity::getName).collect(Collectors.joining(re));
        return result;
    }

    @Override
    public List<OrganizationDto> getAllOrgList() {
        List<OrganizationEntity> list = organizationPersistence.findAll();
        return BeanUtils.convert(list,OrganizationDto.class);
    }

    @Override
    public List<Map<String, Object>> getOrgInfoByNameAndDimension(String departmentName,String dimension) {
        StringBuffer sb=new StringBuffer();
        Map<String, Object> params = new HashMap<String, Object>();
        sb.append(" select * from cos_organization_org where 1=1 and c_deleted = 0 ");
        if (!Assert.isEmpty(departmentName)){
            sb.append("and c_name like ${departmentName} ");
            params.put("departmentName","%"+departmentName+"%");
        }
        if ((!Assert.isEmpty(dimension))){
            sb.append("and c_dimension = ${dimension} ");
            params.put("dimension",dimension);
        }
        return baseDao.query(sb.toString(), params);
    }


    private List<OrganizationDto> getOrgsByIds(String orgIds){
        List<Long> idList = Arrays.asList(orgIds.split(",")).stream().map(Long::new).collect(Collectors.toList());
        List<OrganizationEntity> list = organizationPersistence.findOrgsByIds(idList);
        return BeanUtils.convert(list,OrganizationDto.class);
    }
    //---------------------------------end


    @Override
    public List<Map<String, Object>> getAllOrgListByRelPerson(String search) {
        StringBuffer sb = new StringBuffer();
        Map<String, Object> params = new HashMap<String, Object>();
        sb.append("SELECT DISTINCT o.c_id FROM cos_organization_org o,cos_sys_user_org po,cos_sys_personnel p ");
        sb.append("WHERE o.c_id = po.c_org_id AND po.c_personnel_id = p.c_id and o.c_code !='0000' ");

        if (!Assert.isEmpty(search)) {
            sb.append("and p.c_name like ${search} ");
            params.put("search", "%" + search + "%");
        }
        return baseDao.query(sb.toString(), params);
    }
    
    @Override
    public List<OrganizationDto> getList(String personnelId, String dimension,String orgType) {
        List<OrganizationEntity> list = orgGrantPersistence.findByUserId(MathUtils.numObj2Long(personnelId), dimension).stream()
                .map(OrgGrantEntity::getOrgId).map(aLong -> organizationPersistence.findOne(aLong)).collect(Collectors.toList());
        List<OrganizationEntity> createList = organizationPersistence.findbyCreateAndDimension(MathUtils.numObj2Long(personnelId), dimension,orgType);
        list.addAll(createList);
        List<OrganizationEntity> rtnList = new ArrayList<OrganizationEntity>();
        if (!Assert.isEmpty(list) && list.size() > 0) {
        	for (OrganizationEntity entity : list) {
        		List<OrganizationEntity> tmpList = organizationPersistence.getList(entity.getCode(), entity.getCode(),orgType,dimension);
        		if (!Assert.isEmpty(tmpList)) {
        			rtnList.addAll(tmpList);
        		}
        	}
        }
        return BeanUtils.convert(rtnList.stream().distinct().collect(Collectors.toList()), OrganizationDto.class);
    }
}
