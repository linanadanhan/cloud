package com.gsoft.portal.system.organization.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ResponseMessageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.tree.TreeNode;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.JsonUtils;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.cos3.util.TreeUtils;
import com.gsoft.portal.system.basicdata.dto.DictionaryItemDto;
import com.gsoft.portal.system.organization.dto.IdentityInfoDto;
import com.gsoft.portal.system.organization.dto.OrgGrantDto;
import com.gsoft.portal.system.organization.dto.OrganizationDto;
import com.gsoft.portal.system.organization.dto.PositionDto;
import com.gsoft.portal.system.organization.dto.PositionManageDto;
import com.gsoft.portal.system.organization.entity.IdentityInfoEntity;
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
@RequestMapping(value = "/positionManage")
public class PositionManageController {

    @Autowired
    PositionService positionService;

    @Autowired
    OrganizationService organizationService;

    @Autowired
    PersonnelService personnelService;

    /**
     * 初始化
     */

    @ApiOperation("根据当前人员得到授权管理的机构树")
    @RequestMapping(value = "/getOrgTreeByPerson", method = RequestMethod.GET)
    public List<TreeNode> getOrgTreeByPerson(HttpServletRequest request) {
        List<OrganizationDto> item = positionService.getTreeListByAuthorization(request.getHeader("personnelId"));
        if (item == null) {
            return new ArrayList<TreeNode>();
        }
        List<TreeNode> tree = TreeUtils.convert(item).attrs("cascade,code,level,id,dimension").tree();
        return tree;
    }

    @ApiOperation("根据机构id得到职位")
    @RequestMapping(value = "/getPositionPageByOrgId", method = RequestMethod.GET)
    public PageDto getPositionPageByOrgId(@RequestParam Long orgId, @RequestParam(value = "page", defaultValue = "1") Integer page,
                                          @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return positionService.getPositionPageByOrgId(orgId, page, size);
    }

    @ApiOperation("根据机构id得到职位")
    @RequestMapping(value = "/getPositionListByOrgId", method = RequestMethod.GET)
    public List<PositionDto> getPositionListByOrgId(@RequestParam Long orgId) {
        return positionService.getPositionListByOrgId(orgId);
    }

    /**
     * 生成职位
     */

    @ApiOperation("机构关联岗位生成职位")
    @RequestMapping(value = "/orgConnectPost", method = RequestMethod.POST)
    public ResponseMessageDto orgConnectPost(@RequestParam Long orgId, @RequestParam String postIds) {
        positionService.orgConnectPost(orgId, postIds);
        return ResponseMessageDto.SUCCESS;
    }

    @ApiOperation("得到机构关联的岗位信息")
    @RequestMapping(value = "/getPostByOrg", method = RequestMethod.GET)
    public List<DictionaryItemDto> getPostByOrg(@RequestParam Long orgId) {
        return positionService.getPostByOrg(orgId);
    }

    /**
     * 修改排序
     */

    @ApiOperation("修改职位排序")
    @RequestMapping(value = "/modifyPositionSort", method = RequestMethod.POST)
    public ResponseMessageDto modifyPositionSort(@RequestBody String positionDtos) throws IOException {
        List<PositionEntity> list = JsonUtils.fromJsonList(positionDtos, PositionEntity.class);
        positionService.modifyPositionSort(list);
        return ResponseMessageDto.SUCCESS;
    }

    /**
     * 设置上级
     */

    @ApiOperation("根据选择维度得到机构集合")
    @RequestMapping(value = "/getOrgListByDimension", method = RequestMethod.GET)
    public List<OrganizationDto> getOrgListByDimension(@RequestParam(defaultValue = "${default.dimension}") String dimension) {
        return organizationService.getTreeListByDimension(dimension);
    }

    @ApiOperation("根据机构得到身份集合")
    @RequestMapping(value = "/getIdentityListByOrg", method = RequestMethod.GET)
    public List<IdentityInfoDto> getIdentityListByOrg(@RequestParam Long orgId) {
        return organizationService.getIdentityListByOrg(orgId);
    }

    @ApiOperation("根据机构得到职位集合")
    @RequestMapping(value = "/getPositionListByOrg", method = RequestMethod.GET)
    public List<PositionDto> getPositionListByOrg(@RequestParam Long orgId) {
        return positionService.getPositionListByOrg(orgId);
    }

