package com.gsoft.portal.auth.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.auth.dto.PortalAuthDto;
import com.gsoft.portal.auth.service.PortalAuthService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 门户业务授权
 * 
 * @author SN
 *
 */
@Api(tags = "站点管理", description = "站点页面授权接口服务")
@RestController
@RequestMapping("/portalAuth")
public class PortalAuthController {

	@Resource
	PortalAuthService portalAuthService;

	@ApiOperation("查询门户未授权的人员")
	@RequestMapping(value = "/getHasNoAuthPerson", method = RequestMethod.POST)
	public List<Map<String, Object>> getHasNoAuthPerson(@RequestParam Long ywId, @RequestParam String grantType,
			@RequestParam String ywType, HttpServletRequest request) {
		Long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));
		return portalAuthService.getHasNoAuthPerson(ywId, grantType, ywType, personnelId);
	}

	@ApiOperation("查询未授权的人员")
	@RequestMapping(value = "/getNoAuthPerson", method = RequestMethod.GET)
	public ReturnDto getNoAuthPerson(@RequestParam Long ywId, @RequestParam String grantType, @RequestParam String ywType,
			HttpServletRequest request) {
		Long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));
		return new ReturnDto(portalAuthService.getHasNoAuthPerson(ywId, grantType, ywType, personnelId));
	}

	@ApiOperation("查询门户已授权的人员")
	@RequestMapping(value = "/getHasAuthPerson", method = RequestMethod.POST)
	public List<Map<String, Object>> getHasAuthPerson(@RequestParam Long ywId, @RequestParam String grantType,
			@RequestParam String ywType, HttpServletRequest request) {
		Long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));
		return portalAuthService.getHasAuthPerson(ywId, grantType, ywType, personnelId);
	}

	@ApiOperation("查询已授权的人员")
	@RequestMapping(value = "/getAuthPerson", method = RequestMethod.GET)
	public ReturnDto getAuthPerson(@RequestParam Long ywId, @RequestParam String grantType, @RequestParam String ywType,
			HttpServletRequest request) {
		Long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));
		return new ReturnDto(portalAuthService.getHasAuthPerson(ywId, grantType, ywType, personnelId));
	}

	@ApiOperation("用户授权")
	@RequestMapping(value = "/savePortalAuthUser", method = RequestMethod.POST)
	public void savePortalAuthUser(@RequestBody List<PortalAuthDto> list) {
		portalAuthService.savePortalAuthUser(list);
	}
	
	@ApiOperation("保存用户授权")
	@RequestMapping(value = "/saveAuthUser", method = RequestMethod.POST)
	public ReturnDto saveAuthUser(@RequestBody List<PortalAuthDto> list) {
		portalAuthService.savePortalAuthUser(list);
		return new ReturnDto("保存成功！");
	}

	@ApiOperation("查询门户未授权的角色")
	@RequestMapping(value = "/getPortalHasNoAuthRole", method = RequestMethod.POST)
	public List<Map<String, Object>> getPortalHasNoAuthRole(@RequestParam Long ywId, @RequestParam String grantType,
			@RequestParam String ywType, @RequestParam String roleCatalog) {
		return portalAuthService.getPortalHasNoAuthRole(ywId, grantType, ywType, roleCatalog);
	}
	
	@ApiOperation("查询未授权的角色")
	@RequestMapping(value = "/getNoAuthRole", method = RequestMethod.GET)
	public ReturnDto getNoAuthRole(@RequestParam Long ywId, @RequestParam String grantType,
			@RequestParam String ywType, @RequestParam String roleCatalog) {
		return new ReturnDto(portalAuthService.getPortalHasNoAuthRole(ywId, grantType, ywType, roleCatalog));
	}

	@ApiOperation("查询门户已授权的角色")
	@RequestMapping(value = "/getPortalHasAuthRole", method = RequestMethod.POST)
	public List<Map<String, Object>> getPortalHasAuthRole(@RequestParam Long ywId, @RequestParam String grantType,
			@RequestParam String ywType, @RequestParam String roleCatalog) {
		return portalAuthService.getPortalHasAuthRole(ywId, grantType, ywType, roleCatalog);
	}
	
	@ApiOperation("查询已授权的角色")
	@RequestMapping(value = "/getAuthRole", method = RequestMethod.GET)
	public ReturnDto getAuthRole(@RequestParam Long ywId, @RequestParam String grantType,
			@RequestParam String ywType, @RequestParam String roleCatalog) {
		return new ReturnDto(portalAuthService.getPortalHasAuthRole(ywId, grantType, ywType, roleCatalog));
	}

	@ApiOperation("角色授权")
	@RequestMapping(value = "/savePortalAuthRole", method = RequestMethod.POST)
	public void savePortalAuthRole(@RequestBody List<PortalAuthDto> list) {
		portalAuthService.savePortalAuthRole(list);
	}
	
	@ApiOperation("保存角色授权")
	@RequestMapping(value = "/saveAuthRole", method = RequestMethod.POST)
	public ReturnDto saveAuthRole(@RequestBody List<PortalAuthDto> list) {
		portalAuthService.savePortalAuthRole(list);
		return new ReturnDto("保存成功！");
	}

	@ApiOperation("查询已授权站点信息")
	@RequestMapping(value = "/getAuthSiteInfo", method = RequestMethod.GET)
	public String[] getAuthSiteInfo(@RequestParam Long grantId, @RequestParam String grantType) {
		String rtnStr = portalAuthService.getAuthSiteInfo(grantId, grantType);
		if (Assert.isEmpty(rtnStr)) {
			return new String[0];
		} else {
			return rtnStr.split(",");
		}
	}

	@ApiOperation("保存站点授权")
	@RequestMapping(value = "/saveSiteAuth", method = RequestMethod.GET)
	public void saveSiteAuth(@RequestParam Long grantId, @RequestParam String grantType, @RequestParam String sites) {
		portalAuthService.saveSiteAuth(grantId, grantType, sites);
	}

	@ApiOperation("查询已授权站点页面信息")
	@RequestMapping(value = "/getAuthSitePageInfo", method = RequestMethod.GET)
	public Long[] getAuthSitePageInfo(@RequestParam Long grantId, @RequestParam String grantType) {
		String rtnStr = portalAuthService.getAuthSitePageInfo(grantId, grantType);
		if (Assert.isEmpty(rtnStr)) {
			return new Long[0];
		} else {
			Long[] ss = new Long[rtnStr.split(",").length];
			for (int i = 0; i < rtnStr.split(",").length; i++) {
				ss[i] = MathUtils.numObj2Long(rtnStr.split(",")[i]);
			}
			return ss;
		}
	}

	@ApiOperation("保存站点页面授权")
	@RequestMapping(value = "/saveSitePageAuth", method = RequestMethod.GET)
	public void saveSitePageAuth(@RequestParam Long grantId, @RequestParam String grantType, @RequestParam String ids,
			@RequestParam String siteCode) {
		portalAuthService.saveSitePageAuth(grantId, grantType, ids, siteCode);
	}
}
