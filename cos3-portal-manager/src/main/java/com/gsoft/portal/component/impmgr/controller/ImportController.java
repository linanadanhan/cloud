package com.gsoft.portal.component.impmgr.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gsoft.cos3.datasource.DynamicDataSourceContextHolder;
import com.gsoft.cos3.dto.FileNode;
import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.feign.file.FileManagerFeign;
import com.gsoft.cos3.util.AESUtil;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.FileUtils;
import com.gsoft.cos3.util.HttpClientUtils;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.cos3.util.ZipUtils;
import com.gsoft.portal.common.constans.ParameterConstant;
import com.gsoft.portal.component.compmgr.dto.ComponentDto;
import com.gsoft.portal.component.compmgr.dto.ComponentPackageDto;
import com.gsoft.portal.component.compmgr.service.ComponentService;
import com.gsoft.portal.component.decorate.service.DecorateService;
import com.gsoft.portal.component.impmgr.dto.ImportDto;
import com.gsoft.portal.component.impmgr.service.ImportService;
import com.gsoft.portal.component.layout.service.LayoutService;
import com.gsoft.portal.component.theme.service.ThemeService;
import com.gsoft.portal.system.basicdata.service.ParameterService;
import com.gsoft.portal.webview.widget.service.WidgetService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 导入管理
 * 
 * @author SN
 *
 */
@Api(tags = "导入管理", description = "组件导入接口服务")
@RestController
@RequestMapping("/import")
public class ImportController {
	
	/**
	 * 日志对象
	 */
	Log logger = LogFactory.getLog(getClass());

	@Resource
	ImportService importService;

	@Resource
	ParameterService parameterService;

	@Autowired
	ThemeService themeService;

	@Autowired
	LayoutService layoutService;

	@Autowired
	DecorateService decorateService;

	@Autowired
	WidgetService widgetService;

	@Autowired
	ComponentService componentService;
	
	@Resource
	FileManagerFeign fileManagerFeign;
	
	@Value("${remote.component.package.download.interface}")
	private String downloadUrl;
	
    @Value("${AES.password}")
    private String aesPassword;
    
    @Value("${security.upload.password}")
    private String uploadPassword;

	/**
	 * 本地临时目录
	 */
	public static String tmpRootPath = System.getProperty("java.io.tmpdir");
	
	/**
	 * 签名密钥库文件位置
	 */
	private static String keystorePath = "src/main/resources/gsoft.keystore";

	@ApiOperation("分页查找主题基本信息")
	@RequestMapping(value = "/queryImportData", method = RequestMethod.GET)
	public PageDto queryImportData(@RequestParam String search, @RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size,
			@RequestParam(value = "sortProp", defaultValue = "id") String sortProp,
			@RequestParam(value = "order", defaultValue = "desc") String order) {

		return importService.queryImportData(search, page, size, sortProp, order);
	}

