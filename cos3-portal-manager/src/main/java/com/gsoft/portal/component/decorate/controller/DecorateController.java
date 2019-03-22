package com.gsoft.portal.component.decorate.controller;

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

import com.gsoft.cos3.annotation.Permission;
import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ResponseMessageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.dto.SuccessDto;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.FileUtils;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.cos3.util.ZipUtils;
import com.gsoft.portal.common.constans.ParameterConstant;
import com.gsoft.portal.component.decorate.dto.DecorateDto;
import com.gsoft.portal.component.decorate.service.DecorateService;
import com.gsoft.portal.system.basicdata.service.ParameterService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 修饰器管理
 * 
 * @author SN
 *
 */
@Api(tags = "修饰器管理", description = "修饰器相关接口服务")
@RestController
@RequestMapping("/decorate")
public class DecorateController {

	@Resource
	DecorateService decorateService;
	
	@Resource
	ParameterService parameterService;
	
	/**
	 * 本地临时目录
	 */
	public static String tmpRootPath = System.getProperty("java.io.tmpdir") + "sitepack" + File.separator + "decorates" + File.separator;

	@ApiOperation("分页查找修饰器基本信息")
	@RequestMapping(value = "/queryDecorateDataTable", method = RequestMethod.GET)
	public PageDto queryDecorateDataTable(@RequestParam String search,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size,
			@RequestParam(value = "sortProp", defaultValue = "id") String sortProp,
			@RequestParam(value = "order", defaultValue = "desc") String order) {

		return decorateService.queryDecorateDataTable(search, page, size, sortProp, order);

	}

	@ApiOperation("根据Id获取修饰器信息")
	@RequestMapping(value = "/getDecorateInfoById", method = RequestMethod.GET)
	@Permission(true)
	public DecorateDto getDecorateInfoById(@RequestParam Long id) {
		return decorateService.getDecorateInfoById(id);
	}

	@ApiOperation("判断修饰器代码是否存在")
	@RequestMapping(value = "/isExitDecorateCode", method = RequestMethod.GET)
	public Boolean isExitDecorateCode(@RequestParam(required = false) Long id, @RequestParam String code, @RequestParam(required = false) String projectCode) {
		return decorateService.isExitDecorateCode(id, code, projectCode);
	}

