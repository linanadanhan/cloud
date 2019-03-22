package com.gsoft.portal.system.personnel.controller;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.system.organization.service.OrganizationService;
import com.gsoft.portal.system.personnel.dto.PersonnelDto;
import com.gsoft.portal.system.personnel.dto.PersonnelGroupDetailDto;
import com.gsoft.portal.system.personnel.dto.PersonnelGroupDto;
import com.gsoft.portal.system.personnel.dto.PersonnelGroupSelDto;
import com.gsoft.portal.system.personnel.service.PersonnelGroupService;
import com.gsoft.portal.system.personnel.service.PersonnelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户群组管理
 *
 * @author SN
 */
@Api(tags = "用户群组管理", description = "用户群组管理接口服务")
@RestController
@RequestMapping(value = "/personnelGroup")
public class PersonnelGroupController {

    @Autowired
    private PersonnelGroupService personnelGroupService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private PersonnelService personnelService;

    @ApiOperation("获取用户自定义群组信息列表")
    @RequestMapping(value = "/getPersonGroupOpts", method = RequestMethod.GET)
    public ReturnDto getPersonGroupOpts(HttpServletRequest request, @RequestParam(required = false) Integer groupType) {
        Long personId = MathUtils.numObj2Long(request.getHeader("personnelId"));
        return new ReturnDto(personnelGroupService.getPersonGroupOpts(personId, groupType));
    }

    @ApiOperation("保存用户自定义群组信息")
    @RequestMapping(value = "/savePersonGroup", method = RequestMethod.POST)
    public ReturnDto savePersonGroup(HttpServletRequest request, @RequestBody Map<String, Object> map) throws IOException {
        Long personId = MathUtils.numObj2Long(request.getHeader("personnelId"));
        return new ReturnDto(personnelGroupService.savePersonGroup(personId, map));
    }

    @ApiOperation("删除用户自定义群组信息")
    @RequestMapping(value = "/delPersonGroup", method = RequestMethod.GET)
    public ReturnDto delPersonGroup(@RequestParam Long groupId) {
        personnelGroupService.delPersonGroup(groupId);
        return new ReturnDto("删除成功!");
    }

    @ApiOperation("获取自定义群组下的人员树列表")
    @RequestMapping(value = "/getPersonTreeByGroupIds", method = RequestMethod.GET)
    public ReturnDto getPersonTreeByGroupIds(@RequestParam String chkGroupIds) {
        List<PersonnelGroupSelDto> resultList = new ArrayList<>();
        if (Assert.isEmpty(chkGroupIds)) {
            return new ReturnDto(resultList);
        }

        List<PersonnelGroupDetailDto> list = personnelGroupService.getPersonDetailByGroupIds(chkGroupIds);

        Map<Long, List<PersonnelGroupDetailDto>> detailMap = list.stream().collect(Collectors.groupingBy(PersonnelGroupDetailDto::getOrgId));
        resultList = detailMap.entrySet().stream().map(org -> {
            String personIds = org.getValue().stream().flatMap(p ->
                    Arrays.stream(p.getUserIds().split(","))).distinct().collect(Collectors.joining(","));
            PersonnelGroupSelDto selectDto = new PersonnelGroupSelDto();
            selectDto.setOrgId(org.getKey());
            selectDto.setLabel(organizationService.getCascadeNameById(org.getKey(), "-"));
            selectDto.setUserList(personnelService.getPersonsByIds(personIds));
            return selectDto;
        }).collect(Collectors.toList());

        return new ReturnDto(resultList);
    }


    //----------------------------------------------系统群组
    @ApiOperation("新增系统群组")
    @RequestMapping(value = "/saveSys", method = RequestMethod.POST)
    public Long saveSys(@ModelAttribute("personnelGroupDto") PersonnelGroupDto personnelGroupDto) throws IOException {
       return  personnelGroupService.saveSysGroup(personnelGroupDto);
    }

    @ApiOperation("删除系统群组")
    @RequestMapping(value = "/delById", method = RequestMethod.GET)
    public void delById(@RequestParam Long id) {
        personnelGroupService.delPersonGroup(id);
    }

    @ApiOperation("根据主键ID得到单个对象")
    @RequestMapping(value = "/getOneById", method = RequestMethod.GET)
    public PersonnelGroupDto getOneById(@RequestParam Long id) {

        return personnelGroupService.getOneById(id);
    }

    @ApiOperation("查询系统群组列表")
    @RequestMapping(value = "/getSysGroupList", method = RequestMethod.GET)
    public List<PersonnelGroupDto> getSysGroupList(@RequestParam String keyword) {
        return personnelGroupService.getSysGroupList(keyword);
    }

    @ApiOperation("根据群组id查询已关联用户")
    @RequestMapping(value = "/getRePerson", method = RequestMethod.GET)
    public List<PersonnelDto> getPerson(@RequestParam Long groupId, @RequestParam String dimension,
                                 @RequestParam Long orgId, @RequestParam Boolean isCascade) {
        List<PersonnelDto> allPersons = personnelService.getAllPersons(dimension, orgId, isCascade);
        List<PersonnelGroupDetailDto> detailList = personnelGroupService.getPersonDetailByGroupIds(groupId.toString());
        Set<Long> personSet = detailList.stream().filter(d ->
                Assert.isNotEmpty(d.getUserIds())).flatMap(d ->
                Arrays.stream(d.getUserIds().split(","))).map(Long::valueOf).collect(Collectors.toSet());
        //TODO:需要人员里面带上机构
        return allPersons.stream().filter(p ->
                personSet.contains(p.getId())).collect(Collectors.toList());
    }

    @ApiOperation("获取所有用户")
    @RequestMapping(value = "/getAllPerson", method = RequestMethod.GET)
    public List<PersonnelDto> getAllPerson(@RequestParam String dimension, @RequestParam Long orgId,
                                    @RequestParam Boolean isCascade) {
        //TODO:需要人员里面带上机构
        return personnelService.getAllPersons(dimension, orgId, isCascade);
    }

}
