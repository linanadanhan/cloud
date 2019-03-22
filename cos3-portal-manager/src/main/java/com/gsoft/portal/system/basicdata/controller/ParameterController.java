package com.gsoft.portal.system.basicdata.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.util.Assert;
import com.gsoft.portal.system.basicdata.dto.ParameterDto;
import com.gsoft.portal.system.basicdata.service.ParameterService;

@Api(tags = "系统参数", description = "参数管理模块的服务")
@RestController
@RequestMapping("/parameter")
public class ParameterController {

    @Resource
    ParameterService parameterService;

    @ApiOperation("查询参数分类")
    @RequestMapping(value = "/select/getTypes", method = RequestMethod.GET)
    public List<String> getTypes() {
        return parameterService.getTypes();
    }

    @ApiOperation("根据type查询参数分类")
    @RequestMapping(value = "/select/getPageByType", method = RequestMethod.GET)
    public PageDto getPageForTypes(@RequestParam String type, @RequestParam(value = "page", defaultValue = "1") Integer page,
                            @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return parameterService.getPageByType(type, page, size);
    }

    @ApiOperation("保存参数对象")
    @RequestMapping(value = "/saveParm", method = RequestMethod.POST)
    public ParameterDto saveParm(@RequestBody ParameterDto parmameterDto, HttpServletRequest request) {
        if (Assert.isEmpty(parmameterDto.getId())) { // 新增
        	parmameterDto.setCreateTime(new Date());
        	parmameterDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));
        } else { // 编辑
        	parmameterDto.setUpdateTime(new Date());
        	parmameterDto.setUpdateBy(Long.valueOf(request.getHeader("personnelId")));
        }
        ParameterDto save = parameterService.save(parmameterDto);
        return save;
    }

    @ApiOperation("修改可用状态")
    @RequestMapping(value = "/update/updateStatus", method = RequestMethod.POST)
    public void updateStatus(@RequestParam Long id, @RequestParam Boolean status) {
        parameterService.updateStatus(id, status);
    }


    @ApiOperation("验证参数键是否重复")
    @RequestMapping(value = "/validparmkey", method = RequestMethod.GET)
    public Boolean validarmkey(@RequestParam(required = false) Long id, @RequestParam String key) {
        Boolean isExists = parameterService.isExistsParmKey(id, key);
        return !isExists;
    }

    @ApiOperation("获取参数信息供编辑")
    @RequestMapping(value = "/getParm/{id}", method = RequestMethod.GET)
    public ParameterDto getParm4Update(@PathVariable Long id) {
        ParameterDto result = parameterService.findOneById(id);
        return result;
    }

    @ApiOperation("删除参数信息")
    @RequestMapping(value = "/removeParm/{id}", method = RequestMethod.POST)
    public void removeParm(@PathVariable Long id) {
        parameterService.destoryParm(id);
    }
    
    @ApiOperation("根据key获取参数值")
    @RequestMapping(value = "/getParmByKey", method = RequestMethod.GET)
    public String getParmValueByKey(@RequestParam String key, @RequestParam(required = false) String defaultValue) {
    	String val = parameterService.getParmValueByKey(key, defaultValue);
        return val;
    }
}
