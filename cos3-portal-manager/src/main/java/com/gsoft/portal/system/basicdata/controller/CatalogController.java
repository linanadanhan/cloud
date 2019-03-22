package com.gsoft.portal.system.basicdata.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.TreeUtils;
import com.gsoft.portal.system.basicdata.dto.CatalogDto;
import com.gsoft.portal.system.basicdata.service.CatalogService;

import io.swagger.annotations.ApiOperation;

/**
 * 分类科目 Controller
 *
 * @author SN
 */
@Api(tags = "分类科目", description = "分类科目接口服务")
@RestController
@RequestMapping("/catalog")
public class CatalogController {

    @Resource
    CatalogService catalogService;

    @SuppressWarnings("deprecation")
    @ApiOperation("通过分类科目列表数据")
    @RequestMapping(value = "/queryCatalogDataTable", method = RequestMethod.GET)
    public List<CatalogDto> queryCatalogDataTable() {

        List<CatalogDto> item = catalogService.getCatalogTree();

        if (Assert.isEmpty(item)) {
            item = new ArrayList<CatalogDto>();
        }

        return TreeUtils.convertBeanList(item);
    }

    @ApiOperation("保存分类科目信息")
    @RequestMapping(value = "/saveCatalog", method = RequestMethod.POST)
    public CatalogDto saveCatalog(@ModelAttribute("catalogDto") CatalogDto catalogDto, ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        catalogDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));

        if (Assert.isEmpty(catalogDto.getId())) {
            catalogDto.setCreateTime(new Date());
        } else {
            catalogDto.setUpdateTime(new Date());
        }

        return catalogService.saveCatalog(catalogDto);
    }

    @ApiOperation("判断分类科目标识是否存在")
    @RequestMapping(value = "/isExitRootKey", method = RequestMethod.GET)
    public Boolean isExitRootKey(@RequestParam(required = false) Long id, @RequestParam String rootkey) {
        return catalogService.isExitRootKey(id, rootkey);
    }

    @ApiOperation("根据Id获取分类科目信息")
    @RequestMapping(value = "/getCatalogInfoById", method = RequestMethod.GET)
    public CatalogDto getCatalogInfoById(@RequestParam Long id) {
        return catalogService.getCatalogInfoById(id);
    }

    @ApiOperation("删除分类科目信息")
    @RequestMapping(value = "/delCatalog", method = RequestMethod.GET)
    public void delCatalog(@RequestParam Long id) {

        //查询该分类科目下的所有分类信息
        List<CatalogDto> item = catalogService.getCatalogTree(id);

        //获取当前节点下的所有子节点
        List<Long> ids = new ArrayList<Long>();
        ids.add(id);
        getChildIds(item, id, ids);

        catalogService.delCatalog(ids);
    }

    public void getChildIds(List<CatalogDto> itemList, Long id, List<Long> ids) {
        for (CatalogDto dto : itemList) {
            //遍历出父id等于参数的id，add进子节点集合  
            if (dto.getParentId() == id) {
                //递归遍历下一级  
                getChildIds(itemList, dto.getId(), ids);
                ids.add(dto.getId());
            }
        }
    }

}
