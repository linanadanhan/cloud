package com.gsoft.portal.webview.page.controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.util.Assert;
import com.gsoft.portal.webview.page.dto.SitePageHelpDto;
import com.gsoft.portal.webview.page.dto.SitePageHelpHideDto;
import com.gsoft.portal.webview.page.service.SitePageHelpService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "页面管理", description = "页面帮助接口服务")
@RestController
@RequestMapping("/pageHelp")
public class SitePageHelpController {
	@Resource
	private SitePageHelpService sitePageHelpService;
	
	@ApiOperation("分页查找页面帮助信息")
	@RequestMapping(value = "/querySitePageHelp", method = RequestMethod.GET)
	public PageDto querySitePageHelp( @RequestParam String pageUuId,@RequestParam String siteCode,@RequestParam String type,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size,
			@RequestParam(value = "sortProp", defaultValue = "c_id") String sortProp,
			@RequestParam(value = "order", defaultValue = "desc") String order) {

		return sitePageHelpService.querySitePageHelp(pageUuId, siteCode, type, page, size, sortProp, order);
	}
	
	@ApiOperation("根据Id获取页面帮助信息")
	@RequestMapping(value = "/getPageHelpInfoById", method = RequestMethod.GET)
	public SitePageHelpDto getPageConfInfoById(@RequestParam Long id) {
		return sitePageHelpService.getPageHelpInfoById(id);
	}
	
	@ApiOperation("保存页面帮助信息")
	@RequestMapping(value = "/saveSitePageHelp", method = RequestMethod.POST)
	public SitePageHelpDto saveSitePageConf(@ModelAttribute("sitePageHelpDto") SitePageHelpDto sitePageHelpDto,
			ServletRequest servletRequest) {
		
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		sitePageHelpDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));

		if (Assert.isEmpty(sitePageHelpDto.getId())) {
			sitePageHelpDto.setUuId(UUID.randomUUID().toString().replace("-", ""));
			sitePageHelpDto.setCreateTime(new Date());
		} else {
			sitePageHelpDto.setUpdateTime(new Date());
		}
		return sitePageHelpService.saveSitePageHelp(sitePageHelpDto);
	}
	
	@ApiOperation("删除页面帮助信息")
	@RequestMapping(value = "/delSitePageHelp", method = RequestMethod.GET)
	public void delSitePageConf(@RequestParam Long id) throws JSONException {
		sitePageHelpService.delSitePageHelp(id);
	}
	
	@ApiOperation("获取站点的帮助信息")
	@RequestMapping(value = "/querySiteHelp", method = RequestMethod.GET)
	public ReturnDto querySiteHelp( @RequestParam String siteCode,@RequestParam String owner) {
		return new ReturnDto(sitePageHelpService.querySiteHelp(siteCode, owner));
	}
	
	@ApiOperation("获取站点中某页面的帮助信息")
	@RequestMapping(value = "/queryPageHelp", method = RequestMethod.GET)
	public ReturnDto queryPageHelp( @RequestParam String siteCode,@RequestParam String pageUuId) {
		return new ReturnDto(sitePageHelpService.queryPageHelp(siteCode, pageUuId));
	}
	
	@ApiOperation("保存不在提示信息")
	@RequestMapping(value = "/saveSitePageHelpHide", method = RequestMethod.POST)
	public ReturnDto saveSitePageHelpHide(@RequestBody SitePageHelpHideDto sitePageHelpHideDto,HttpServletRequest request) {
		
		sitePageHelpHideDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));

		if (Assert.isEmpty(sitePageHelpHideDto.getId())) {
			sitePageHelpHideDto.setUuId(UUID.randomUUID().toString().replace("-", ""));
			sitePageHelpHideDto.setCreateTime(new Date());
		} else {
			sitePageHelpHideDto.setUpdateTime(new Date());
		}
		return new ReturnDto(sitePageHelpService.saveSitePageHelpHide(sitePageHelpHideDto));
	}
	
	@ApiOperation("根据参数获取站点页面帮助信息")
	@RequestMapping(value = "/getPageHelpByParams", method = RequestMethod.GET)
	public List<SitePageHelpDto> getPageHelpByParams( @RequestParam String siteCode,@RequestParam String pageUuId,@RequestParam String Type) {
		return sitePageHelpService.getPageHelpByParams(siteCode, pageUuId, Type);
	}
}
