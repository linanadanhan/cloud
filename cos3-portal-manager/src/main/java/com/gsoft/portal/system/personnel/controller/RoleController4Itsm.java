package com.gsoft.portal.system.personnel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.util.Assert;
import com.gsoft.portal.system.organization.service.OrganizationService;
import com.gsoft.portal.system.personnel.dto.RoleDto;
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
@RequestMapping(value = "/role4Itsm")
public class RoleController4Itsm {

    @Autowired
    RoleService roleService;
    @Autowired
    PersonnelService personnelService;
    @Autowired
    PermissionService permissionService;
    @Autowired
    OrganizationService organizationService;


    @ApiOperation("得到所有角色")
    @RequestMapping(value = "/select/getAllRoles", method = RequestMethod.GET)
    ReturnDto getAllRoles() {
        return new ReturnDto(roleService.getAllRoles());
    }
    
    @ApiOperation("根据维度查询角色")
	@RequestMapping(value = "/getRolesByDimension", method = RequestMethod.GET)
	public List<RoleDto> getRolesByDimension(@RequestParam String dimension){
		if(Assert.isEmpty(dimension)){
			return (roleService.getAllRoles());
		}
		return (roleService.getRolesByDimension(dimension));
	}

}
