package com.gsoft.portal.webview.page.controller;

import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.util.Assert;
import com.gsoft.portal.webview.page.dto.SitePageConfDto;
import com.gsoft.portal.webview.page.service.SitePageConfService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 页面配置管理
 * 
 * @author SN
 *
 */
@Api(tags = "页面管理", description = "页面配置接口服务")
@RestController
@RequestMapping("/pageConf")
public class SitePageConfController {

	@Resource
	SitePageConfService sitePageConfService;

	@ApiOperation("分页查找页面配置信息")
	@RequestMapping(value = "/querySitePageConf", method = RequestMethod.GET)
	public PageDto querySitePageConf(@RequestParam String search, @RequestParam String pageUuId,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size,
			@RequestParam(value = "sortProp", defaultValue = "id") String sortProp,
			@RequestParam(value = "order", defaultValue = "desc") String order) {

		return sitePageConfService.querySitePageConfInfo(search, pageUuId, page, size, sortProp, order);
	}

	@ApiOperation("根据Id获取页面配置信息")
	@RequestMapping(value = "/getPageConfInfoById", method = RequestMethod.GET)
	public SitePageConfDto getPageConfInfoById(@RequestParam Long id) {
		return sitePageConfService.getPageConfInfoById(id);
	}

	@ApiOperation("保存页面配置信息")
	@RequestMapping(value = "/saveSitePageConf", method = RequestMethod.POST)
	public SitePageConfDto saveSitePageConf(@ModelAttribute("sitePageConfDto") SitePageConfDto sitePageConfDto,
			ServletRequest servletRequest) {
		
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		sitePageConfDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));

		if (Assert.isEmpty(sitePageConfDto.getId())) {
			sitePageConfDto.setUuId(UUID.randomUUID().toString().replace("-", ""));
			sitePageConfDto.setCreateTime(new Date());
		} else {
			sitePageConfDto.setUpdateTime(new Date());
		}
		return sitePageConfService.saveSitePageConf(sitePageConfDto);
	}
	
	@ApiOperation("删除页面配置信息")
	@RequestMapping(value = "/delSitePageConf", method = RequestMethod.GET)
	public void delSitePageConf(@RequestParam String uuId, @RequestParam String pageUuId) throws JSONException {
		sitePageConfService.delSitePageConf(uuId, pageUuId);
	}
}
