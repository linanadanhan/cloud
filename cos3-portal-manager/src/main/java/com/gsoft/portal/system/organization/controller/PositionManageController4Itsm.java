package com.gsoft.portal.system.organization.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.ResponseMessageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.JsonUtils;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.system.organization.dto.OrgGrantDto;
import com.gsoft.portal.system.organization.dto.PositionDto;
import com.gsoft.portal.system.organization.dto.PositionManageDto;
import com.gsoft.portal.system.organization.entity.PositionEntity;
import com.gsoft.portal.system.organization.service.OrganizationService;
import com.gsoft.portal.system.organization.service.PositionService;
import com.gsoft.portal.system.personnel.dto.PersonnelDto;
import com.gsoft.portal.system.personnel.service.PersonnelService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 职位管理
 *
 * @author plsy
 */
@Api(tags = "职位管理", description = "职位管理接口服务")
@RestController
@RequestMapping(value = "/positionManage4Itsm")
public class PositionManageController4Itsm {

    @Autowired
    PositionService positionService;

    @Autowired
    OrganizationService organizationService;

    @Autowired
    PersonnelService personnelService;

    @ApiOperation("根据机构id得到职位")
    @RequestMapping(value = "/getPositionPageByOrgId", method = RequestMethod.GET)
    public ReturnDto getPositionPageByOrgId(@RequestParam Long orgId, @RequestParam(value = "page", defaultValue = "1") Integer page,
                                          @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return new ReturnDto(positionService.getPositionPageByOrgId(orgId, page, size));
    }

    @ApiOperation("根据机构id得到职位")
    @RequestMapping(value = "/getPositionListByOrgId", method = RequestMethod.GET)
    public ReturnDto getPositionListByOrgId(@RequestParam Long orgId) {
        return new ReturnDto(positionService.getPositionListByOrgId(orgId));
    }
    
    @RequestMapping(value = "/getPositionListByOrg", method = RequestMethod.GET)
    public ReturnDto getPositionListByOrg(@RequestParam Long orgId) {
        return new ReturnDto(positionService.getPositionListByOrg(orgId));
    }

    @ApiOperation("根据用户和机构得到关联的身份信息")
    @RequestMapping(value = "/getIdentityInfoByPersonAndOrg", method = RequestMethod.GET)
    public ReturnDto getIdentityInfoByPersonAndOrg(@RequestParam Long personalId, @RequestParam Long orgId) {
        return new ReturnDto(positionService.getIdentityInfoByPersonAndOrg(personalId, orgId));
    }

    @ApiOperation("关联职位与人员")
    @RequestMapping(value = "/personConnectPosition", method = RequestMethod.GET)
    public ReturnDto personConnectPosition(@RequestParam(required = true) Long personId, @RequestParam(required = true) String positionIds) {
        positionService.personConnectPosition(personId, positionIds);
        return new ReturnDto(ResponseMessageDto.SUCCESS);
    }
    
    @ApiOperation("保存职位管理关系")
    @RequestMapping(value = "/savePositionManage", method = RequestMethod.POST)
    public ReturnDto savePositionManage(@RequestBody PositionManageDto positionManageDto) {
        positionService.savePositionManage(positionManageDto);
        return new ReturnDto(ResponseMessageDto.SUCCESS);
    }
    
    @ApiOperation("关联机构与身份")
    @RequestMapping(value = "/connectd/saveOrgIdentityInfo", method = RequestMethod.POST)
    ReturnDto saveOrgIdentityInfo(@RequestBody List<OrgGrantDto> list) {
        organizationService.saveOrgIdentityInfo(list);
        return new ReturnDto("保存成功!");
    }
    
    /**
     * 生成职位
     */

    @ApiOperation("机构关联岗位生成职位")
    @RequestMapping(value = "/orgConnectPost", method = RequestMethod.GET)
    public ReturnDto orgConnectPost(@RequestParam Long orgId, @RequestParam String postIds) {
        positionService.orgConnectPost(orgId, postIds);
        return new ReturnDto(ResponseMessageDto.SUCCESS);
    }
    
    @ApiOperation("得到机构关联的岗位信息")
    @RequestMapping(value = "/getPostByOrg", method = RequestMethod.GET)
    public ReturnDto getPostByOrg(@RequestParam Long orgId) {
        return new ReturnDto(positionService.getPostByOrg(orgId));
    }
    
    /**
     * 修改排序
     */

    @ApiOperation("修改职位排序")
    @RequestMapping(value = "/modifyPositionSort", method = RequestMethod.POST)
    public ReturnDto modifyPositionSort(@RequestBody String positionDtos) throws IOException {
        List<PositionEntity> list = JsonUtils.fromJsonList(positionDtos, PositionEntity.class);
        positionService.modifyPositionSort(list);
        return new ReturnDto(ResponseMessageDto.SUCCESS);
    }
    
    @ApiOperation("根据职位得到职位管理关系")
    @RequestMapping(value = "/getPositionManage", method = RequestMethod.GET)
    public ReturnDto getPositionManage(@RequestParam Long positionId) {
        return new ReturnDto(positionService.getPositionManage(positionId));
    }
    