	@ApiOperation("保存修饰器信息")
	@RequestMapping(value = "/saveDecorate", method = RequestMethod.POST)
	public DecorateDto saveDecorate(@ModelAttribute("organizationDto") DecorateDto decorateDto, ServletRequest servletRequest) throws IOException, JSONException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		decorateDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));

		if (Assert.isEmpty(decorateDto.getId())) {
			decorateDto.setCreateTime(new Date());
		} else {
			decorateDto.setUpdateTime(new Date());
		}
		
		DecorateDto dto = null;
		
		//保存成功后移动压缩包
		if (!Assert.isEmpty(decorateDto.getReferenceId()) && !Assert.isEmpty(decorateDto.getIsImp()) && "1".equals(decorateDto.getIsImp())) {
			//读取解压目录json文件
			File jsonFile = new File(tmpRootPath + decorateDto.getCode() + "/" + decorateDto.getCode() + "/mainfest.json");
			//校验主题code是否已存在
			String nContent = FileUtils.readFileToString(jsonFile, "UTF-8");
			JSONObject jsonObj = new JSONObject(nContent);
			String projectCode = jsonObj.getString("projectCode");
			String isSystem = jsonObj.getString("isSystem");
			String title = jsonObj.getString("title");
			decorateDto.setProjectCode(projectCode);
			decorateDto.setIsSystem(("1".equals(isSystem) ? true : false));
			decorateDto.setName(title);
			
			dto = decorateService.saveDecorate(decorateDto);
			
			//移动主题包
			copyFolders(decorateDto.getProjectCode(), tmpRootPath + decorateDto.getCode() + "/", decorateDto.getCode());
			
		}else {
			dto = decorateService.saveDecorate(decorateDto);
		}

		return dto;
	}

	@ApiOperation("删除修饰器信息")
	@RequestMapping(value = "/delDecorate", method = RequestMethod.GET)
	public void delDecorate(@RequestParam Long id, @RequestParam String code) {
		decorateService.delDecorate(id, code);//若修饰器有使用则删除后默认关联默认修饰器 TODO
	}
	
	@ApiOperation("获取所有修饰器信息")
	@RequestMapping(value = "/getDecorateList", method = RequestMethod.GET)
	public List<DecorateDto> getDecorateList(){
		return decorateService.getDecorateList();
	}
	
	@ApiOperation("获取所有修饰器信息")
	@RequestMapping(value = "/getAllDecorateList", method = RequestMethod.GET)
	public ReturnDto getAllDecorateList(){
		return new ReturnDto(decorateService.getDecorateList());
	}
	
	/**
	 * 修饰器导入
	 * @param file
	 * @param request
	 * @param type
	 * @param json
	 * @return
	 * @throws Exception
	 */
	@ApiOperation("修饰器导入")
	@RequestMapping(value = "/importDecorate")
	public ResponseMessageDto importDecorate(@RequestParam(required = false) MultipartFile file, HttpServletRequest request, 
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
					errMsg = "导入修饰器json文件不存在！|";
				}else {
					//校验修饰器code是否已存在
					String nContent = FileUtils.readFileToString(jsonFile, "UTF-8");
					
					try {
						jsonObj = new JSONObject(nContent);
						
						if (!jsonObj.has("name") || Assert.isEmpty(jsonObj.get("name"))) {
							errMsg+= "导入修饰器json文件中修饰器名称不存在或为空！|";
						}
						
						if (!jsonObj.has("projectCode") || Assert.isEmpty(jsonObj.get("projectCode"))) {
							errMsg+= "导入修饰器json文件中项目代码不存在或为空！|";
						}
						
					}catch (Exception e) {
						errMsg+= "导入修饰器json文件格式有误!|";
					}
				}
				
				if (errMsg.length() > 0) {
					errMsg = errMsg.substring(0, errMsg.length() - 1);
					return new ResponseMessageDto(false, errMsg);
				}
				
				String decorateCode = jsonObj.getString("name");
				String projectCode = jsonObj.getString("projectCode");
				String isSystem = jsonObj.getString("isSystem");
				String title = jsonObj.getString("title");
				
				if (!Assert.isEmpty(decorateCode)) {
					//校验修饰器是否已存在
					ResponseMessageDto responseMessageDto = checkDecorate(jsonObj);
					if (!Assert.isEmpty(responseMessageDto)) {
						return responseMessageDto;
					}
					
					//数据入库
					DecorateDto decorateDto = new DecorateDto();
					decorateDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));
					decorateDto.setCreateTime(new Date());
					decorateDto.setCode(decorateCode);
					decorateDto.setName(title);
					decorateDto.setProjectCode(projectCode);
					decorateDto.setIsSystem(("1".equals(isSystem) ? true : false));
					decorateDto.setReferenceId("");
					decorateDto.setIsImp("1");
					decorateService.saveDecorate(decorateDto);
					
					//移动修饰器包
					copyFolders(projectCode, tmpRootPath + dirName + "/", dirName);
					
				}else {
					return new ResponseMessageDto(false, "导入修饰器json文件中修饰器名称不存在！");
				}
				
			}else if ("0".equals(type)) {//修饰器新增或修改时导入
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
					errMsg = "导入主题json文件不存在！|";
				}else {
					//校验主题code是否已存在
					String nContent = FileUtils.readFileToString(jsonFile, "UTF-8");
					
					try {
						jsonObj = new JSONObject(nContent);
						
						if (jsonObj.has("name") && !Assert.isEmpty(jsonObj.get("name"))) {
							String themeCode = jsonObj.getString("name");
							//校验当前修饰器code和json文件中的是否一致
							if (!code.equals(themeCode)) {
								errMsg += "导入修饰器json文件中修饰器名称与当前修饰器不一致！|";
							}
						}else {
							errMsg += "导入修饰器json文件中修饰器名称不存在或为空！|";
						}
						
					}catch (Exception e) {
						errMsg+= "导入修饰器json文件格式有误!|";
					}
				}
				
				if (errMsg.length() > 0) {
					errMsg = errMsg.substring(0, errMsg.length() - 1);
					return new ResponseMessageDto(false, errMsg);
				}
				
				return new ResponseMessageDto(true, "导入成功", "", jsonObj.toString());
				
			}else if ("1".equals(type)) {//新增时修饰器已存在确认覆盖
				
				JSONObject jsonObj = new JSONObject(json);
				String decorateCode = jsonObj.getString("name");
				String projectCode = jsonObj.getString("projectCode");
				String isSystem = jsonObj.getString("isSystem");
				String title = jsonObj.getString("title");
				
				//数据入库
				DecorateDto decorateDto = decorateService.getDecorateInfoByCode(decorateCode);
				if (Assert.isEmpty(decorateDto.getId())) {
					decorateDto = new DecorateDto();
				}
				
				decorateDto.setCode(decorateCode);
				decorateDto.setName(title);
				decorateDto.setProjectCode(projectCode);
				decorateDto.setIsSystem(("1".equals(isSystem) ? true : false));
				decorateDto.setReferenceId("");
				decorateDto.setIsImp("1");
				
				decorateService.saveDecorate(decorateDto);
				
				//移动修饰器包
				copyFolders(projectCode, tmpRootPath + decorateCode + "/", decorateCode);
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
	 * 校验修饰器CODE是否已存在
	 * @param isCover
	 * @return
	 * @throws JSONException 
	 */
	private ResponseMessageDto checkDecorate(JSONObject jsonObj) throws JSONException {
		
		DecorateDto decorateDto = decorateService.getDecorateInfoByCode(MathUtils.stringObj(jsonObj.getString("name")));
		if (Assert.isNotEmpty(decorateDto.getId())) {
			return new ResponseMessageDto(true,decorateDto.getCode(),"1",jsonObj.toString());
		}
		return null;
	}
	
	@ApiOperation("修饰器导出")
	@RequestMapping(value = "/exportDecorate", method = RequestMethod.POST)
	public ResponseMessageDto exportDecorate(HttpServletRequest httpRequest, @RequestParam String code) throws Exception {
		
		boolean resFlag = true;
		String errMsg = "";
		
		try {
			String targetPath = parameterService.getParmValueByKey(ParameterConstant.ZIP_PACK_PATH[0], ParameterConstant.ZIP_PACK_PATH[1]);
			if (!targetPath.endsWith("/")) {
				targetPath = targetPath + "/";
			}
			
			targetPath = targetPath + "decorators/";
			
			DecorateDto decorateDto = decorateService.getDecorateInfoByCode(code);
			String projectCode = decorateDto.getProjectCode();
			
			//复制主题包到本地临时目录下
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
	 * 移动服务器上主题文件到本地临时目录
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
		destDir = destDir + projectCode + "/decorators/" + code + "/";
		
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
		
		destDir = destDir + projectCode + "/decorators/"+ dirName + "/";
		
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

}
