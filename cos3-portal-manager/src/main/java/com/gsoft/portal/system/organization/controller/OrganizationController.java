package com.gsoft.portal.system.organization.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
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
import com.gsoft.cos3.exception.BusinessException;
import com.gsoft.cos3.feign.file.FileManagerFeign;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.tree.TreeNode;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.cos3.util.TreeUtils;
import com.gsoft.cos3.util.excel.ExcelUtil;
import com.gsoft.portal.system.organization.dto.OrganizationDto;
import com.gsoft.portal.system.organization.service.OrganizationService;
import com.gsoft.portal.system.personnel.service.PersonnelService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 组织机构管理
 *
 * @author SN
 */
@Api(tags = "机构管理", description = "组织机构管理接口服务")
@RestController
@RequestMapping("/org")
public class OrganizationController {

	@Resource
	OrganizationService organizationService;
	@Resource
	BaseDao baseDao;
	@Resource
	PersonnelService personnelService;
	
	@Resource
	FileManagerFeign fileManagerFeign;

	@ApiOperation("通过code获取组织机构")
	@RequestMapping(value = "/select/getPageByCode", method = RequestMethod.GET)
	public PageDto getListByCode(@RequestParam String code, @RequestParam(required = false) Boolean isCascade,
			@RequestParam(required = false) Long id, @RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size) {
		return organizationService.getListByCode(code, isCascade, id, page, size);
	}

	@ApiOperation("通过用户获取组织机构列表")
	@RequestMapping(value = "/select/getTreeByCode", method = RequestMethod.GET)
	public List<TreeNode> getTreeList(ServletRequest servletRequest, @RequestParam(required = false) String dimension) {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		// String orgCode = request.getHeader("orgCode"); //登录人员机构code
		String personnelId = request.getHeader("personnelId");
		List<OrganizationDto> item = null;
		if (Assert.isNotEmpty(dimension)) {
			item = organizationService.getTreeList(personnelId, dimension);
		} else {
			item = organizationService.getTreeList(personnelId);
		}

		if (item == null) {
			return new ArrayList<TreeNode>();
		}
		List<TreeNode> tree = TreeUtils.convert(item).attrs("cascade,code,level,id").tree();
		return tree;
	}

	@ApiOperation("根据Id获取组织机构信息")
	@RequestMapping(value = "/select/getOneById", method = RequestMethod.GET)
	public OrganizationDto getOneById(@RequestParam Long id) {
		return organizationService.getOneById(id);
	}

	@ApiOperation("判断组织机构代码是否存在")
	@RequestMapping(value = "/isExitOrgCode", method = RequestMethod.GET)
	public Boolean isExitOrgCode(@RequestParam(required = false) Long id, @RequestParam String code,
			@RequestParam String dimension) {
		return organizationService.isExitCode(id, code, dimension);
	}

