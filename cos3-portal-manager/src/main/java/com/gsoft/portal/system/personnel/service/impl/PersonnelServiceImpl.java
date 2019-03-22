package com.gsoft.portal.system.personnel.service.impl;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ResponseMessageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.entity.BaseEntity;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.cos3.util.MD5Util;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.system.organization.entity.IdentityInfoEntity;
import com.gsoft.portal.system.organization.entity.OrganizationEntity;
import com.gsoft.portal.system.organization.persistence.IdentityInfoPersistence;
import com.gsoft.portal.system.organization.persistence.OrganizationPersistence;
import com.gsoft.portal.system.personnel.dto.PasswordInfoDto;
import com.gsoft.portal.system.personnel.dto.PersonnelDto;
import com.gsoft.portal.system.personnel.dto.RolePersonnelDto;
import com.gsoft.portal.system.personnel.entity.PersonnelEntity;
import com.gsoft.portal.system.personnel.entity.RoleEntity;
import com.gsoft.portal.system.personnel.entity.RolePersonnelEntity;
import com.gsoft.portal.system.personnel.entity.UserOrganizationEntity;
import com.gsoft.portal.system.personnel.persistence.PersonnelPersistence;
import com.gsoft.portal.system.personnel.persistence.RolePersistence;
import com.gsoft.portal.system.personnel.persistence.RolePersonnelPersistence;
import com.gsoft.portal.system.personnel.persistence.UserOrganizationPersistence;
import com.gsoft.portal.system.personnel.service.PersonnelService;

@Service
public class PersonnelServiceImpl implements PersonnelService {

    @Resource
    PersonnelPersistence personnelPersistence;

    @Resource
    RolePersistence rolePersistence;


    @Resource
    UserOrganizationPersistence userOrganizationPersistence;

    @Resource
    RolePersonnelPersistence rolePersonnelPersistence;

    @Resource
    OrganizationPersistence organizationPersistence;

    @Resource
    IdentityInfoPersistence identityInfoPersistence;

    @Autowired
    BaseDao baseDao;

    @Override
    public Boolean isExitByLoginName(Long id, String loginName) {
        PersonnelEntity entity = null;
        if (Assert.isEmpty(id)) { // 新增
            entity = personnelPersistence.isExitByLoginName(loginName);
        } else {// 修改
            entity = personnelPersistence.isExitByLoginName(id, loginName);
        }
        if (Assert.isNotEmpty(entity)) {// 不为空，已经存在，返回true
            return true;
        }
        return false;
    }

    @Override
    public Boolean isExitByPhone(Long id, String phone) {
        PersonnelEntity entity = null;
        if (Assert.isEmpty(id)) { // 新增
            entity = personnelPersistence.isExitByPhone(phone);
        } else {// 修改
            entity = personnelPersistence.isExitByPhone(id, phone);
        }
        if (Assert.isNotEmpty(entity)) {// 不为空，已经存在，返回true
            return true;
        }
        return false;
    }

    @Override
    public PersonnelDto getOneById(Long id) {
        PersonnelEntity entity = personnelPersistence.getOne(id);
        if (Assert.isNotEmpty(entity)) {
            return BeanUtils.convert(entity, PersonnelDto.class);
        }
        return null;
    }