	/**
	 * 导入压缩包
	 * 
	 * @param file
	 * @param request
	 * @param type
	 * @param json
	 * @return
	 * @throws Exception
	 */
	@ApiOperation("导入")
	@RequestMapping(value = "/importData")
	public ReturnDto importData(
			@RequestParam(required = false) String referenceId,
			@RequestParam(required = false) String fileName,
			HttpServletRequest request,
			@RequestParam(required = false) String type, 
			@RequestParam(required = false) String json) throws Exception {

		boolean resFlag = true;
		String errMsg = "";
		JSONObject jo = new JSONObject();

		try {
			if (!tmpRootPath.endsWith("/")) {
				tmpRootPath = tmpRootPath + "/";
			}
			
			if (Assert.isEmpty(type)) {
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
				
				// 校验压缩文件是否已签名
				boolean isSign = chekFileSign(tmpFile.getAbsolutePath());
				
				if (isSign) {
					jo.put("isSign", true);
				} else {
					jo.put("isSign", false);
				}

				// 3.解压压缩文件
				ZipUtils.unZip(tmpFile.getAbsolutePath(), tmpRootPath + dirName);

				// 4.遍历文件目录,校验并组装合并json文件
				jo.put("filePath", tmpRootPath + dirName);// 解压上传zip文件的目录
				jo.put("fileName", fileName);// 解压上传zip文件的名称
				jo.put("personnelId", request.getHeader("personnelId"));
				jo.put("fileAlias", referenceId);// 文件别名
				jo.put("projectCode", dirName);
				jo.put("referenceId", referenceId);

				// 保存压缩包文件校验错误信息
				StringBuffer errSb = new StringBuffer();

				// 校验部件包json文件是否存在
				File compJsonFile = new File(tmpRootPath + dirName + "/mainfest.json");
				if (compJsonFile.exists()) {
					String compJsonContent = FileUtils.readFileToString(compJsonFile, "UTF-8");

					try {
						JSONObject jsonObj = new JSONObject(compJsonContent);

						if (!jsonObj.has("name") || Assert.isEmpty(jsonObj.get("name"))) {
							errSb.append("部件包：" + fileName + " json文件中部件代码不存在或为空！").append("|");
						} else {
							dirName = MathUtils.stringObj(jsonObj.get("name"));
							jo.put("compCode", dirName);
							jo.put("projectCode", dirName);
						}

						if (!jsonObj.has("text") || Assert.isEmpty(jsonObj.get("text"))) {
							errSb.append("部件包：" + fileName + " json文件中部件名称不存在或为空！").append("|");
						} else {
							jo.put("compName", jsonObj.get("text"));
						}

						if (jsonObj.has("desc")) {
							jo.put("compDesc", jsonObj.get("desc"));
						}
						
						if (jsonObj.has("version")) {
							jo.put("version", jsonObj.get("version"));
						}

					} catch (Exception e) {
						logger.error("导入失败！", e);
						errSb.append("部件包：" + fileName + " json文件格式有误！").append("|");
					}

				} else {
					
					// 兼容V2.0 检核部件json文件是否已存在
					compJsonFile = new File(tmpRootPath + dirName + "/manifest.json");
					if (compJsonFile.exists()) {
						String compJsonContent = FileUtils.readFileToString(compJsonFile, "UTF-8");
	
						try {
							JSONObject jsonObj = new JSONObject(compJsonContent);
	
							if (!jsonObj.has("name") || Assert.isEmpty(jsonObj.get("name"))) {
								errSb.append("部件包：" + fileName + " json文件中部件代码不存在或为空！").append("|");
							} else {
								dirName = MathUtils.stringObj(jsonObj.get("name"));
								jo.put("compCode", dirName);
								jo.put("projectCode", dirName);
							}
	
							if (!jsonObj.has("text") || Assert.isEmpty(jsonObj.get("text"))) {
								errSb.append("部件包：" + fileName + " json文件中部件名称不存在或为空！").append("|");
							} else {
								jo.put("compName", jsonObj.get("text"));
							}
	
							if (jsonObj.has("desc")) {
								jo.put("compDesc", jsonObj.get("desc"));
							}
							
							if (jsonObj.has("version")) {
								jo.put("version", jsonObj.get("version"));
							}
							
							if (jsonObj.has("author")) {
								jo.put("author", jsonObj.get("author"));
							}
							
							if (jsonObj.has("provider")) {
								jo.put("provider", jsonObj.get("provider"));
							}
							
							if (jsonObj.has("main")) {
								jo.put("main", jsonObj.get("main"));
							}
							
							// 拼装主题、布局、修饰器、widget及公共组件
							JSONArray jo_theme = new JSONArray();
							JSONArray jo_layout = new JSONArray();
							JSONArray jo_decorator = new JSONArray();
							JSONArray jo_widget = new JSONArray();
							JSONArray jo_components = new JSONArray();
							
							if (jsonObj.has("themes")) {
								jo_theme = jsonObj.getJSONArray("themes");
							}
							jo.put("themes", jo_theme);
							
							if (jsonObj.has("layouts")) {
								jo_layout = jsonObj.getJSONArray("layouts");
							}
							jo.put("layouts", jo_layout);
							
							if (jsonObj.has("decorators")) {
								jo_decorator = jsonObj.getJSONArray("decorators");
							}
							jo.put("decorators", jo_decorator);
							
							if (jsonObj.has("widgets")) {
								jo_widget = jsonObj.getJSONArray("widgets");
							}
							jo.put("widgets", jo_widget);
							
							if (jsonObj.has("components")) {
								jo_components = jsonObj.getJSONArray("components");
							}
							jo.put("components", jo_components);

						} catch (Exception e) {
							logger.error("导入失败！", e);
							errSb.append("部件包：" + fileName + " json文件格式有误！").append("|");
						}
						
					} else {
						errSb.append("部件包：" + fileName + " json文件不存在！").append("|");
					}
				}

				ReturnDto returnDto = null;
				
				// V1.0 导入处理检核部件包信息
				if (!jo.has("main")) {
					// 检核组件信息
					checkComponent(jo, dirName, errSb);
				}

				String err = errSb.toString();
				if (errSb.length() > 0) {
					err = err.substring(0, err.length() - 1);
					// 记录上传文件操作
					handleImportRecord(jo, "0", err);
					return new ReturnDto(500, err);
				}

				// 校验部件包是否已存在
				returnDto = checkComponentPacks(jo);
				if (!Assert.isEmpty(returnDto)) {
					return returnDto;
				}

				resFlag = handleImportData(jo);
				
			} else if ("0".equals(type)) {
				JSONObject jsonObj = new JSONObject(json);
				resFlag = handleAllImportData(jsonObj);
			}
		} catch (Exception e) {
			logger.error("导入失败！", e);
			errMsg = e.getMessage();
			resFlag = false;
		}

		if (!Assert.isEmpty(type)) {
			jo = new JSONObject(json);
		}

		if (resFlag) {
			// 记录导入记录操作
			handleImportRecord(jo, "1", "");
			// 记录部件安装信息
			handleComponentRecord(jo);
			// 记录部件包信息
			handleComponentPackage(jo);
			
			return new ReturnDto("{}");
		} else {
			// 记录上传文件操作
			handleImportRecord(jo, "0", errMsg);
			return new ReturnDto("导入失败,原因:" + errMsg);
		}
	}

