package com.gsoft.portal.system.personnel.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.excel.ExcelUtil;
import com.gsoft.portal.system.organization.service.OrganizationService;
import com.gsoft.portal.system.personnel.dto.PasswordInfoDto;
import com.gsoft.portal.system.personnel.dto.PersonnelDto;
import com.gsoft.portal.system.personnel.dto.RoleDto;
import com.gsoft.portal.system.personnel.dto.RolePersonnelDto;
import com.gsoft.portal.system.personnel.service.PersonnelService;
import com.gsoft.portal.system.personnel.service.RoleService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 用户管理
 *
 * @author SN
 *
 */
@Api(tags = "用户管理", description = "用户管理接口服务")
@RestController
@RequestMapping(value = "/personnel")
public class PersonnelController {

	@Autowired
	PersonnelService personnelService;

	@Autowired
	RoleService roleService;

	@Resource
	BaseDao baseDao;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	OrganizationService organizationService;
	
	@Resource
	FileManagerFeign fileManagerFeign;

	@ApiOperation("根据选中的树节点查询人员列表分页")
	@RequestMapping(value = "/select/getPageList", method = RequestMethod.GET)
	public PageDto getListByOrgCode(HttpServletRequest request, 
			@RequestParam String dimension,
			@RequestParam String orgCode, 
			@RequestParam(required = false) Boolean isCascade,
			@RequestParam(required = false) String search,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size,
			@RequestParam(value = "sortProp", defaultValue = "C_SORT_NO") String sortProp,
			@RequestParam(value = "order", defaultValue = "asc") String order) {
		String personnelId = request.getHeader("personnelId"); // 登录人ID
		return personnelService.getListByOrgCode(dimension, orgCode, isCascade, search, page, size, sortProp, order, personnelId);
	}
	
	/**
	 * 门户widget使用
	 * @param request
	 * @param orgCode
	 * @param isCascade
	 * @param search
	 * @param page
	 * @param size
	 * @param sortProp
	 * @param order
	 * @return
	 */
	@ApiOperation("根据选中的树节点查询人员列表分页")
	@RequestMapping(value = "/select/getPageList4Widget", method = RequestMethod.GET)
	public ReturnDto getListByOrgCode4Widget(HttpServletRequest request, @RequestParam String orgCode, @RequestParam(required = false) Boolean isCascade,
			@RequestParam(required = false) String search,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size,
			@RequestParam(value = "sortProp", defaultValue = "C_SORT_NO") String sortProp,
			@RequestParam(value = "order", defaultValue = "asc") String order) {
		String personnelId = request.getHeader("personnelId"); // 登录人ID
		return new ReturnDto(personnelService.getListByOrgCode("", orgCode, isCascade, search, page, size, sortProp, order, personnelId));
	}

	/**
	 * 根据手机验证码重置密码
	 *
	 * @return 重置密码是否成功
	 */
	@ApiOperation("根据手机验证码重置密码")
	@RequestMapping(value = "/update/resetPassword", method = RequestMethod.POST)
	public ResponseMessageDto resetPassword(@RequestParam(required = false) String mobile, @RequestParam String password1,
			@RequestParam String password2, @RequestParam String msgValiCode) {
		// 检测mobile是否存在
		Assert.isTrue(personnelService.isExitByPhone(null, mobile), "手机号不存在");

		Assert.equals(password1, password2, "两次输入密码不相同");
		if (stringRedisTemplate.getExpire(mobile, TimeUnit.SECONDS) < 0) {
			throw new BusinessException("验证码不正确或已过期失效，请重新发送");
		}
		String msgValiCodeInCache = stringRedisTemplate.opsForValue().get(mobile);
		Assert.equals(msgValiCodeInCache, msgValiCode, "手机验证码无法匹配，请重试");
		personnelService.resetPassword(mobile, password1);
		return ResponseMessageDto.SUCCESS;
	}
	
	@ApiOperation("重置密码为系统默认")
	@RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
	ReturnDto resetPwd(@RequestParam Long id) {
		return personnelService.resetPassword(id);
	}

	/**
	 * 验证登录名是否唯一
	 *
	 * @param id
	 * @param loginName
	 * @return 验证通过，返回ture,即不存在
	 */
	@ApiOperation("验证登录名是否唯一")
	@RequestMapping(value = "/add/validateLoginName", method = RequestMethod.GET)
	public Boolean validateLoginName(@RequestParam(required = false) Long id, @RequestParam String loginName) {
		Boolean flag = personnelService.isExitByLoginName(id, loginName);
		return !flag;
	}

	/**
	 * 验证手机号唯一
	 *
	 * @param id
	 * @param phone
	 * @return
	 */
	@ApiOperation("新增时验证手机号唯一")
	@RequestMapping(value = "/add/validatePhone", method = RequestMethod.GET)
	public Boolean validatePhone(@RequestParam(required = false) Long id, @RequestParam String phone) {
		Boolean flag = personnelService.isExitByPhone(id, phone);
		return !flag;
	}