    @Override
    public PersonnelDto save(PersonnelDto personnelDto) {
        if (Assert.isEmpty(personnelDto.getId())) {// 新增
            personnelDto.setUuid(UUID.randomUUID().toString().replace("-", ""));
            personnelDto.setPassWord(MD5Util.encode("123456"));// 初始密码
        } else {
            // 修改的时候查找是否存在职位,修改职位信息
            identityInfoPersistence.findByUserId(personnelDto.getId()).forEach(identityInfoEntity -> {
                identityInfoEntity.setMobilePhone(personnelDto.getMobilePhone());
                identityInfoEntity.setName(personnelDto.getName());
                identityInfoEntity.setSortNo(personnelDto.getSortNo());
                identityInfoPersistence.save(identityInfoEntity);
            });
        }
        PersonnelEntity entity = personnelPersistence.save(BeanUtils.convert(personnelDto, PersonnelEntity.class));

        if (Assert.isNotEmpty(entity)) {
            // 新增用户组织机构关系数据
            UserOrganizationEntity uOrgEntity = userOrganizationPersistence.findOneByPIdAndOrgId(entity.getId(),
                    personnelDto.getOrgId());

            if (Assert.isEmpty(uOrgEntity)) {
                uOrgEntity = new UserOrganizationEntity();
                uOrgEntity.setPersonId(entity.getId());
                uOrgEntity.setOrgId(personnelDto.getOrgId());
                userOrganizationPersistence.save(uOrgEntity);
            }

            return BeanUtils.convert(entity, PersonnelDto.class);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        personnelPersistence.delById(id);// 删除人员,逻辑删除
        userOrganizationPersistence.delByPersonnelId(id);// 删除人员与组织机构关系
        rolePersonnelPersistence.delByPersonnelId(id);// 删除人员和角色关系
        // 删除人员身份信息
        baseDao.update("DELETE FROM cos_sys_identity_info WHERE c_user_id = ?", MathUtils.numObj2Long(id));
        // 删除关联站点信息 //删除关联页面信息
        baseDao.update("DELETE FROM cos_portal_permission WHERE c_grant_type = '1' and c_grant_id = ? ",
                MathUtils.numObj2Long(id));
        // 删除即时通讯群成员信息
        baseDao.update("DELETE FROM cos_im_user_group WHERE c_user_id = ?", MathUtils.numObj2Long(id));
    }

    @Override
    public void updateStatus(Long id, Boolean status) {
        personnelPersistence.updateStatus(id, status);
    }

    @Override
    public PersonnelDto getOneByPhone(String phone) {
        PersonnelEntity entity = personnelPersistence.getOneByPhone(phone);
        if (Assert.isNotEmpty(entity)) {
            return BeanUtils.convert(entity, PersonnelDto.class);
        }
        return null;
    }

    @Override
    public PersonnelDto getOneByloginName(String loginName) {
        PersonnelEntity entity = personnelPersistence.getOneByloginNameAndAreaCode(loginName);
        if (Assert.isNotEmpty(entity)) {
            return BeanUtils.convert(entity, PersonnelDto.class);
        }
        return null;
    }

    @Override
    public void connectRolePersonnel(List<RolePersonnelDto> list) {
        if (list != null && list.size() > 0) {
            // 删除该人员关联的该分类下的角色，1.得到该分类下的关联的权限ID，然后删掉
            // 再保存该人员与该分类下的角色
            if (Assert.isEmpty(list.get(0).getType())) {
                rolePersonnelPersistence.delByPersonnelId(list.get(0).getPersonnelId());
            } else {
                rolePersonnelPersistence.delHasConnectRole(list.get(0).getPersonnelId(), list.get(0).getType());
            }
            if (Assert.isNotEmpty(list.get(0).getRoleId())) { // 因为可能取消某分类下的所有关联，这时候就是空的，就不用保存了。
                rolePersonnelPersistence.save(BeanUtils.convert(list, RolePersonnelEntity.class));
            }
        }
    }

    @Override
    public List<PersonnelDto> getHasConnectPerson(String orgCode, Boolean isCascade, Long roleId, String personnelId) {
        List<PersonnelDto> list = null;

        StringBuffer sb = new StringBuffer();
        Map<String, Object> params = new HashMap<String, Object>();

        sb.append("SELECT DISTINCT p.* FROM cos_sys_personnel p,cos_sys_role r,cos_sys_role_personal pr ");
        sb.append("WHERE p.c_id = pr.c_personnel_id AND r.c_id = pr.c_role_id AND r.c_id = ${roleId} ");
        sb.append(
                "AND p.c_deleted = 0 and EXISTS (SELECT o.* FROM cos_organization_org o,cos_sys_user_org po,cos_sys_personnel pp ");
        sb.append("WHERE o.c_id = po.c_org_id AND po.c_personnel_id = pp.c_id  AND pp.c_id = p.c_id ");
        sb.append(" and p.c_id != ${personnelId} ");

        params.put("personnelId", MathUtils.numObj2Long(personnelId));

        if (Assert.isNotEmpty(isCascade) && isCascade) {
            sb.append(" AND o.c_cascade like ${isCascade} ");
            params.put("isCascade", "%" + orgCode + "%");

        } else {
            // list = personnelPersistence.getHasConnectPerson(roleId, areaCode);
            sb.append(" and o.c_code = ${orgCode} ");
            params.put("orgCode", orgCode);
        }

        sb.append(")");
        params.put("roleId", roleId);
        List<Map<String, Object>> resList = baseDao.query(sb.toString(), params);

        if (Assert.isNotEmpty(resList)) {

            list = new ArrayList<PersonnelDto>();

            for (Map<String, Object> map : resList) {
                PersonnelDto dto = new PersonnelDto();
                dto.setId(MathUtils.numObj2Long(map.get("C_ID")));
                dto.setName(MathUtils.stringObj(map.get("C_NAME")));
                dto.setLoginName(MathUtils.stringObj(map.get("C_LOGIN_NAME")));
                list.add(dto);
            }

        } else {
            return new ArrayList<PersonnelDto>();
        }
        return list;
    }

    // 移动
    @Override
    @Transactional
    public void movePersonnel(String ids, Long orgId) {
        String[] idArr = ids.split(",");
        // 批量查询人员
        List<Long> idList = new ArrayList<Long>();
        List<UserOrganizationEntity> uOrgEntityList = new ArrayList<UserOrganizationEntity>();

        for (String id : idArr) {
            idList.add(Long.valueOf(id));
            UserOrganizationEntity nEntity = new UserOrganizationEntity();
            nEntity.setPersonId(MathUtils.numObj2Long(id));
            nEntity.setOrgId(orgId);
            uOrgEntityList.add(nEntity);
        }

        // 删除原用户与组织机构关联关系
        userOrganizationPersistence.delByPersonIds(idList);
        // 新增用户机构关联关系
        userOrganizationPersistence.save(uOrgEntityList);
    }

    @Override
    public void resetPassword(String mobile, String password1) {
        String md5Pwd = MD5Util.encode(password1);
        personnelPersistence.updatePassword(mobile, md5Pwd);
    }

    @Override
    public ReturnDto modifyPassword(PasswordInfoDto passwordInfoDto) {
        Long id = passwordInfoDto.getId();
        String newPassword = passwordInfoDto.getNewPassword();
        String secondPassword = passwordInfoDto.getSecondPassword();
        String oldPassword = passwordInfoDto.getOldPassword();
        PersonnelEntity personnelEntity = personnelPersistence.findOne(id);
        Assert.isTrue(newPassword.equals(secondPassword), "输入新密码不一致！");
        Assert.isTrue(Assert.isNotEmpty(personnelEntity), "未查到人员信息！");
        Assert.isTrue(!newPassword.equals(oldPassword), "新旧密码不能一样！");
        Assert.isTrue(personnelEntity.getPassWord().equals(MD5Util.encode(oldPassword)), "旧密码错误！");
        baseDao.update("UPDATE cos_sys_personnel p SET p.c_password=? WHERE p.c_id=?", MD5Util.encode(newPassword), id);
        return new ReturnDto("修改成功！");
    }

    @Override
    public ResponseMessageDto bindPhone(String id, String mobile) {
        PersonnelEntity oneByPhone = personnelPersistence.getOneByPhone(mobile);
        if (Assert.isNotEmpty(oneByPhone)) {
            return new ResponseMessageDto(false, "该手机号已经被绑定！");
        } else {
            baseDao.update("UPDATE cos_sys_personnel p SET p.c_mobile_phone=? WHERE p.c_id =?", mobile, id);
            return new ResponseMessageDto(true, "绑定成功！");
        }
    }

    @Override
    public PageDto getListByOrgCode(String dimension, String orgCode, Boolean isCascade, String search, Integer page, Integer size,
                                    String sortProp, String order, String personnelId) {

        Map<String, Object> params = new HashMap<String, Object>();
        StringBuffer sb = new StringBuffer();

        sb.append("SELECT p.* FROM cos_sys_personnel p,cos_organization_org o,cos_sys_user_org po ");
        sb.append("WHERE p.c_id = po.c_personnel_id AND o.c_id = po.c_org_id and p.c_id !=1 ");
        sb.append(" AND o.c_deleted = 0 AND p.c_deleted = 0 ");
        params.put("personnelId", MathUtils.numObj2Long(personnelId));
        
        // 增加纬度字段
        if (!Assert.isEmpty(dimension)) {
        	sb.append(" and o.c_dimension = ${dimension} ");
        	params.put("dimension", dimension);
        }

        if (Assert.isNotEmpty(isCascade) && isCascade) { // 级联，查询当前和以下机构的人员
            sb.append(" and (o.c_cascade like ");
            sb.append(" (SELECT CONCAT('%',c_cascade,c_code, '/') FROM cos_organization_org WHERE c_code = ${orgCode} AND c_dimension = ${dimension}) ");
            sb.append(" or o.c_code = ${orgCode} )");
            params.put("dimension", dimension);
            params.put("orgCode", orgCode);


        } else {
            // 如果为空，就只查当前机构的人员
            sb.append(" and o.c_code = ${orgCode} ");
            params.put("orgCode", orgCode);
        }

        if (!Assert.isEmpty(search)) {
            sb.append(" and (p.c_login_name like ${search} or p.c_name like ${search}) ");
            params.put("search", "%" + search + "%");
        }

        sb.append(" ORDER BY c_sort_no, c_id asc ");
        return baseDao.query(page, size, sb.toString(), params);
    }

    @Override
    public PersonnelDto importPersonnel(PersonnelDto dto) {

        if (Assert.isEmpty(dto.getName())) {
            return null;
        }

        if (Assert.isEmpty(dto.getLoginName())) {
            return null;
        }

        if (Assert.isEmpty(dto.getOrgCode())) {
            return null;
        }

        // 验证登录名是否已存在
        PersonnelEntity entity = personnelPersistence.isExitByLoginName(dto.getLoginName());

        if (Assert.isNotEmpty(entity)) {// 不为空，已经存在
            return null;
        }

        // 验证手机号是否唯一
        if (Assert.isNotEmpty(dto.getMobilePhone())) { // 如果手机号不为空，就判断是否唯一
            if (this.isExitByPhone(dto.getId(), dto.getMobilePhone())) {
                return null;
            }
        }

        // 新增用户
        OrganizationEntity oEntity = organizationPersistence.findByCode(dto.getOrgCode());

        if (Assert.isEmpty(oEntity)) {
            return null;
        }

        dto.setOrgId(oEntity.getId());

        return this.save(dto);
    }

    @Override
    public List<PersonnelDto> getHasNoConnectPerson(String orgCode, Boolean isCascade, String personnelId) {

        Map<String, Object> params = new HashMap<String, Object>();

        StringBuffer sb = new StringBuffer();

        sb.append("SELECT DISTINCT p.* FROM cos_sys_personnel p,cos_organization_org o,cos_sys_user_org po ");
        sb.append("WHERE p.c_id = po.c_personnel_id AND o.c_id = po.c_org_id ");

        sb.append(" and p.c_id != ${personnelId} ");
        params.put("personnelId", MathUtils.numObj2Long(personnelId));

        if (Assert.isNotEmpty(isCascade) && isCascade) {
            sb.append(" and (o.c_cascade like ${cascade} or o.c_code = ${orgCode} )");
            params.put("cascade", "%" + orgCode + "%");
            params.put("orgCode", orgCode);
        } else {
            sb.append(" and o.c_code = ${orgCode} ");
            params.put("orgCode", orgCode);
        }

        List<Map<String, Object>> rList = baseDao.query(sb.toString(), params);

        if (Assert.isEmpty(rList)) {
            return new ArrayList<PersonnelDto>();
        } else {
            List<PersonnelDto> rtnList = new ArrayList<PersonnelDto>();

            for (Map<String, Object> map : rList) {
                PersonnelDto dto = new PersonnelDto();
                dto.setId(MathUtils.numObj2Long(map.get("C_ID")));
                dto.setName(MathUtils.stringObj(map.get("C_NAME")));
                dto.setLoginName(MathUtils.stringObj(map.get("C_LOGIN_NAME")));
                rtnList.add(dto);
            }

            return rtnList;
        }
    }

    @Override
    public List<PersonnelDto> vagueQueryPerson(String condition, String positionId) {
        List<PersonnelEntity> list = personnelPersistence.vagueQueryPerson(condition);
        List<IdentityInfoEntity> listByPositionId = identityInfoPersistence
                .findListByPositionId(MathUtils.numObj2Long(positionId));

        List<PersonnelEntity> collect = list.stream().filter(personnelEntity -> !personnelEntity.getDeleted())
                .filter(personnelEntity -> listByPositionId.stream().noneMatch(
                        identityInfoEntity -> identityInfoEntity.getUserId().equals(personnelEntity.getId())))
                .limit(10).collect(toList());

        return BeanUtils.convert(collect, PersonnelDto.class);
    }

    @Override
    @Transactional
    public void batchDelPersonnel(String ids) {
        String[] idArr = ids.split(",");
        for (String id : idArr) {
            personnelPersistence.delById(MathUtils.numObj2Long(id));// 删除人员,逻辑删除
            userOrganizationPersistence.delByPersonnelId(MathUtils.numObj2Long(id));// 删除人员与组织机构关系
            rolePersonnelPersistence.delByPersonnelId(MathUtils.numObj2Long(id));// 删除人员和角色关系

            // 删除人员身份信息
            baseDao.update("DELETE FROM cos_sys_identity_info WHERE c_user_id = ?", MathUtils.numObj2Long(id));
            // 删除关联站点信息 //删除关联页面信息
            baseDao.update("DELETE FROM cos_portal_permission WHERE c_grant_type = '1' and c_grant_id = ? ",
                    MathUtils.numObj2Long(id));

            // 删除即时通讯群成员信息
            baseDao.update("DELETE FROM cos_im_user_group WHERE c_user_id = ?", MathUtils.numObj2Long(id));
        }
    }

    @Override
    public void modifyHeadImg(Long id, String referenceId) {
        baseDao.update("UPDATE cos_sys_personnel p SET p.c_head_img = ? where c_id = ?", referenceId, id);
    }

    @Override
    public List<PersonnelDto> getPersonsByRoleCode(String code) {
        // 得到role
        List<RoleEntity> listByCode = rolePersistence.getListByCode(code);
        if (listByCode.size() > 0) {
            // 得到关联的RolePersonnel
            List<RolePersonnelEntity> personsByRoleId = listByCode.stream().map(BaseEntity::getId)
                    .map(aLong -> rolePersonnelPersistence.getPersonsByRoleId(aLong)).flatMap(Collection::stream)
                    .collect(toList());
            if (personsByRoleId.size() > 0) {
                // 得到关联的personnel
                List<PersonnelEntity> list = personsByRoleId.stream()
                        .map(rolePersonnelEntity -> personnelPersistence.getOne(rolePersonnelEntity.getPersonnelId()))
                        .collect(toList());
                return BeanUtils.convert(list, PersonnelDto.class);
            }
        }
        return null;
    }

    @Override
    public List<PersonnelDto> getAllPersonByDimension(String dimension) {
        //这个方法里面人员里面的orgId没有
        List<PersonnelEntity> list = personnelPersistence.getAllPersonByDimension(dimension);
        if (list.size() > 0) {
            return BeanUtils.convert(list, PersonnelDto.class);
        }
        return new ArrayList<>();
    }


    @Override
    public List<PersonnelDto> getPersonsByOrgId(Long orgId) {
        return BeanUtils.convert(personnelPersistence.getPersons(orgId), PersonnelDto.class);
    }

    @Override
    public void connectRolePersonnel(String personalId, String roleIds) {
        rolePersonnelPersistence.delByPersonnelId(Long.valueOf(personalId));
        if (Assert.isNotEmpty(roleIds)) {
            String[] split = roleIds.split(",");
            for (String roleId : split) {
                RolePersonnelEntity rolePersonnelEntity = new RolePersonnelEntity();
                rolePersonnelEntity.setRoleId(Long.valueOf(roleId));
                rolePersonnelEntity.setPersonnelId(Long.valueOf(personalId));
                rolePersonnelPersistence.save(rolePersonnelEntity);
            }
        }
    }

    @Override
    public List<PersonnelDto> getPersonsByOrgIdAndLetters(Long orgId, String selLetters) {

        List<PersonnelDto> rtnList = null;

        StringBuffer sb = new StringBuffer();
        sb.append(
                "SELECT p.* from cos_sys_personnel p WHERE p.c_id in (SELECT po.c_personnel_id FROM cos_sys_user_org po WHERE po.c_org_id = ${orgId}) ");
        sb.append("AND p.c_deleted = 0 ");

        if (selLetters.length() == 1) {
            sb.append(" and GET_FIRST_PINYIN_CHAR(p.c_name) = ${selLetters} ");
        } else if (selLetters.length() == 2) {
            sb.append(" and GET_TWO_PINYIN_CHAR(p.c_name) = ${selLetters} ");
        } else {
            sb.append(" and GET_PINYIN_CHAR(p.c_name) = ${selLetters} ");
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orgId", orgId);
        params.put("selLetters", selLetters);
        List<Map<String, Object>> resList = baseDao.query(sb.toString(), params);

        if (Assert.isNotEmpty(resList)) {
            rtnList = new ArrayList<PersonnelDto>();
            for (Map<String, Object> map : resList) {
                PersonnelDto dto = new PersonnelDto();
                dto.setId(MathUtils.numObj2Long(map.get("C_ID")));
                dto.setName(MathUtils.stringObj(map.get("C_NAME")));
                dto.setLoginName(MathUtils.stringObj(map.get("C_LOGIN_NAME")));
                rtnList.add(dto);
            }
        } else {
            return new ArrayList<PersonnelDto>();
        }
        return rtnList;
    }

    @Override
    public List<PersonnelDto> getCascadePersonnelByOrg(Long orgId) {
        ArrayList<PersonnelEntity> list = new ArrayList<>();
        OrganizationEntity organizationEntity = organizationPersistence.findOne(orgId);
        if (Assert.isNotEmpty(organizationEntity)) {
            String code = organizationEntity.getCode();
            String cascade = organizationEntity.getCascade();
            List<OrganizationEntity> orgList = organizationPersistence.getTreeList(code, cascade+code+"/");
            for (OrganizationEntity entity : orgList) {
                List<PersonnelEntity> persons = personnelPersistence.findPersonByOrgId(entity.getId());
                if (persons.size() > 0) {
                    list.addAll(persons);
                }
            }
        }
        return BeanUtils.convert(list, PersonnelDto.class);
    }

//	@Override
//	public List<PersonnelDto> vagueQueryPersonList(String search) {
//		List<PersonnelEntity> list = personnelPersistence.vagueQueryPersonList(search);
//		return BeanUtils.convert(list, PersonnelDto.class);
//	}

    @Override
    public List<PersonnelDto> getAllPerson() {
        List<PersonnelEntity> list = personnelPersistence.findAll();
        return BeanUtils.convert(list, PersonnelDto.class);
    }

    @Override
    public List<PersonnelDto> getPersonsByIds(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::new).collect(toList());
        List<PersonnelEntity> list = personnelPersistence.getMovePersonnel(idList);
        return BeanUtils.convert(list, PersonnelDto.class);
    }


    @Override
    public List<PersonnelDto> getDimensionPersonsByIds(String ids, String dimension) {
        //人员里面带orgId
        String sql = " SELECT p.c_id, p.c_name, p.c_sort_no, o.c_id as orgId,o.c_code as orgCode,o.c_dimension FROM cos_sys_personnel p , cos_sys_user_org uo , cos_organization_org o WHERE p.c_id in (%s) and p.c_id = uo.c_personnel_id AND o.c_id = uo.c_org_id AND o.c_dimension = ? ";
        List<Map<String, Object>> query = baseDao.query(String.format(sql, ids), dimension);
        return mapperPersons(query);
    }

    @Override
    public List<PersonnelDto> getDimensionPersonsByIds(String ids) {
        String sql = " SELECT p.c_id, p.c_name, p.c_sort_no, o.c_id as orgId,o.c_code as orgCode,o.c_dimension FROM cos_sys_personnel p , cos_sys_user_org uo , cos_organization_org o WHERE p.c_id in (%s) and p.c_id = uo.c_personnel_id AND o.c_id = uo.c_org_id ";
        List<Map<String, Object>> query = baseDao.query(String.format(sql, ids));
        return mapperPersons(query);
    }

    @Override
    public List<PersonnelDto> getDimensionPersonsByOrgIds(String orgIds) {
        String sql = " SELECT p.c_id, p.c_name, p.c_sort_no, o.c_id as orgId,o.c_code as orgCode,o.c_dimension FROM cos_organization_org o,cos_sys_user_org uo , cos_sys_personnel p     WHERE o.c_id in (%s) and o.c_id = uo.c_org_id and uo.c_personnel_id = p.c_id ";
        List<Map<String, Object>> query = baseDao.query(String.format(sql, orgIds));
        return mapperPersons(query);
    }

    @Override
    public List<PersonnelDto> getDimensionPersonsByName(String personName) {
        String sql = " SELECT p.c_id, p.c_name, p.c_sort_no, o.c_id as orgId,o.c_code as orgCode,o.c_dimension FROM cos_sys_personnel p , cos_sys_user_org uo , cos_organization_org o WHERE p.c_name like ? and p.c_id = uo.c_personnel_id AND o.c_id = uo.c_org_id ";
        List<Map<String, Object>> query = baseDao.query(sql, "%" + personName + "%");
        return mapperPersons(query);
    }

    @Override
    public List<PersonnelDto> getPersonsByRoleIds(String roleIds) {
        //根据角色得到人员
        List<Long> roleList = Arrays.stream(roleIds.split(",")).map(Long::valueOf).collect(toList());
        String ids = rolePersonnelPersistence.getPersonsByRoleIds(roleList).stream().map(r ->
                r.getPersonnelId().toString()).distinct().collect(Collectors.joining(","));

        //人员里面带orgId,dimension
        return this.getDimensionPersonsByIds(ids);
    }

    private List<PersonnelDto> mapperPersons(List<Map<String, Object>> list) {
        return list.stream().map(m -> {
            PersonnelDto dto = new PersonnelDto();
            dto.setId(MathUtils.numObj2Long(m.get("C_ID")));
            dto.setName(m.get("C_NAME").toString());
            dto.setSortNo(MathUtils.numObj2Integer(m.get("C_SORT_NO")));
            dto.setOrgId(MathUtils.numObj2Long(m.get("orgId")));
            dto.setOrgCode(MathUtils.stringObj(m.get("orgCode")));
            dto.setDimension(MathUtils.stringObj(m.get("C_DIMENSION")));
            return dto;
        }).collect(toList());
    }

    @Override
    public List<PersonnelDto> getAllPersons(String dimension, Long orgId, Boolean isCascade) {
        List<PersonnelDto> list;
        if (Assert.isNotEmpty(isCascade) && isCascade) {
            OrganizationEntity one = organizationPersistence.findOne(orgId);
            list = personnelPersistence.getAllPersons(dimension, orgId, "%/" + one.getCode() + "/%");
        } else {
            list = personnelPersistence.getAllPersons(dimension, orgId);
        }
        return list;
    }

	@Override
	public ReturnDto resetPassword(Long id) {
		String password = MD5Util.encode("123456");
		personnelPersistence.resetPassword(id, password);
		return new ReturnDto("重置成功！");
	}
}
