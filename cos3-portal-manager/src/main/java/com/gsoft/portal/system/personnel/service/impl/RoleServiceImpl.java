package com.gsoft.portal.system.personnel.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.cos3.util.JPAUtil;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.system.personnel.dto.RoleDto;
import com.gsoft.portal.system.personnel.dto.RolePermissionDto;
import com.gsoft.portal.system.personnel.dto.RolePersonnelDto;
import com.gsoft.portal.system.personnel.entity.RoleEntity;
import com.gsoft.portal.system.personnel.entity.RolePermissionEntity;
import com.gsoft.portal.system.personnel.entity.RolePersonnelEntity;
import com.gsoft.portal.system.personnel.persistence.PersonnelPersistence;
import com.gsoft.portal.system.personnel.persistence.RolePermissionPersistence;
import com.gsoft.portal.system.personnel.persistence.RolePersistence;
import com.gsoft.portal.system.personnel.persistence.RolePersonnelPersistence;
import com.gsoft.portal.system.personnel.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    RolePersistence rolePersistence;
    @Resource
    RolePersonnelPersistence rolePersonnelPersistence;
    @Resource
    RolePermissionPersistence rolePermissionPersistence;

    @Resource
    PersonnelPersistence personnelPersistence;

    @Resource
    BaseDao baseDao;

    @Override
    public List<String> getRoleTypes() {
        List<String> list = rolePersistence.getRoleTypes();
        if (list == null) {
            return new ArrayList<String>();
        }
        return list;
    }

    @Override
    public PageDto getListByCreateBy(Long createBy, String type, Integer page, Integer size) {
        Pageable pageable = JPAUtil.createPageRequest(page, size, "type", "asc"); //这里排序可以根据多个查吗
        Page<RoleEntity> dtoList = null;
        if (Assert.isEmpty(type)) {
            dtoList = rolePersistence.getAllListByCreateBy(createBy, pageable);
        } else {
            dtoList = rolePersistence.getAllListByCreateBy(createBy, type, pageable);
        }
        return new PageDto(dtoList);
    }

    @Override
    public Boolean isExitCode(Long id, String code) {
        RoleEntity entity = null;
        if (Assert.isNotEmpty(id)) {
            entity = rolePersistence.isExitByCode(id, code);
        } else {
            entity = rolePersistence.isExitByCode(code);
        }
        if (Assert.isNotEmpty(entity)) { //不为空，代表存在，返回true
            return true;
        }
        return false;
    }

    @Override
    public RoleDto save(RoleDto roleDto) {
        if (Assert.isEmpty(roleDto)) {
            return null;
        }
        RoleEntity entity = rolePersistence.save(BeanUtils.convert(roleDto, RoleEntity.class));
        if (Assert.isNotEmpty(entity)) {
            return BeanUtils.convert(entity, RoleDto.class);
        }
        return null;
    }

    //删除角色，还要删除角色与人员，角色与权限关联
    @Override
    public void deleteById(Long id) {
        rolePersistence.delById(id); //逻辑删除
        rolePersonnelPersistence.delByRoleId(id);
        rolePermissionPersistence.delByRoleId(id);
    }

    @Override
    public void updateStatus(Long id, Boolean status) {
        rolePersistence.updateStatus(id, status);
    }

    @Override
    public void saveConnectRolePersonnel(List<RolePersonnelDto> list) {
        if (list != null && list.size() > 0) {
            RolePersonnelDto dto = list.get(0);
            if (Assert.isEmpty(dto)) {
                return;
            }

            StringBuffer sb = new StringBuffer();
            sb.append("DELETE FROM cos_sys_role_personal WHERE c_role_id = " + dto.getRoleId() + " AND c_personnel_id IN ");
            sb.append("(SELECT pp.c_id FROM cos_organization_org o,cos_sys_user_org po,cos_sys_personnel pp ");
            sb.append("WHERE o.c_id = po.c_org_id AND po.c_personnel_id = pp.c_id  ");

            if (dto.getIsCascade()) {  //删除选中的组织机构的人员与该角色的关联
                sb.append(" AND o.c_cascade like '%" + dto.getOrgCode() + "%' )");
            } else {
                sb.append(" AND o.c_code = '" + dto.getOrgCode() + "' )");
            }

            baseDao.update(sb.toString());

            if (Assert.isNotEmpty(dto.getPersonnelId())) { //因为有可能列表是空的
                rolePersonnelPersistence.save(BeanUtils.convert(list, RolePersonnelEntity.class));
            }
        }
    }

    @Override
    public void connectRolePermission(List<RolePermissionDto> list) {
        //删除该角色关联的某类型的权限,根据角色和权限类型删
        //如果类型为空，就根据角色删除
        if (list != null && list.size() > 0) {
//        	RolePermissionDto dto=list.get(0);
            if (Assert.isEmpty(list.get(0).getType())) {
                rolePermissionPersistence.deleteByRoleId(list.get(0).getRoleId());
            } else {
                rolePermissionPersistence.deleteByRoleId(list.get(0).getRoleId(), list.get(0).getType());
            }
            if (Assert.isNotEmpty(list.get(0).getPermissionId())) {
                rolePermissionPersistence.save(BeanUtils.convert(list, RolePermissionEntity.class));
            }
        }

    }

    @Override
    public void connectRolePermission(Long roleId, String permissionIds) {
        rolePermissionPersistence.deleteByRoleId(roleId);
        if (Assert.isNotEmpty(permissionIds)) {
            String[] split = permissionIds.split(",");
            for (String permissionId : split) {
                RolePermissionEntity entity = new RolePermissionEntity();
                entity.setRoleId(roleId);
                entity.setPermissionId(Long.valueOf(permissionId));
                rolePermissionPersistence.save(entity);
            }
        }
    }

    @Override
    public void connectRolePermission1(List<RolePermissionDto> list) {
        //删除该角色关联的某类型的权限,根据角色和权限类型删
        //如果类型为空，就根据角色删除
        if (list != null && list.size() > 0) {
//        	RolePermissionDto dto=list.get(0);
            if (Assert.isEmpty(list.get(0).getType())) {
                rolePermissionPersistence.deleteByPermissionId(list.get(0).getPermissionId());
            } else {
                rolePermissionPersistence.deleteByPermissionId(list.get(0).getPermissionId(), list.get(0).getType());
            }
            if (Assert.isNotEmpty(list.get(0).getRoleId())) {
                rolePermissionPersistence.save(BeanUtils.convert(list, RolePermissionEntity.class));
            }
        }

    }

    @Override
    public void deleteConnectRole(Long personnelId) {
        rolePersonnelPersistence.delByPersonnelId(personnelId);
    }

    @Override
    public List<RoleDto> getAllRoles() {
        List<RoleEntity> all = rolePersistence.findAll();
        if (all.size() > 0) {
            return BeanUtils.convert(all, RoleDto.class);
        }
        return new ArrayList<>();
    }

    @Override
    public void batchDeleteByIds(String ids) {
        if (Assert.isNotEmpty(ids)) {
            String[] split = ids.split(",");
            for (String roleId : split) {
                deleteById(Long.valueOf(roleId));
            }
        }
    }

    @Override
    public List<String> getHasNoConnectRoleTypes(Long loginPersonnelId) {
        List<String> returnList = new ArrayList<String>();
        List<String> list = rolePersistence.getHasNoConnectRoleTypes(loginPersonnelId, loginPersonnelId);
        if (list != null && list.size() > 0) {
            if (list.size() > 1) {
                returnList.add("全部");
            }
            returnList.addAll(list);
        }
        return returnList;
    }

    @Override
    public List<RoleDto> getHasNoConnectRole(Long loginPersonnelId, String type) {
        List<RoleEntity> entityList = null;
        if (Assert.isEmpty(type)) {
            entityList = rolePersistence.getHasNoConnectRole(loginPersonnelId, loginPersonnelId);
        } else {
            entityList = rolePersistence.getHasNoConnectRole(loginPersonnelId, loginPersonnelId, type);
        }
        if (entityList == null) {
            return new ArrayList<RoleDto>();
        }
        return BeanUtils.convert(entityList, RoleDto.class);
    }


    @Override
    public List<RoleDto> getHasConnectRole(Long personnelId, String type) {
        List<RoleDto> list = null;
        if (Assert.isEmpty(type)) {
            list = rolePersistence.getHasConnectRole(personnelId);
        } else {
            list = rolePersistence.getHasConnectRole(personnelId, type);
        }
        if (list == null) {
            list = new ArrayList<RoleDto>();
        }
        return list;
    }


    @Override
    public List<RoleDto> getHasNoConnectListByType(String type) {
        List<RoleEntity> listByType = null;
        if (Assert.isEmpty(type)) {
            listByType = rolePersistence.getList();
        } else {
            listByType = rolePersistence.getListByType(type);
        }
        if (listByType == null) {
            return new ArrayList<RoleDto>();
        }
        return BeanUtils.convert(listByType, RoleDto.class);
    }


    @Override
    public List<RoleDto> getHasNoConnectRole(Long loginPersonId, String type,
                                             Long permissionId) {
        List<RoleEntity> resList = null;

        //查询登录人员所属组织机构信息
        Map<String, Object> params = new HashMap<String, Object>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT o.* FROM cos_organization_org o,cos_sys_user_org po,cos_sys_personnel p ");
        sb.append("WHERE o.c_id = po.c_org_id AND po.c_personnel_id = p.c_id ");
        sb.append("AND p.c_id = ${personnelId} ");

        params.put("personnelId", loginPersonId);

        List<Map<String, Object>> orgList = baseDao.query(sb.toString(), params);

        if (!Assert.isEmpty(orgList)) {

            resList = new ArrayList<RoleEntity>();

            for (Map<String, Object> map : orgList) {
                if (Assert.isEmpty(type)) {
                    List<RoleEntity> entityList = rolePersistence.getHasNoConnectRole(MathUtils.stringObj(map.get("C_CODE")), loginPersonId);
                    resList.addAll(entityList);
                } else {
                    List<RoleEntity> entityList = rolePersistence.getHasNoConnectRole(MathUtils.stringObj(map.get("C_CODE")), loginPersonId, type);
                    resList.addAll(entityList);
                }
            }
        }

        if (resList == null) {
            return new ArrayList<RoleDto>();
        }
        return BeanUtils.convert(resList, RoleDto.class);
    }


    @Override
    public List<RoleDto> getHasConnectRoles(Long permissionId, String type) {
        List<RoleEntity> entityList = null;
        if (Assert.isEmpty(type)) {
            entityList = rolePersistence.getHasConnectPermission(permissionId);

        } else {
            entityList = rolePersistence.getHasConnectPermission(permissionId, type);

        }
        if (entityList == null) {
            return new ArrayList<RoleDto>();
        }
        return BeanUtils.convert(entityList, RoleDto.class);
    }

    @Override
    public List<RoleDto> getRolesByIds(String ids){
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::new).collect(Collectors.toList());
        List<RoleEntity> list = rolePersistence.getRolesByIds(idList);
        return BeanUtils.convert(list,RoleDto.class);
    }

    @Override
    public List<RoleDto> getRolesByType(String type) {
        List<RoleEntity> list = rolePersistence.getListByType(type);
        return BeanUtils.convert(list,RoleDto.class);
    }

    @Override
    public List<RoleDto> getRolesByDimension(String role_dimension) {
        List<RoleEntity> list = rolePersistence.getRolesByDimension(role_dimension);
        return BeanUtils.convert(list,RoleDto.class);
    }

    @Override
    public List<RoleDto> getRolesByTypeAndDemensionAndName(String type,String role_dimension,String roleName) {
        Map<String, Object> params = new HashMap<String, Object>();
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT * FROM `cos_sys_role` WHERE 1=1 ");
        if(!Assert.isEmpty(type)){
           sb.append("AND c_type =${type}" );
           params.put("type",type);
        }
        if(!Assert.isEmpty(role_dimension)){
            sb.append("AND c_dimension =${role_dimension} ");
            params.put("role_dimension",role_dimension);
        }
        if(!Assert.isEmpty(roleName)){
            sb.append("AND c_name like ${roleName} ");
            params.put("roleName","%"+roleName+"%");
        }
        List<Map<String, Object>> roleList = baseDao.query(sb.toString(), params);
        List<RoleDto> collect = roleList.stream().map(m -> {
            RoleDto selDto = new RoleDto();
            Long orgId = MathUtils.numObj2Long(m.get("C_ID"));
            selDto.setId(orgId);
            selDto.setCode(MathUtils.stringObj(m.get("C_CODE")));
            selDto.setName(MathUtils.stringObj(m.get("C_NAME")));
            selDto.setType(MathUtils.stringObj(m.get("C_TYPE")));
            return selDto;
        }).collect(Collectors.toList());
        return BeanUtils.convert(collect,RoleDto.class);
    }

}
