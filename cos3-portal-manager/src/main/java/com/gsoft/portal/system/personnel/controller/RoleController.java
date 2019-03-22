package com.gsoft.portal.system.personnel.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.tree.TreeNode;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.TreeUtils;
import com.gsoft.portal.system.organization.dto.OrganizationDto;
import com.gsoft.portal.system.organization.service.OrganizationService;
import com.gsoft.portal.system.personnel.dto.PermissionDto;
import com.gsoft.portal.system.personnel.dto.PersonnelDto;
import com.gsoft.portal.system.personnel.dto.RoleDto;
import com.gsoft.portal.system.personnel.dto.RolePermissionDto;
import com.gsoft.portal.system.personnel.dto.RolePersonnelDto;
import com.gsoft.portal.system.personnel.service.PermissionService;
import com.gsoft.portal.system.personnel.service.PersonnelService;
import com.gsoft.portal.system.personnel.service.RoleService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * 角色类
 *
 * @author SN
 */
@Api(tags = "角色管理", description = "角色管理接口服务")
@RestController
@RequestMapping(value = "/role")
public class RoleController {

    @Autowired
    RoleService roleService;
    @Autowired
    PersonnelService personnelService;
    @Autowired
    PermissionService permissionService;
    @Autowired
    OrganizationService organizationService;

    /**
     * 1.得到左侧角色分类
     *
     * @param httpServletRequest
     * @return
     */
    @ApiOperation("查询角色分类")
    @RequestMapping(value = "/select/getTypes", method = RequestMethod.GET)
    public List<String> getTypes(HttpServletRequest httpServletRequest) {
        return roleService.getRoleTypes();
    }
    
    @ApiOperation("查询角色分类")
    @RequestMapping(value = "/getRoleTypes", method = RequestMethod.GET)
    public ReturnDto getRoleTypes() {
    	return new ReturnDto(roleService.getRoleTypes());
    }

    /**
     * 2.根据分类查询角色列表--分页
     *
     * @param type
     * @param page
     * @param size
     * @return
     */
    @ApiOperation("根据行政区划查询此区划的所有角色列表")
    @RequestMapping(value = "/select/getPage", method = RequestMethod.GET)
    public PageDto getListByAreaCode(HttpServletRequest httpRequest, @RequestParam String type, @RequestParam(value = "page", defaultValue = "1") Integer page,
                              @RequestParam(value = "size", defaultValue = "10") Integer size) {

        return roleService.getListByCreateBy(Long.valueOf(httpRequest.getHeader("personnelId")), type, page, size);
    }


    @ApiOperation("得到所有角色")
    @RequestMapping(value = "/select/getAllRoles", method = RequestMethod.GET)
    public List<RoleDto> getAllRoles() {
        return roleService.getAllRoles();
    }
    
    @ApiOperation("验证角色代码唯一")
    @RequestMapping(value = "/add/validateCode", method = RequestMethod.GET)
    public Boolean validateCode(@RequestParam(required = false) Long id, @RequestParam String code) {
        Boolean flag = roleService.isExitCode(id, code);
        return !flag;
    }

    /**
     * 新增
     *
     * @param request
     * @param roleDto
     * @return
     */
    @ApiOperation("新增角色")
    @RequestMapping(value = "/add/save", method = RequestMethod.POST)
    public RoleDto save(ServletRequest request, @RequestBody RoleDto roleDto) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String personnelId = httpRequest.getHeader("personnelId");//得到当前登录人的人员编号

