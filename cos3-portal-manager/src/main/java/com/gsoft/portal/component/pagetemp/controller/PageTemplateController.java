package com.gsoft.portal.component.pagetemp.controller;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.util.Assert;
import com.gsoft.portal.component.pagetemp.dto.PageTemplateDto;
import com.gsoft.portal.component.pagetemp.service.PageTemplateService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 页面模版管理
 * 
 * @author SN
 *
 */
@Api(tags = "页面模版管理", description = "页面模版管理相关接口服务")
@RestController
@RequestMapping("/pageTemp")
public class PageTemplateController {

	@Resource
	PageTemplateService pageTemplateService;
	
	@ApiOperation("分页查找页面模版信息")
	@RequestMapping(value = "/getPageTempList", method = RequestMethod.GET)
	public ReturnDto getPageTempList(@RequestParam String search,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size,
			@RequestParam(value = "sortProp", defaultValue = "id") String sortProp,
			@RequestParam(value = "order", defaultValue = "desc") String order) {
		return new ReturnDto(pageTemplateService.getPageTempList(search, page, size, sortProp, order));
	}

	@ApiOperation("保存页面模版数据")
	@RequestMapping(value = "/savePageTempInfo", method = RequestMethod.POST)
	public ReturnDto savePageTempInfo(HttpServletRequest request,
			@RequestBody PageTemplateDto pageTemplateDto) throws Exception {
		pageTemplateDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));
		if (Assert.isEmpty(pageTemplateDto.getId())) {
			pageTemplateDto.setCreateTime(new Date());
		} else {
			pageTemplateDto.setUpdateTime(new Date());
		}
		pageTemplateService.savePageTempInfo(pageTemplateDto);
		return new ReturnDto("保存成功");
	}

	@ApiOperation("删除页面模版数据")
	@RequestMapping(value = "/delPageTemp", method = RequestMethod.GET)
	public ReturnDto delPageTemp(HttpServletRequest request, @RequestParam Long id) throws JSONException {
		return pageTemplateService.delPageTemplate(id);
	}

	@ApiOperation("根据主键ID获取单笔页面模版数据")
	@RequestMapping(value = "/getPageTempInfoById", method = RequestMethod.GET)
	public ReturnDto getPageTempInfoById(@RequestParam Long id) {
		return new ReturnDto(pageTemplateService.getPageTempInfoById(id));
	}
	
    @ApiOperation("校验页面模版代码是否已存在")
    @RequestMapping(value = "/isUniquPageTempCode", method = RequestMethod.GET)
	public ReturnDto isUniquPageTempCode(@RequestParam(required = false) Long id, @RequestParam String code) {
		return pageTemplateService.isUniquPageTempCode(id, code);
	}
    
	@ApiOperation("保存页面模版配置数据")
	@RequestMapping(value = "/savePageTempConf", method = RequestMethod.GET)
	public ReturnDto savePageTempConf(@RequestParam String code, @RequestParam String json) {
		pageTemplateService.savePageTempConf(code, json);
		return new ReturnDto("保存成功");
	}
	
	@ApiOperation("根据组件ID获取业务组件配置数据")
	@RequestMapping(value = "/getPageTempConfInfo", method = RequestMethod.GET)
	public ReturnDto getPageTempConfInfo(@RequestParam String layout, @RequestParam String pageUuId, @RequestParam String code) throws JSONException {
		return pageTemplateService.getPageTempConfInfo(layout, code, pageUuId);
	}
	
	@ApiOperation("获取所有页面模版信息")
	@RequestMapping(value = "/getAllPageTempList", method = RequestMethod.GET)
	public ReturnDto getAllPageTempList() {
		return new ReturnDto(pageTemplateService.getAllPageTempList());
	}
}
