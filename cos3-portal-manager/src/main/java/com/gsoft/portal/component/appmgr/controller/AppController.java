package com.gsoft.portal.component.appmgr.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gsoft.cos3.dto.FileNode;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.feign.file.FileManagerFeign;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.FileUtils;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.cos3.util.ZipUtils;
import com.gsoft.portal.component.appmgr.dto.AppDto;
import com.gsoft.portal.component.appmgr.service.AppService;
import com.gsoft.portal.component.appreltemp.dto.AppRelPageTempDto;
import com.gsoft.portal.component.appreltemp.service.AppRelPageTempService;
import com.gsoft.portal.component.pagetemp.dto.PageTemplateConfDto;
import com.gsoft.portal.component.pagetemp.dto.PageTemplateDto;
import com.gsoft.portal.component.pagetemp.service.PageTemplateService;
import com.gsoft.portal.webview.widgetconf.dto.ProfileConfDto;
import com.gsoft.portal.webview.widgetconf.service.ProfileConfService;
import com.gsoft.portal.webview.widgetconf.service.WidgetConfService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 应用管理
 * 
 * @author SN
 *
 */
@Api(tags = "应用管理", description = "应用管理相关接口服务")
@RestController
@RequestMapping("/app")
public class AppController {

	/**
	 * 本地临时目录
	 */
	public static String tmpRootPath = System.getProperty("java.io.tmpdir");

	@Resource
	AppService appService;

	@Resource
	AppRelPageTempService appRelPageTempService;

	@Resource
	FileManagerFeign fileManagerFeign;

	@Resource
	PageTemplateService pageTemplateService;

	@Resource
	WidgetConfService widgetConfService;
	
	@Resource
	ProfileConfService profileConfService;

	@ApiOperation("分页查找应用信息")
	@RequestMapping(value = "/getAppList", method = RequestMethod.GET)
	public ReturnDto getAppList(@RequestParam String search,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size,
			@RequestParam(value = "sortProp", defaultValue = "id") String sortProp,
			@RequestParam(value = "order", defaultValue = "desc") String order) {
		return new ReturnDto(appService.getAppList(search, page, size, sortProp, order));
	}

	@ApiOperation("启用/停用")
	@RequestMapping(value = "/updateAppStatus", method = RequestMethod.GET)
	public ReturnDto updateAppStatus(@RequestParam String ids, @RequestParam String status) {
		appService.updateAppStatus(ids, status);
		return new ReturnDto("修改成功！");
	}

