package com.gsoft.portal.system.organization.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ResponseMessageDto;
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
@RequestMapping(value = "/orgGrant")
public class OrgGrantController {

    @Resource
    OrganizationService organizationService;

    @ApiOperation("根据选择维度得到所有授权机构树")
    @RequestMapping(value = "/getOrgTreeByDimension", method = RequestMethod.GET)
    public List<TreeNode> getOrgTreeByDimension(HttpServletRequest request, @RequestParam(defaultValue = "${default.dimension}") String dimension) {
		String personnelId = request.getHeader("personnelId");
		List<OrganizationDto> item = null;
		
		if (!Assert.isEmpty(personnelId) && "1".equals(personnelId)) {
			 item = organizationService.getTreeListByDimension(dimension);
		} else {
			item = organizationService.getTreeList(personnelId, dimension);
		}
        
        List<TreeNode> tree = TreeUtils.convert(item).attrs("cascade,code,level,id").tree();
        return tree;
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

    @ApiOperation("通过code获取身份信息")
    @RequestMapping(value = "/getIdentityInfoListByOrgCode", method = RequestMethod.GET)
    public PageDto getIdentityInfoListByOrgCode(@RequestParam String code, @RequestParam(required = false) Boolean isCascade,
                                                @RequestParam(required = false) Long id, @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return organizationService.getIdentityInfoListByOrgCode(code, isCascade, id, page, size);
    }

    @ApiOperation("身份与相同维度机构关联保存")
    @RequestMapping(value = "/saveDataGrant", method = RequestMethod.POST)
    public ResponseMessageDto saveDataGrant(@RequestParam Long identityId, @RequestParam String orgIds, @RequestParam(defaultValue = "${default.dimension}") String dimension) {
        return organizationService.saveDataGrant(identityId, orgIds, dimension);
    }

    @ApiOperation("根据身份查找关联机构的ids")
    @RequestMapping(value = "/getOrgIdsByIdentity", method = RequestMethod.GET)
    public List<Long> getOrgIdsByIdentity(@RequestParam Long identityId, @RequestParam(defaultValue = "${default.dimension}") String dimension) {
        return organizationService.getOrgIdsByIdentity(identityId, dimension);
    }

}
