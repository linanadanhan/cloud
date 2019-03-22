package com.gsoft.portal.webview.page.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.tree.TreeNode;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.cos3.util.TreeUtils;
import com.gsoft.portal.webview.page.dto.SitePageDto;
import com.gsoft.portal.webview.page.service.SitePageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 页面管理
 * 
 * @author SN
 *
 */
@Api(tags = "页面管理", description = "页面管理接口服务")
@RestController
@RequestMapping("/page")
public class SitePageController {

	@Resource
	SitePageService sitePageService;

	@ApiOperation("通过站点code获取页面tree")
	@RequestMapping(value = "/getSitePageTree", method = RequestMethod.GET)
	public List<TreeNode> getSitePageTree(ServletRequest servletRequest, @RequestParam String siteCode,
			@RequestParam(required = false) String type) {

		List<SitePageDto> item = sitePageService.getSitePageTree(siteCode, type);
		if (Assert.isEmpty(item)) {
			item = new ArrayList<SitePageDto>();
		}

		SitePageDto sy = new SitePageDto();
		sy.setParentId(0);
		sy.setId(2l);
		sy.setType("0");
		sy.setName("私有页面");
		sy.setCascade("");
		sy.setPath("");
		sy.setSortNo(1);
		sy.setSiteCode(siteCode);
		item.add(sy);

		if (Assert.isEmpty(type)) {
			// 添加公共页面和私有页面节点信息
			SitePageDto gg = new SitePageDto();
			gg.setParentId(0);
			gg.setId(1l);
			gg.setType("1");
			gg.setName("公开页面");
			gg.setPath("");
			gg.setCascade("");
			gg.setSortNo(1);
			gg.setSiteCode(siteCode);
			item.add(gg);
		}

		List<TreeNode> tree = TreeUtils.convert(item)
				.attrs("siteCode,themeCode,layoutCode,isMenu,type,path,cascade,uuId,allowWidget,allowLayout,sortNo,openSelf,navHidden,status,isFolder,themeStyle,linkUrl,linkUrl").tree();
		return tree;
	}
	
	@ApiOperation("通过站点code获取用户有权限的页面tree, 返回ReturnDto")
	@RequestMapping(value = "/getSysPageTree", method = RequestMethod.GET)
	public ReturnDto getSysPageTree(HttpServletRequest request, @RequestParam String siteCode,
			@RequestParam(required = false) String type) {
		// 获取登录用户ID
		Long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));
		List<SitePageDto> item = sitePageService.getAuthSitePageTree(siteCode, personnelId);
		if (Assert.isEmpty(item)) {
			item = new ArrayList<SitePageDto>();
		}

		List<TreeNode> tree = TreeUtils.convert(item)
				.attrs("siteCode,themeCode,layoutCode,isMenu,type,path,cascade,uuId,allowWidget,allowLayout,sortNo,openSelf,navHidden,status,isFolder,themeStyle,linkUrl,linkUrl").tree();
		return new ReturnDto(tree);
	}

	@ApiOperation("根据Id获取页面信息")
	@RequestMapping(value = "/getSitePageInfoById", method = RequestMethod.GET)
	public SitePageDto getSitePageInfoById(@RequestParam Long id) {
		return sitePageService.getSitePageInfoById(id);
	}

	@ApiOperation("保存站点页面信息")
	@RequestMapping(value = "/saveSitePage", method = RequestMethod.POST)
	public SitePageDto saveSitePage(@ModelAttribute("sitePageDto") SitePageDto sitePageDto, ServletRequest servletRequest) throws JSONException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		sitePageDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));

		if (Assert.isEmpty(sitePageDto.getId())) {
			sitePageDto.setCreateTime(new Date());
		} else {
			sitePageDto.setUpdateTime(new Date());
		}

		return sitePageService.saveSitePage(sitePageDto);
	}

	@ApiOperation("拖动保存站点页面信息")
	@RequestMapping(value = "/saveSitePageTree", method = RequestMethod.POST)
	public ReturnDto saveSitePageTree(@RequestParam String siteCode, @RequestParam String draggingNode,@RequestParam String dataTree,
			@RequestParam Long parentId, ServletRequest servletRequest) throws JSONException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		Long personnelId = Long.valueOf(request.getHeader("personnelId"));
		return sitePageService.saveSitePageTree(personnelId, siteCode, draggingNode, dataTree, parentId);
	}

	@ApiOperation("删除站点页面信息")
	@RequestMapping(value = "/delSitePage", method = RequestMethod.GET)
	public void delSitePage(@RequestParam Long id, @RequestParam Long parentId, @RequestParam String siteCode) {

		// 查询站点下的所有页面信息
		List<SitePageDto> item = sitePageService.getSitePageTree(siteCode, null);

		// 获取当前节点下的所有子节点
		List<Long> ids = new ArrayList<Long>();
		if (parentId != 0) {
			ids.add(id);
		}
		getChildIds(item, id, ids);

		sitePageService.delSitePage(ids, parentId);
	}

	public void getChildIds(List<SitePageDto> itemList, Long id, List<Long> ids) {
		for (SitePageDto dto : itemList) {
			// 遍历出父id等于参数的id，add进子节点集合
			if (dto.getParentId() == id) {
				// 递归遍历下一级
				getChildIds(itemList, dto.getId(), ids);
				ids.add(dto.getId());
			}
		}
	}

	@ApiOperation("判断页面path是否存在")
	@RequestMapping(value = "/isExitPagePath", method = RequestMethod.GET)
	public Boolean isExitPagePath(@RequestParam(required = false) Long id, @RequestParam String path,
			@RequestParam String cascade, @RequestParam String type, @RequestParam String siteCode) {
		return sitePageService.isExitPagePath(id, path, cascade, type, siteCode);
	}
	
	@ApiOperation("复制页面信息")
	@RequestMapping(value = "/copyPage", method = RequestMethod.GET)
	public ReturnDto copyPage(@RequestParam Long id, @RequestParam String name,
			ServletRequest servletRequest) throws Exception {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		Long personnelId = Long.valueOf(request.getHeader("personnelId"));
		
		// 查询页面信息
		SitePageDto sitePageDto = sitePageService.getSitePageInfoById(id);
		sitePageDto.setName(name);
		sitePageDto.setCreateBy(personnelId);
		
		return new ReturnDto(sitePageService.copyPage(sitePageDto));
	}

}
