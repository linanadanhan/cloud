package com.gsoft.portal.component.layout.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
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
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.dto.SuccessDto;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.FileUtils;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.cos3.util.ZipUtils;
import com.gsoft.portal.common.constans.ParameterConstant;
import com.gsoft.portal.common.constans.ResultConstant;
import com.gsoft.portal.common.utils.ReadJsonUtil;
import com.gsoft.portal.component.layout.dto.LayoutDto;
import com.gsoft.portal.component.layout.service.LayoutService;
import com.gsoft.portal.system.basicdata.service.ParameterService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 布局管理
 * 
 * @author SN
 *
 */
@Api(tags = "布局器管理", description = "布局接口服务")
@RestController
@RequestMapping("/layout")
public class LayoutController {

	@Resource
	LayoutService layoutService;

	@Resource
	ParameterService parameterService;
	
	/**
	 * 本地临时目录
	 */
	public static String tmpRootPath = System.getProperty("java.io.tmpdir") + "sitepack" + File.separator + "layouts" + File.separator;

	@ApiOperation("分页查找布局基本信息")
	@RequestMapping(value = "/queryLayoutDataTable", method = RequestMethod.GET)
	public PageDto queryLayoutDataTable(@RequestParam String search,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size,
			@RequestParam(value = "sortProp", defaultValue = "id") String sortProp,
			@RequestParam(value = "order", defaultValue = "desc") String order) {

		return layoutService.queryLayoutDataTable(search, page, size, sortProp, order);

	}

	@ApiOperation("根据Id获取布局信息")
	@RequestMapping(value = "/getLayoutInfoById", method = RequestMethod.GET)
	public LayoutDto getLayoutInfoById(@RequestParam Long id) {
		return layoutService.getLayoutInfoById(id);
	}

	@ApiOperation("判断布局代码是否存在")
	@RequestMapping(value = "/isExitLayoutCode", method = RequestMethod.GET)
	public Boolean isExitLayoutCode(@RequestParam(required = false) Long id, @RequestParam String code, @RequestParam(required = false) String projectCode) {
		return layoutService.isExitLayoutCode(id, code, projectCode);
	}

