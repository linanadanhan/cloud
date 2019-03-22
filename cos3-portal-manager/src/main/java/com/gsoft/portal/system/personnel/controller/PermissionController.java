package com.gsoft.portal.system.personnel.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.util.Assert;
import com.gsoft.portal.system.personnel.dto.PermissionDto;
import com.gsoft.portal.system.personnel.dto.RoleDto;
import com.gsoft.portal.system.personnel.dto.RolePermissionDto;
import com.gsoft.portal.system.personnel.service.PermissionService;
import com.gsoft.portal.system.personnel.service.RoleService;

import io.swagger.annotations.ApiOperation;

@Api(tags = "权限管理", description = "权限管理接口服务")
@RestController
@RequestMapping(value = "/permission")
public class PermissionController {

    @Autowired
    PermissionService permissionService;

    @Autowired
    RoleService roleService;

    //列表
    /**
     * 1.左侧分类--查询所有权限分类
     * @return
     */
    @ApiOperation("查询权限分类")
    @RequestMapping(value = "/select/getTypes", method = RequestMethod.GET)
    public List<String> getTypes() {
    	//只有平台管理员用这个功能，所以全部都可以看到,但是在角色管理那里，关联权限就不是所有都可以看到
        return permissionService.getTypes();
    }

    @ApiOperation("查询所有权限")
    @RequestMapping(value = "/select/getAllPermission", method = RequestMethod.GET)
    public List<PermissionDto> getAllPermission(){
        return permissionService.getAllPermission();
    }


    /**
     * 2.根据分类查询未授权角色列表
     * @param request
     * @param roleId
     * @return
     */
    @ApiOperation("查询未授权的角色")
    @RequestMapping(value = "/connectd/getHasNoConnectRole", method = RequestMethod.GET)
    public List<RoleDto> getHasNoConnectRole(HttpServletRequest httpRequest, @RequestParam Long permissionId,@RequestParam String type) {
    	//如果是平台管理员，直接根据type查询 
    	//如果是行政区划人员,查询当前行政区划的角色所关联的权限 ，或者登录人可以转授的角色关联权限的总集 ，条件带上type
        String personnelId = httpRequest.getHeader("personnelId"); //登录人ID
        //String currentOrgCode = httpRequest.getHeader("orgCode"); //当前组织机构
        if(Assert.isNotEmpty(personnelId)&&personnelId.equals("1")){//平台管理员，查询所有
        	return roleService.getHasNoConnectListByType(type);
        }
        return roleService.getHasNoConnectRole(Long.valueOf(personnelId), type, permissionId);
    }
    /**
     * 3.根据分类得到已经授权的角色
     * @param roleId
     * @return
     */
    @ApiOperation("查询已经授权的角色")
    @RequestMapping(value = "/connectd/getHasConnectRole", method = RequestMethod.GET)
    public List<RoleDto> getHasConnectRole(HttpServletRequest httpRequest, @RequestParam Long permissionId,@RequestParam String type) {
    	//直接根据type,roleId查询 
        return roleService.getHasConnectRoles(permissionId, type);
    }
    
    @ApiOperation("关联角色与权限")
    @RequestMapping(value = "/connectd/saveRolePermission", method = RequestMethod.POST)
    public void saveRolePermission(@RequestBody List<RolePermissionDto> rolePermissionList) {
    	//先删除，再
        roleService.connectRolePermission1(rolePermissionList);
    }
    
    /**
     * 得到分类列表
     *
     * @return
     */
    @ApiOperation("根据type查询权限分类")
    @RequestMapping(value = "/select/getPageByType", method = RequestMethod.GET)
    public PageDto getPageForTypes(@RequestParam String type, @RequestParam(value = "page", defaultValue = "1") Integer page,
                            @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return permissionService.getPageByType(type, page, size);
    }


    /**
     * 根据ID得到对象
     *
     * @param id
     * @return
     */
    @ApiOperation("根据主键ID得到单个对象")
    @RequestMapping(value = "/select/getOneById", method = RequestMethod.GET)
    public PermissionDto getOneById(@RequestParam Long id) {
        return permissionService.getOneById(id);
    }

    /**
     * 验证权限代码唯一
     *
     * @param id
     * @param code
     * @return
     */
    @ApiOperation("验证权限代码唯一")
    @RequestMapping(value = "/add/validateCode", method = RequestMethod.GET)
    public Boolean validateCode(@RequestParam(required = false) Long id, @RequestParam String code) {
        Boolean flag = permissionService.isExitCode(id, code);
        return !flag;
    }

    @ApiOperation("新增权限")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public PermissionDto save(@RequestBody PermissionDto permissionDto) {
        return permissionService.save(permissionDto);
    }

    /**
     * 删除权限，同时删除角色与权限
     *
     * @param id
     * @return
     */
    @ApiOperation("删除权限")
    @RequestMapping(value = "/delete/deleteById", method = RequestMethod.POST)
    public void deleteById(@RequestParam Long id) {
        permissionService.deleteById(id);
    }
}