	@ApiOperation("保存组织机构")
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public OrganizationDto save(@ModelAttribute("organizationDto") OrganizationDto organizationDto,
			ServletRequest servletRequest) {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		organizationDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));
		return organizationService.save(organizationDto);
	}

	@ApiOperation("根据ParentId获取Cascade")
	@RequestMapping(value = "/select/getCascadeByParentId", method = RequestMethod.GET)
	public String getCascadeByParentId(@RequestParam Long parentId) {
		return organizationService.getCascadeByParentId(parentId);
	}

	@ApiOperation("新增机构后添加对应的权限")
	@RequestMapping(value = "/addGrant", method = RequestMethod.POST)
	public ResponseMessageDto addGrant(@RequestParam String orgId, @RequestParam String dimension,
			HttpServletRequest request) {
		String personnelId = request.getHeader("personnelId");
		return organizationService.addGrant(orgId, dimension, personnelId);
	}

	@Transactional
	@ApiOperation("删除组织机构")
	@RequestMapping(value = "/delete/delOrgById", method = RequestMethod.GET)
	public Boolean delOrgById(@RequestParam Long id, @RequestParam String code) {
		return organizationService.deleteById(id, code);
	}

	@ApiOperation("获取人员所属机构信息")
	@RequestMapping(value = "/getOrgInfoByUserId", method = RequestMethod.GET)
	public Map<String, Object> getOrgInfoByUserId(@RequestParam Long userId, @RequestParam String dimension) {
		return organizationService.getOrgInfoByUserId(userId, dimension);
	}

	@ApiOperation("获取人员所属机构信息")
	@RequestMapping(value = "/getOrgDtoByPersonnelIdAndDimension", method = RequestMethod.GET)
    public List<OrganizationDto> getOrgDtoByPersonnelIdAndDimension(@RequestParam Long personnelId, @RequestParam String dimension) {
		return organizationService.getOrgDtoByUserId(personnelId, dimension);
	}

    @ApiOperation("获取人员所属机构信息")
    @RequestMapping(value = "/getOrgDtoByPersonnelId", method = RequestMethod.GET)
    public List<OrganizationDto> getOrgDtoByPersonnelId(@RequestParam Long personnelId) {
        return organizationService.getOrgDtoByUserId(personnelId);
    }

	@ApiOperation("组织机构导入")
	@RequestMapping(value = "/import/importOrg", method = RequestMethod.POST)
	public ResponseMessageDto importOrg(HttpServletRequest httpRequest, @ModelAttribute("fileNode") FileNode fileNode)
			throws Exception {
		List<OrganizationDto> list = new ArrayList<OrganizationDto>();
		List<OrganizationDto> errList = new ArrayList<OrganizationDto>();
		ExcelUtil<OrganizationDto> excel = null;
		InputStream input = null;
		HttpEntity entity = null;
		try {
			// 调用文件存储服务进行文件上传
			ResponseEntity<byte[]> res = fileManagerFeign.download(fileNode.getReferenceId());
			input = new ByteArrayInputStream(res.getBody());
			
			excel = new ExcelUtil<OrganizationDto>(OrganizationDto.class);
			list = excel.importExcel(fileNode.getName(), input, 2);
		} catch (Exception e) {
			throw new BusinessException("导入失败:"+e.getMessage(), e);
		} finally {
			if (null != entity) {
				// 释放连接
				EntityUtils.consumeQuietly(entity);
			}
		}

		for (OrganizationDto dto : list) {
			dto.setCreateBy(Long.valueOf(httpRequest.getHeader("personnelId")));
			OrganizationDto errDto = organizationService.orgImport(dto);
			if (Assert.isEmpty(errDto)) {
				errList.add(dto);
				dto.setImpResult("失败");
			} else {
				dto.setImpResult("成功");
			}
		}

		String resFile = System.getProperty("java.io.tmpdir") + File.separator + "org_import_templet.xls";
		boolean exp = excel.exportExcel(list, "组织机构信息", 65536, new FileOutputStream(resFile));
		String referenceId = "";

		if (exp) {
			// 上传到文件存储服务
			File f = new File(resFile);
			FileInputStream inp = new FileInputStream(f);
	        MultipartFile multipartFile = new MockMultipartFile("file", f.getName(), "text/plain", inp);
			FileNode fNode = fileManagerFeign.webUploader(multipartFile);
			referenceId = fNode.getReferenceId();
		}
		return new SuccessDto("导入成功", referenceId);
	}
	
	@ApiOperation("根据部门名模糊查询部门")
	@RequestMapping(value = "/getDepByNameAndDimension", method = RequestMethod.GET)
	public ReturnDto getDepByNameAndDimension(@RequestParam(required = false) String departmentName,
			@RequestParam(required = false) String dimension) {
		if (Assert.isEmpty(departmentName) && Assert.isEmpty((dimension))) {
			return new ReturnDto(organizationService.getAllOrgList());
		}
		List<Map<String, Object>> orgInfo = organizationService.getOrgInfoByNameAndDimension(departmentName, dimension);
		List<OrganizationDto> collect = orgInfo.stream().map(m -> {
			OrganizationDto selDto = new OrganizationDto();
			Long orgId = MathUtils.numObj2Long(m.get("C_ID"));
			Long parentId = MathUtils.numObj2Long((m.get("C_PARENT_ID")));
			selDto.setParentId(parentId);
			selDto.setId(orgId);
			selDto.setName(m.get("C_NAME").toString());
			return selDto;
		}).collect(Collectors.toList());
		return new ReturnDto(collect);
	}

}