	/**
	 * 检核部件信息
	 * @param jo
	 * @param dirName
	 * @param errSb
	 * @throws IOException
	 * @throws JSONException
	 */
	private void checkComponent(JSONObject jo, String dirName, StringBuffer errSb) throws IOException, JSONException {
		// 主题目录是否存在
		File themesFile = new File(tmpRootPath + dirName + "/themes/");
		JSONArray themeArr = new JSONArray();

		if (themesFile.exists()) {
			File[] files = themesFile.listFiles();
			if (files.length > 0) {
				for (File file1 : files) {
					if (file1.isDirectory()) {
						// 读取解压目录json文件
						File jsonFile = new File(file1.getAbsolutePath() + "/mainfest.json");
						if (!jsonFile.exists()) {
							errSb.append("主题包：" + file1.getName() + " json文件不存在！").append("|");
							break;
						}

						String nContent = FileUtils.readFileToString(jsonFile, "UTF-8");

						try {
							JSONObject jsonObj = new JSONObject(nContent);

							if (!jsonObj.has("name") || Assert.isEmpty(jsonObj.get("name"))) {
								errSb.append("主题包：" + file1.getName() + " json文件中主题名称不存在或为空！").append("|");
							} else {
								jsonObj.put("name", dirName + "/themes/" + jsonObj.get("name"));
							}
							themeArr.put(jsonObj);
						} catch (Exception e) {
							logger.error("导入失败！", e);
							errSb.append("主题包：" + file1.getName() + " json文件格式有误！").append("|");
						}

					} else {
						errSb.append(file1.getName() + " 不是主题包！").append("|");
					}
				}
			}
		}
		jo.put("themes", themeArr);

		// 布局目录是否存在
		File layoutFile = new File(tmpRootPath + dirName + "/layouts/");
		JSONArray layoutArr = new JSONArray();

		if (layoutFile.exists()) {
			File[] files = layoutFile.listFiles();
			if (files.length > 0) {
				for (File file1 : files) {
					if (file1.isDirectory()) {
						// 读取解压目录json文件
						File jsonFile = new File(file1.getAbsolutePath() + "/mainfest.json");
						if (!jsonFile.exists()) {
							errSb.append("布局包：" + file1.getName() + " json文件不存在！").append("|");
							break;
						}

						String nContent = FileUtils.readFileToString(jsonFile, "UTF-8");

						try {
							JSONObject jsonObj = new JSONObject(nContent);

							if (!jsonObj.has("name") || Assert.isEmpty(jsonObj.get("name"))) {
								errSb.append("布局包：" + file1.getName() + " json文件中布局名称不存在或为空！").append("|");
							} else {
								jsonObj.put("name", dirName + "/layouts/" + jsonObj.get("name"));
							}

							if (!jsonObj.has("params") || Assert.isEmpty(jsonObj.getJSONArray("params"))) {
								errSb.append("布局包：" + file1.getName() + " json文件中布局参数不存在或为空！").append("|");
							}

							layoutArr.put(jsonObj);

						} catch (Exception e) {
							logger.error("导入失败！", e);
							errSb.append("布局包：" + file1.getName() + " json文件格式有误！").append("|");
						}

					} else {
						errSb.append(file1.getName() + " 不是布局包！").append("|");
					}
				}
			}
		}
		jo.put("layouts", layoutArr);

		// 修饰器目录是否存在
		File decoratorFile = new File(tmpRootPath + dirName + "/decorators/");
		JSONArray decoratorArr = new JSONArray();

		if (decoratorFile.exists()) {
			File[] files = decoratorFile.listFiles();
			if (files.length > 0) {
				for (File file1 : files) {
					if (file1.isDirectory()) {
						// 读取解压目录json文件
						File jsonFile = new File(file1.getAbsolutePath() + "/mainfest.json");
						if (!jsonFile.exists()) {
							errSb.append("修饰器包：" + file1.getName() + " json文件不存在！").append("|");
							break;
						}

						String nContent = FileUtils.readFileToString(jsonFile, "UTF-8");

						try {
							JSONObject jsonObj = new JSONObject(nContent);

							if (!jsonObj.has("name") || Assert.isEmpty(jsonObj.get("name"))) {
								errSb.append("修饰器包：" + file1.getName() + " json文件中修饰器名称不存在或为空！").append("|");
							} else {
								jsonObj.put("name", dirName + "/decorators/" + jsonObj.get("name"));
							}
							decoratorArr.put(jsonObj);

						} catch (Exception e) {
							logger.error("导入失败！", e);
							errSb.append("修饰器包：" + file1.getName() + " json文件格式有误！").append("|");
						}

					} else {
						errSb.append(file1.getName() + " 不是修饰器包！").append("|");
					}
				}
			}
		}
		jo.put("decorators", decoratorArr);

		// widget目录是否存在
		File widgetFile = new File(tmpRootPath + dirName + "/widgets/");
		JSONArray widgetArr = new JSONArray();

		if (widgetFile.exists()) {
			File[] files = widgetFile.listFiles();
			if (files.length > 0) {
				for (File file1 : files) {
					if (file1.isDirectory()) {
						// 读取解压目录json文件
						File jsonFile = new File(file1.getAbsolutePath() + "/mainfest.json");
						if (!jsonFile.exists()) {
							errSb.append("widget包：" + file1.getName() + " json文件不存在！").append("|");
							break;
						}

						String nContent = FileUtils.readFileToString(jsonFile, "UTF-8");

						try {
							JSONObject jsonObj = new JSONObject(nContent);

							if (!jsonObj.has("name") || Assert.isEmpty(jsonObj.get("name"))) {
								errSb.append("widget包：" + file1.getName() + " json文件中widget名称不存在或为空！")
										.append("|");
							} else {
								jsonObj.put("name", dirName + "/widgets/" + jsonObj.get("name"));
							}
							widgetArr.put(jsonObj);

						} catch (Exception e) {
							logger.error("导入失败！", e);
							errSb.append("widget包：" + file1.getName() + " json文件格式有误！").append("|");
						}
					} else {
						errSb.append(file1.getName() + " 不是widget包！").append("|");
					}
				}
			}
			jo.put("widgets", widgetArr);
		}
		jo.put("widgets", widgetArr);

		// static目录是否存在
		File staticFile = new File(tmpRootPath + dirName + "/static/");
		if (jo.getJSONArray("widgets").length() == 0 && jo.getJSONArray("decorators").length() == 0
				&& jo.getJSONArray("layouts").length() == 0 && jo.getJSONArray("themes").length() == 0
				&& !staticFile.exists()) {
			errSb.append(jo.get("fileName") + " 非组件包！").append("|");
		}
	}
	
