package com.gsoft.portal.system.organization.controller;

import com.gsoft.cos3.tree.TreeNode;
import com.gsoft.cos3.util.TreeUtils;
import com.gsoft.portal.system.organization.dto.AdministrativeAreaDto;
import com.gsoft.portal.system.organization.service.AdministrativeAreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "行政区划", description = "行政区划管理接口服务")
@RestController
@RequestMapping("/area")
public class AdministrativeAreaController {

    @Autowired
    AdministrativeAreaService administrativeAreaService;

    @ApiOperation("获取行政区划列表")
    @RequestMapping(value = "/select/getAllAreaTree", method = RequestMethod.GET)
    public List<TreeNode> getAllAreaTree() {
        List<AdministrativeAreaDto> item = administrativeAreaService.getTreeList();
        if (item == null) {
            return new ArrayList<TreeNode>();
        }
        List<TreeNode> tree = TreeUtils.convert(item).attrs("cascade,code,level").tree();
        return tree;
    }

    @ApiOperation("获取行政区划级联需要的数据")
    @RequestMapping(value = "/select/getAreaCascader", method = RequestMethod.GET)
    public String[] getAreaCascader(@RequestParam(required = false) String areaCode) {
        return administrativeAreaService.getAreaCascader(areaCode);
    }

    @ApiOperation("根据code获取行政区划信息")
    @RequestMapping(value = "/select/getAreaByCode", method = RequestMethod.GET)
    public AdministrativeAreaDto getAreaByCode(@RequestParam String areaCode) {
        return administrativeAreaService.getAreaByCode(areaCode);
    }
}
