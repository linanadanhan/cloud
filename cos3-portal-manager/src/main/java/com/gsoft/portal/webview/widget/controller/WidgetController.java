package com.gsoft.portal.webview.widget.controller;

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
import com.gsoft.portal.system.basicdata.service.ParameterService;
import com.gsoft.portal.webview.widget.dto.WidgetDto;
import com.gsoft.portal.webview.widget.service.WidgetService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * widget管理
 * 
 * @author SN
 *
 */
@Api(tags = "widget管理", description = "widget相关接口服务")
@RestController
@RequestMapping("/widget")
public class WidgetController {

	@Resource
	WidgetService widgetService;
	
	@Resource
	ParameterService parameterService;
	
	/**
	 * 本地临时目录
	 */
	public static String tmpRootPath = System.getProperty("java.io.tmpdir") + "sitepack" + File.separator + "widgets" + File.separator;

	@ApiOperation("分页查找widget基本信息")
	@RequestMapping(value = "/queryWidgetDataTable", method = RequestMethod.GET)
	public PageDto queryWidgetDataTable(@RequestParam String search,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size,
			@RequestParam(value = "sortProp", defaultValue = "id") String sortProp,
			@RequestParam(value = "order", defaultValue = "desc") String order) {

		return widgetService.queryWidgetDataTable(search, page, size, sortProp, order);

	}

	@ApiOperation("根据Id获取widget信息")
	@RequestMapping(value = "/getWidgetInfoById", method = RequestMethod.GET)
	public WidgetDto getWidgetInfoById(@RequestParam Long id) {
		return widgetService.getWidgetInfoById(id);
	}

	@ApiOperation("判断widget代码是否存在")
	@RequestMapping(value = "/isExitWidgetCode", method = RequestMethod.GET)
	public Boolean isExitWidgetCode(@RequestParam(required = false) Long id, @RequestParam String code,@RequestParam(required = false) String projectCode) {
		return widgetService.isExitWidgetCode(id, code, projectCode);
	}
	
	@ApiOperation("获取所有分类widget信息")
	@RequestMapping(value = "/getCatlogWidgetList", method = RequestMethod.GET)
	public String getCatlogWidgetList() throws JSONException{
		return widgetService.getCatlogWidgetList();
	}
	
	@ApiOperation("获取widget所有信息")
	@RequestMapping(value = "/getWidgetList", method = RequestMethod.GET)
	public List<WidgetDto> getWidgetList(){
		return widgetService.getWidgetList();
	}