	/**
	 * 校验部件包是否已存在
	 * @param jo
	 * @return
	 * @throws JSONException 
	 */
	private ReturnDto checkComponentPacks(JSONObject jo) throws JSONException {
		String compCode = MathUtils.stringObj(jo.get("compCode"));
		ComponentDto componentDto = componentService.getComponentByCode(compCode);
		if (Assert.isNotEmpty(componentDto.getId())) {
			jo.put("flag", true);
			return new ReturnDto(200, jo.toString());
		}
		return null;
	}

	/**
	 * 导入压缩包--no reply
	 * 
	 * @param file
	 * @param request
	 * @param type
	 * @param json
	 * @return
	 * @throws Exception
	 */
	@ApiOperation("导入-非应答")
	@RequestMapping(value = "/importDataNoReply")
	public ReturnDto importDataNoReply(@RequestParam(required = false) MultipartFile file,
			HttpServletRequest request) throws Exception {
		
		JSONObject jo = new JSONObject();

		try {
			
			// 校验是否允许文件上传
			String token = request.getHeader("token");
			
			if (!uploadPassword.equals(AESUtil.decrypt(token, aesPassword))) {
				return new ReturnDto(500, "not permission to access!");
			}
			
			// 1.根据流获取上传文件
			if (!tmpRootPath.endsWith("/")) {
				tmpRootPath = tmpRootPath + "/";
			}
			String originalFilename = file.getOriginalFilename()
					.substring(file.getOriginalFilename().lastIndexOf('\\') + 1);
			File tmpFile = new File(tmpRootPath + originalFilename);
			FileUtils.copyInputStreamToFile(file.getInputStream(), tmpFile);
			String dirName = originalFilename.substring(0, originalFilename.indexOf('.'));
			
			// 2.上传到文件存储服务
			FileNode fileNode = fileManagerFeign.webUploader(file);
			String referenceId = fileNode.getReferenceId();
			
			// 3.解压前先判断原本地文件是否存在，存在则先删除,避免脏数据
			File dirFile = new File(tmpRootPath + dirName);
			if (dirFile.exists()) {
				FileUtils.deleteQuietly(dirFile);
			}
			
			// 3.解压压缩文件
			ZipUtils.unZip(tmpFile.getAbsolutePath(), tmpRootPath + dirName);
			
			File compJsonFile = new File(tmpRootPath + dirName + "/mainfest.json");
			if (compJsonFile.exists()) {
				String compJsonContent = FileUtils.readFileToString(compJsonFile, "UTF-8");
				JSONObject jsonObj = new JSONObject(compJsonContent);
				
				if (jsonObj.has("version")) {
					jo.put("version", jsonObj.get("version"));
				}
				if (jsonObj.has("name")) {
					jo.put("compCode", jsonObj.get("name"));
				}
			} else {
				// 兼容V2.0 检核部件json文件是否已存在
				compJsonFile = new File(tmpRootPath + dirName + "/manifest.json");
				if (compJsonFile.exists()) {
					String compJsonContent = FileUtils.readFileToString(compJsonFile, "UTF-8");
					JSONObject jsonObj = new JSONObject(compJsonContent);
					
					if (jsonObj.has("version")) {
						jo.put("version", jsonObj.get("version"));
					}
					if (jsonObj.has("name")) {
						jo.put("compCode", jsonObj.get("name"));
					}
				}
			}
			jo.put("referenceId", referenceId);// 文件别名
			
			// 记录部件包信息
			handleComponentPackage(jo);
			return new ReturnDto("导入成功！");
		} catch(Exception e) {
			logger.error("导入失败！", e);
			return new ReturnDto(500, "导入失败,原因:" + e);
		}
	}
	
