package com.gsoft.portal.system.organization.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.tree.TreeNode;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.TreeUtils;
import com.gsoft.portal.system.organization.dto.OrganizationDto;
import com.gsoft.portal.system.organization.service.OrganizationService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 机构授权
 *
 * @author plsy
 */
@Api(tags = "机构授权", description = "组织机构授权接口服务")
@RestController
@RequestMapping(value = "/orgGrant4Itsm")
public class OrgGrantController4Itsm {

    @Resource
    OrganizationService organizationService;

    @ApiOperation("根据选择维度得到所有授权机构树")
    @RequestMapping(value = "/getOrgTreeByDimension", method = RequestMethod.GET)
    public ReturnDto getOrgTreeByDimension(HttpServletRequest request, @RequestParam(defaultValue = "${default.dimension}") String dimension,@RequestParam(required = false) String orgType) {
		String personnelId = request.getHeader("personnelId");
		List<OrganizationDto> item = null;
		
		if (!Assert.isEmpty(personnelId) && "1".equals(personnelId)) {
			 item = organizationService.getTreeListByDimension(dimension);
		} else {
			if(Assert.isNotEmpty(orgType)) {
				item = organizationService.getTreeList(personnelId, dimension,orgType);
			}else {
				item = organizationService.getTreeList(personnelId, dimension);
			}
			
		}
        
        List<TreeNode> tree = TreeUtils.convert(item).attrs("cascade,code,level,id").tree();
        return new ReturnDto(tree);
    }

    /**
     * 门户widget用
     *
     * @param dimension
     * @return
     */
    @ApiOperation("根据选择维度得到所有授权机构树")
    @RequestMapping(value = "/getOrgTree4Dimension", method = RequestMethod.GET)
    public ReturnDto getOrgTreeByDimension4Widget(@RequestParam(defaultValue = "${default.dimension}") String dimension) {
        List<OrganizationDto> item = organizationService.getTreeListByDimension(dimension);
        if (item == null) {
            return new ReturnDto(new ArrayList<TreeNode>());
        }
        List<TreeNode> tree = TreeUtils.convert(item).attrs("cascade,code,level,id").tree();
        return new ReturnDto(tree);
    }
    
    @ApiOperation("根据选择维度得到所有授权机构")
    @RequestMapping(value = "/getOrgByDimension", method = RequestMethod.GET)
    public ReturnDto getOrgDimension(HttpServletRequest request, @RequestParam(defaultValue = "${default.dimension}") String dimension,@RequestParam(required = false) String orgType) {
		String personnelId = request.getHeader("personnelId");
		List<OrganizationDto> item = null;
		item = organizationService.getList(personnelId, dimension,orgType);
        return new ReturnDto(item);
    }


}