        if (Assert.isEmpty(roleDto.getId())) {
            roleDto.setCreateBy(Long.valueOf(personnelId));
            roleDto.setCreateTime(new Date());
        } else {
            roleDto.setCreateBy(Long.valueOf(personnelId));
            roleDto.setCreateTime(new Date());
        }
        return roleService.save(roleDto);
    }

    /**
     * 逻辑删除
     *
     * @param id
     */
    @ApiOperation("删除角色")
    @RequestMapping(value = "/delete/deleteById", method = RequestMethod.POST)
    public void deleteById(@RequestParam Long id) {
        roleService.deleteById(id);
    }

    /**
     * 批量逻辑删除
     *
     * @param ids
     */
    @ApiOperation("批量逻辑删除")
    @RequestMapping(value = "/delete/batchDeleteByIds", method = RequestMethod.POST)
    public void batchDeleteByIds(@RequestParam String ids) {
        roleService.batchDeleteByIds(ids);
    }

    /**
     * 停用或启用
     *
     * @param id
     * @param status
     */
    @ApiOperation("修改可用状态")
    @RequestMapping(value = "/update/updateStatus", method = RequestMethod.POST)
    public void updateStatus(@RequestParam Long id, @RequestParam Boolean status) {
        roleService.updateStatus(id, status);
    }

    @ApiOperation("查询所有权限分类")
    @RequestMapping(value = "/connectd/getPermissionTypes", method = RequestMethod.GET)
    public List<String> getPermissionTypes(HttpServletRequest httpRequest) {
        //如果是平台管理员，查询所有
        //如果是其他则查询登录人可以转授的角色关联权限的总集
        String personnelId = httpRequest.getHeader("personnelId"); //登录人ID

        if (Assert.isNotEmpty(personnelId) && personnelId.equals("1")) {//系统管理员，查询所有
            return permissionService.getTypes();
        }

        return permissionService.getTypesByLoginPersonnel(Long.valueOf(personnelId));
    }

    @ApiOperation("查询未授权的权限")
    @RequestMapping(value = "/connectd/getHasNoConnectPermission", method = RequestMethod.GET)
    public List<PermissionDto> getHasNoConnectPermission(HttpServletRequest httpRequest, @RequestParam Long roleId, @RequestParam String type) {
        //如果是平台管理员，直接根据type查询
        //如果是其他则查询登录人可以转授的角色关联权限的总集 ，条件带上type
        String personnelId = httpRequest.getHeader("personnelId"); //登录人ID

        if (Assert.isNotEmpty(personnelId) && personnelId.equals("1")) {//平台管理员，查询所有
            return permissionService.getHasNoConnectListByType(type);
        }
        return permissionService.getHasNoConnectPermission(Long.valueOf(personnelId), type, roleId);
    }

    /**
     * 3.根据分类得到已经授权的权限
     *
     * @param roleId
     * @return
     */
    @ApiOperation("查询已经授权的权限")
    @RequestMapping(value = "/connectd/getHasConnectPermission", method = RequestMethod.GET)
    public List<PermissionDto> getHasConnectPermission(HttpServletRequest httpRequest, @RequestParam Long roleId, @RequestParam String type) {
        //直接根据type,roleId查询
        return permissionService.getHasConnectPermission(roleId, type);
    }

    @ApiOperation("关联角色与权限")
    @RequestMapping(value = "/connectd/saveRolePermission", method = RequestMethod.POST)
    public void saveRolePermission(@RequestBody List<RolePermissionDto> rolePermissionList) {
        //先删除，再新增
        roleService.connectRolePermission(rolePermissionList);
    }

    @ApiOperation("关联角色与权限")
    @RequestMapping(value = "/connectd/saveRolePermissionByIds", method = RequestMethod.POST)
    public void saveRolePermission(@RequestParam Long roleId, @RequestParam String permissionIds) {
        roleService.connectRolePermission(roleId, permissionIds);
    }

    @ApiOperation("通过登录人所在组织机构获取组织机构列表")
    @RequestMapping(value = "/connect/getTreeByCode", method = RequestMethod.GET)
    public List<TreeNode> getTreeByCode(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String personnelId = request.getHeader("personnelId");

        List<OrganizationDto> item = organizationService.getTreeList(personnelId);
        if (item == null) {
            return new ArrayList<TreeNode>();
        }
        return TreeUtils.convert(item).attrs("cascade,code,level,id").tree();
    }

    @ApiOperation("查询未授权的人员")
    @RequestMapping(value = "/connectd/getHasNoConnectPerson", method = RequestMethod.POST)
    public List<PersonnelDto> getHasNoConnectPerson(HttpServletRequest request, @RequestParam String orgCode, @RequestParam Boolean isCascade) {
        //查询当前组织机构和下级组织机构的人员--剔除掉已授权了的人员
        String personnelId = request.getHeader("personnelId"); // 登录人ID
        return personnelService.getHasNoConnectPerson(orgCode, isCascade, personnelId);
    }

    @ApiOperation("查询已经授权的人员")
    @RequestMapping(value = "/connectd/getHasConnectPerson", method = RequestMethod.POST)
    public List<PersonnelDto> getHasConnectPerson(HttpServletRequest request, @RequestParam String orgCode, @RequestParam Boolean isCascade, @RequestParam Long roleId) {
        String personnelId = request.getHeader("personnelId"); // 登录人ID
        return personnelService.getHasConnectPerson(orgCode, isCascade, roleId, personnelId);
    }

    @ApiOperation("关联角色与人员")
    @RequestMapping(value = "/connectd/saveRolePersonnel", method = RequestMethod.POST)
    public void saveRolePersonnel(@RequestBody List<RolePersonnelDto> list) {
        roleService.saveConnectRolePersonnel(list);
    }

    @ApiOperation("根据role得到授权的人员")
    @RequestMapping(value = "/getPersonsByRoleCode", method = RequestMethod.GET)
    public List<PersonnelDto> getPersonsByRoleCode(@RequestParam String code) {
        return personnelService.getPersonsByRoleCode(code);
    }
    
	@ApiOperation("根据类型查询角色")
	@RequestMapping(value = "/getRolesByType", method = RequestMethod.GET)
	public ReturnDto getRolesByType( @RequestParam String type){
		if(Assert.isEmpty(type)){
			return new ReturnDto(roleService.getAllRoles());
		}
		return new ReturnDto(roleService.getRolesByType(type));
	}

	@ApiOperation("根据维度查询角色")
	@RequestMapping(value = "/getRolesByDimension", method = RequestMethod.GET)
	public List<RoleDto> getRolesByDimension(@RequestParam String role_dimension){
		if(Assert.isEmpty(role_dimension)){
			return (roleService.getAllRoles());
		}
		return (roleService.getRolesByDimension(role_dimension));
	}

	@ApiOperation("根据类型、维度和角色名模糊查询角色")
	@RequestMapping(value = "/getRolesByTypeAndDemensionAndName", method = RequestMethod.GET)
	public ReturnDto getRolesByTypeAndDemensionAndName(@RequestParam (required = false) String type,@RequestParam
			(required = false) String role_dimension, @RequestParam (required = false) String roleName){
		if(Assert.isEmpty(roleName)&&Assert.isEmpty(type)&&Assert.isEmpty(role_dimension)){
			return new ReturnDto(roleService.getAllRoles());
		}
		return new ReturnDto(roleService.getRolesByTypeAndDemensionAndName(type,role_dimension,roleName));
	}

}