	/**
	 * 安装部件包
	 * 
	 * @param referenceId
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ApiOperation("安装部件包")
	@RequestMapping(value = "/installComponent")
	public ReturnDto installComponent(@RequestParam String referenceId, @RequestParam String fileName,
			HttpServletRequest request) throws Exception {

		boolean resFlag = true;
		String errMsg = "";
		JSONObject jo = new JSONObject();

		try {
			// 1.根据流获取上传文件
			if (!tmpRootPath.endsWith("/")) {
				tmpRootPath = tmpRootPath + "/";
			}

			if (!downloadUrl.endsWith("/")) {
				downloadUrl = downloadUrl + "/";
			}
			// 租户
			String customer = request.getHeader("Site-info");
			HttpEntity entity = null;
			InputStream ins = null;
			if (Assert.isEmpty(customer)) {
				entity = HttpClientUtils.download(downloadUrl + referenceId);
			} else {
				DynamicDataSourceContextHolder.clearDataSource();
				// 调用文件存储服务进行文件上传
				ResponseEntity<byte[]> entity1 = fileManagerFeign.download(referenceId);
				ins = new ByteArrayInputStream(entity1.getBody());
				DynamicDataSourceContextHolder.setDataSource(customer);
			}
			
			ins = entity.getContent();
			File tmpFile = new File(tmpRootPath + fileName + ".zip");
			FileUtils.copyInputStreamToFile(ins, tmpFile);

			// 2.解压前先判断原本地文件是否存在，存在则先删除,避免脏数据
			File dirFile = new File(tmpRootPath + fileName);
			if (dirFile.exists()) {
				FileUtils.deleteQuietly(dirFile);
			}

			// 校验压缩文件是否已签名
			boolean isSign = chekFileSign(tmpFile.getAbsolutePath());

			if (isSign) {
				jo.put("isSign", true);
			} else {
				jo.put("isSign", false);
			}

			// 3.解压压缩文件
			ZipUtils.unZip(tmpFile.getAbsolutePath(), tmpRootPath + fileName);

			// 4.遍历文件目录,校验并组装合并json文件
			jo.put("filePath", tmpRootPath + fileName);// 解压上传zip文件的目录
			jo.put("fileName", fileName + ".zip");// 解压上传zip文件的名称
			jo.put("personnelId", request.getHeader("personnelId"));
			jo.put("fileAlias", referenceId);// 文件别名
			jo.put("projectCode", fileName);

			// 保存压缩包文件校验错误信息
			StringBuffer errSb = new StringBuffer();

			// 校验部件包json文件是否存在
			File compJsonFile = new File(tmpRootPath + fileName + "/mainfest.json");
			if (compJsonFile.exists()) {
				String compJsonContent = FileUtils.readFileToString(compJsonFile, "UTF-8");

				try {
					JSONObject jsonObj = new JSONObject(compJsonContent);

					if (!jsonObj.has("name") || Assert.isEmpty(jsonObj.get("name"))) {
						errSb.append("部件包：" + fileName + " json文件中部件代码不存在或为空！").append("|");
					} else {
						jo.put("compCode", jsonObj.get("name"));
						jo.put("projectCode", jsonObj.get("name"));
					}

					if (!jsonObj.has("text") || Assert.isEmpty(jsonObj.get("text"))) {
						errSb.append("部件包：" + fileName + " json文件中部件名称不存在或为空！").append("|");
					} else {
						jo.put("compName", jsonObj.get("text"));
					}

					if (jsonObj.has("desc")) {
						jo.put("compDesc", jsonObj.get("desc"));
					}
					
					if (jsonObj.has("version")) {
						jo.put("version", jsonObj.get("version"));
					}

				} catch (Exception e) {
					logger.error("导入失败！", e);
					errSb.append("部件包：" + fileName + " json文件格式有误！").append("|");
				}

			} else {
				
				// 兼容V2.0 检核部件json文件是否已存在
				compJsonFile = new File(tmpRootPath + fileName + "/manifest.json");
				if (compJsonFile.exists()) {
					String compJsonContent = FileUtils.readFileToString(compJsonFile, "UTF-8");

					try {
						JSONObject jsonObj = new JSONObject(compJsonContent);

						if (!jsonObj.has("name") || Assert.isEmpty(jsonObj.get("name"))) {
							errSb.append("部件包：" + fileName + " json文件中部件代码不存在或为空！").append("|");
						} else {
							jo.put("compCode", jsonObj.get("name"));
							jo.put("projectCode", jsonObj.get("name"));
						}

						if (!jsonObj.has("text") || Assert.isEmpty(jsonObj.get("text"))) {
							errSb.append("部件包：" + fileName + " json文件中部件名称不存在或为空！").append("|");
						} else {
							jo.put("compName", jsonObj.get("text"));
						}

						if (jsonObj.has("desc")) {
							jo.put("compDesc", jsonObj.get("desc"));
						}
						
						if (jsonObj.has("version")) {
							jo.put("version", jsonObj.get("version"));
						}
						
						if (jsonObj.has("author")) {
							jo.put("author", jsonObj.get("author"));
						}
						
						if (jsonObj.has("provider")) {
							jo.put("provider", jsonObj.get("provider"));
						}
						
						if (jsonObj.has("main")) {
							jo.put("main", jsonObj.get("main"));
						}
						
						// 拼装主题、布局、修饰器、widget及公共组件
						JSONArray jo_theme = new JSONArray();
						JSONArray jo_layout = new JSONArray();
						JSONArray jo_decorator = new JSONArray();
						JSONArray jo_widget = new JSONArray();
						JSONArray jo_components = new JSONArray();
						
						if (jsonObj.has("themes")) {
							jo_theme = jsonObj.getJSONArray("themes");
						}
						jo.put("themes", jo_theme);
						
						if (jsonObj.has("layouts")) {
							jo_layout = jsonObj.getJSONArray("layouts");
						}
						jo.put("layouts", jo_layout);
						
						if (jsonObj.has("decorators")) {
							jo_decorator = jsonObj.getJSONArray("decorators");
						}
						jo.put("decorators", jo_decorator);
						
						if (jsonObj.has("widgets")) {
							jo_widget = jsonObj.getJSONArray("widgets");
						}
						jo.put("widgets", jo_widget);
						
						if (jsonObj.has("components")) {
							jo_components = jsonObj.getJSONArray("components");
						}
						jo.put("components", jo_components);

					} catch (Exception e) {
						logger.error("导入失败！", e);
						errSb.append("部件包：" + fileName + " json文件格式有误！").append("|");
					}
					
				} else {
					errSb.append("部件包：" + fileName + " json文件不存在！").append("|");
				}
			}
			
			// V1.0 导入处理检核部件包信息
			if (!jo.has("main")) {
				// 检核组件信息
				checkComponent(jo, fileName, errSb);
			}

			String err = errSb.toString();
			if (errSb.length() > 0) {
				err = err.substring(0, err.length() - 1);
				// 记录上传文件操作
				handleImportRecord(jo, "0", err);
				return new ReturnDto(500, err);
			}

			// 处理导入数据
			resFlag = handleAllImportData(jo);

		} catch (Exception e) {
			logger.error("导入失败！", e);
			errMsg = e.getMessage();
			resFlag = false;
		}
        
		if (resFlag) {
			// 记录上传文件操作
			handleImportRecord(jo, "1", "");
			// 记录部件安装信息
			handleComponentRecord(jo);
			return new ReturnDto("安装成功");
		} else {
			// 记录上传文件操作
			handleImportRecord(jo, "0", errMsg);
			return new ReturnDto(500, "安装失败,原因:" + errMsg);
		}
	}

	/**
	 * 不检核导入所有的
	 * @param jsonObj
	 * @return
	 * @throws JSONException
	 * @throws IOException 
	 */
	private boolean handleAllImportData(JSONObject jsonObj) throws JSONException, IOException {
		// 1.数据库操作
		boolean dataFlag = importService.handleImportData(jsonObj);
		String projectCode = jsonObj.getString("projectCode");

		if (dataFlag) {
			String tarPath = jsonObj.getString("filePath");
			// 移动widget包根目录到服务器
			File toFile = null;
			String destDir = parameterService.getParmValueByKey(ParameterConstant.PORTAL_MODULES_PATH[0],
					ParameterConstant.PORTAL_MODULES_PATH[1]);

			if (!destDir.endsWith("/")) {
				destDir = destDir + "/";
			}
			
			toFile = new File(destDir + projectCode);
			
			File sourceFile = new File(tarPath);

			if (!toFile.exists()) {
				toFile.mkdirs();
			} else {
				// 删除历史旧数据
				FileUtils.deleteQuietly(toFile);
			}

			if (!sourceFile.exists()) {
				sourceFile.mkdirs();
			}

			FileUtils.copyFolder(sourceFile, toFile);

			// 移动完成后清除本地解压文件夹
			FileUtils.deleteQuietly(new File(tarPath));
			return true;
		}

		return false;
	}

