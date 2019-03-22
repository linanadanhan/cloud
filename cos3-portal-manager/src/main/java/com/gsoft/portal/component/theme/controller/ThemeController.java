package com.gsoft.portal.component.theme.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ResponseMessageDto;
import com.gsoft.cos3.dto.SuccessDto;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.FileUtils;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.cos3.util.ZipUtils;
import com.gsoft.portal.common.constans.ParameterConstant;
import com.gsoft.portal.component.theme.dto.ThemeDto;
import com.gsoft.portal.component.theme.service.ThemeService;
import com.gsoft.portal.system.basicdata.service.ParameterService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 主题管理
 * 
 * @author SN
 *
 */
@Api(tags = "主题管理", description = "主题相关接口服务")
@RestController
@RequestMapping("/theme")
public class ThemeController {

	@Resource
	ThemeService themeService;

	@Resource
	ParameterService parameterService;

	/**
	 * 本地临时目录
	 */
	public static String tmpRootPath = System.getProperty("java.io.tmpdir") + "sitepack" + File.separator + "themes"
			+ File.separator;

	@ApiOperation("分页查找主题基本信息")
	@RequestMapping(value = "/queryThemeDataTable", method = RequestMethod.GET)
	public PageDto queryThemeDataTable(@RequestParam String search,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size,
			@RequestParam(value = "sortProp", defaultValue = "id") String sortProp,
			@RequestParam(value = "order", defaultValue = "desc") String order) {

		return themeService.queryThemeDataTable(search, page, size, sortProp, order);

	}

	@ApiOperation("根据Id获取主题信息")
	@RequestMapping(value = "/getThemeInfoById", method = RequestMethod.GET)
	public ThemeDto getThemeInfoById(@RequestParam Long id) {
		return themeService.getThemeInfoById(id);
	}

	@ApiOperation("判断主题代码是否存在")
	@RequestMapping(value = "/isExitThemeCode", method = RequestMethod.GET)
	public Boolean isExitThemeCode(@RequestParam(required = false) Long id, @RequestParam String code,
			@RequestParam(required = false) String projectCode) {
		return themeService.isExitThemeCode(id, code, projectCode);
	}