	@ApiOperation("保存布局信息")
	@RequestMapping(value = "/saveLayout", method = RequestMethod.POST)
	public LayoutDto saveLayout(@ModelAttribute("layoutDto") LayoutDto layoutDto, ServletRequest servletRequest) throws JSONException, IOException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		layoutDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));

		if (Assert.isEmpty(layoutDto.getId())) {
			layoutDto.setCreateTime(new Date());
		} else {
			layoutDto.setUpdateTime(new Date());
		}
		
		LayoutDto dto = null;
		//保存成功后移动压缩包
		if (!Assert.isEmpty(layoutDto.getReferenceId()) && !Assert.isEmpty(layoutDto.getIsImp()) && "1".equals(layoutDto.getIsImp())) {
			//读取解压目录json文件
			File jsonFile = new File(tmpRootPath + layoutDto.getCode() + "/" + layoutDto.getCode() + "/mainfest.json");
			String nContent = FileUtils.readFileToString(jsonFile, "UTF-8");
			JSONObject jsonObj = new JSONObject(nContent);
			String layoutCode = jsonObj.getString("name");
			String projectCode = jsonObj.getString("projectCode");
			String isSystem = jsonObj.getString("isSystem");
			String title = jsonObj.getString("title");
			layoutDto.setCode(layoutCode);
			layoutDto.setName(title);
			layoutDto.setProjectCode(projectCode);
			layoutDto.setIsSystem(("1".equals(isSystem) ? true : false));
			layoutDto.setReferenceId("");
			layoutDto.setIsImp("1");
			
			dto = layoutService.saveLayout(layoutDto);
			
			//移动布局包
			copyFolders(layoutDto.getProjectCode(), tmpRootPath + layoutDto.getCode() + "/", layoutDto.getCode());
			
		}else {
			dto = layoutService.saveLayout(layoutDto);
		}

		return dto;
	}

	@ApiOperation("删除布局信息")
	@RequestMapping(value = "/delLayout", method = RequestMethod.GET)
	public void delLayout(@RequestParam Long id, @RequestParam String code) {
		layoutService.delLayout(id, code);//若布局有使用则删除后默认关联默认布局 TODO
	}
	
	@ApiOperation("获取所有布局信息")
	@RequestMapping(value = "/getLayoutList", method = RequestMethod.GET)
	public List<LayoutDto> getLayoutList(){
		return layoutService.getLayoutList();
	}
	
	@ApiOperation("获取所有布局信息--返回ReturnDto")
	@RequestMapping(value = "/getAllLayOutList", method = RequestMethod.GET)
    public ReturnDto getAllLayOutList() {
    	return new ReturnDto(layoutService.getLayoutList());
    }
	
	@ApiOperation("获取所有布局list")
	@RequestMapping(value = "/layoutList", method = RequestMethod.GET)
	public String layoutList() throws JSONException {
		
		JSONObject jo = new JSONObject();
		
		JSONArray joArr = new JSONArray();
		List<LayoutDto> layoutList = layoutService.getLayoutList();
		
		if (Assert.isNotEmpty(layoutList) && layoutList.size() > 0) {
			for (LayoutDto dto : layoutList) {
				JSONObject dtoJo = new JSONObject();
				dtoJo.put("name", dto.getCode());
				dtoJo.put("title", dto.getName());
				joArr.put(dtoJo);
			}
		}
		jo.put("status", ResultConstant.RESULT_RETURN_OK_STATUS);
		jo.put("data", joArr);
		
		return jo.toString();
	}
	
	/**
	 * 布局导入
	 * @param file
	 * @param request
	 * @param type
	 * @param json
	 * @return
	 * @throws Exception
	 */
	@ApiOperation("布局导入")
	@RequestMapping(value = "/importLayout")
	public ResponseMessageDto importLayout(@RequestParam(required = false) MultipartFile file, HttpServletRequest request, 
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
					errMsg = "导入布局json文件不存在！|";
				}else {
					//校验布局code是否已存在
					String nContent = FileUtils.readFileToString(jsonFile, "UTF-8");
					
					try {
						jsonObj = new JSONObject(nContent);
						
						if (!jsonObj.has("name") || Assert.isEmpty(jsonObj.get("name"))) {
							errMsg+= "导入布局json文件中主题名称不存在或为空！|";
						}
						
						if (!jsonObj.has("projectCode") || Assert.isEmpty(jsonObj.get("projectCode"))) {
							errMsg+= "导入布局json文件中项目代码不存在或为空！|";
						}
						
						if (!jsonObj.has("params") || Assert.isEmpty(jsonObj.getJSONArray("params"))) {
							errMsg+= "导入布局json文件中布局参数不存在或为空！|";
						}
						
					}catch (Exception e) {
						errMsg+= "导入布局json文件格式有误!|";
					}
				}
				
				if (errMsg.length() > 0) {
					errMsg = errMsg.substring(0, errMsg.length() - 1);
					return new ResponseMessageDto(false, errMsg);
				}

				String layoutCode = jsonObj.getString("name");
				String projectCode = jsonObj.getString("projectCode");
				String isSystem = jsonObj.getString("isSystem");
				String title = jsonObj.getString("title");
				
				if (!Assert.isEmpty(layoutCode)) {
					//校验主题是否已存在
					ResponseMessageDto responseMessageDto = checkLayout(jsonObj);
					if (!Assert.isEmpty(responseMessageDto)) {
						return responseMessageDto;
					}
					
					//数据入库
					LayoutDto layoutDto = new LayoutDto();
					layoutDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));
					layoutDto.setCreateTime(new Date());
					layoutDto.setCode(layoutCode);
					layoutDto.setName(title);
					layoutDto.setProjectCode(projectCode);
					layoutDto.setIsSystem(("1".equals(isSystem) ? true : false));
					layoutDto.setReferenceId("");
					layoutDto.setIsImp("1");
					layoutService.saveLayout(layoutDto);
					
					//移动布局包
					copyFolders(projectCode, tmpRootPath + dirName + "/", dirName);
				}
				
			}else if ("0".equals(type)) {//布局新增或修改时导入
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
					errMsg = "导入布局json文件不存在！|";
				}else {
					//校验布局code是否已存在
					String nContent = FileUtils.readFileToString(jsonFile, "UTF-8");
					
					try {
						jsonObj = new JSONObject(nContent);
						
						if (jsonObj.has("name") && !Assert.isEmpty(jsonObj.get("name"))) {
							String layoutCode = jsonObj.getString("name");
							//校验当前布局code和json文件中的是否一致
							if (!code.equals(layoutCode)) {
								errMsg += "导入布局json文件中布局名称与当前布局不一致！|";
							}
						}else {
							errMsg += "导入布局json文件中布局名称不存在或为空！|";
						}
						
					}catch (Exception e) {
						errMsg+= "导入布局json文件格式有误!|";
					}
				}
				
				if (errMsg.length() > 0) {
					errMsg = errMsg.substring(0, errMsg.length() - 1);
					return new ResponseMessageDto(false, errMsg);
				}
				
				return new ResponseMessageDto(true, "导入成功", "", jsonObj.toString());
				
			}else if ("1".equals(type)) {//新增时布局已存在确认覆盖
				
				JSONObject jsonObj = new JSONObject(json);
				String layoutCode = jsonObj.getString("name");
				String projectCode = jsonObj.getString("projectCode");
				String isSystem = jsonObj.getString("isSystem");
				String title = jsonObj.getString("title");
				
				//数据入库
				LayoutDto layoutDto = layoutService.getLayoutInfoByCode(layoutCode);
				if (Assert.isEmpty(layoutDto.getId())) {
					layoutDto = new LayoutDto();
				}
				
				layoutDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));
				layoutDto.setCreateTime(new Date());
				layoutDto.setCode(layoutCode);
				layoutDto.setName(title);
				layoutDto.setProjectCode(projectCode);
				layoutDto.setIsSystem(("1".equals(isSystem) ? true : false));
				layoutDto.setReferenceId("");
				layoutDto.setIsImp("1");
				layoutService.saveLayout(layoutDto);
				
				//移动布局包
				copyFolders(projectCode, tmpRootPath + layoutCode + "/", layoutCode);
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
	 * 校验布局CODE是否已存在
	 * @param isCover
	 * @return
	 * @throws JSONException 
	 */
	private ResponseMessageDto checkLayout(JSONObject jsonObj) throws JSONException {
		
		LayoutDto layoutDto = layoutService.getLayoutInfoByCode(MathUtils.stringObj(jsonObj.getString("name")));
		if (Assert.isNotEmpty(layoutDto.getId())) {
			return new ResponseMessageDto(true,layoutDto.getCode(),"1",jsonObj.toString());
		}
		return null;
	}
	
	@ApiOperation("布局导出")
	@RequestMapping(value = "/exportLayout", method = RequestMethod.POST)
	public ResponseMessageDto exportLayout(HttpServletRequest httpRequest, @RequestParam String code) throws Exception {
		
		boolean resFlag = true;
		String errMsg = "";
		
		try {
			String targetPath = parameterService.getParmValueByKey(ParameterConstant.ZIP_PACK_PATH[0], ParameterConstant.ZIP_PACK_PATH[1]);
			if (!targetPath.endsWith("/")) {
				targetPath = targetPath + "/";
			}
			
			targetPath = targetPath + "layouts/";
			
			LayoutDto layoutDto = layoutService.getLayoutInfoByCode(code);
			String projectCode = layoutDto.getProjectCode();
			
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
	 * 移动服务器上布局文件到本地临时目录
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
		destDir = destDir + projectCode + "/layouts/" + code + "/";
		
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
		
		destDir = destDir + projectCode + "/layouts/"+ dirName + "/";
		
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
	
	@ApiOperation("获取布局json数据")
	@RequestMapping(value = "/getLayoutDatas", method = RequestMethod.GET)
	public String getLayoutDatas(@RequestParam String layoutCode) throws Exception {
		
		String destDir = parameterService.getParmValueByKey(ParameterConstant.PORTAL_MODULES_PATH[0], ParameterConstant.PORTAL_MODULES_PATH[1]);
		JSONObject jsonObj = null;
		
		String rtn = null;
		
		if (!destDir.endsWith("/")) {
			destDir = destDir + "/";
		}
		
		LayoutDto dto = layoutService.getLayoutInfoByCode(layoutCode);
		
		destDir = destDir + dto.getProjectCode() + "/layouts/"+ layoutCode + "/";
		
		jsonObj = ReadJsonUtil.readJsonFile(destDir, "mainfest.json"); //TODO
		
		rtn = jsonObj.toString();
		
		return rtn;
	}

}