	/**
	 * 操作组件包记录信息
	 * @param jo
	 * @throws JSONException 
	 */
	private ComponentPackageDto handleComponentPackage(JSONObject jo) throws JSONException {
		ComponentPackageDto componentPackageDto = new ComponentPackageDto();
		componentPackageDto.setComponentName(MathUtils.stringObj(jo.get("compCode")));
		componentPackageDto.setReferenceId(MathUtils.stringObj(jo.get("referenceId")));
		componentPackageDto.setVersion(MathUtils.stringObj(jo.get("version")));
		return componentService.saveComponentPackageInfo(componentPackageDto);
	}

	/**
	 * 校验文件是否已签名
	 * @param absolutePath
	 * @return
	 */
	private boolean chekFileSign(String absolutePath) {
		boolean isCheck = false;
		String command = "jarsigner -keystore " + keystorePath + " -verify -strict " + absolutePath;
		String line = null;
		StringBuilder sb = new StringBuilder();
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec(command);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line + "\n");
			}
			if (sb.toString().contains("错误") || sb.toString().contains("unsigned") || sb.toString().contains("Exception")) {
				isCheck = false;
			}else {
				isCheck = true;
			}
		} catch (IOException e) {
			logger.error("签名失败！", e);
			isCheck = false;
		}
		return isCheck;
	}

	/**
	 * 记录部件安装信息
	 * 
	 * @param jo
	 * @throws JSONException
	 */
	private void handleComponentRecord(JSONObject jo) throws JSONException {
		// 根据部件代码查询部件是否存在
		ComponentDto componentDto = componentService.getComponentByCode(MathUtils.stringObj(jo.get("compCode")));

		componentDto.setCode(MathUtils.stringObj(jo.get("compCode")));
		componentDto.setName(MathUtils.stringObj(jo.get("compName")));
		componentDto.setDesc(MathUtils.stringObj(jo.get("compDesc")));
		componentDto.setIsAuth(jo.getBoolean("isSign"));
		componentDto.setStatus(true);
		// 增加命名文件
		componentDto.setChunkFile(jo.has("main") ? MathUtils.stringObj(jo.get("main")) : null);
		componentService.saveComponentInfo(componentDto);
	}

	/**
	 * 处理导入记录信息
	 * 
	 * @param jo
	 * @param string
	 * @param string2
	 * @throws JSONException
	 */
	private void handleImportRecord(JSONObject jo, String result, String errMsg) {

		try {
			String personalId = jo.getString("personnelId");
			String fileName = jo.getString("fileName");
			String fileAlias = jo.getString("fileAlias");

			ImportDto importDto = new ImportDto();
			importDto.setCreateBy(MathUtils.numObj2Long(personalId));
			importDto.setFileName(fileName);
			importDto.setFailReason(errMsg);
			importDto.setFileAlias(fileAlias);
			importDto.setResult(result);
			importService.saveImportRecord(importDto);

		} catch (Exception e) {
			logger.error("导入失败！", e);
            e.printStackTrace();
		}
	}

	/**
	 * 导入压缩包数据处理
	 * 
	 * @param jsonObj
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 */
	private boolean handleImportData(JSONObject jsonObj) throws JSONException, IOException {

		// 1.数据库操作
		boolean dataFlag = importService.handleImportData(jsonObj);
		String projectCode = jsonObj.getString("projectCode");

		// 2.根据选择移动主题、布局、修饰器、widget文件包
		if (dataFlag) {	
			String tarPath = jsonObj.getString("filePath");
			// 移动widget包根目录到服务器
			File toFile = null;
			String destDir = parameterService.getParmValueByKey(ParameterConstant.PORTAL_MODULES_PATH[0],
					ParameterConstant.PORTAL_MODULES_PATH[1]);

			if (!destDir.endsWith("/")) {
				destDir = destDir + "/";
			}
			
			toFile = new File(destDir + projectCode);
			
			File sourceFile = new File(tarPath);

			if (!toFile.exists()) {
				toFile.mkdirs();
			} else {
				// 存在则先清掉
				FileUtils.deleteQuietly(toFile);
			}

			if (!sourceFile.exists()) {
				sourceFile.mkdirs();
			}

			FileUtils.copyFolder(sourceFile, toFile);
			
			// 移动完成后清除本地解压文件夹
			FileUtils.deleteQuietly(new File(tarPath));
			return true;
		}

		return false;
	}
	
	/**
	 * 校验压缩包是否已存在
	 * 
	 * @param referenceId
	 * @return
	 * @throws Exception
	 */
	@ApiOperation("校验压缩包是否已存在")
	@RequestMapping(value = "/validateComponent")
	public ReturnDto validateComponent(@RequestParam String referenceId, @RequestParam String fileName) throws Exception {

		ReturnDto returnDto = null;
		JSONObject jo = new JSONObject();
		
		//1. 调用文件存储服务进行文件下载
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
		
		// 4.校验部件包json文件是否存在
		File compJsonFile = new File(tmpRootPath + dirName + "/mainfest.json");
		if (compJsonFile.exists()) {
			String compJsonContent = FileUtils.readFileToString(compJsonFile, "UTF-8");

			JSONObject jsonObj = new JSONObject(compJsonContent);

			if (jsonObj.has("name") && !Assert.isEmpty(jsonObj.get("name"))) {
				dirName = MathUtils.stringObj(jsonObj.get("name"));
				jo.put("compCode", dirName);
				returnDto = this.checkComponentPacks(jo);
			}
		} else {
			// 兼容V2.0 检核部件json文件是否已存在
			compJsonFile = new File(tmpRootPath + dirName + "/manifest.json");
			if (compJsonFile.exists()) {
				String compJsonContent = FileUtils.readFileToString(compJsonFile, "UTF-8");

				JSONObject jsonObj = new JSONObject(compJsonContent);

				if (jsonObj.has("name") && !Assert.isEmpty(jsonObj.get("name"))) {
					dirName = MathUtils.stringObj(jsonObj.get("name"));
					jo.put("compCode", dirName);
					returnDto = this.checkComponentPacks(jo);
				}
			}
		}
		
		// 5. 删除本地临时文件
		FileUtils.deleteQuietly(new File(tmpRootPath + dirName));
		if (Assert.isEmpty(returnDto)) {
			jo.put("flag", false);
			return new ReturnDto(200, jo.toString());
		} 
		return returnDto;
	}
	
	/**
	 * 导入部件包接口(供app2接口调用测试)
	 * 
	 * @param referenceId
	 * @param request
	 * @param fileName
	 * @param type 0=校验  1=安装
	 * @throws Exception
	 */
	@ApiOperation("导入部件包接口")
	@RequestMapping(value = "/importComponent")
	public ReturnDto importComponent(@RequestParam String type,
			@RequestParam String referenceId,
			@RequestParam String fileName,
			HttpServletRequest request) throws Exception {

		boolean resFlag = true;
		String errMsg = "";
		JSONObject jo = new JSONObject();

		try {
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
			
			// 校验压缩文件是否已签名
			boolean isSign = chekFileSign(tmpFile.getAbsolutePath());
			
			if (isSign) {
				jo.put("isSign", true);
			} else {
				jo.put("isSign", false);
			}

			// 3.解压压缩文件
			ZipUtils.unZip(tmpFile.getAbsolutePath(), tmpRootPath + dirName);
			
			//---------校验-----------------------------------------
			if(Assert.isNotEmpty(type) && "0".equals(type)) { 
				ReturnDto returnDto = null;
				// 4.校验部件包json文件是否存在
				File compJsonFile = new File(tmpRootPath + dirName + "/mainfest.json");
				if (compJsonFile.exists()) {
					String compJsonContent = FileUtils.readFileToString(compJsonFile, "UTF-8");

					JSONObject jsonObj = new JSONObject(compJsonContent);

					if (jsonObj.has("name") && !Assert.isEmpty(jsonObj.get("name"))) {
						dirName = MathUtils.stringObj(jsonObj.get("name"));
						jo.put("compCode", dirName);
						returnDto = this.checkComponentPacks(jo);
					}
				} else {
					// 兼容V2.0 检核部件json文件是否已存在
					compJsonFile = new File(tmpRootPath + dirName + "/manifest.json");
					if (compJsonFile.exists()) {
						String compJsonContent = FileUtils.readFileToString(compJsonFile, "UTF-8");

						JSONObject jsonObj = new JSONObject(compJsonContent);

						if (jsonObj.has("name") && !Assert.isEmpty(jsonObj.get("name"))) {
							dirName = MathUtils.stringObj(jsonObj.get("name"));
							jo.put("compCode", dirName);
							returnDto = this.checkComponentPacks(jo);
						}
					}
				}
				
				// 5. 删除本地临时文件
				FileUtils.deleteQuietly(new File(tmpRootPath + dirName));
				if (Assert.isEmpty(returnDto)) {
					jo.put("flag", false);
					return new ReturnDto(200, jo.toString());
				} 
				return returnDto;
			}
			
			//---------安装-----------------------------------------
			// 4.遍历文件目录,校验并组装合并json文件
			jo.put("filePath", tmpRootPath + dirName);// 解压上传zip文件的目录
			jo.put("fileName", fileName);// 解压上传zip文件的名称
			jo.put("personnelId", request.getHeader("personnelId"));
			jo.put("fileAlias", referenceId);// 文件别名
			jo.put("projectCode", dirName);
			jo.put("referenceId", referenceId);

			// 保存压缩包文件校验错误信息
			StringBuffer errSb = new StringBuffer();

			// 校验部件包json文件是否存在
			File compJsonFile = new File(tmpRootPath + dirName + "/mainfest.json");
			if (compJsonFile.exists()) {
				String compJsonContent = FileUtils.readFileToString(compJsonFile, "UTF-8");

				try {
					JSONObject jsonObj = new JSONObject(compJsonContent);

					if (!jsonObj.has("name") || Assert.isEmpty(jsonObj.get("name"))) {
						errSb.append("部件包：" + fileName + " json文件中部件代码不存在或为空！").append("|");
					} else {
						dirName = MathUtils.stringObj(jsonObj.get("name"));
						jo.put("compCode", dirName);
						jo.put("projectCode", dirName);
					}

					if (!jsonObj.has("text") || Assert.isEmpty(jsonObj.get("text"))) {
						errSb.append("部件包：" + fileName + " json文件中部件名称不存在或为空！").append("|");
					} else {
						jo.put("compName", jsonObj.get("text"));
					}

					if (jsonObj.has("desc")) {
						jo.put("compDesc", jsonObj.get("desc"));
					}
					
					if (jsonObj.has("version")) {
						jo.put("version", jsonObj.get("version"));
					}

				} catch (Exception e) {
					logger.error("导入失败！", e);
					errSb.append("部件包：" + fileName + " json文件格式有误！").append("|");
				}

			} else {
				
				// 兼容V2.0 检核部件json文件是否已存在
				compJsonFile = new File(tmpRootPath + dirName + "/manifest.json");
				if (compJsonFile.exists()) {
					String compJsonContent = FileUtils.readFileToString(compJsonFile, "UTF-8");

					try {
						JSONObject jsonObj = new JSONObject(compJsonContent);

						if (!jsonObj.has("name") || Assert.isEmpty(jsonObj.get("name"))) {
							errSb.append("部件包：" + fileName + " json文件中部件代码不存在或为空！").append("|");
						} else {
							dirName = MathUtils.stringObj(jsonObj.get("name"));
							jo.put("compCode", dirName);
							jo.put("projectCode", dirName);
						}

						if (!jsonObj.has("text") || Assert.isEmpty(jsonObj.get("text"))) {
							errSb.append("部件包：" + fileName + " json文件中部件名称不存在或为空！").append("|");
						} else {
							jo.put("compName", jsonObj.get("text"));
						}

						if (jsonObj.has("desc")) {
							jo.put("compDesc", jsonObj.get("desc"));
						}
						
						if (jsonObj.has("version")) {
							jo.put("version", jsonObj.get("version"));
						}
						
						if (jsonObj.has("author")) {
							jo.put("author", jsonObj.get("author"));
						}
						
						if (jsonObj.has("provider")) {
							jo.put("provider", jsonObj.get("provider"));
						}
						
						if (jsonObj.has("main")) {
							jo.put("main", jsonObj.get("main"));
						}
						
						// 拼装主题、布局、修饰器、widget及公共组件
						JSONArray jo_theme = new JSONArray();
						JSONArray jo_layout = new JSONArray();
						JSONArray jo_decorator = new JSONArray();
						JSONArray jo_widget = new JSONArray();
						JSONArray jo_components = new JSONArray();
						
						if (jsonObj.has("themes")) {
							jo_theme = jsonObj.getJSONArray("themes");
						}
						jo.put("themes", jo_theme);
						
						if (jsonObj.has("layouts")) {
							jo_layout = jsonObj.getJSONArray("layouts");
						}
						jo.put("layouts", jo_layout);
						
						if (jsonObj.has("decorators")) {
							jo_decorator = jsonObj.getJSONArray("decorators");
						}
						jo.put("decorators", jo_decorator);
						
						if (jsonObj.has("widgets")) {
							jo_widget = jsonObj.getJSONArray("widgets");
						}
						jo.put("widgets", jo_widget);
						
						if (jsonObj.has("components")) {
							jo_components = jsonObj.getJSONArray("components");
						}
						jo.put("components", jo_components);

					} catch (Exception e) {
						logger.error("导入失败！", e);
						errSb.append("部件包：" + fileName + " json文件格式有误！").append("|");
					}
					
				} else {
					errSb.append("部件包：" + fileName + " json文件不存在！").append("|");
				}
			}
			
			// V1.0 导入处理检核部件包信息
			if (!jo.has("main")) {
				// 检核组件信息
				checkComponent(jo, dirName, errSb);
			}

			String err = errSb.toString();
			if (errSb.length() > 0) {
				err = err.substring(0, err.length() - 1);
				// 记录上传文件操作
				handleImportRecord(jo, "0", err);
				return new ReturnDto(500, err);
			}

			// 处理导入数据
			resFlag = handleAllImportData(jo);
			
		} catch (Exception e) {
			logger.error("导入失败！", e);
			errMsg = e.getMessage();
			resFlag = false;
		}

		if (resFlag) {
			// 记录导入记录操作
			handleImportRecord(jo, "1", "");
			// 记录部件安装信息
			handleComponentRecord(jo);
			// 记录部件包信息
			handleComponentPackage(jo);
			
			return new ReturnDto("导入成功！");
		} else {
			// 记录上传文件操作
			handleImportRecord(jo, "0", errMsg);
			return new ReturnDto("导入失败,原因:" + errMsg);
		}
	}
}