	@ApiOperation("保存主题信息")
	@RequestMapping(value = "/saveTheme", method = RequestMethod.POST)
	public ThemeDto saveTheme(@ModelAttribute("themeDto") ThemeDto themeDto, ServletRequest servletRequest)
			throws IOException, JSONException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		themeDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));

		if (Assert.isEmpty(themeDto.getId())) {
			themeDto.setCreateTime(new Date());
		} else {
			themeDto.setUpdateTime(new Date());
		}

		ThemeDto dto = null;

		// 保存成功后移动压缩包
		if (!Assert.isEmpty(themeDto.getReferenceId()) && !Assert.isEmpty(themeDto.getIsImp())
				&& "1".equals(themeDto.getIsImp())) {
			// 读取解压目录json文件
			File jsonFile = new File(tmpRootPath + themeDto.getCode() + "/" + themeDto.getCode() + "/mainfest.json");
			// 校验主题code是否已存在
			String nContent = FileUtils.readFileToString(jsonFile, "UTF-8");
			JSONObject jsonObj = new JSONObject(nContent);
			String projectCode = jsonObj.getString("projectCode");
			String isOpen = jsonObj.getString("isOpen");
			String isSystem = jsonObj.getString("isSystem");
			String title = jsonObj.getString("title");
			themeDto.setProjectCode(projectCode);
			themeDto.setIsOpen(isOpen);
			themeDto.setIsSystem(("1".equals(isSystem) ? true : false));
			themeDto.setName(title);

			dto = themeService.saveTheme(themeDto);

			// 移动主题包
			copyFolders(themeDto.getProjectCode(), tmpRootPath + themeDto.getCode() + "/", themeDto.getCode());

		} else {
			dto = themeService.saveTheme(themeDto);
		}

		return dto;
	}

	@ApiOperation("删除主题信息")
	@RequestMapping(value = "/delTheme", method = RequestMethod.GET)
	public void delTheme(@RequestParam Long id, @RequestParam String code) {
		themeService.delTheme(id, code);
	}

	@ApiOperation("获取所有主题信息")
	@RequestMapping(value = "/getThemeList", method = RequestMethod.GET)
	public List<ThemeDto> getThemeList(@RequestParam(required = false) String isOpen,
			@RequestParam(required = false) String siteCode) {
		return themeService.getThemeList(isOpen, siteCode);
	}

	@ApiOperation("主题导出")
	@RequestMapping(value = "/exportTheme", method = RequestMethod.POST)
	public ResponseMessageDto exportTheme(HttpServletRequest httpRequest, @RequestParam String code) throws Exception {

		boolean resFlag = true;
		String errMsg = "";

		try {
			String targetPath = parameterService.getParmValueByKey(ParameterConstant.ZIP_PACK_PATH[0],
					ParameterConstant.ZIP_PACK_PATH[1]);
			if (!targetPath.endsWith("/")) {
				targetPath = targetPath + "/";
			}

			targetPath = targetPath + "themes/";

			ThemeDto themeDto = themeService.getThemeInfoByCode(code);
			String projectCode = themeDto.getProjectCode();

			// 复制主题包到本地临时目录下
			movePackToTemp(tmpRootPath + code, code, projectCode);

			String zipFilePath = tmpRootPath + code + ".zip";

			// 压缩文件
			ZipUtils.zip(tmpRootPath + code, zipFilePath);

			// 删除临时文件夹
			FileUtils.deleteQuietly(new File(tmpRootPath + code));

			// 上传到前端服务对应目录
			FileUtils.copyFolder(new File(zipFilePath), new File(targetPath + code + ".zip"));

			// 删除zip临时文件
			FileUtils.deleteQuietly(new File(zipFilePath));

		} catch (Exception e) {
			errMsg = e.getMessage();
			resFlag = false;
		}

		if (resFlag) {
			return new SuccessDto("导出成功");
		} else {
			return new ResponseMessageDto(false, "导出失败,原因:" + errMsg);
		}

	}

	/**
	 * 移动服务器上主题文件到本地临时目录
	 * 
	 * @param tmpRootPath
	 * @param code
	 * @param projectCode
	 * @throws IOException
	 */
	private void movePackToTemp(String tarPath, String code, String projectCode) throws IOException {

		String destDir = parameterService.getParmValueByKey(ParameterConstant.PORTAL_MODULES_PATH[0],
				ParameterConstant.PORTAL_MODULES_PATH[1]);

		if (!destDir.endsWith("/")) {
			destDir = destDir + "/";
		}

		File toFile = new File(tarPath + File.separator + code);
		destDir = destDir + projectCode + "/themes/" + code + "/";

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
	 * 主题导入
	 * 
	 * @param file
	 * @param request
	 * @param type
	 * @param json
	 * @return
	 * @throws Exception
	 */
	@ApiOperation("主题导入")
	@RequestMapping(value = "/importTheme")
	public ResponseMessageDto importTheme(@RequestParam(required = false) MultipartFile file,
			HttpServletRequest request, @RequestParam(required = false) String type,
			@RequestParam(required = false) String code, @RequestParam(required = false) String json) throws Exception {

		boolean resFlag = true;
		String errMsg = "";

		try {
			if (Assert.isEmpty(type)) {// 列表界面新增时导入
				// 1.根据流获取上传文件
				File tmpFile = new File(tmpRootPath + file.getOriginalFilename());
				FileUtils.copyInputStreamToFile(file.getInputStream(), tmpFile);
				String dirName = file.getOriginalFilename().substring(0, file.getOriginalFilename().indexOf("."));

				// 2.解压压缩文件
				ZipUtils.unZip(tmpFile.getAbsolutePath(), tmpRootPath + dirName);

				// 删除临时压缩文件
				FileUtils.deleteQuietly(tmpFile);

				// 读取解压目录json文件
				File jsonFile = new File(tmpRootPath + dirName + "/" + dirName + "/mainfest.json");

				JSONObject jsonObj = null;

				if (!jsonFile.exists()) {
					errMsg = "导入主题json文件不存在！|";
				} else {
					// 校验主题code是否已存在
					String nContent = FileUtils.readFileToString(jsonFile, "UTF-8");

					try {
						jsonObj = new JSONObject(nContent);

						if (!jsonObj.has("name") || Assert.isEmpty(jsonObj.get("name"))) {
							errMsg += "导入主题json文件中主题名称不存在或为空！|";
						}

						if (!jsonObj.has("projectCode") || Assert.isEmpty(jsonObj.get("projectCode"))) {
							errMsg += "导入主题json文件中项目代码不存在或为空！|";
						}

					} catch (Exception e) {
						errMsg += "导入主题json文件格式有误!|";
					}
				}

				if (errMsg.length() > 0) {
					errMsg = errMsg.substring(0, errMsg.length() - 1);
					return new ResponseMessageDto(false, errMsg);
				}

				String themeCode = jsonObj.getString("name");
				String projectCode = jsonObj.getString("projectCode");
				String isOpen = jsonObj.getString("isOpen");
				String isSystem = jsonObj.getString("isSystem");
				String title = jsonObj.getString("title");

				if (!Assert.isEmpty(themeCode)) {
					// 校验主题是否已存在
					ResponseMessageDto responseMessageDto = checkTheme(jsonObj);
					if (!Assert.isEmpty(responseMessageDto)) {
						return responseMessageDto;
					}

					// 数据入库
					ThemeDto themeDto = new ThemeDto();
					themeDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));
					themeDto.setCreateTime(new Date());
					themeDto.setCode(themeCode);
					themeDto.setProjectCode(projectCode);
					themeDto.setName(title);
					themeDto.setIsOpen(isOpen);
					themeDto.setReferenceId("");
					themeDto.setIsSystem(("1".equals(isSystem) ? true : false));
					themeDto.setIsImp("1");
					themeService.saveTheme(themeDto);

					// 移动主题包
					copyFolders(projectCode, tmpRootPath + dirName + "/", dirName);
				}

			} else if ("0".equals(type)) {// 主题新增或修改时导入
				// 1.根据流获取上传文件
				File tmpFile = new File(tmpRootPath + file.getOriginalFilename());
				FileUtils.copyInputStreamToFile(file.getInputStream(), tmpFile);
				String dirName = file.getOriginalFilename().substring(0, file.getOriginalFilename().indexOf("."));

				// 2.解压压缩文件
				ZipUtils.unZip(tmpFile.getAbsolutePath(), tmpRootPath + dirName);

				// 删除临时压缩文件
				FileUtils.deleteQuietly(tmpFile);

				// 读取解压目录json文件
				File jsonFile = new File(tmpRootPath + dirName + "/" + dirName + "/mainfest.json");
				JSONObject jsonObj = null;

				if (!jsonFile.exists()) {
					errMsg = "导入主题json文件不存在！|";
				} else {
					// 校验主题code是否已存在
					String nContent = FileUtils.readFileToString(jsonFile, "UTF-8");

					try {
						jsonObj = new JSONObject(nContent);

						if (jsonObj.has("name") && !Assert.isEmpty(jsonObj.get("name"))) {
							String themeCode = jsonObj.getString("name");
							// 校验当前主题code和json文件中的是否一致
							if (!code.equals(themeCode)) {
								errMsg += "导入主题json文件中主题名称与当前主题不一致！|";
							}
						} else {
							errMsg += "导入主题json文件中主题名称不存在或为空！|";
						}

					} catch (Exception e) {
						errMsg += "导入主题json文件格式有误!|";
					}
				}

				if (errMsg.length() > 0) {
					errMsg = errMsg.substring(0, errMsg.length() - 1);
					return new ResponseMessageDto(false, errMsg);
				}

				return new ResponseMessageDto(true, "导入成功", "", jsonObj.toString());

			} else if ("1".equals(type)) {// 新增时主题已存在确认覆盖

				JSONObject jsonObj = new JSONObject(json);
				String themeCode = jsonObj.getString("name");
				String projectCode = jsonObj.getString("projectCode");
				String isOpen = jsonObj.getString("isOpen");
				String isSystem = jsonObj.getString("isSystem");
				String title = jsonObj.getString("title");

				// 数据入库
				ThemeDto themeDto = themeService.getThemeInfoByCode(themeCode);
				if (Assert.isEmpty(themeDto.getId())) {
					themeDto = new ThemeDto();
				}

				themeDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));
				themeDto.setCreateTime(new Date());
				themeDto.setCode(themeCode);
				themeDto.setProjectCode(projectCode);
				themeDto.setName(title);
				themeDto.setIsOpen(isOpen);
				themeDto.setReferenceId("");
				themeDto.setIsSystem(("1".equals(isSystem) ? true : false));
				themeDto.setIsImp("1");
				themeService.saveTheme(themeDto);

				// 移动主题包
				copyFolders(projectCode, tmpRootPath + themeCode + "/", themeCode);
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
	 * 校验主题CODE是否已存在
	 * 
	 * @param isCover
	 * @return
	 * @throws JSONException
	 */
	private ResponseMessageDto checkTheme(JSONObject jsonObj) throws JSONException {

		ThemeDto themeDto = themeService.getThemeInfoByCode(MathUtils.stringObj(jsonObj.getString("name")));
		if (Assert.isNotEmpty(themeDto.getId())) {
			return new ResponseMessageDto(true, themeDto.getCode(), "1", jsonObj.toString());
		}
		return null;
	}

	/**
	 * 移动文件夹
	 * 
	 * @param projectCode
	 * @param tmpDir
	 * @param dirName
	 * @throws IOException
	 */
	private void copyFolders(String projectCode, String tmpDir, String dirName) throws IOException {
		// 移动文件夹到对应目录下
		String destDir = parameterService.getParmValueByKey(ParameterConstant.PORTAL_MODULES_PATH[0],
				ParameterConstant.PORTAL_MODULES_PATH[1]);

		if (!destDir.endsWith("/")) {
			destDir = destDir + "/";
		}

		destDir = destDir + projectCode + "/themes/" + dirName + "/";

		File localFile = new File(tmpDir + dirName);
		File toFile = new File(destDir);

		if (!toFile.exists()) {
			toFile.mkdirs();
		}

		// 复制本地临时目录下的文件到服务器对应目录
		FileUtils.copyFolder(localFile, toFile);
		FileUtils.deleteQuietly(localFile);
	}

}
