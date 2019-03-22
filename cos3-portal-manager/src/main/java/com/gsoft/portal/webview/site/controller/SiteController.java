package com.gsoft.portal.webview.site.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gsoft.cos3.dto.FileNode;
import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ResponseMessageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.dto.SuccessDto;
import com.gsoft.cos3.feign.file.FileManagerFeign;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.FileUtils;
import com.gsoft.cos3.util.ZipUtils;
import com.gsoft.portal.common.constans.ParameterConstant;
import com.gsoft.portal.component.decorate.dto.DecorateDto;
import com.gsoft.portal.component.decorate.service.DecorateService;
import com.gsoft.portal.component.layout.dto.LayoutDto;
import com.gsoft.portal.component.layout.service.LayoutService;
import com.gsoft.portal.component.theme.dto.ThemeDto;
import com.gsoft.portal.component.theme.service.ThemeService;
import com.gsoft.portal.system.basicdata.service.ParameterService;
import com.gsoft.portal.webview.page.dto.SitePageDto;
import com.gsoft.portal.webview.page.dto.SitePageHelpDto;
import com.gsoft.portal.webview.page.service.SitePageConfService;
import com.gsoft.portal.webview.page.service.SitePageHelpService;
import com.gsoft.portal.webview.page.service.SitePageService;
import com.gsoft.portal.webview.site.dto.SiteDto;
import com.gsoft.portal.webview.site.service.SiteService;
import com.gsoft.portal.webview.widget.dto.WidgetDto;
import com.gsoft.portal.webview.widget.service.WidgetService;
import com.gsoft.portal.webview.widgetconf.service.CustomWidgetConfService;
import com.gsoft.portal.webview.widgetconf.service.ProfileConfService;
import com.gsoft.portal.webview.widgetconf.service.WidgetConfService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 站点管理
 * 
 * @author SN
 *
 */
@Api(tags = "站点管理", description = "站点相关接口服务")
@RestController
@RequestMapping("/site")
public class SiteController {

	@Resource
	SiteService siteService;

	@Resource
	ParameterService parameterService;

	@Resource
	ThemeService themeService;

	@Resource
	SitePageService sitePageService;

	@Resource
	LayoutService layoutService;

	@Resource
	WidgetConfService widgetConfService;

	@Resource
	WidgetService widgetService;

	@Resource
	DecorateService decorateService;

	@Resource
	CustomWidgetConfService customWidgetConfService;

	@Resource
	SitePageConfService sitePageConfService;

	@Resource
	ProfileConfService profileConfService;
	
	@Resource
	SitePageHelpService sitePageHelpService;
	
	@Resource
	FileManagerFeign fileManagerFeign;

	/**
	 * 本地临时目录
	 */
	public static String tmpRootPath = System.getProperty("java.io.tmpdir") + File.separator + "sitepack" + File.separator;

	@ApiOperation("分页查找站点基本信息")
	@RequestMapping(value = "/querySiteDataTable", method = RequestMethod.GET)
	public PageDto querySiteDataTable(@RequestParam String search,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size,
			@RequestParam(value = "sortProp", defaultValue = "id") String sortProp,
			@RequestParam(value = "order", defaultValue = "desc") String order) {

		return siteService.querySiteDataTable(search, page, size, sortProp, order);

	}

	@ApiOperation("根据Id获取站点信息")
	@RequestMapping(value = "/getSiteInfoById", method = RequestMethod.GET)
	public SiteDto getSiteInfoById(@RequestParam Long id) {
		return siteService.getSiteInfoById(id);
	}

	@ApiOperation("获取所有站点信息")
	@RequestMapping(value = "/getAllSiteList", method = RequestMethod.GET)
	public List<SiteDto> getAllSiteList() {
		return siteService.getAllSiteList();
	}
	
	@ApiOperation("获取所有站点列表")
	@RequestMapping(value = "/getSiteList", method = RequestMethod.GET)
	public ReturnDto getSiteList() {
		return new ReturnDto(siteService.getAllSiteList());
	}

	@ApiOperation("判断站点代码是否存在")
	@RequestMapping(value = "/isExitSiteCode", method = RequestMethod.GET)
	public Boolean isExitSiteCode(@RequestParam(required = false) Long id, @RequestParam String code) {
		return siteService.isExitSiteCode(id, code);
	}
	
	@ApiOperation("判断站点域名是否已存在")
	@RequestMapping(value = "/isExitSiteDomain", method = RequestMethod.GET)
	public Boolean isExitSiteDomain(@RequestParam String domain, @RequestParam String siteCode) {
		return siteService.isExitSiteDomain(domain, siteCode);
	}

