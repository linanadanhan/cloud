package com.gsoft.portal.api.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.auth.service.PortalAuthService;
import com.gsoft.portal.common.constans.ResultConstant;
import com.gsoft.portal.webview.badge.service.PageBadgeService;
import com.gsoft.portal.webview.page.dto.SitePageDto;
import com.gsoft.portal.webview.page.service.SitePageService;
import com.gsoft.portal.webview.site.service.SiteService;
import com.gsoft.portal.webview.widgetconf.service.CustomProfileConfService;
import com.gsoft.portal.webview.widgetconf.service.CustomWidgetConfService;
import com.gsoft.portal.webview.widgetconf.service.ProfileConfService;
import com.gsoft.portal.webview.widgetconf.service.WidgetConfService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 获取页面导航接口
 * 
 * @author SN
 * 
 *
 */
@Api(tags = "站点管理", description = "获取页面导航接口服务")
@RestController
@RequestMapping("/site")
public class GetPageNavController {
	
	@Resource
	SitePageService sitePageService;
	
	@Resource
	SiteService siteService;
	
	@Resource
	CustomWidgetConfService customWidgetConfService;
	
	@Resource
	WidgetConfService widgetConfService;
	
	@Resource
	CustomProfileConfService customProfileConfService;
	
	@Resource
	ProfileConfService profileConfService;
	
	@Resource
	PageBadgeService pageBadgeService;
	
	@Resource
	PortalAuthService portalAuthService;
	
	@ApiOperation("获取站点下的所有页面信息")
	@RequestMapping(value = "/pages", method = RequestMethod.GET)
	public String getPages(@RequestParam String site, HttpServletRequest request) throws JSONException {
		
		JSONObject jo = new JSONObject();
		Long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));
		List<SitePageDto> item = null;
		
		String[] paramArr = StringUtils.split(site, "/");
		String siteCode = paramArr[0];
		String groupType = paramArr[1];
		JSONArray joArr = new JSONArray();
		
		if ("public".equals(groupType)) {// 公开页面
			item = sitePageService.getPages(siteCode,null);
			if (Assert.isNotEmpty(item)) {
				joArr = treeMenuList(item, siteCode, 1l, 0l);
			}
			
		}else {
			// 用户未登陆 zuul header过来的用户信息为空此做规避处理
			if(Assert.isEmpty(personnelId)) {
				jo.put("status", ResultConstant.RESULT_RETURN_NO_LOGIN_STATUS);
				jo.put("data", ResultConstant.RESULT_RETURN_NO_LOGIN_MSG);
				return jo.toString();
			}
			item = sitePageService.getPages(siteCode,personnelId);
			
			// 可见性校验过滤
			Iterator<SitePageDto> it = item.iterator();
			while(it.hasNext()){
				SitePageDto sitePageDto = it.next();
				boolean isShow = portalAuthService.checkPermission(sitePageDto.getShow(), personnelId);
				if (!isShow) {
					it.remove();
				}
			}
			
			if (Assert.isNotEmpty(item)) {
				joArr = treeMenuList(item, siteCode, 2l, personnelId);
			}
		}
		
		jo.put("status", ResultConstant.RESULT_RETURN_OK_STATUS);
		jo.put("data", joArr);
		
		return jo.toString();
	}
	
	/**
	 * 组装菜单项
	 * @param menuList
	 * @param siteCode
	 * @param parentId
	 * @return
	 * @throws JSONException
	 */
	public JSONArray treeMenuList(List<SitePageDto> menuList, String siteCode, long parentId, long personnelId) throws JSONException {

		JSONArray childMenu = new JSONArray();
		for (SitePageDto dto : menuList) {
			JSONObject jsonMenu = new JSONObject();
			jsonMenu.put("title", dto.getName());
			jsonMenu.put("iconCls", "");
			jsonMenu.put("folder", dto.getIsFolder());
			jsonMenu.put("hidden", ("0".equals(dto.getNavHidden()) ? false : true));
			jsonMenu.put("submenu", dto.getIsMenu());
			jsonMenu.put("pageUuId", dto.getUuId());
			
			String path = "";
			if ("0".equals(dto.getType())) {//私有页面
				path = "/" + siteCode + "/private" + dto.getCascade() + dto.getPath();
			}else {//公开页面
				path = "/" + siteCode + "/public" + dto.getCascade() + dto.getPath();
			}
			
			jsonMenu.put("path", path);
			jsonMenu.put("linkUrl", dto.getLinkUrl());
			jsonMenu.put("isLink", dto.getIsLink());

			long menuId = dto.getId();
			long pid = dto.getParentId();

			if (parentId == pid) {
				JSONArray c_node = treeMenuList(menuList, siteCode, menuId, personnelId);
				if (c_node.length() > 0) {
					jsonMenu.put("children", c_node);
				}
				Set<String> badgeName = getBadgeName(jsonMenu);
				jsonMenu.put("badgeNames", badgeName);
				childMenu.put(jsonMenu);
			}
		}
		return childMenu;
	}

	/**
	 * 根据页面ID获取widget实例是的badges
	 * @param pageUuId
	 * @return
	 * @throws JSONException 
	 */
	private Set<String> getBadgeName(JSONObject jsonMenu) throws JSONException {
		Set<String> setList = new HashSet<String>();
		getBadgeName(jsonMenu, setList);
		return pageBadgeService.getBadgeNames(new ArrayList<>(setList));
	}
	
	private void getBadgeName(JSONObject jsonMenu, Set<String> setList) throws JSONException {
		setList.add(MathUtils.stringObj(jsonMenu.getString("pageUuId")));
		if (jsonMenu.has("children")) {
			JSONArray childArr = jsonMenu.getJSONArray("children");
			for (int i = 0; i < childArr.length(); i++) {
				getBadgeName(childArr.getJSONObject(i), setList);
			}
		}
	}
}