    /**
     * 设置上级
     */

    @ApiOperation("根据选择维度得到机构集合")
    @RequestMapping(value = "/getOrgListByDimension", method = RequestMethod.GET)
    public ReturnDto getOrgListByDimension(@RequestParam(defaultValue = "${default.dimension}") String dimension) {
        return new ReturnDto(organizationService.getTreeListByDimension(dimension));
    }
    
    @ApiOperation("根据机构得到身份集合")
    @RequestMapping(value = "/getIdentityListByOrg", method = RequestMethod.GET)
    public ReturnDto getIdentityListByOrg(@RequestParam Long orgId) {
        return new ReturnDto(organizationService.getIdentityListByOrg(orgId));
    }
    
    /**
     * 机构授权
     */

    @ApiOperation("查询未授权的人员")
    @RequestMapping(value = "/connectd/getHasNoConnectIdentity", method = RequestMethod.GET)
    ReturnDto getHasNoConnectIdentity(@RequestParam String orgId, @RequestParam Boolean isCascade, @RequestParam String orgCode, @RequestParam(defaultValue = "${default.dimension}") String dimension) {
        //查询当前组织机构和下级组织机构的人员--剔除掉已授权了的人员
        return new ReturnDto(organizationService.getHasNoConnectIdentity(orgId, isCascade, orgCode, dimension));
    }
    
    @ApiOperation("查询已经授权的人员")
    @RequestMapping(value = "/connectd/getHasConnectIdentity", method = RequestMethod.GET)
    ReturnDto getHasConnectIdentity(@RequestParam String orgId, @RequestParam Boolean isCascade, @RequestParam String orgCode, @RequestParam(defaultValue = "${default.dimension}") String dimension) {
        return new ReturnDto(organizationService.getHasConnectIdentity(orgId, isCascade, orgCode, dimension));
    }

	@ApiOperation("获取对应岗位下的人员树列表")
	@RequestMapping(value = "/getPersonTreeByPostIds", method = RequestMethod.POST)
	public ReturnDto getPersonTreeByPostIds(@RequestBody Map<String, Object> map) {
		@SuppressWarnings("unchecked")
		List<String> chkPostIds = (List<String>) map.get("chkPostIds");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> treeData = (List<Map<String, Object>>) map.get("treeData");
		Set<Long> orgSetList = new HashSet<Long>();
		
		List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();
		Map<String, Object> tmpMap = null;
		for (String postId : chkPostIds) {
			// 获取岗位下的不同机构信息
			List<PositionDto> orgList = positionService.getPositionListByPostId(postId);
			
			if (!Assert.isEmpty(orgList) && orgList.size() > 0) {
				for (PositionDto positionDto : orgList) {
					if (!orgSetList.contains(positionDto.getOrgId())) {
						orgSetList.add(positionDto.getOrgId());
						tmpMap = new HashMap<String, Object>();
						tmpMap.put("orgId", positionDto.getOrgId());
						StringBuffer sb = new StringBuffer();
						getPersonOrgLabel(MathUtils.stringObj(positionDto.getOrgId()), treeData, treeData, sb);
						String orgLabel = "";
						if (sb.length() > 0) {
							orgLabel = sb.substring(0, sb.length()-1);
						}
						if (Assert.isEmpty(orgLabel)) {
							continue;
						}
						tmpMap.put("label", orgLabel);
						tmpMap.put("isIndeterminate", false);
						tmpMap.put("expand", true);
						tmpMap.put("checkAll", false);
						tmpMap.put("checkedUsers", new ArrayList<String>());
						List<PersonnelDto> userList = personnelService.getPersonsByOrgId(MathUtils.numObj2Long(positionDto.getOrgId()));
						if (!Assert.isEmpty(userList) && userList.size() > 0) {
							tmpMap.put("userList", userList);
							rtnList.add(tmpMap);
						}
					}
				}
			}
		}
		return new ReturnDto(rtnList);
	}
	
	/**
	 * 获取人员机构层级展示label
	 * @param orgId
	 * @param treeData
	 * @param treeData1
	 * @param sb
	 */
	@SuppressWarnings("unchecked")
	private void getPersonOrgLabel(String orgId, List<Map<String, Object>> treeData, List<Map<String, Object>> treeData1, StringBuffer sb) {
		for (Map<String, Object> tmpMap : treeData) {
			if (orgId.equals(tmpMap.get("id"))) {
				if (MathUtils.numObj2Long(tmpMap.get("parentId")) != 0l) {
					getPersonOrgLabel(MathUtils.stringObj(tmpMap.get("parentId")), treeData1, treeData1, sb);
				}
				sb.append(tmpMap.get("text")).append("-");
			}
			if (tmpMap.containsKey("children") && !Assert.isEmpty(tmpMap.get("children"))) {
				getPersonOrgLabel(orgId, (List<Map<String, Object>>)tmpMap.get("children"), treeData1, sb);
			}
		}
	}
}
