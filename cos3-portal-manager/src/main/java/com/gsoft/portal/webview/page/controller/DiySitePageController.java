package com.gsoft.portal.webview.page.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.tree.TreeNode;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.cos3.util.TreeUtils;
import com.gsoft.portal.webview.page.dto.DiySitePageDto;
import com.gsoft.portal.webview.page.dto.SitePageDto;
import com.gsoft.portal.webview.page.service.DiySitePageService;
import com.gsoft.portal.webview.page.service.SitePageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 个性化页面管理
 * 
 * @author SN
 *
 */
@Api(tags = "个性化页面管理", description = "个性化页面管理接口服务")
@RestController
@RequestMapping("/diyPage")
public class DiySitePageController {

	@Resource
	DiySitePageService diySitePageService;

	@Resource
	SitePageService sitePageService;

	@ApiOperation("获取个性化站点页面tree")
	@RequestMapping(value = "/getDiyPageTree", method = RequestMethod.GET)
	public ReturnDto getDiyPageTree(HttpServletRequest request, @RequestParam String siteCode) {
		// 获取登录用户ID
		Long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));
		List<DiySitePageDto> item = diySitePageService.getDiyPageTree(siteCode, personnelId);
		if (Assert.isEmpty(item)) {
			item = new ArrayList<DiySitePageDto>();
			// 若为空的话则查询系统菜单值
			// 查询站点下的该用户所有有权限的页面信息
			List<SitePageDto> sysPages = sitePageService.getAuthSitePageTree(siteCode, personnelId);
			if (!Assert.isEmpty(sysPages) && sysPages.size() > 0) {
				DiySitePageDto tmpDto = null;
				for (SitePageDto sitePageDto : sysPages) {
					tmpDto = new DiySitePageDto();

					tmpDto.setUuId(sitePageDto.getUuId());
					tmpDto.setName(sitePageDto.getName());
					tmpDto.setId(sitePageDto.getId());
					tmpDto.setPath(sitePageDto.getPath());
					tmpDto.setCascade(sitePageDto.getCascade());
					tmpDto.setParentId(sitePageDto.getParentId());
					tmpDto.setSiteCode(sitePageDto.getSiteCode());
					tmpDto.setThemeCode(sitePageDto.getThemeCode());
					tmpDto.setLayoutCode(sitePageDto.getLayoutCode());
					tmpDto.setOpenSelf(sitePageDto.getOpenSelf());
					tmpDto.setNavHidden(sitePageDto.getNavHidden());
					tmpDto.setIsMenu(sitePageDto.getIsMenu());
					tmpDto.setIsFolder(sitePageDto.getIsFolder());
					tmpDto.setThemeStyle(sitePageDto.getThemeStyle());
					tmpDto.setSortNo(sitePageDto.getSortNo());
					tmpDto.setLinkUrl(sitePageDto.getLinkUrl());
					tmpDto.setIsLink(sitePageDto.getIsLink());
					tmpDto.setIsSystem(true);
					item.add(tmpDto);
				}
			}
		}

		List<TreeNode> tree = TreeUtils.convert(item).attrs(
				"siteCode,themeCode,layoutCode,isMenu,type,path,cascade,uuId,allowWidget,allowLayout,sortNo,isSystem, sysPageId")
				.tree();
		return new ReturnDto(tree);
	}

	@ApiOperation("判断个性化站点页面path是否存在")
	@RequestMapping(value = "/isExitPagePath", method = RequestMethod.GET)
	public ReturnDto isExitPagePath(HttpServletRequest request, @RequestParam(required = false) Long id,
			@RequestParam String path, @RequestParam String cascade, @RequestParam String type,
			@RequestParam String siteCode) {
		// 获取登录用户ID
		Long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));
		return new ReturnDto(diySitePageService.isExitPagePath(personnelId, id, path, cascade, siteCode));
	}

	@ApiOperation("保存个性化站点页面信息")
	@RequestMapping(value = "/saveDiySitePage", method = RequestMethod.POST)
	public ReturnDto saveDiySitePage(@RequestBody DiySitePageDto diySitePageDto, HttpServletRequest request) throws JSONException {

		// 获取登录用户ID
		Long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));
		// 判断是否存在该用户个性化站点页面数据，若存在则继续，若不存在则copy系统数据一份
		boolean isExist = checkExistDiySitePageInfo(personnelId, diySitePageDto);
		if (!isExist) {
			if (diySitePageDto.getParentId() != 2) {
				SitePageDto sitePageDto = sitePageService.getSitePageInfoById(diySitePageDto.getParentId());
				DiySitePageDto dto = diySitePageService.getDiySitePageInfoByUuId(personnelId, sitePageDto.getUuId());
				diySitePageDto.setParentId(dto.getId());
			}
			
			if (!Assert.isEmpty(diySitePageDto.getId())) {
				DiySitePageDto dto1 = diySitePageService.getDiySitePageInfoByUuId(personnelId, diySitePageDto.getUuId());
				diySitePageDto.setId(dto1.getId());
			}

		}

		diySitePageDto.setCreateBy(personnelId);
		diySitePageDto.setUserId(personnelId);
		if (Assert.isEmpty(diySitePageDto.getId())) {
			diySitePageDto.setCreateTime(new Date());
		} else {
			diySitePageDto.setUpdateTime(new Date());
		}
		return new ReturnDto(diySitePageService.saveDiySitePage(diySitePageDto));
	}

	/**
	 * 判断是否存在该用户个性化站点页面数据
	 * 
	 * @param personnelId
	 * @param siteCode
	 * @throws JSONException 
	 */
	private boolean checkExistDiySitePageInfo(Long personnelId, DiySitePageDto diySitePageDto) throws JSONException {
		List<DiySitePageDto> item = diySitePageService.getDiyPageTree(diySitePageDto.getSiteCode(), personnelId);
		if (Assert.isEmpty(item)) {
			// 查询站点下的所有系统页面信息
			List<SitePageDto> sysPages = sitePageService.getAuthSitePageTree(diySitePageDto.getSiteCode(), personnelId);
			if (!Assert.isEmpty(sysPages) && sysPages.size() > 0) {
				List<TreeNode> tree = TreeUtils.convert(sysPages).attrs(
						"siteCode,name,themeCode,themeStyle,linkUrl,isLink,layoutCode,isMenu,openSelf,navHidden,type,path,cascade,uuId,allowWidget,allowLayout,sortNo,isSystem,isFolder,sysPageId")
						.tree();
				copySysSitePageData(tree, 2L, personnelId);
			}
			return false;
		}else {
			return true;
		}
	}

	/**
	 * 复制系统站点页面数据信息
	 * @param sysPages
	 * @param parentId
	 * @param personnelId
	 * @throws JSONException 
	 */
	private void copySysSitePageData(List<TreeNode> tree, Long parentId, Long personnelId) throws JSONException {
		DiySitePageDto diySitePageDto = null;
		for (TreeNode treeNode : tree) {
			diySitePageDto = new DiySitePageDto();
			diySitePageDto.setUuId(treeNode.getAttribute("uuId"));
			diySitePageDto.setName(treeNode.getAttribute("name"));
			diySitePageDto.setSysPageId(MathUtils.numObj2Long(treeNode.getId()));
			diySitePageDto.setPath(treeNode.getAttribute("path"));
			diySitePageDto.setCascade(treeNode.getAttribute("cascade"));
			diySitePageDto.setParentId(parentId);
			diySitePageDto.setSiteCode(treeNode.getAttribute("siteCode"));
			diySitePageDto.setThemeCode(treeNode.getAttribute("themeCode"));
			diySitePageDto.setLayoutCode(treeNode.getAttribute("layoutCode"));
			diySitePageDto.setOpenSelf(treeNode.getAttribute("openSelf"));
			diySitePageDto.setNavHidden(treeNode.getAttribute("navHidden"));
			diySitePageDto.setIsMenu(MathUtils.booleanValueOf(treeNode.getAttribute("isMenu")));
			diySitePageDto.setIsFolder(MathUtils.booleanValueOf(treeNode.getAttribute("isFolder")));
			diySitePageDto.setThemeStyle(treeNode.getAttribute("themeStyle"));
			diySitePageDto.setSortNo(MathUtils.numObj2Integer(treeNode.getAttribute("sortNo")));
			diySitePageDto.setLinkUrl(treeNode.getAttribute("linkUrl"));
			diySitePageDto.setIsLink(MathUtils.booleanValueOf(treeNode.getAttribute("isLink")));
			diySitePageDto.setIsSystem(true);
			diySitePageDto.setUserId(personnelId);
			//新增数据
			DiySitePageDto rtnDto = diySitePageService.saveDiySitePage(diySitePageDto);
			if (!Assert.isEmpty(treeNode.getChildren()) && !Assert.isEmpty(rtnDto.getId()))
			{
				copySysSitePageData(treeNode.getChildren(), rtnDto.getId(), personnelId);
			}
		}
	}

	@ApiOperation("根据Id获取个性化站点页面信息")
	@RequestMapping(value = "/getDiySitePageInfoById", method = RequestMethod.GET)
	public ReturnDto getDiySitePageInfoById(@RequestParam Long id, @RequestParam String siteCode, HttpServletRequest request) {
		// 获取登录用户ID
		Long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));
		// 查询站点下的所有页面信息
		List<DiySitePageDto> item = diySitePageService.getDiyPageTree(siteCode, personnelId);
		if (Assert.isEmpty(item)) {
			return new ReturnDto(sitePageService.getSitePageInfoById(id));
		} else {
			return new ReturnDto(diySitePageService.getDiySitePageInfoById(id));
		}
	}

	@ApiOperation("删除个性化站点页面信息")
	@RequestMapping(value = "/delDiySitePage", method = RequestMethod.GET)
	public ReturnDto delDiySitePage(@RequestParam Long id, @RequestParam Long parentId, @RequestParam String siteCode,
			HttpServletRequest request) {
		// 获取登录用户ID
		Long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));
		// 查询站点下的所有页面信息
		List<DiySitePageDto> item = diySitePageService.getDiyPageTree(siteCode, personnelId);
		// 获取当前节点下的所有子节点
		List<Long> ids = new ArrayList<Long>();
		if (parentId != 0) {
			ids.add(id);
		}
		getChildIds(item, id, ids);
		diySitePageService.delDiySitePage(ids, parentId, personnelId);
		return new ReturnDto("删除成功！");
	}

	/**
	 * 递归获取页面ID
	 * 
	 * @param itemList
	 * @param id
	 * @param ids
	 */
	public void getChildIds(List<DiySitePageDto> itemList, Long id, List<Long> ids) {
		for (DiySitePageDto dto : itemList) {
			// 遍历出父id等于参数的id，add进子节点集合
			if (dto.getParentId() == id) {
				// 递归遍历下一级
				getChildIds(itemList, dto.getId(), ids);
				ids.add(dto.getId());
			}
		}
	}

	@ApiOperation("拖动保存个性化站点页面信息")
	@RequestMapping(value = "/saveDiySitePageTree", method = RequestMethod.POST)
	public ReturnDto saveDiySitePageTree(@RequestBody Map<String, Object> map, HttpServletRequest request) throws JSONException {
		String siteCode = MathUtils.stringObj(map.get("siteCode"));
		String draggingNode = MathUtils.stringObj(map.get("draggingNode"));
		String dataTree = MathUtils.stringObj(map.get("dataTree"));
		Long parentId = MathUtils.numObj2Long(map.get("parentId"));
		Long personnelId = Long.valueOf(request.getHeader("personnelId"));
		
		// 判断是否存在该用户个性化站点页面数据，若存在则继续，若不存在则copy系统数据一份
		DiySitePageDto diySitePageDto = new DiySitePageDto();
		diySitePageDto.setSiteCode(siteCode);
		boolean isExist = checkExistDiySitePageInfo(personnelId, diySitePageDto);
		
		return diySitePageService.saveDiySitePageTree(personnelId, siteCode, draggingNode, dataTree, parentId, isExist);
	}
	
	@ApiOperation("添加系统站点页面信息")
	@RequestMapping(value = "/addSysSitePage", method = RequestMethod.POST)
	public ReturnDto addSysSitePage(@RequestBody Map<String, Object> map, HttpServletRequest request) throws JSONException {
		String siteCode = MathUtils.stringObj(map.get("siteCode"));
		String currentNode = MathUtils.stringObj(map.get("currentNode"));
		Long personnelId = Long.valueOf(request.getHeader("personnelId"));
		DiySitePageDto diySitePageDto = new DiySitePageDto();
		diySitePageDto.setSiteCode(siteCode);
		boolean isExist = checkExistDiySitePageInfo(personnelId, diySitePageDto);
		return diySitePageService.addSysSitePage(personnelId, siteCode, currentNode, isExist);
	}

	@ApiOperation("恢复站点默认页面信息")
	@RequestMapping(value = "/resetDefSitePage", method = RequestMethod.GET)
	public ReturnDto resetDefSitePage(@RequestParam String siteCode, HttpServletRequest request) {
		Long personnelId = Long.valueOf(request.getHeader("personnelId"));
		diySitePageService.resetDefSitePage(siteCode, personnelId);
		return new ReturnDto("恢复成功！");
	}
	
	@ApiOperation("复制个性化站点页面信息")
	@RequestMapping(value = "/copyDiyPage", method = RequestMethod.GET)
	public ReturnDto copyDiyPage(@RequestParam Long id, @RequestParam String name, @RequestParam String siteCode,
			ServletRequest servletRequest) throws Exception {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		// 获取登录用户ID
		Long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));
		DiySitePageDto tmpDto = new DiySitePageDto();
		tmpDto.setSiteCode(siteCode);
		// 判断是否存在该用户个性化站点页面数据，若存在则继续，若不存在则copy系统数据一份
		boolean isExist = checkExistDiySitePageInfo(personnelId, tmpDto);
		DiySitePageDto diySitePageDto = new DiySitePageDto();
		
		if (!isExist) {
			SitePageDto sitePageDto = sitePageService.getSitePageInfoById(id);
			diySitePageDto = diySitePageService.getDiySitePageInfoByUuId(personnelId, sitePageDto.getUuId());
		}else {
			diySitePageDto = diySitePageService.getDiySitePageInfoById(id);
		}
		
		diySitePageDto.setName(name);
		diySitePageDto.setCreateBy(personnelId);
		diySitePageDto.setIsSystem(false);
		diySitePageDto.setSysPageId(null);
		
		return new ReturnDto(diySitePageService.copyDiyPage(diySitePageDto));
	}

}
