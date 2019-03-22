package com.gsoft.portal.system.personnel.service.impl;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.cos3.util.JPAUtil;
import com.gsoft.portal.system.personnel.dto.PermissionDto;
import com.gsoft.portal.system.personnel.entity.PermissionEntity;
import com.gsoft.portal.system.personnel.persistence.PermissionPersistence;
import com.gsoft.portal.system.personnel.persistence.RolePermissionPersistence;
import com.gsoft.portal.system.personnel.service.PermissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Resource
    PermissionPersistence permissionPersistence;
    @Resource
    RolePermissionPersistence rolePermissionPersistence;

    @Resource
    BaseDao baseDao;

    @Override
    public PageDto getPageByType(String type, Integer page, Integer size) {
        Pageable pageable = JPAUtil.createPageRequest(page, size, "type", "asc");
        Page<PermissionEntity> listByType = null;
        if (Assert.isEmpty(type)) {
            listByType = permissionPersistence.getPages(pageable);
        } else {
            listByType = permissionPersistence.getPageByType(type, pageable);
        }
        return new PageDto(listByType);
    }

    @Override
    public PermissionDto getOneById(Long id) {
        PermissionEntity entity = permissionPersistence.findOne(id);
        if (Assert.isNotEmpty(entity)) {
            return BeanUtils.convert(entity, PermissionDto.class);
        }
        return null;
    }

    @Override
    public Boolean isExitCode(Long id, String code) {
        PermissionEntity entity = null;
        if (Assert.isNotEmpty(id)) {
            entity = permissionPersistence.isExitByCode(id, code);
        } else {
            entity = permissionPersistence.isExitByCode(code);
        }
        if (Assert.isNotEmpty(entity)) {
            return true;
        }
        return false;
    }

    @Override
    public PermissionDto save(PermissionDto permissionDto) {
        PermissionEntity entity = null;
        if (Assert.isNotEmpty(permissionDto)) {
            entity = permissionPersistence.save(BeanUtils.convert(permissionDto, PermissionEntity.class));
        }
        if (Assert.isNotEmpty(entity)) {
            return BeanUtils.convert(entity, PermissionDto.class);
        }
        return null;
    }

    //删除权限，同时删除权限角色关联
    @Override
    public void deleteById(Long id) {
        permissionPersistence.delete(id);
        rolePermissionPersistence.delByPermissionId(id);
    }

    @Override
    public List<PermissionDto> getHasConnectPermission(Long roleId, String type) {
        List<PermissionEntity> entityList = null;
        if (Assert.isEmpty(type)) {
            entityList = permissionPersistence.getHasConnectPermission(roleId);

        } else {
            entityList = permissionPersistence.getHasConnectPermission(roleId, type);

        }
        if (entityList == null) {
            return new ArrayList<PermissionDto>();
        }
        return BeanUtils.convert(entityList, PermissionDto.class);
    }

    @Override
    public List<PermissionDto> getHasNoConnectListByType(String type) {
        List<PermissionEntity> listByType = null;
        if (Assert.isEmpty(type)) {
            listByType = permissionPersistence.getList();
        } else {
            listByType = permissionPersistence.getListByType(type);
        }
        if (listByType == null) {
            return new ArrayList<PermissionDto>();
        }
        return BeanUtils.convert(listByType, PermissionDto.class);
    }

    @Override
    public List<String> getTypes() {
        List<String> list = permissionPersistence.getTypes();
        if (list == null) {
            return new ArrayList<String>();
        }
        return list;
    }

    @Override
    public List<String> getIncludeResources() {
        return null;
    }

    @Override
    public List<String> getExcludeResources() {
        return null;
    }

    @Override
    public List<String> getTypesByLoginPersonnel(Long personnelId) {
        List<String> returnList = new ArrayList<String>();
        List<String> list = permissionPersistence.getTypesByLoginPersonnel(personnelId);
        if (list != null && list.size() > 0) {
            if (list.size() > 1) {
                returnList.add("全部");
            }
            returnList.addAll(list);
        }
        return returnList;
    }

    @Override
    public List<PermissionDto> getHasNoConnectPermission(Long personnelId, String type, Long roleId) {
        List<PermissionEntity> entityList = null;
        if (Assert.isEmpty(type)) {
            entityList = permissionPersistence.getHasNoConnectPermission(personnelId);

        } else {
            entityList = permissionPersistence.getHasNoConnectPermission(personnelId, type);

        }
        if (entityList == null) {
            return new ArrayList<PermissionDto>();
        }

        return BeanUtils.convert(entityList, PermissionDto.class);
    }

    @Override
    public List<PermissionDto> getAllPermission() {
        List<PermissionEntity> list = permissionPersistence.findAll();
        if (list == null) {
            return new ArrayList<PermissionDto>();
        }
        return BeanUtils.convert(list, PermissionDto.class);
    }

}
