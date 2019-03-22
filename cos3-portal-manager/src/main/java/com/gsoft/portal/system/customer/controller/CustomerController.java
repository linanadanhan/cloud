package com.gsoft.portal.system.customer.controller;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.util.Assert;
import com.gsoft.portal.system.customer.dto.CustomerDto;
import com.gsoft.portal.system.customer.service.CustomerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 租户管理 Controller
 *
 * @author SN
 */
@Api(tags = "租户管理", description = "租户管理接口服务")
@RestController
@RequestMapping("/customer")
public class CustomerController {

	@Resource
	private CustomerService customerService;

	@ApiOperation("分页查询租户列表信息")
	@RequestMapping(value = "/getCustomerList", method = RequestMethod.GET)
	public ReturnDto getCustomerList(@RequestParam String search,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "20") Integer size,
			@RequestParam(value = "sortProp", defaultValue = "id") String sortProp,
			@RequestParam(value = "order", defaultValue = "desc") String order) {
		return new ReturnDto(customerService.getCustomerList(search, page, size, sortProp, order));
	}

	@ApiOperation("根据主键ID获取单笔租户信息")
	@RequestMapping(value = "/getCustomerById", method = RequestMethod.GET)
	public ReturnDto getCustomerById(@RequestParam Long id) {
		return new ReturnDto(customerService.getCustomerById(id));
	}

	@ApiOperation("保存租户数据")
	@RequestMapping(value = "/saveCustomer", method = RequestMethod.POST)
	public ReturnDto saveCustomer(HttpServletRequest request, @RequestBody CustomerDto customerDto) {
		customerDto.setCreateBy(Long.valueOf(request.getHeader("personnelId")));
		if (Assert.isEmpty(customerDto.getId())) {
			customerDto.setCreateTime(new Date());
		} else {
			customerDto.setUpdateTime(new Date());
		}
		if ("Mysql".equals(customerDto.getDbType())) {
			customerDto.setDsDriverClassName("com.mysql.jdbc.Driver");
		} else {
			customerDto.setDsDriverClassName("oracle.jdbc.driver.OracleDriver");
		}
		return new ReturnDto(customerService.saveCustomer(customerDto));
	}

	@ApiOperation("删除租户数据")
	@RequestMapping(value = "/deleteCustomer", method = RequestMethod.GET)
	public ReturnDto deleteCustomer(@RequestParam Long id) {
		customerService.deleteCustomer(id);
		return new ReturnDto("删除成功！");
	}
	
    @ApiOperation("校验租户标识是否已存在")
    @RequestMapping(value = "/isUniqueCustomerCode", method = RequestMethod.GET)
    public ReturnDto isUniqueCustomerCode(@RequestParam Long id, @RequestParam String code) {
        return new ReturnDto(customerService.isUniqueCustomerCode(id, code));
    }
    
	@ApiOperation("测试连接")
	@RequestMapping(value = "/testCustomerConn", method = RequestMethod.POST)
	public ReturnDto testCustomerConn(@RequestBody CustomerDto customerDto) throws Exception {
		customerService.testCustomerConn(customerDto);
		return new ReturnDto("测试连接成功！");
	} 
	
    @ApiOperation("校验域名是否已存在")
    @RequestMapping(value = "/isUniqueDomain", method = RequestMethod.GET)
    public ReturnDto isUniqueDomain(@RequestParam Long id, @RequestParam String domain) {
        return new ReturnDto(customerService.isUniqueDomain(id, domain));
    }
}