    @ApiOperation("保存职位管理关系")
    @RequestMapping(value = "/savePositionManage", method = RequestMethod.POST)
    public ResponseMessageDto savePositionManage(@RequestBody PositionManageDto positionManageDto) {
        positionService.savePositionManage(positionManageDto);
        return ResponseMessageDto.SUCCESS;
    }

    @ApiOperation("根据职位得到职位管理关系")
    @RequestMapping(value = "/getPositionManage", method = RequestMethod.GET)
    public PositionManageDto getPositionManage(@RequestParam Long positionId) {
        return positionService.getPositionManage(positionId);
    }

    /**
     * 添加用户
     */

    @ApiOperation("模糊查询用户")
    @RequestMapping(value = "/vagueQueryPerson", method = RequestMethod.GET)
    public List<PersonnelDto> vagueQueryPerson(@RequestParam String condition, @RequestParam String positionId) {
        return personnelService.vagueQueryPerson(condition, positionId);
    }

    @ApiOperation("根据职位得到关联的身份信息")
    @RequestMapping(value = "/getIdentityInfoByPosition", method = RequestMethod.GET)
    public PageDto getIdentityInfoByPosition(@RequestParam Long positionId, @RequestParam(value = "page", defaultValue = "1") Integer page,
                                             @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return positionService.getIdentityInfoByPosition(positionId, page, size);
    }

    @ApiOperation("根据用户和机构得到关联的身份信息")
    @RequestMapping(value = "/getIdentityInfoByPersonAndOrg", method = RequestMethod.GET)
    public List<IdentityInfoEntity> getIdentityInfoByPersonAndOrg(@RequestParam Long personalId, @RequestParam Long orgId) {
        return positionService.getIdentityInfoByPersonAndOrg(personalId, orgId);
    }

    @ApiOperation("关联职位与人员")
    @RequestMapping(value = "/positionConnectPerson", method = RequestMethod.POST)
    public ResponseMessageDto positionConnectPerson(@RequestParam Long positionId, @RequestParam String personIds) {
        positionService.positionConnectPerson(positionId, personIds);
        return ResponseMessageDto.SUCCESS;
    }

    @ApiOperation("关联职位与人员")
    @RequestMapping(value = "/personConnectPosition", method = RequestMethod.POST)
    public ResponseMessageDto personConnectPosition(@RequestParam Long personId, @RequestParam String positionIds) {
        positionService.personConnectPosition(personId, positionIds);
        return ResponseMessageDto.SUCCESS;
    }


    @ApiOperation("删除职位与人员的关联，身份信息")
    @RequestMapping(value = "/deleteIdentityInfo", method = RequestMethod.POST)
    public ResponseMessageDto deleteIdentityInfo(@RequestParam String identityIds) {
        positionService.deleteIdentityInfo(identityIds);
        return ResponseMessageDto.SUCCESS;
    }

    /**
     * 机构授权
     */

    @ApiOperation("查询未授权的人员")
    @RequestMapping(value = "/connectd/getHasNoConnectIdentity", method = RequestMethod.POST)
    public List<IdentityInfoDto> getHasNoConnectIdentity(@RequestParam String orgId, @RequestParam Boolean isCascade, @RequestParam String orgCode, @RequestParam(defaultValue = "${default.dimension}") String dimension) {
        //查询当前组织机构和下级组织机构的人员--剔除掉已授权了的人员
        return organizationService.getHasNoConnectIdentity(orgId, isCascade, orgCode, dimension);
    }

    @ApiOperation("查询已经授权的人员")
    @RequestMapping(value = "/connectd/getHasConnectIdentity", method = RequestMethod.POST)
    public List<IdentityInfoDto> getHasConnectIdentity(@RequestParam String orgId, @RequestParam Boolean isCascade, @RequestParam String orgCode, @RequestParam(defaultValue = "${default.dimension}") String dimension) {
        return organizationService.getHasConnectIdentity(orgId, isCascade, orgCode, dimension);
    }

    @ApiOperation("关联机构与身份")
    @RequestMapping(value = "/connectd/saveOrgIdentityInfo", method = RequestMethod.POST)
    public void saveOrgIdentityInfo(@RequestBody List<OrgGrantDto> list) {
        organizationService.saveOrgIdentityInfo(list);
    }
    
    @ApiOperation("查询所有职位信息")
    @RequestMapping(value = "/getAllPostOpts", method = RequestMethod.GET)
    public ReturnDto getAllPostOpts() {
    	return new ReturnDto(positionService.getAllPostOpts());
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