	@ApiOperation("保存站点信息")
	@RequestMapping(value = "/saveSite", method = RequestMethod.POST)
	public SiteDto saveSite(@ModelAttribute("siteDto") SiteDto siteDto, ServletRequest servletRequest) {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		
		if (Assert.isEmpty(siteDto.getId())) {
			siteDto.setCreateTime(new Date());
			siteDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));
		} else {
			siteDto.setUpdateTime(new Date());
			siteDto.setUpdateBy(Long.valueOf(request.getHeader("personnelId")));
		}
		
		// 租户
		String customer = request.getHeader("Site-info");
		siteDto.setCustomer(customer);

		return siteService.saveSite(siteDto);
	}

	@ApiOperation("删除站点信息")
	@RequestMapping(value = "/delSite", method = RequestMethod.GET)
	public void delSite(@RequestParam Long id, @RequestParam String code) {
		siteService.delSite(id, code);
	}

	@ApiOperation("复制站点信息")
	@RequestMapping(value = "/copySite", method = RequestMethod.GET)
	public ReturnDto copySite(@RequestParam String oSiteCode, @RequestParam String nSiteCode, @RequestParam String siteName,
			ServletRequest servletRequest) throws Exception {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		Long personnelId = Long.valueOf(request.getHeader("personnelId"));
		
		JSONObject jo = new JSONObject();

		Set<String> themeSet = new HashSet<String>();// 所有主题代码
		Set<String> layoutSet = new HashSet<String>();// 所有布局代码
		Set<String> widgetSet = new HashSet<String>();// 所有widget代码
		Set<String> decorateSet = new HashSet<String>();// 所有修饰器代码

		// 查询站点信息
		SiteDto siteDto = siteService.getSiteInfoByCode(oSiteCode);

		jo.put("siteCode", nSiteCode);
		jo.put("siteName", siteName);
		jo.put("title", siteDto.getTitle());
		jo.put("loginType", siteDto.getLoginType());
		jo.put("logo", siteDto.getLogo());
		jo.put("copyright", siteDto.getCopyright());
		jo.put("randomTheme", siteDto.getRandomTheme());
		jo.put("holidayTheme", siteDto.getHolidayTheme());
		jo.put("holidayRange", siteDto.getHolidayRange());
		jo.put("profileTheme", siteDto.getPrivateTheme());
		jo.put("openIm", siteDto.getOpenIm());
		jo.put("domainName", siteDto.getDomainName());
		
		// 公开页面引导页 
		List<SitePageHelpDto> publicGuide = sitePageHelpService.getPageHelpByParams(oSiteCode, null, "1");
		JSONArray pGuideJo = new JSONArray();
		if (!Assert.isEmpty(publicGuide) && publicGuide.size() > 0) {
			JSONObject tmpJo = null;
			for (SitePageHelpDto dto : publicGuide) {
				tmpJo = new JSONObject();
				tmpJo.put("pageUuId", dto.getPageUuId());
				tmpJo.put("siteCode", nSiteCode);
				tmpJo.put("type", dto.getType());
				tmpJo.put("photo", dto.getPhoto());
				tmpJo.put("content", dto.getContent());
				tmpJo.put("title", dto.getTitle());
				tmpJo.put("files", dto.getFiles());
				pGuideJo.put(tmpJo);
			}
		}
		jo.put("publicGuide", pGuideJo);
		
		// 私有页面引导页
		List<SitePageHelpDto> privateGuide = sitePageHelpService.getPageHelpByParams(oSiteCode, null, "0");
		
		JSONArray sGuideJo = new JSONArray();
		if (!Assert.isEmpty(privateGuide) && privateGuide.size() > 0) {
			JSONObject tmpJo = null;
			for (SitePageHelpDto dto : privateGuide) {
				tmpJo = new JSONObject();
				tmpJo.put("pageUuId", dto.getPageUuId());
				tmpJo.put("siteCode", nSiteCode);
				tmpJo.put("type", dto.getType());
				tmpJo.put("photo", dto.getPhoto());
				tmpJo.put("content", dto.getContent());
				tmpJo.put("title", dto.getTitle());
				tmpJo.put("files", dto.getFiles());
				sGuideJo.put(tmpJo);
			}
		}
		jo.put("privateGuide", sGuideJo);

		// 公共主题
		String publicTheme = siteDto.getPublicTheme();
		jo.put("publicTheme", publicTheme);
		themeSet.add(publicTheme);

		// 私有主题
		String privateTheme = siteDto.getPrivateTheme();
		jo.put("privateTheme", privateTheme);
		themeSet.add(privateTheme);

		// 页面信息
		List<JSONArray> pageJoArrs = new ArrayList<JSONArray>();
		// 公开页面信息
		List<SitePageDto> publicPageList = sitePageService.getSitePageTree(oSiteCode, "1");

		JSONArray jsonArr = new JSONArray();

		if (Assert.isNotEmpty(publicPageList)) {
			jsonArr = treePageList(publicPageList, nSiteCode, 1l, "1", layoutSet, widgetSet, decorateSet);
		}
		pageJoArrs.add(jsonArr);

		// 私有页面信息
		List<SitePageDto> privatePageList = sitePageService.getSitePageTree(oSiteCode, "0");

		jsonArr = new JSONArray();

		if (Assert.isNotEmpty(publicPageList)) {
			jsonArr = treePageList(privatePageList, nSiteCode, 2l, "0", layoutSet, widgetSet, decorateSet);
		}
		pageJoArrs.add(jsonArr);
		jo.put("pages", pageJoArrs);
		siteService.copySite(new JSONObject(jo.toString()), personnelId);
		return new ReturnDto("复制成功！");
	}

	@ApiOperation("站点导出")
	@RequestMapping(value = "/exportSite", method = RequestMethod.POST)
	public ResponseMessageDto exportSite(HttpServletRequest httpRequest, @RequestParam String siteCode)
			throws Exception {

		boolean resFlag = true;
		String referenceId = "";
		String zipFilePath = "";
		String errMsg = "";

		JSONObject jo = new JSONObject();

		Set<String> themeSet = new HashSet<String>();// 所有主题代码
		Set<String> layoutSet = new HashSet<String>();// 所有布局代码
		Set<String> widgetSet = new HashSet<String>();// 所有widget代码
		Set<String> decorateSet = new HashSet<String>();// 所有修饰器代码

		try {
			File tmpFile = new File(tmpRootPath);
			if (!tmpFile.exists()) {
				tmpFile.mkdirs();
			}

			// 查询站点信息
			SiteDto siteDto = siteService.getSiteInfoByCode(siteCode);

			jo.put("siteCode", siteCode);
			jo.put("siteName", siteDto.getName());
			jo.put("title", siteDto.getTitle());
			jo.put("loginType", siteDto.getLoginType());
			jo.put("logo", siteDto.getLogo());
			jo.put("copyright", siteDto.getCopyright());
			jo.put("randomTheme", siteDto.getRandomTheme());
			jo.put("holidayTheme", siteDto.getHolidayTheme());
			jo.put("holidayRange", siteDto.getHolidayRange());
			jo.put("profileTheme", siteDto.getPrivateTheme());
			jo.put("openIm", siteDto.getOpenIm());
			jo.put("domainName", siteDto.getDomainName());

			// 公共主题
			String publicTheme = siteDto.getPublicTheme();
			jo.put("publicTheme", publicTheme);
			themeSet.add(publicTheme);

			// 私有主题
			String privateTheme = siteDto.getPrivateTheme();
			jo.put("privateTheme", privateTheme);
			themeSet.add(privateTheme);
			
			// 公开页面引导页 
			List<SitePageHelpDto> publicGuide = sitePageHelpService.getPageHelpByParams(siteCode, null, "1");
			JSONArray pGuideJo = new JSONArray();
			if (!Assert.isEmpty(publicGuide) && publicGuide.size() > 0) {
				JSONObject tmpJo = null;
				for (SitePageHelpDto dto : publicGuide) {
					tmpJo = new JSONObject();
					tmpJo.put("pageUuId", dto.getPageUuId());
					tmpJo.put("siteCode", dto.getSiteCode());
					tmpJo.put("type", dto.getType());
					tmpJo.put("photo", dto.getPhoto());
					tmpJo.put("content", dto.getContent());
					tmpJo.put("title", dto.getTitle());
					tmpJo.put("files", dto.getFiles());
					pGuideJo.put(tmpJo);
				}
			}
			jo.put("publicGuide", pGuideJo);
			
			// 私有页面引导页
			List<SitePageHelpDto> privateGuide = sitePageHelpService.getPageHelpByParams(siteCode, null, "0");
			
			JSONArray sGuideJo = new JSONArray();
			if (!Assert.isEmpty(privateGuide) && privateGuide.size() > 0) {
				JSONObject tmpJo = null;
				for (SitePageHelpDto dto : privateGuide) {
					tmpJo = new JSONObject();
					tmpJo.put("pageUuId", dto.getPageUuId());
					tmpJo.put("siteCode", dto.getSiteCode());
					tmpJo.put("type", dto.getType());
					tmpJo.put("photo", dto.getPhoto());
					tmpJo.put("content", dto.getContent());
					tmpJo.put("title", dto.getTitle());
					tmpJo.put("files", dto.getFiles());
					sGuideJo.put(tmpJo);
				}
			}
			jo.put("privateGuide", sGuideJo);
			
			// 页面信息
			List<JSONArray> pageJoArrs = new ArrayList<JSONArray>();
			// 公开页面信息
			List<SitePageDto> publicPageList = sitePageService.getSitePageTree(siteCode, "1");
			JSONArray jsonArr = new JSONArray();

			if (Assert.isNotEmpty(publicPageList)) {
				jsonArr = treePageList(publicPageList, siteCode, 1l, "1", layoutSet, widgetSet, decorateSet);
			}
			pageJoArrs.add(jsonArr);

			// 私有页面信息
			List<SitePageDto> privatePageList = sitePageService.getSitePageTree(siteCode, "0");
			jsonArr = new JSONArray();

			if (Assert.isNotEmpty(publicPageList)) {
				jsonArr = treePageList(privatePageList, siteCode, 2l, "0", layoutSet, widgetSet, decorateSet);
			}
			pageJoArrs.add(jsonArr);

			// 单独拼装站点用到的所有主题、布局、widget、修饰器
			// 主题
			JSONArray themeArr = new JSONArray();
			if (Assert.isNotEmpty(themeSet)) {
				for (String themeCode : themeSet) {
					JSONObject jsonTheme = new JSONObject();
					ThemeDto themeDto = themeService.getThemeInfoByCode(themeCode);
					String projectCode = themeDto.getProjectCode();
					jsonTheme.put("code", themeCode);
					jsonTheme.put("name", themeDto.getName());
					jsonTheme.put("projectCode", projectCode);
					jsonTheme.put("isSystem", themeDto.getIsSystem());
					jsonTheme.put("isOpen", themeDto.getIsOpen());

					if (!Assert.isEmpty(themeDto.getId()) && !themeDto.getIsSystem()) {
						// 复制主题包
						movePackToLocal(tmpRootPath + siteCode, themeCode, projectCode, "theme");
						themeArr.put(jsonTheme);
					}
				}
			}
			jo.put("themes", themeArr);

			// 布局
			JSONArray layoutArr = new JSONArray();
			if (Assert.isNotEmpty(layoutSet)) {
				for (String layoutCode : layoutSet) {
					JSONObject jsonLayout = new JSONObject();
					LayoutDto layoutDto = layoutService.getLayoutInfoByCode(layoutCode);
					String projectCode = layoutDto.getProjectCode();
					jsonLayout.put("code", layoutCode);
					jsonLayout.put("name", layoutDto.getName());
					jsonLayout.put("projectCode", projectCode);
					jsonLayout.put("isSystem", layoutDto.getIsSystem());

					if (!Assert.isEmpty(layoutDto.getId()) && !layoutDto.getIsSystem()) {
						// 复制布局包
						movePackToLocal(tmpRootPath + siteCode, layoutCode, projectCode, "layout");
						layoutArr.put(jsonLayout);
					}
				}
			}
			jo.put("layouts", layoutArr);

			// widget
			JSONArray widgetArr = new JSONArray();
			if (Assert.isNotEmpty(widgetSet)) {
				for (String widgetCode : widgetSet) {
					JSONObject jsonWidget = new JSONObject();
					WidgetDto widgetDto = widgetService.getWidgetInfoByCode(widgetCode);
					String projectCode = widgetDto.getProjectCode();
					jsonWidget.put("code", widgetCode);
					jsonWidget.put("name", widgetDto.getName());
					jsonWidget.put("projectCode", projectCode);
					jsonWidget.put("isSystem", widgetDto.getIsSystem());
					jsonWidget.put("isNested",
							(Assert.isEmpty(widgetDto.getIsNested()) ? false : widgetDto.getIsNested()));

					if (!Assert.isEmpty(widgetDto.getId()) && !widgetDto.getIsSystem()) {
						// 复制widget包
						if (Assert.isEmpty(widgetDto.getIsNested()) || !widgetDto.getIsNested()) {
							movePackToLocal(tmpRootPath + siteCode, widgetCode, projectCode, "widget");
						}
						widgetArr.put(jsonWidget);
					}
				}
			}
			jo.put("widgets", widgetArr);

			// 修饰器
			JSONArray decorateArr = new JSONArray();
			if (Assert.isNotEmpty(decorateSet)) {
				for (String decorateCode : decorateSet) {
					JSONObject jsonDecorate = new JSONObject();
					DecorateDto decorateDto = decorateService.getDecorateInfoByCode(decorateCode);
					String projectCode = decorateDto.getProjectCode();
					jsonDecorate.put("code", decorateCode);
					jsonDecorate.put("name", decorateDto.getName());
					jsonDecorate.put("projectCode", projectCode);
					jsonDecorate.put("isSystem", decorateDto.getIsSystem());

					if (!Assert.isEmpty(decorateDto.getId()) && !decorateDto.getIsSystem()) {
						// 复制修饰器包
						movePackToLocal(tmpRootPath + siteCode, decorateCode, projectCode, "decorator");
						decorateArr.put(jsonDecorate);
					}
				}
			}
			jo.put("decorates", decorateArr);
			jo.put("pages", pageJoArrs);

			// 写json文件
			File jsonDir = new File(tmpRootPath + siteCode + "/");
			if (!jsonDir.exists()) {
				jsonDir.mkdirs();
			}

			File jsonFile = new File(tmpRootPath + siteCode + "/" + siteCode + ".json");

			FileOutputStream out = null;
			out = new FileOutputStream(jsonFile, false);

			out.write(jo.toString().getBytes("utf-8"));

			if (null != out) {
				out.close();
			}

			zipFilePath = tmpRootPath + siteCode + ".zip";

			// 压缩文件
			ZipUtils.zip(tmpRootPath + siteCode, zipFilePath);

			// 删除临时文件夹
			FileUtils.deleteQuietly(new File(tmpRootPath + siteCode));
			
			// 上传到文件存储服务
			File f = new File(zipFilePath);
			FileInputStream inp = new FileInputStream(f);
			MultipartFile multipartFile = new MockMultipartFile("file", f.getName(), "text/plain", inp);
			FileNode fNode = fileManagerFeign.webUploader(multipartFile);
			referenceId = fNode.getReferenceId();

			// 删除zip临时文件
			FileUtils.deleteQuietly(new File(zipFilePath));

		} catch (Exception e) {
			errMsg = e.getMessage();
			resFlag = false;
		}

		if (resFlag) {
			return new SuccessDto("导出成功", referenceId);
		} else {
			return new ResponseMessageDto(false, "导出失败,原因:" + errMsg);
		}
	}

	/**
	 * 页面json数据组织
	 * 
	 * @param pageList
	 * @param siteCode
	 * @param parentId
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 */
	private JSONArray treePageList(List<SitePageDto> pageList, String siteCode, long parentId, String type,
			Set<String> layoutSet, Set<String> widgetSet, Set<String> decorateSet) throws JSONException, IOException {

		JSONArray jsonPageArr = new JSONArray();
		for (SitePageDto dto : pageList) {
			String pageUuId = dto.getUuId();
			JSONObject jsonPage = new JSONObject();

			jsonPage.put("uuId", pageUuId);
			jsonPage.put("path", dto.getPath());
			jsonPage.put("name", dto.getName());
			jsonPage.put("cascade", dto.getCascade());
			jsonPage.put("type", type);
			jsonPage.put("openSelf", dto.getOpenSelf());
			jsonPage.put("navHidden", dto.getNavHidden());
			jsonPage.put("allowWidget", dto.getAllowWidget());
			jsonPage.put("allowLayout", dto.getAllowLayout());
			jsonPage.put("isMenu", dto.getIsMenu());
			jsonPage.put("isFolder", dto.getIsFolder());
			jsonPage.put("themeStyle", dto.getThemeStyle());
			jsonPage.put("siteCode", siteCode);
			jsonPage.put("sortNo", dto.getSortNo());
			jsonPage.put("linkUrl", dto.getLinkUrl());
			jsonPage.put("isLink", dto.getIsLink());
			
			// 页面帮助信息
			List<SitePageHelpDto> pageHelpList = sitePageHelpService.getPageHelpByParams(siteCode, pageUuId, type);
			if (!Assert.isEmpty(pageHelpList) && pageHelpList.size() > 0) {
				SitePageHelpDto tmpSitePageHelpDto = pageHelpList.get(0);
				JSONObject tmpJo = new JSONObject();
				tmpJo.put("pageUuId", tmpSitePageHelpDto.getPageUuId());
				tmpJo.put("siteCode", tmpSitePageHelpDto.getSiteCode());
				tmpJo.put("type", tmpSitePageHelpDto.getType());
				tmpJo.put("photo", tmpSitePageHelpDto.getPhoto());
				tmpJo.put("content", tmpSitePageHelpDto.getContent());
				tmpJo.put("title", tmpSitePageHelpDto.getTitle());
				tmpJo.put("files", tmpSitePageHelpDto.getFiles());
				jsonPage.put("pageHelp", tmpJo);
			}
			
			// 页面布局
			String layoutCode = dto.getLayoutCode();
			jsonPage.put("layoutCode", layoutCode);
			layoutSet.add(layoutCode);

			// 页面widgets配置实例信息组织
			JSONArray pageWidgetInstanceJoArr = new JSONArray();
			String widgetInstanceJson = widgetConfService.getWidgetJson(pageUuId);
			if (!Assert.isEmpty(widgetInstanceJson)) {
				pageWidgetInstanceJoArr = new JSONArray(widgetInstanceJson);
			}
			jsonPage.put("pageWidgetInstances", pageWidgetInstanceJoArr);

			// 组装实例系统配置信息
			JSONObject widgetParamJsonObj = new JSONObject();
			getWidgetInstanceParams(pageUuId, widgetParamJsonObj);
			jsonPage.put("widgetInstanceParams", widgetParamJsonObj);

			long menuId = dto.getId();
			long pid = dto.getParentId();

			if (parentId == pid) {
				JSONArray c_node = treePageList(pageList, siteCode, menuId, type, layoutSet, widgetSet, decorateSet);
				if (c_node.length() > 0) {
					jsonPage.put("children", c_node);
				}
				jsonPageArr.put(jsonPage);
			}
		}

		return jsonPageArr;
	}

	/**
	 * 组装widget 系统参数信息
	 * 
	 * @param pageWidgetInstanceJoArr
	 * @param widgetParamJsonObj
	 * @throws JSONException
	 */
	private void getWidgetInstanceParams(String pageUuId, JSONObject widgetParamJsonObj) throws JSONException {
		// 根据页面ID获取所有widget系统配置信息
		JSONObject sysJo = profileConfService.getSysConfListByPageUuId(pageUuId);
		if (!Assert.isEmpty(sysJo) && sysJo.length() > 0) {
			@SuppressWarnings("rawtypes")
			Iterator iterator = sysJo.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				JSONObject value = sysJo.getJSONObject(key);
				widgetParamJsonObj.put(key, value);
			}			
		}
	}

	/**
	 * 移动对应主题、布局、修饰器、widget到本地临时目录下
	 * 
	 * @param tarPath
	 * @param code
	 * @param projectCode
	 * @param type
	 * @throws IOException
	 */
	private void movePackToLocal(String tarPath, String code, String projectCode, String type) throws IOException {

		if (Assert.isEmpty(projectCode)) {
			return;
		}

		File toFile = null;

		String destDir = parameterService.getParmValueByKey(ParameterConstant.PORTAL_MODULES_PATH[0],
				ParameterConstant.PORTAL_MODULES_PATH[1]);

		if (!destDir.endsWith("/")) {
			destDir = destDir + "/";
		}

		if ("theme".equals(type)) {
			toFile = new File(tarPath + File.separator + "themes/" + code);
			destDir = destDir + projectCode + "/themes/" + code + "/";

		} else if ("layout".equals(type)) {
			toFile = new File(tarPath + File.separator + "layouts/" + code);
			destDir = destDir + projectCode + "/layouts/" + code + "/";

		} else if ("widget".equals(type)) {
			toFile = new File(tarPath + File.separator + "widgets/" + code);
			destDir = destDir + projectCode + "/widgets/" + code + "/";

		} else if ("decorator".equals(type)) {
			toFile = new File(tarPath + File.separator + "decorators/" + code);
			destDir = destDir + projectCode + "/decorators/" + code + "/";

		}

		File sourceFile = new File(destDir);

		if (!toFile.exists()) {
			toFile.mkdirs();
		}

		if (!sourceFile.exists()) {
			sourceFile.mkdirs();
		}

		FileUtils.copyFolder(sourceFile, toFile);
	}

	/**
	 * 移动对应主题、布局、修饰器、widget到本地临时目录下
	 * 
	 * @param tarPath
	 * @param code
	 * @param projectCode
	 * @param type
	 * @throws IOException
	 */
	private void movePackToLine(String tarPath, String code, String projectCode, String type) throws IOException {

		if (Assert.isEmpty(projectCode)) {
			return;
		}

		File toFile = null;

		String destDir = parameterService.getParmValueByKey(ParameterConstant.PORTAL_MODULES_PATH[0],
				ParameterConstant.PORTAL_MODULES_PATH[1]);

		if (!destDir.endsWith("/")) {
			destDir = destDir + "/";
		}

		if ("theme".equals(type)) {
			code = code.replace(projectCode + "/themes/", "");
			toFile = new File(destDir + projectCode + "/themes/" + code + "/");
			destDir = tarPath + "/themes/" + code + "/";

		} else if ("layout".equals(type)) {
			code = code.replace(projectCode + "/layouts/", "");
			toFile = new File(destDir + projectCode + "/layouts/" + code + "/");
			destDir = tarPath + "/layouts/" + code + "/";

		} else if ("widget".equals(type)) {
			code = code.replace(projectCode + "/widgets/", "");
			toFile = new File(destDir + projectCode + "/widgets/" + code + "/");
			destDir = tarPath + "/widgets/" + code + "/";

		} else if ("decorator".equals(type)) {
			code = code.replace(projectCode + "/decorators/", "");
			toFile = new File(destDir + projectCode + "/decorators/" + code + "/");
			destDir = tarPath + "/decorators/" + code + "/";
		}

		File sourceFile = new File(destDir);

		if (!toFile.exists()) {
			toFile.mkdirs();
		}

		if (!sourceFile.exists()) {
			sourceFile.mkdirs();
		}

		FileUtils.copyFolder(sourceFile, toFile);
	}

	/**
	 * @param file
	 * @param request
	 * @param type
	 * @param json
	 * @return
	 * @throws Exception
	 */
	@ApiOperation("站点导入")
	@RequestMapping(value = "/importSite")
	public ResponseMessageDto importSite(@RequestParam(required = false) String referenceId, 
			@RequestParam(required = false) String type, 
			@RequestParam(required = false) String fileName,
			@RequestParam(required = false) String json) throws Exception {

		boolean resFlag = false;
		String errMsg = "";

		try {
			// 第一次导入
			if (Assert.isEmpty(type)) {
				ResponseEntity<byte[]> entity = fileManagerFeign.download(referenceId);
				InputStream ins = new ByteArrayInputStream(entity.getBody());
				
				File tmpFile = new File(tmpRootPath + fileName);
				FileUtils.copyInputStreamToFile(ins, tmpFile);
				
				String dirName = fileName.substring(0, fileName.indexOf("."));
				
				// 2.解压压缩文件
				ZipUtils.unZip(tmpFile.getAbsolutePath(), tmpRootPath + dirName);

				// 删除临时压缩文件
				FileUtils.deleteQuietly(tmpFile);

				// 读取解压目录json文件
				File jsonFile = new File(tmpRootPath + dirName + "/" + dirName + ".json");

				if (jsonFile.exists()) {

					String nContent = FileUtils.readFileToString(jsonFile, "UTF-8");
					JSONObject jsonObj = new JSONObject(nContent);

					// 读取站点信息
					String siteCode = jsonObj.getString("siteCode");

					if (!Assert.isEmpty(siteCode)) {
						// 检核站点CODE是否已存在
						SiteDto siteDto = siteService.getSiteInfoByCode(siteCode);

						if (Assert.isNotEmpty(siteDto.getId())) {
							return new ResponseMessageDto(true, siteCode, "0", jsonObj.toString());// 站点已存在
						} else {
							ResponseMessageDto responseMessageDto = checkPacks(jsonObj);
							if (!Assert.isEmpty(responseMessageDto)) {
								return responseMessageDto;
							}

							// 处理导入数据
							resFlag = handleImportData(jsonObj);
						}

					} else {
						return new ResponseMessageDto(false, "导入站点json文件站点CODE不存在！");
					}

				} else {
					return new ResponseMessageDto(false, "导入站点json文件不存在！");
				}
			} else if ("0".equals(type)) {// 站点
				JSONObject jsonObj = new JSONObject(json);
				ResponseMessageDto responseMessageDto = checkPacks(jsonObj);
				if (!Assert.isEmpty(responseMessageDto)) {
					return responseMessageDto;
				}

				// 处理导入数据
				resFlag = handleImportData(jsonObj);

			} else if ("1".equals(type)) {// 主题
				JSONObject jsonObj = new JSONObject(json);
				ResponseMessageDto responseMessageDto = checkPacks(jsonObj);
				if (!Assert.isEmpty(responseMessageDto)) {
					return responseMessageDto;
				}

				// 处理导入数据
				resFlag = handleImportData(jsonObj);

			} else if ("2".equals(type)) {// 布局
				JSONObject jsonObj = new JSONObject(json);
				ResponseMessageDto responseMessageDto = checkPacks(jsonObj);
				if (!Assert.isEmpty(responseMessageDto)) {
					return responseMessageDto;
				}

				// 处理导入数据
				resFlag = handleImportData(jsonObj);

			} else if ("3".equals(type)) {// 修饰器
				JSONObject jsonObj = new JSONObject(json);
				ResponseMessageDto responseMessageDto = checkPacks(jsonObj);
				if (!Assert.isEmpty(responseMessageDto)) {
					return responseMessageDto;
				}

				// 处理导入数据
				resFlag = handleImportData(jsonObj);

			} else if ("4".equals(type)) {// widget
				JSONObject jsonObj = new JSONObject(json);
				ResponseMessageDto responseMessageDto = checkPacks(jsonObj);
				if (!Assert.isEmpty(responseMessageDto)) {
					return responseMessageDto;
				}

				// 处理导入数据
				resFlag = handleImportData(jsonObj);
			}
		} catch (Exception e) {
			errMsg = e.getMessage();
			resFlag = false;
		}

		if (resFlag) {
			return new SuccessDto("导入成功");
		} else {
			return new ResponseMessageDto(false, "导入失败,原因:" + errMsg);
		}
	}

	/**
	 * 检核主题、布局、修饰器、widget
	 * 
	 * @param jsonObj
	 * @return
	 * @throws JSONException
	 */
	private ResponseMessageDto checkPacks(JSONObject jsonObj) throws JSONException {
		// 检核主题
		ResponseMessageDto responseMessageDto = checkTheme(jsonObj);
		if (!Assert.isEmpty(responseMessageDto)) {
			return responseMessageDto;
		}

		// 检核布局
		responseMessageDto = checkLayout(jsonObj);
		if (!Assert.isEmpty(responseMessageDto)) {
			return responseMessageDto;
		}

		// 检核修饰器
		responseMessageDto = checkDecorate(jsonObj);
		if (!Assert.isEmpty(responseMessageDto)) {
			return responseMessageDto;
		}

		// 检核widget
		responseMessageDto = checkWidget(jsonObj);
		if (!Assert.isEmpty(responseMessageDto)) {
			return responseMessageDto;
		}

		return null;
	}

	/**
	 * 导入站点数据处理
	 * 
	 * @param jsonObj
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 */
	private boolean handleImportData(JSONObject jsonObj) throws Exception {

		// 1.数据库操作
		boolean dataFlag = siteService.handleImportData(jsonObj);

		// 2.根据选择移动主题、布局、修饰器、widget文件包
		if (dataFlag) {
			String siteCode = jsonObj.getString("siteCode");

			// 主题包移动
			JSONArray themeArray = jsonObj.getJSONArray("themes");
			for (int i = 0; i < themeArray.length(); i++) {
				JSONObject obj = themeArray.getJSONObject(i);
				if (!obj.has("isCover") || (obj.has("isCover") && "1".equals(obj.get("isCover")))) {
					String themeCode = obj.getString("code");
					ThemeDto themeDto = themeService.getThemeInfoByCode(themeCode);
					movePackToLine(tmpRootPath + siteCode, themeCode, themeDto.getProjectCode(), "theme");
				}
			}

			// 布局包移动
			JSONArray layoutArray = jsonObj.getJSONArray("layouts");
			for (int i = 0; i < layoutArray.length(); i++) {
				JSONObject obj = layoutArray.getJSONObject(i);
				if (!obj.has("isCover") || (obj.has("isCover") && "1".equals(obj.get("isCover")))) {
					String layoutCode = obj.getString("code");
					LayoutDto layoutDto = layoutService.getLayoutInfoByCode(layoutCode);
					movePackToLine(tmpRootPath + siteCode, layoutCode, layoutDto.getProjectCode(), "layout");
				}
			}

			// 修饰器包移动
			JSONArray decorateArray = jsonObj.getJSONArray("decorates");
			for (int i = 0; i < decorateArray.length(); i++) {
				JSONObject obj = decorateArray.getJSONObject(i);
				if (!obj.has("isCover") || (obj.has("isCover") && "1".equals(obj.get("isCover")))) {
					String decorateCode = obj.getString("code");
					DecorateDto decorateDto = decorateService.getDecorateInfoByCode(decorateCode);
					movePackToLine(tmpRootPath + siteCode, decorateCode, decorateDto.getProjectCode(), "decorator");
				}
			}

			// widget包移动
			JSONArray widgetArray = jsonObj.getJSONArray("widgets");
			for (int i = 0; i < widgetArray.length(); i++) {
				JSONObject obj = widgetArray.getJSONObject(i);
				if (!obj.has("isCover") || (obj.has("isCover") && "1".equals(obj.get("isCover")))) {
					String widgetCode = obj.getString("code");
					WidgetDto widgetDto = widgetService.getWidgetInfoByCode(widgetCode);
					if (Assert.isEmpty(widgetDto.getIsNested()) || !widgetDto.getIsNested()) {
						movePackToLine(tmpRootPath + siteCode, widgetCode, widgetDto.getProjectCode(), "widget");
					}
				}
			}

			// 移动完成后清除本地解压文件夹
			FileUtils.deleteQuietly(new File(tmpRootPath + siteCode));

			return true;
		}

		return false;
	}

	/**
	 * 导入时检核主题
	 * 
	 * @param jsonObj
	 * @return
	 * @throws JSONException
	 */
	private ResponseMessageDto checkTheme(JSONObject jsonObj) throws JSONException {
		JSONArray themeArray = jsonObj.getJSONArray("themes");
		if (Assert.isEmpty(themeArray) || themeArray.length() <= 0) {
			// return new ResponseMessageDto(false, "导入站点json文件主题不存在！");

		} else {
			boolean isExsit = false;
			String tmpTheme = "";
			for (int i = 0; i < themeArray.length(); i++) {
				JSONObject obj = themeArray.getJSONObject(i);
				String themeCode = obj.getString("code");

				if (!obj.has("isCover")) {
					ThemeDto themeDto = themeService.getThemeInfoByCode(themeCode);
					if (Assert.isNotEmpty(themeDto.getId())) {
						tmpTheme = themeCode;
						isExsit = true;
						break;
					}
				}
			}
			if (isExsit) {
				return new ResponseMessageDto(true, tmpTheme, "1", jsonObj.toString());
			}
		}

		return null;
	}

	/**
	 * 导入时检核布局
	 * 
	 * @param jsonObj
	 * @return
	 * @throws JSONException
	 */
	private ResponseMessageDto checkLayout(JSONObject jsonObj) throws JSONException {
		JSONArray layoutArray = jsonObj.getJSONArray("layouts");
		if (Assert.isEmpty(layoutArray) || layoutArray.length() <= 0) {
			// return new ResponseMessageDto(false, "导入站点json文件布局不存在！");

		} else {
			boolean isExsit = false;
			String tmpLayout = "";
			for (int i = 0; i < layoutArray.length(); i++) {
				JSONObject obj = layoutArray.getJSONObject(i);
				String layoutCode = obj.getString("code");

				if (!obj.has("isCover")) {
					LayoutDto layoutDto = layoutService.getLayoutInfoByCode(layoutCode);
					if (Assert.isNotEmpty(layoutDto.getId())) {
						tmpLayout = layoutCode;
						isExsit = true;
						break;
					}
				}
			}
			if (isExsit) {
				return new ResponseMessageDto(true, tmpLayout, "2", jsonObj.toString());
			}
		}
		return null;
	}

	/**
	 * 导入时检核修饰器
	 * 
	 * @param jsonObj
	 * @return
	 * @throws JSONException
	 */
	private ResponseMessageDto checkDecorate(JSONObject jsonObj) throws JSONException {
		JSONArray decorateArray = jsonObj.getJSONArray("decorates");
		if (Assert.isEmpty(decorateArray) || decorateArray.length() <= 0) {
			// return new ResponseMessageDto(false, "导入站点json文件修饰器不存在！");

		} else {
			boolean isExsit = false;
			String tmpDecorate = "";
			for (int i = 0; i < decorateArray.length(); i++) {
				JSONObject obj = decorateArray.getJSONObject(i);
				String decorateCode = obj.getString("code");

				if (!obj.has("isCover")) {
					DecorateDto decorateDto = decorateService.getDecorateInfoByCode(decorateCode);
					if (Assert.isNotEmpty(decorateDto.getId())) {
						tmpDecorate = decorateCode;
						isExsit = true;
						break;
					}
				}
			}
			if (isExsit) {
				return new ResponseMessageDto(true, tmpDecorate, "3", jsonObj.toString());
			}
		}
		return null;
	}

	/**
	 * 导入时检核widget
	 * 
	 * @param jsonObj
	 * @return
	 * @throws JSONException
	 */
	private ResponseMessageDto checkWidget(JSONObject jsonObj) throws JSONException {
		JSONArray widgetArray = jsonObj.getJSONArray("widgets");
		if (Assert.isEmpty(widgetArray) || widgetArray.length() <= 0) {
			// return new ResponseMessageDto(false, "导入站点json文件widget不存在！");

		} else {
			boolean isExsit = false;
			String tmpWidget = "";
			for (int i = 0; i < widgetArray.length(); i++) {
				JSONObject obj = widgetArray.getJSONObject(i);
				String widgetCode = obj.getString("code");

				if (!obj.has("isCover")) {
					WidgetDto widgetDto = widgetService.getWidgetInfoByCode(widgetCode);
					if (Assert.isNotEmpty(widgetDto.getId())) {
						tmpWidget = widgetCode;
						isExsit = true;
						break;
					}
				}
			}
			if (isExsit) {
				return new ResponseMessageDto(true, tmpWidget, "4", jsonObj.toString());
			}
		}
		return null;
	}

}
