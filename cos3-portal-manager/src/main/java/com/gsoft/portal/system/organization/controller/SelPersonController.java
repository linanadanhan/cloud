package com.gsoft.portal.system.organization.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.system.basicdata.service.DictionaryService;
import com.gsoft.portal.system.organization.service.OrganizationService;
import com.gsoft.portal.system.personnel.dto.PersonnelDto;
import com.gsoft.portal.system.personnel.dto.PersonnelGroupSelDto;
import com.gsoft.portal.system.personnel.dto.RoleDto;
import com.gsoft.portal.system.personnel.service.PersonnelService;
import com.gsoft.portal.system.personnel.service.RoleService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 组织机构管理
 *
 * @author SN
 */
@Api(tags = "选人", description = "选人接口服务")
@RestController
@RequestMapping("/selPerson")
public class SelPersonController {

	@Resource
	private OrganizationService organizationService;
	@Resource
	private PersonnelService personnelService;
	@Resource
	private RoleService roleService;
	@Resource
	private DictionaryService dictionaryService;


	@ApiOperation("左侧机构")
	@RequestMapping(value = "/getOrgTree", method = RequestMethod.GET)
	public ReturnDto getOrgTree( @RequestParam String dimension,@RequestParam String orgIds,@RequestParam Boolean isCascade) {
		//可能都是空的
		//纬度，机构集合，是否级联，得到指定机构
		return new ReturnDto(organizationService.getOrgTreeBySelPerson(dimension, orgIds, isCascade));
	}

	@ApiOperation("获取选择机构下人员树列表")
	@RequestMapping(value = "/getPersonTreeByOrgIds", method = RequestMethod.GET)
	public ReturnDto getPersonTreeByOrgIds(@RequestParam String orgIds) {
		//根据多个机构id得到多个人员
		List<PersonnelDto> list = personnelService.getDimensionPersonsByOrgIds(orgIds);
		List<PersonnelGroupSelDto> result = convertPersonnelGroupSelDtos(list);
		return new ReturnDto(result);
	}

	@ApiOperation("左侧-扩展人员")
	@RequestMapping(value = "/getExPersons", method = RequestMethod.GET)
	public ReturnDto getExPersons( @RequestParam String personIds,@RequestParam(required = false) String dimension) {

		List<PersonnelDto> list;
		if(Assert.isNotEmpty(dimension)){
			List<PersonnelDto> pList = personnelService.getDimensionPersonsByIds(personIds, dimension);
			list=pList;
		}else{
			List<PersonnelDto> pList = personnelService.getDimensionPersonsByIds(personIds);
			list=pList;
		}
		//TOOD:里面的机构id是空的，不能用这个接口
		List<PersonnelGroupSelDto> result = convertPersonnelGroupSelDtos(list);
		return new ReturnDto(result);
	}


	@ApiOperation("查询所有或指定角色")
	@RequestMapping(value = "/getAllRolesByIds", method = RequestMethod.GET)
	public ReturnDto getAllRolesInfo(@RequestParam(required = false) String roleIds) {
		if(Assert.isEmpty(roleIds)){
			return new ReturnDto(roleService.getAllRoles());
		}
		return new ReturnDto(roleService.getRolesByIds(roleIds));
	}


	@ApiOperation("获取对应角色下的人员树列表")
	@RequestMapping(value = "/getPersonTreeByRoleIds", method = RequestMethod.GET)
	public ReturnDto getPersonTreeByRoleIds(HttpServletRequest request, @RequestParam String roleIds,@RequestParam(required = false) String dimension) {
		//根据多个roleIds得到下面的所有人员,如果有纬度，就根据纬度过滤，如果没有纬度，就是所有纬度的
		List<PersonnelDto> personList = personnelService.getPersonsByRoleIds(roleIds);

		if(Assert.isNotEmpty(dimension)){
			List<PersonnelDto> list = personList.stream().filter(p -> dimension.equals(p.getDimension())).collect(Collectors.toList());
			return new ReturnDto(convertPersonnelGroupSelDtos(list));
		}else{
			return new ReturnDto(convertPersonnelGroupSelDtos(personList));
		}
	}

	//按机构分组人员，转成前台所需要的对象
	private List<PersonnelGroupSelDto> convertPersonnelGroupSelDtos(List<PersonnelDto> list){
		//过滤根目录，根据角色分组,getOrgId不能是空
		Map<Long, List<PersonnelDto>> collect = list.stream().filter(p->!"0000".equals(p.getOrgCode())).collect(Collectors.groupingBy(PersonnelDto::getOrgId));
		List<PersonnelGroupSelDto> resultList = collect.entrySet().stream().map(m -> {
			PersonnelGroupSelDto selDto = new PersonnelGroupSelDto();
			selDto.setOrgId(m.getKey());
			selDto.setLabel(organizationService.getCascadeNameById(m.getKey(), "-"));
			selDto.setUserList(m.getValue());
			return selDto;
		}).collect(Collectors.toList());
		return resultList;
	}

	@ApiOperation("左侧-获取对应拼音首字母下的人员树列表")
	@RequestMapping(value = "/getPersonTreeByLetters", method = RequestMethod.GET)
	public ReturnDto getPersonTreeByLetters(@RequestParam String letters,@RequestParam(required = false) String dimension) {
		// 查询满足拼音搜索条件人员所属的机构信息
		List<Map<String, Object>> allOrgList = organizationService.getOrgInfoByLetter(letters,dimension);
		if (Assert.isEmpty(allOrgList) || allOrgList.size() == 0) {
			return new ReturnDto(new ArrayList<>());
		}

		List<PersonnelGroupSelDto> collect = allOrgList.stream().map(m -> {
			PersonnelGroupSelDto selDto = new PersonnelGroupSelDto();
			Long orgId = MathUtils.numObj2Long(m.get("C_ID"));
			selDto.setOrgId(orgId);
			selDto.setLabel(organizationService.getCascadeNameById(orgId, "-"));
			selDto.setUserList(personnelService.getPersonsByOrgIdAndLetters(orgId, letters));//得到人员
			return selDto;
		}).collect(Collectors.toList());

		return new ReturnDto(collect);
	}

	@ApiOperation("获取当前所有机构下的所有用户信息")
	@RequestMapping(value = "/getPersonListByAllOrgs", method = RequestMethod.GET)
	public ReturnDto getPersonListByAllOrgs( @RequestParam String personName,@RequestParam(required = false) String dimension) { List<PersonnelDto> personList = personnelService.getDimensionPersonsByName(personName);

		if(Assert.isNotEmpty(dimension)){
			List<PersonnelDto> list = personList.stream().filter(p -> dimension.equals(p.getDimension())).collect(Collectors.toList());
			personList=list;
		}

		List<Map<String, Object>> collect = personList.stream().map(p -> {
			Map<String, Object> tmpMap = new HashMap<>();
			tmpMap.put("orgId", p.getOrgId());
			tmpMap.put("label", organizationService.getCascadeNameById(p.getOrgId(), "-"));
			tmpMap.put("user", p);
			return tmpMap;
		}).collect(Collectors.toList());
		return new ReturnDto(collect);

	}
	
	@ApiOperation("根据key查询指定字典下字典项列表--默认查询纬度")
	@RequestMapping(value = "/getDicItemListByDicKey", method = RequestMethod.GET)
	public ReturnDto getDicItemListByDicKey(@RequestParam(required = false) String dicKey) { //org_dimension
		if(Assert.isEmpty(dicKey)){
			dicKey="org_dimension"; //纬度
		}
		return new ReturnDto(dictionaryService.getDicItemsByKey(dicKey));
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
}