	@ApiOperation("创建用户")
	@RequestMapping(value = "/add/save", method = RequestMethod.POST)
	public PersonnelDto addPersonnel(HttpServletRequest request, @ModelAttribute("personnelDto") PersonnelDto personnelDto) {
		if (Assert.isEmpty(personnelDto.getId())) {// 新增
			personnelDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));
			personnelDto.setCreateTime(new Date());
		} else {// 修改
            personnelDto.setUpdateBy(Long.valueOf(request.getHeader("personnelId")));
            personnelDto.setUpdateTime(new Date());
        }
        return personnelService.save(personnelDto);
    }

	/**
	 * 删除人员，另外还要删除人员与角色的关系
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation("删除人员")
	@RequestMapping(value = "/delete/delById", method = RequestMethod.GET)
	public void delById(@RequestParam Long id) {
		personnelService.deleteById(id);
	}
	

	@ApiOperation("删除人员")
	@RequestMapping(value = "/batchDelPersonnel", method = RequestMethod.GET)
	public void batchDelPersonnel(@RequestParam String ids) {
		personnelService.batchDelPersonnel(ids);
	}

	@ApiOperation("修改人员状态")
	@RequestMapping(value = "/update/updateStatus")
	public void updateStatus2(@RequestParam Long id, @RequestParam Boolean status) {
		personnelService.updateStatus(id, status);
	}
	
	@ApiOperation("修改人员头像")
	@RequestMapping(value = "/modifyHeadImg")
	public void modifyHeadImg(@RequestParam Long id, @RequestParam String referenceId) {
		personnelService.modifyHeadImg(id, referenceId);
	}


	@ApiOperation("根据人员编号得到单个对象")
	@RequestMapping(value = "/select/getOneById", method = RequestMethod.GET)
	public PersonnelDto getOneById(@RequestParam Long id) {
		return personnelService.getOneById(id);
	}

	// --------------------------人员管理角色------------

	//1.查询左侧的角色分类
	@ApiOperation("根据type查询未授权的权限")
	@RequestMapping(value = "/connect/getHasNoConnectRoleTypes", method = RequestMethod.GET)
	public List<String> getRoleTypes(HttpServletRequest httpRequest) {
		// 如果是系统管理员，查询所有
		// 如果是组织机构人员，就查询当前组织机构的角色所关联的权限 ，或者登录人可以转授的角色关联权限的总集
		String personnelId = httpRequest.getHeader("personnelId"); // 登录人ID
		return roleService.getHasNoConnectRoleTypes(Long.valueOf(personnelId));
	}


	//2.查询未授权的角色
	@ApiOperation("查询某人员未授权的角色")
	@RequestMapping(value = "/connect/getHasNoConnectRole", method = RequestMethod.GET)
	public List<RoleDto> getHasNoGrantRole(HttpServletRequest request, @RequestParam String type) {
		// 查询当前登录人创建的角色或登录人可以转授的角色--剔除掉已经授权的角色(到前台剔除)
		// 这里不用区分平台管理员，因为平台管理员就是查询自己创建的角色，也可以适用上面的逻辑
		String loginPersonnelId = request.getHeader("personnelId");// 得到当前登录人的人员编号
		return roleService.getHasNoConnectRole(Long.valueOf(loginPersonnelId), type);
	}

	//3.查询已经授权的角色
	@ApiOperation("查询某人员已经授权的角色")
	@RequestMapping(value = "/connect/getHasConnectRole", method = RequestMethod.GET)
	public List<RoleDto> getHasGrantRole(@RequestParam Long personnelId, @RequestParam String type) {
		return roleService.getHasConnectRole(personnelId, type);
	}


	//4.保存人员关联角色-保存
	@ApiOperation("人员关联角色")
	@RequestMapping(value = "/connect/addRolePersonnel", method = RequestMethod.POST)
	public void addRolePersonnel(@RequestBody List<RolePersonnelDto> list) {
		personnelService.connectRolePersonnel(list);
	}

	//4.保存人员关联角色-保存
	@ApiOperation("人员关联角色")
	@RequestMapping(value = "/personnelConnectRoles", method = RequestMethod.POST)
	public void personnelConnectRoles(@RequestParam String personalId,@RequestParam String roleIds) {
		personnelService.connectRolePersonnel(personalId,roleIds);
	}


	@ApiOperation("删除某人员关联的角色")
	@RequestMapping(value = "/connect/deleteConnectRole", method = RequestMethod.POST)
	public void deleteConnectRole(@RequestParam Long personnelId) {
		roleService.deleteConnectRole(personnelId);
	}

	// --------------------------------------测试验证service-----------

	/**
	 * 根据手机号得到单个对象
	 *
	 * @param phone
	 * @return
	 */
	@ApiOperation("根据手机号得到单个对象")
	@RequestMapping(value = "/select/getOneByPhone", method = RequestMethod.GET)
	public PersonnelDto getOneByPhone(@RequestParam String phone) {
		return personnelService.getOneByPhone(phone);
	}


	//根据登录名和行政区划ID得到单个对象
	@ApiOperation("根据登录名和行政区划得到单个对象")
	@RequestMapping(value = "/select/getOneByloginNameAndAreaCode", method = RequestMethod.GET)
	public PersonnelDto getOneByloginNameAndAreaCode(@RequestParam String loginName, @RequestParam String areaCode) {
		return personnelService.getOneByloginName(loginName);
	}

	@ApiOperation("将人员移动至另一个组织机构")
	@RequestMapping(value = "/move/movePersonnel", method = RequestMethod.GET)
	public ResponseMessageDto movePersonnel(@RequestParam String ids, @RequestParam Long orgId) {
		personnelService.movePersonnel(ids, orgId);
		return new SuccessDto("移动成功");
	}

	@ApiOperation("修改用户密码")
	@RequestMapping(value = "/modifyPassword", method = RequestMethod.POST)
	public ReturnDto modifyPassword(@RequestBody PasswordInfoDto passwordInfoDto) {
		return personnelService.modifyPassword(passwordInfoDto);
	}

	/**
	 * 根据手机验证码重置密码
	 *
	 * @return 重置密码是否成功
	 */
	@ApiOperation("根据手机验证码重置密码")
	@RequestMapping(value = "/bindPhone", method = RequestMethod.POST)
	public ResponseMessageDto bindPhone(@RequestParam String id, @RequestParam String mobile,
			@RequestParam String msgValiCode) {
		if (stringRedisTemplate.getExpire(mobile, TimeUnit.SECONDS) < 0) {
			throw new BusinessException("验证码不正确或已过期失效，请重新发送");
		}
		String msgValiCodeInCache = stringRedisTemplate.opsForValue().get(mobile);
		Assert.equals(msgValiCodeInCache, msgValiCode, "手机验证码无法匹配，请重试");
		return personnelService.bindPhone(id, mobile);
	}

	@ApiOperation("人员导入")
	@RequestMapping(value = "/import/importPersonnel", method = RequestMethod.POST)
	public ResponseMessageDto importPersonnel(HttpServletRequest httpRequest,
			@ModelAttribute("fileNode") FileNode fileNode) throws Exception {
		List<PersonnelDto> list = new ArrayList<PersonnelDto>();
		List<PersonnelDto> errList = new ArrayList<PersonnelDto>();
		ExcelUtil<PersonnelDto> excel = null;
		InputStream input = null;
		HttpEntity entity = null;
		try {
			// 调用文件存储服务进行文件上传
			ResponseEntity<byte[]> res = fileManagerFeign.download(fileNode.getReferenceId());
			input = new ByteArrayInputStream(res.getBody());
			excel = new ExcelUtil<PersonnelDto>(PersonnelDto.class);
			list = excel.importExcel(fileNode.getName(), input, 2);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != entity) {
				EntityUtils.consumeQuietly(entity);
			}
		}

		for (PersonnelDto dto : list) {
			dto.setCreateBy(Long.valueOf(httpRequest.getHeader("personnelId")));
			PersonnelDto errDto = personnelService.importPersonnel(dto);
            if (Assert.isEmpty(errDto)) {
                errList.add(dto);
                dto.setImpResult("失败");
            }else {
            	dto.setImpResult("成功");
            }
		}

	 	String resFile = System.getProperty("java.io.tmpdir") + File.separator + "person_import_templet.xls";
        boolean exp = excel.exportExcel(list, "人员信息", 65536, new FileOutputStream(resFile));

        String referenceId = "";

        if (exp) {
			File f = new File(resFile);
			FileInputStream inp = new FileInputStream(f);
			MultipartFile multipartFile = new MockMultipartFile("file", f.getName(), "text/plain", inp);
			FileNode fNode = fileManagerFeign.webUploader(multipartFile);
			referenceId = fNode.getReferenceId();
        }

        return new SuccessDto("导入成功",referenceId);

	}

	@ApiOperation("根据维度拿到下面所有的人")
	@RequestMapping(value = "/getAllPersonByDimension", method = RequestMethod.GET)
	public List<PersonnelDto> getAllPersonByDimension(@RequestParam String dimension) {
		return personnelService.getAllPersonByDimension(dimension);
	}
	
	@ApiOperation("所有的人")
	@RequestMapping(value = "/getAllPerson", method = RequestMethod.GET)
	public List<PersonnelDto> getAllPerson() {
		return personnelService.getAllPerson();
	}
	
	@ApiOperation("根据机构id获取下面所有的人")
	@RequestMapping(value = "/getPersonsByOrgId", method = RequestMethod.GET)
	public List<PersonnelDto> getAllPersonByOrgId(@RequestParam Long orgId) {
		return personnelService.getPersonsByOrgId(orgId);
	}

	@ApiOperation("根据机构得到机构下所有人员包括子机构")
	@RequestMapping(value = "/getCascadePersonnelByOrg", method = RequestMethod.GET)
	public List<PersonnelDto> getCascadePersonnelByOrg(@RequestParam Long orgId) {
		return personnelService.getCascadePersonnelByOrg(orgId);
	}

}