	@ApiOperation("保存widget信息")
	@RequestMapping(value = "/saveWidget", method = RequestMethod.POST)
	public WidgetDto saveWidget(@ModelAttribute("widgetDto") WidgetDto widgetDto, ServletRequest servletRequest) throws JSONException, IOException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		widgetDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));

		if (Assert.isEmpty(widgetDto.getId())) {
			widgetDto.setCreateTime(new Date());
		} else {
			widgetDto.setUpdateTime(new Date());
		}
		
		WidgetDto dto = null;
		
		//保存成功后移动压缩包
		if (!Assert.isEmpty(widgetDto.getReferenceId()) && !Assert.isEmpty(widgetDto.getIsImp()) && "1".equals(widgetDto.getIsImp())) {
			//读取解压目录json文件
			File jsonFile = new File(tmpRootPath + widgetDto.getCode() + "/" + widgetDto.getCode() + "/mainfest.json");
			//校验主题code是否已存在
			String nContent = FileUtils.readFileToString(jsonFile, "UTF-8");
			JSONObject jsonObj = new JSONObject(nContent);
			String projectCode = jsonObj.getString("projectCode");
			String isOpen = jsonObj.getString("isOpen");
			String isSystem = jsonObj.getString("isSystem");
			String title = jsonObj.getString("title");
			widgetDto.setProjectCode(projectCode);
			widgetDto.setIsOpen(isOpen);
			widgetDto.setIsSystem(("1".equals(isSystem) ? true : false));
			widgetDto.setName(title);
			dto = widgetService.saveWidget(widgetDto);
			
			//移动widget包
			copyFolders(widgetDto.getProjectCode(), tmpRootPath + widgetDto.getCode() + "/", widgetDto.getCode());
			
		}else {
			dto = widgetService.saveWidget(widgetDto);
		}

		return dto;
	}

	@ApiOperation("删除widget信息")
	@RequestMapping(value = "/delWidget", method = RequestMethod.GET)
	public void delWidget(@RequestParam Long id, @RequestParam String code) {
		widgetService.delWidget(id, code);
	}
	
	/**
	 * widget导入
	 * @param file
	 * @param request
	 * @param type
	 * @param json
	 * @return
	 * @throws Exception
	 */
	@ApiOperation("widget导入")
	@RequestMapping(value = "/importWidget")
	public ResponseMessageDto importWidget(@RequestParam(required = false) MultipartFile file, HttpServletRequest request, 
			@RequestParam(required = false) String type,
			@RequestParam(required = false) String code,
			@RequestParam(required = false) String json) throws Exception {
		
		boolean resFlag = true;
		String errMsg = "";
		
		try {
			if (Assert.isEmpty(type)) {//列表界面新增时导入
				//1.根据流获取上传文件
				File tmpFile = new File(tmpRootPath + file.getOriginalFilename());
				FileUtils.copyInputStreamToFile(file.getInputStream(), tmpFile);
				String dirName = file.getOriginalFilename().substring(0, file.getOriginalFilename().indexOf("."));
				
				//2.解压压缩文件
				ZipUtils.unZip(tmpFile.getAbsolutePath(), tmpRootPath + dirName);
				
				//删除临时压缩文件
				FileUtils.deleteQuietly(tmpFile);
				
				//读取解压目录json文件
				File jsonFile = new File(tmpRootPath + dirName + "/" + dirName + "/mainfest.json");
				JSONObject jsonObj = null;
				
				if (!jsonFile.exists()) {
					errMsg = "导入widget json文件不存在！|";
				}else {
					//校验widget code是否已存在
					String nContent = FileUtils.readFileToString(jsonFile, "UTF-8");
					
					try {
						jsonObj = new JSONObject(nContent);
						
						if (!jsonObj.has("name") || Assert.isEmpty(jsonObj.get("name"))) {
							errMsg+= "导入widget json文件中widget名称不存在或为空！|";
						}
						
						if (!jsonObj.has("projectCode") || Assert.isEmpty(jsonObj.get("projectCode"))) {
							errMsg+= "导入widget json文件中项目代码不存在或为空！|";
						}
						
					}catch (Exception e) {
						errMsg+= "导入widget json文件格式有误!|";
					}
				}
				
				if (errMsg.length() > 0) {
					errMsg = errMsg.substring(0, errMsg.length() - 1);
					return new ResponseMessageDto(false, errMsg);
				}

				String widgetCode = jsonObj.getString("name");
				String projectCode = jsonObj.getString("projectCode");
				String isOpen = jsonObj.getString("isOpen");
				String isSystem = jsonObj.getString("isSystem");
				String title = jsonObj.getString("title");
				
				if (!Assert.isEmpty(widgetCode)) {
					//校验主题是否已存在
					ResponseMessageDto responseMessageDto = checkWidget(jsonObj);
					if (!Assert.isEmpty(responseMessageDto)) {
						return responseMessageDto;
					}
					
					//数据入库
					WidgetDto widgetDto = new WidgetDto();
					widgetDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));
					widgetDto.setCreateTime(new Date());
					widgetDto.setCode(widgetCode);
					widgetDto.setName(title);
					widgetDto.setProjectCode(projectCode);
					widgetDto.setIsSystem(("1".equals(isSystem) ? true : false));
					widgetDto.setIsOpen(isOpen);
					widgetDto.setReferenceId("");
					widgetDto.setIsImp("1");
					
					widgetService.saveWidget(widgetDto);
					
					//移动widget包
					copyFolders(projectCode, tmpRootPath + dirName + "/", dirName);
				}
				
			}else if ("0".equals(type)) {//widget新增或修改时导入
				//1.根据流获取上传文件
				File tmpFile = new File(tmpRootPath + file.getOriginalFilename());
				FileUtils.copyInputStreamToFile(file.getInputStream(), tmpFile);
				String dirName = file.getOriginalFilename().substring(0, file.getOriginalFilename().indexOf("."));
				
				//2.解压压缩文件
				ZipUtils.unZip(tmpFile.getAbsolutePath(), tmpRootPath + dirName);
				
				//删除临时压缩文件
				FileUtils.deleteQuietly(tmpFile);
				
				//读取解压目录json文件
				File jsonFile = new File(tmpRootPath + dirName + "/" + dirName + "/mainfest.json");
				JSONObject jsonObj = null;
				
				if (!jsonFile.exists()) {
					errMsg = "导入widget json文件不存在！|";
					
				}else {
					//校验widget code是否已存在
					String nContent = FileUtils.readFileToString(jsonFile, "UTF-8");
					
					try {
						jsonObj = new JSONObject(nContent);
						
						if (jsonObj.has("name") && !Assert.isEmpty(jsonObj.get("name"))) {
							String themeCode = jsonObj.getString("name");
							//校验当前widget code和json文件中的是否一致
							if (!code.equals(themeCode)) {
								errMsg += "导入widget json文件中widget名称与当前widget不一致！|";
							}
						}else {
							errMsg += "导入widget json文件中widget名称不存在或为空！|";
						}
						
					}catch (Exception e) {
						errMsg+= "导入widget json文件格式有误!|";
					}
				}
				
				if (errMsg.length() > 0) {
					errMsg = errMsg.substring(0, errMsg.length() - 1);
					return new ResponseMessageDto(false, errMsg);
				}
				
				return new ResponseMessageDto(true, "导入成功", "", jsonObj.toString());
				
			}else if ("1".equals(type)) {//新增时widget已存在确认覆盖
				
				JSONObject jsonObj = new JSONObject(json);
				String widgetCode = jsonObj.getString("name");
				String projectCode = jsonObj.getString("projectCode");
				String isSystem = jsonObj.getString("isSystem");
				String isOpen = jsonObj.getString("isOpen");
				String title = jsonObj.getString("title");
				
				//数据入库
				WidgetDto widgetDto = widgetService.getWidgetInfoByCode(widgetCode);
				if (Assert.isEmpty(widgetDto.getId())) {
					widgetDto = new WidgetDto();
				}
				
				widgetDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));
				widgetDto.setCreateTime(new Date());
				widgetDto.setCode(widgetCode);
				widgetDto.setName(title);
				widgetDto.setProjectCode(projectCode);
				widgetDto.setIsSystem(("1".equals(isSystem) ? true : false));
				widgetDto.setIsOpen(isOpen);
				widgetDto.setReferenceId("");
				widgetDto.setIsImp("1");
				
				widgetService.saveWidget(widgetDto);
				
				//移动widget包
				copyFolders(projectCode, tmpRootPath + widgetCode + "/", widgetCode);
			}
			
		}catch (Exception e) {
			errMsg = e.getMessage();
			resFlag = false;
		}
		
		if (resFlag) {
			return new SuccessDto("导入成功");
		}else {
			return new ResponseMessageDto(false, "导入失败,原因:"+errMsg);
		}
	}
	
	/**
	 * 校验widget CODE是否已存在
	 * @param isCover
	 * @return
	 * @throws JSONException 
	 */
	private ResponseMessageDto checkWidget(JSONObject jsonObj) throws JSONException {
		
		WidgetDto widgetDto = widgetService.getWidgetInfoByCode(MathUtils.stringObj(jsonObj.getString("name")));
		if (Assert.isNotEmpty(widgetDto.getId())) {
			return new ResponseMessageDto(true,widgetDto.getCode(),"1",jsonObj.toString());
		}
		return null;
	}

	@ApiOperation("widget导出")
	@RequestMapping(value = "/exportWidget", method = RequestMethod.POST)
	public ResponseMessageDto exportWidget(HttpServletRequest httpRequest, @RequestParam String code) throws Exception {
		
		boolean resFlag = true;
		String errMsg = "";
		
		try {
			String targetPath = parameterService.getParmValueByKey(ParameterConstant.ZIP_PACK_PATH[0], ParameterConstant.ZIP_PACK_PATH[1]);
			if (!targetPath.endsWith("/")) {
				targetPath = targetPath + "/";
			}
			
			targetPath = targetPath + "widgets/";
			
			WidgetDto widgetDto = widgetService.getWidgetInfoByCode(code);
			String projectCode = widgetDto.getProjectCode();
			
			//复制widget包到本地临时目录下
			movePackToTemp(tmpRootPath+code,code,projectCode);
			
			String zipFilePath = tmpRootPath+code+".zip";
			
			//压缩文件
			ZipUtils.zip(tmpRootPath+code, zipFilePath);
			
			//删除临时文件夹
			FileUtils.deleteQuietly(new File(tmpRootPath + code));
			
			//上传到前端服务对应目录
			FileUtils.copyFolder(new File(zipFilePath), new File(targetPath+code+".zip"));
			
			//删除zip临时文件
			FileUtils.deleteQuietly(new File(zipFilePath));
			
		}catch (Exception e) {
			errMsg = e.getMessage();
			resFlag = false;
		}
		
		if (resFlag) {
			return new SuccessDto("导出成功");
		}else {
			return new ResponseMessageDto(false, "导出失败,原因:"+errMsg);
		}
	}

	/**
	 * 移动服务器上widget文件到本地临时目录
	 * @param tmpRootPath
	 * @param code
	 * @param projectCode
	 * @throws IOException 
	 */
	private void movePackToTemp(String tarPath, String code, String projectCode) throws IOException {
		
		String destDir = parameterService.getParmValueByKey(ParameterConstant.PORTAL_MODULES_PATH[0], ParameterConstant.PORTAL_MODULES_PATH[1]);
		
		if (!destDir.endsWith("/")) {
			destDir = destDir + "/";
		}
		
		File toFile = new File(tarPath + File.separator + code);
		destDir = destDir + projectCode + "/widgets/" + code + "/";
		
		File sourceFile = new File(destDir);
		
		if (!toFile.exists())
		{
			toFile.mkdirs();
		}
		
		if (!sourceFile.exists()) {
			sourceFile.mkdirs();
		}
		
		FileUtils.copyFolder(sourceFile,toFile);
	}

	/**
	 * 移动文件夹
	 * @param projectCode
	 * @param tmpDir
	 * @param dirName
	 * @throws IOException
	 */
	private void copyFolders(String projectCode, String tmpDir, String dirName) throws IOException {
		//移动文件夹到对应目录下
		String destDir = parameterService.getParmValueByKey(ParameterConstant.PORTAL_MODULES_PATH[0], ParameterConstant.PORTAL_MODULES_PATH[1]);
		
		if (!destDir.endsWith("/")) {
			destDir = destDir + "/";
		}
		
		destDir = destDir + projectCode + "/widgets/"+ dirName + "/";
		
		File localFile = new File(tmpDir + dirName);
		File toFile = new File(destDir);
		
		if (!toFile.exists())
		{
		    toFile.mkdirs();
		}
		
		//复制本地临时目录下的文件到服务器对应目录
		FileUtils.copyFolder(localFile,toFile);
		FileUtils.deleteQuietly(localFile);
	}
	
	/**
	 * 修改widget code 信息
	 */
	@ApiOperation("修改widget code 信息")
	@RequestMapping(value = "/udpWidgetCode")
	public void udpWidgetCode() {
		widgetService.udpWidgetCode();
	}
}