	@ApiOperation("保存应用数据")
	@RequestMapping(value = "/saveAppInfo", method = RequestMethod.POST)
	public ReturnDto saveAppInfo(HttpServletRequest request, @RequestBody AppDto appDto) {
		appDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));
		if (Assert.isEmpty(appDto.getId())) {
			appDto.setCreateTime(new Date());
		} else {
			appDto.setUpdateTime(new Date());
		}
		return new ReturnDto(appService.saveAppInfo(appDto));
	}

	@ApiOperation("删除应用数据")
	@RequestMapping(value = "/delApp", method = RequestMethod.GET)
	public ReturnDto delApp(HttpServletRequest request, @RequestParam Long id) throws JSONException {
		return appService.delApp(id);
	}

	@ApiOperation("根据主键ID获取单笔应用数据")
	@RequestMapping(value = "/getAppInfoById", method = RequestMethod.GET)
	public ReturnDto getAppInfoById(@RequestParam Long id) {
		return new ReturnDto(appService.getAppInfoById(id));
	}

	@ApiOperation("校验应用代码是否已存在")
	@RequestMapping(value = "/isUniquAppCode", method = RequestMethod.GET)
	public ReturnDto isUniquAppCode(@RequestParam(required = false) Long id, @RequestParam String code) {
		return appService.isUniquAppCode(id, code);
	}

	@ApiOperation("复制应用数据")
	@RequestMapping(value = "/copyAppInfo", method = RequestMethod.POST)
	public ReturnDto copyAppInfo(HttpServletRequest request, @RequestBody AppDto appDto) {
		appDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));
		appDto.setCreateTime(new Date());
		return new ReturnDto(appService.copyAppInfo(appDto));
	}

	@ApiOperation("导出应用数据")
	@RequestMapping(value = "/exportApp", method = RequestMethod.GET)
	public ReturnDto exportApp(@RequestParam Long id) throws Exception {

		String referenceId = null;
		JSONObject jo = new JSONObject();

		// 1. 查询应用信息
		AppDto appDto = appService.getAppInfoById(id);
		JSONObject appJo = new JSONObject();
		appJo.put("code", appDto.getCode());
		appJo.put("name", appDto.getName());
		appJo.put("desc", appDto.getDesc());

		jo.put("appData", appJo);

		// 2. 查询应用关联的页面模版信息
		Set<String> pageTempCodeSet = new HashSet<String>(); // 组装所有的页面模版code
		List<AppRelPageTempDto> appRelPageTempList = appRelPageTempService.getRelPageTempList(appDto.getCode());
		JSONArray relPageTempArr = new JSONArray();
		JSONObject relPageTempJo = null;

		if (!Assert.isEmpty(appRelPageTempList) && appRelPageTempList.size() > 0) {
			PageTemplateDto pageTemplateDto = null;
			for (AppRelPageTempDto relDto : appRelPageTempList) {
				relPageTempJo = new JSONObject();
				relPageTempJo.put("appCode", relDto.getAppCode());
				relPageTempJo.put("pageTempCode", relDto.getPageTempCode());
				pageTemplateDto = pageTemplateService.getPageTempInfo(relDto.getPageTempCode());
				relPageTempJo.put("pageTempName", pageTemplateDto.getName());
				relPageTempJo.put("layoutCode", pageTemplateDto.getLayoutCode());
				pageTempCodeSet.add(relDto.getPageTempCode());
				relPageTempArr.put(relPageTempJo);
			}
		}
		jo.put("relPageTemp", relPageTempArr);

		// 3. 页面模版配置信息
		JSONObject pageTempConfJo = new JSONObject();
		JSONObject widgetParamsJo = new JSONObject();
		if (pageTempCodeSet.size() > 0) {
			PageTemplateConfDto pageTemplateConfDto = null;
			for (String pageTempCode : pageTempCodeSet) {
				pageTemplateConfDto = pageTemplateService.getPageTempConfInfo(pageTempCode);
				if (!Assert.isEmpty(pageTemplateConfDto) && !Assert.isEmpty(pageTemplateConfDto.getJson())) {
					pageTempConfJo.put(pageTempCode, new JSONArray(pageTemplateConfDto.getJson()));
					widgetConfService.getBusinessWidgetInstanceParams(new JSONArray(pageTemplateConfDto.getJson()),
							widgetParamsJo);
				}
			}
		}

		jo.put("pageTempConf", pageTempConfJo);

		// 4. widget参数配置信息
		jo.put("widgetParams", widgetParamsJo);

		if (!tmpRootPath.endsWith("/")) {
			tmpRootPath = tmpRootPath + "/";
		}

		File jsonDir = new File(tmpRootPath + appDto.getCode() + File.separator);
		if (!jsonDir.exists()) {
			jsonDir.mkdirs();
		}

		File jsonFile = new File(tmpRootPath + appDto.getCode() + File.separator + "mainfest.json");

		FileOutputStream out = null;
		out = new FileOutputStream(jsonFile, false);

		out.write(jo.toString().getBytes("utf-8"));

		if (null != out) {
			out.close();
		}

		String zipFilePath = tmpRootPath + appDto.getCode() + ".zip";

		// 压缩文件
		ZipUtils.zip(tmpRootPath + appDto.getCode(), zipFilePath);

		// 删除临时文件夹
		FileUtils.deleteQuietly(new File(tmpRootPath + appDto.getCode()));

		// 上传到文件存储服务
		File zipFile = new File(zipFilePath);
		FileInputStream fis = new FileInputStream(zipFile);
		MultipartFile multipartFile = new MockMultipartFile("file", zipFile.getName(), "text/plain", fis);
		FileNode fileNode = fileManagerFeign.webUploader(multipartFile);
		referenceId = fileNode.getReferenceId();

		// 删除zip临时文件
		FileUtils.deleteQuietly(new File(zipFilePath));

		return new ReturnDto(referenceId);
	}

	@ApiOperation("app导入")
	@RequestMapping(value = "/importApp")
	public ReturnDto importApp(@RequestParam String referenceId, @RequestParam(required = false) String fileName,
			@RequestParam(required = false) String isCover) throws Exception {

		if (!tmpRootPath.endsWith("/")) {
			tmpRootPath = tmpRootPath + "/";
		}

		// 调用文件存储服务进行文件上传
		ResponseEntity<byte[]> entity = fileManagerFeign.download(referenceId);
		InputStream ins = new ByteArrayInputStream(entity.getBody());

		File tmpFile = new File(tmpRootPath + fileName);
		FileUtils.copyInputStreamToFile(ins, tmpFile);
		String dirName = fileName.substring(0, fileName.indexOf("."));

		// 2.解压前先判断原本地文件是否存在，存在则先删除,避免脏数据
		File dirFile = new File(tmpRootPath + dirName);
		if (dirFile.exists()) {
			FileUtils.deleteQuietly(dirFile);
		}

		// 3.解压压缩文件
		ZipUtils.unZip(tmpFile.getAbsolutePath(), tmpRootPath + dirName);

		// 4.校验应用包json文件是否存在
		File compJsonFile = new File(tmpRootPath + dirName + "/mainfest.json");
		JSONObject jsonObj = null;

		if (compJsonFile.exists()) {
			String appJsonContent = FileUtils.readFileToString(compJsonFile, "UTF-8");
			jsonObj = new JSONObject(appJsonContent);

		} else {

			return new ReturnDto(500, "应用包：" + fileName + " json文件不存在！");
		}

		if (Assert.isEmpty(isCover)) { // 第一次导入
			// 5. 校验应用是否已存在
			JSONObject appJo = jsonObj.getJSONObject("appData");
			String appCode = appJo.getString("code");
			AppDto dto = appService.getAppInfoByCode(appCode);

			if (!Assert.isEmpty(dto) && !Assert.isEmpty(dto.getId())) {
				return new ReturnDto(200, "1");

			} else {
				// 导入数据处理
				this.handleImportData(jsonObj);
			}

		} else {

			// 导入数据处理
			this.handleImportData(jsonObj);
		}

		return new ReturnDto("导入成功！");
	}

	/**
	 * 应用导入数据处理
	 * 
	 * @param jsonObj
	 * @throws Exception
	 */
	private void handleImportData(JSONObject jsonObj) throws Exception {

		// 应用数据
		JSONObject appJo = jsonObj.getJSONObject("appData");

		// 关联的页面模版数据
		JSONArray pageTempArr = jsonObj.getJSONArray("relPageTemp");

		// 页面模版配置数据
		JSONObject pageTempConfJo = jsonObj.getJSONObject("pageTempConf");

		// widget配置数据
		JSONObject widgetParamsJo = jsonObj.getJSONObject("widgetParams");

		// 新增页面模版数据
		PageTemplateDto pageTemplateDto = null;
		List<AppRelPageTempDto> appRelPageTempList = new ArrayList<AppRelPageTempDto>();
		AppRelPageTempDto appRelPageTempDto = null;

		JSONObject nPageTempConfJo = new JSONObject();
		JSONObject nWidgetParamsJo = new JSONObject();
		JSONArray nJsonArr = null;

		for (int i = 0; i < pageTempArr.length(); i++) {
			JSONObject pageTempJo = pageTempArr.getJSONObject(i);

			// 页面模版是否存在，存在则先删除
			pageTemplateDto = pageTemplateService.getPageTempInfo(pageTempJo.getString("pageTempCode"));

			if (!Assert.isEmpty(pageTemplateDto) && !Assert.isEmpty(pageTemplateDto.getId())) {
				pageTemplateService.delPageTemplate(pageTemplateDto.getId());
			}

			pageTemplateDto = new PageTemplateDto();

			pageTemplateDto.setCode(pageTempJo.getString("pageTempCode"));
			pageTemplateDto.setName(pageTempJo.getString("pageTempName"));
			pageTemplateDto.setLayoutCode(pageTempJo.getString("layoutCode"));
			pageTemplateDto.setCreateBy(1L);

			pageTemplateService.savePageTempInfo(pageTemplateDto);

			// 保存应用模版关联关系
			appRelPageTempDto = new AppRelPageTempDto();
			appRelPageTempDto.setAppCode(pageTempJo.getString("appCode"));
			appRelPageTempDto.setPageTempCode(pageTempJo.getString("pageTempCode"));
			appRelPageTempDto.setCreateBy(1L);
			appRelPageTempList.add(appRelPageTempDto);

			nJsonArr = new JSONArray();

			// 保存页面模版配置信息
			this.getPageTempConf(pageTempConfJo.getJSONArray(pageTempJo.getString("pageTempCode")), widgetParamsJo,
					nJsonArr, nWidgetParamsJo, true);

			nPageTempConfJo.put(pageTempJo.getString("pageTempCode"), nJsonArr);
		}

		if (appRelPageTempList.size() > 0) {
			appRelPageTempService.saveAppRelPageTemp(appRelPageTempList);
		}

		// 模版配置批量保存 
		if (nPageTempConfJo.length() > 0) {
			// 汇总页面模版配置信息
			List<PageTemplateConfDto> nPageTemplateConfList = new ArrayList<PageTemplateConfDto>();
			PageTemplateConfDto pageTemplateConfDto = null;

			@SuppressWarnings("rawtypes")
			Iterator iterator = nPageTempConfJo.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				JSONArray valObj = nPageTempConfJo.getJSONArray(key);
				pageTemplateConfDto = new PageTemplateConfDto();
				pageTemplateConfDto.setCode(key);
				pageTemplateConfDto.setCreateBy(1l);
				pageTemplateConfDto.setJson(valObj.toString());
				nPageTemplateConfList.add(pageTemplateConfDto);
			}

			if (nPageTemplateConfList.size() > 0) {
				pageTemplateService.batchSave(nPageTemplateConfList);
			}
		}

		if (nWidgetParamsJo.length() > 0) {
			// 需要保存的对应参数配置信息
			List<ProfileConfDto> widgetConfList = new ArrayList<ProfileConfDto>();
			ProfileConfDto profileConfDto = null;

			@SuppressWarnings("rawtypes")
			Iterator iterator = nWidgetParamsJo.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				JSONObject valObj = nWidgetParamsJo.getJSONObject(key);
				profileConfDto = new ProfileConfDto();
				profileConfDto.setWidgetUuId(MathUtils.stringObj(key));
				profileConfDto.setJson(valObj.has("nestedPage") ? MathUtils.stringObj(valObj.get("nestedPage")) : MathUtils.stringObj(valObj));
				profileConfDto.setCreateBy(1l);
				widgetConfList.add(profileConfDto);
			}
			
			if (widgetConfList.size() > 0) {
				profileConfService.batchSave(widgetConfList);
			}
		}

		// 新增app数据
		AppDto appDto = appService.getAppInfoByCode(appJo.getString("code"));
		if (!Assert.isEmpty(appDto) && !Assert.isEmpty(appDto.getId())) {
			appService.delApp(appDto.getId());
		}

		appDto = new AppDto();
		appDto.setCode(appJo.getString("code"));
		appDto.setName(appJo.getString("name"));
		appDto.setDesc(appJo.getString("desc"));
		appDto.setStatus("1");
		appDto.setCreateBy(1l);
		appService.saveAppInfo(appDto);
	}

	/**
	 * 重新组装页面模版配置信息
	 * 
	 * @param jsonArray
	 * @param widgetParamsJo
	 * @param nPageTemplateConfList
	 * @param profileConfList
	 * @throws JSONException
	 */
	private void getPageTempConf(JSONArray jsonArr, JSONObject oWidgetParamsJo, JSONArray nJsonArr,
			JSONObject nWidgetParamsJo, boolean levelOne) throws JSONException {

		if (!Assert.isEmpty(jsonArr) && jsonArr.length() > 0) {
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONArray jo = jsonArr.getJSONArray(i);
				JSONArray newJo = new JSONArray();

				if (!Assert.isEmpty(jo) && jo.length() > 0) {
					for (int j = 0; j < jo.length(); j++) {
						JSONObject jobj = jo.getJSONObject(j);

						JSONObject resJo = new JSONObject();
						JSONObject paramJo = new JSONObject();

						// 旧的widget实例
						String oldWidgetUuId = MathUtils.stringObj(jobj.get("id"));
						// 新的widget实例
						String newWidgetUuId = MathUtils.stringObj(System.nanoTime());

						jobj.put("id", newWidgetUuId);
						if (levelOne) {
							JSONObject tmpJo = new JSONObject();
							tmpJo.put("id", jobj.get("id"));
							tmpJo.put("name", jobj.get("name"));
							newJo.put(tmpJo);
						}

						if ("nested".equals(jobj.get("name"))) {// 嵌套
							paramJo = oWidgetParamsJo.getJSONObject(oldWidgetUuId).getJSONObject("nestedPage");
							@SuppressWarnings("rawtypes")
							Iterator iterator = paramJo.keys();
							while (iterator.hasNext()) {
								String key = (String) iterator.next();
								if (!"widgets".equals(key)) {
									Object valObj = paramJo.get(key);
									resJo.put(key, valObj);
								}
							}

							resJo.put("nestedPage", paramJo);
							if (paramJo.has("widgets")) {
								JSONArray nestJsonArr = paramJo.getJSONArray("widgets");
								getPageTempConf(nestJsonArr, oWidgetParamsJo, nJsonArr, nWidgetParamsJo, false);
							}
						} else if ("tab".equals(jobj.get("name"))) {// tab面板
							paramJo = oWidgetParamsJo.getJSONObject(oldWidgetUuId);
							@SuppressWarnings("rawtypes")
							Iterator iterator = paramJo.keys();
							while (iterator.hasNext()) {
								String key = (String) iterator.next();
								if (!"widgets".equals(key) && !"tabs".equals(key)) {
									Object valObj = paramJo.get(key);
									resJo.put(key, valObj);
								}
							}

							JSONArray tabArr = new JSONArray();

							// 循环tabs
							if (paramJo.has("tabs")) {
								tabArr = paramJo.getJSONArray("tabs");
								resJo.put("tabs", tabArr);

								if (tabArr.length() > 0) {
									for (int m = 0; m < tabArr.length(); m++) {
										JSONObject tabJo = tabArr.getJSONObject(m);
										if (tabJo.has("widgets")) {
											JSONArray nestJsonArr = tabJo.getJSONArray("widgets");
											getPageTempConf(nestJsonArr, oWidgetParamsJo, nJsonArr, nWidgetParamsJo,
													false);
										}
									}
								}
							} else {
								resJo.put("tabs", tabArr);
							}
						} else {// 非嵌套
							paramJo = oWidgetParamsJo.getJSONObject(oldWidgetUuId);
							@SuppressWarnings("rawtypes")
							Iterator iterator = paramJo.keys();
							while (iterator.hasNext()) {
								String key = (String) iterator.next();
								Object valObj = paramJo.get(key);
								resJo.put(key, valObj);
							}
						}
						nWidgetParamsJo.put(newWidgetUuId, resJo);
					}
				}
				if (levelOne) {
					nJsonArr.put(newJo);
				}
			}
		}
	}
}
