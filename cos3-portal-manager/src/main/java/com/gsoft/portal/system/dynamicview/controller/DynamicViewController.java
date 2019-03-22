package com.gsoft.portal.system.dynamicview.controller;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.portal.system.dynamicview.service.DynamicViewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 动态视图实作类
 *
 * @author chenxx
 */
@Api(tags = "动态视图", description = "动态视图业务类接口")
@RestController
@RequestMapping("/dynamicView")
public class DynamicViewController {

    @Resource
    private DynamicViewService dynamicViewService;

    @ApiOperation("查询数据源信息")
    @RequestMapping(value = "/queryDynamicView", method = RequestMethod.GET)
    public PageDto queryDynamicView(@RequestParam String search,
                             @RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return dynamicViewService.queryDynamicView(search, page, size);
    }

    @ApiOperation("获取指定视图的所有数据记录")
    @RequestMapping(value = "/view/{viewName}/queryAll", method = RequestMethod.GET)
    public List<Map<String, Object>> queryAll(@PathVariable String viewName, @RequestParam Map<String, Object> map) {
        return dynamicViewService.queryAll(viewName, map);
    }

    @ApiOperation("分页获取指定视图的所有数据记录")
    @RequestMapping(value = "view/{viewName}/queryPage", method = RequestMethod.GET)
    public PageDto queryPage(@PathVariable String viewName, @RequestParam Map<String, Object> map,
                      @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "20") int pageSize) {
        return dynamicViewService.queryPage(viewName, map, pageNum, pageSize);
    }
    
    @ApiOperation("分页获取指定视图的所有数据记录(根据参数获取)")
    @RequestMapping(value = "view/{viewName}/queryByParams", method = RequestMethod.GET)
    public List<Map<String, Object>> queryByParams(@PathVariable String viewName, @RequestParam Map<String, Object> map) {
        return dynamicViewService.queryAllByParams(viewName, map);
    }
}
