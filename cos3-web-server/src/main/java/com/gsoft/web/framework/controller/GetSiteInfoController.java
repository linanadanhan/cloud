package com.gsoft.web.framework.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.datasource.DynamicDataSourceContextHolder;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.DateUtils;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.common.constans.ResultConstant;
import com.gsoft.portal.component.theme.service.CustomThemeService;
import com.gsoft.portal.webview.site.dto.SiteDto;
import com.gsoft.portal.webview.site.service.SiteService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 获取站点信息
 * 
 * @author SN
 *
 */
@Api(tags = "站点管理", description = "获取站点信息接口服务")
@RestController
@RequestMapping("/site")
public class GetSiteInfoController {

	@Resource
	SiteService siteService;

	@Resource
	CustomThemeService customThemeService;
	
	@Autowired
	BaseDao baseDao;

	@ApiOperation("获取站点数据信息")
	@RequestMapping(value = "/siteInfo", method = RequestMethod.GET)
	public String getSiteInfo(HttpServletRequest request, @RequestParam String site, @RequestParam String domain)
			throws JSONException, ParseException {

		//根据域名查询租户信息
		Map<String, Object> siteInfo = baseDao.load("SELECT c_custormer_code, c_site_code FROM cos_custormer_site_domain WHERE c_domain = ?", domain);
		if (!Assert.isEmpty(siteInfo)) {
			// 租户信息放入会话中
			request.getSession().setAttribute("customerCode", siteInfo.get("C_CUSTORMER_CODE"));
			DynamicDataSourceContextHolder.setDataSource(MathUtils.stringObj(siteInfo.get("C_CUSTORMER_CODE")));
			site = MathUtils.stringObj(siteInfo.get("C_SITE_CODE"));
		}

		JSONObject jo = new JSONObject();
		Long personnelId = MathUtils.numObj2Long(request.getHeader("personnelId"));

		// 根据站点code获取站点信息
		SiteDto siteDto = siteService.getSiteInfoByCode(site);

		JSONObject dataJo = new JSONObject();
		dataJo.put("site", site);
		dataJo.put("name", site);
		dataJo.put("title", siteDto.getTitle());

		// 公开主题
		dataJo.put("publicTheme", siteDto.getPublicTheme());

		// 判断是否有设置节日主题和随机主题，优先顺序节日主题-->自定义主题-->随机主题-->系统默认配置
		// 节日主题
		if (!Assert.isEmpty(siteDto.getHolidayTheme())) {
			// 判断有没有设置时间范围
			if (!Assert.isEmpty(siteDto.getHolidayRange())) {
				String[] holidayRange = siteDto.getHolidayRange().split(",");
				SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss z", Locale.US);
				boolean isBelong = DateUtils.belongCalendar(new Date(),
						sdf.parse(holidayRange[0].replace("GMT", "").replaceAll("\\(.*\\)", "")),
						sdf.parse(holidayRange[1].replace("GMT", "").replaceAll("\\(.*\\)", "")));
				if (isBelong) {
					dataJo.put("privateTheme", siteDto.getHolidayTheme());
					dataJo.put("publicTheme", siteDto.getHolidayTheme());
				} else {
					setPrivateTheme(site, personnelId, siteDto, dataJo);
				}
			} else {
				setPrivateTheme(site, personnelId, siteDto, dataJo);
			}
		} else {
			setPrivateTheme(site, personnelId, siteDto, dataJo);
		}

		dataJo.put("loginType", siteDto.getLoginType());
		dataJo.put("loginWidget", "login");// TODO
		dataJo.put("logo", siteDto.getLogo());
		dataJo.put("copyright", siteDto.getCopyright());
		dataJo.put("openIm", siteDto.getOpenIm());
		dataJo.put("openDiyMenu", siteDto.getOpenDiyMenu());
		if (!Assert.isEmpty(siteInfo)) {
			dataJo.put("C_CUSTORMER_CODE", siteInfo.get("C_CUSTORMER_CODE"));
		}

		jo.put("status", ResultConstant.RESULT_RETURN_OK_STATUS);
		jo.put("data", dataJo);

		return jo.toString();
	}

	/**
	 * 设置私有主题
	 * 
	 * @param site
	 * @param personnelId
	 * @param siteDto
	 * @param dataJo
	 * @throws JSONException
	 */
	private void setPrivateTheme(String site, Long personnelId, SiteDto siteDto, JSONObject dataJo)
			throws JSONException {
		// 私有主题
		if (!Assert.isEmpty(personnelId)) {
			// 查询用户自定义主题信息
			Map<String, Object> customThemeMap = customThemeService.getCustomThemeInfo(site, personnelId);
			if (!Assert.isEmpty(customThemeMap)) {
				dataJo.put("privateTheme", customThemeMap.get("C_THEME_CODE"));
			} else {
				// 随机主题
				if (!Assert.isEmpty(siteDto.getRandomTheme())) {
					String[] randomThemes = siteDto.getRandomTheme().split(",");
					int index = (int) (Math.random() * randomThemes.length);
					dataJo.put("privateTheme", randomThemes[index]);
					dataJo.put("publicTheme", randomThemes[index]);
				} else {
					dataJo.put("privateTheme", siteDto.getPrivateTheme());
				}
			}
		} else {
			dataJo.put("privateTheme", siteDto.getPrivateTheme());
		}
	}

}
