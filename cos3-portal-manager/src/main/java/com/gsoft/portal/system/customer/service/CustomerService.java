package com.gsoft.portal.system.customer.service;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.portal.system.customer.dto.CustomerDto;

/**
 * 租户管理service接口类
 * @author chenxx
 *
 */
public interface CustomerService {

	/**
	 * 分页查询租户列表信息
	 * @param search
	 * @param page
	 * @param size
	 * @param sortProp
	 * @param order
	 * @return
	 */
	public PageDto getCustomerList(String search, Integer page, Integer size, String sortProp, String order);

	/**
	 * 根据主键获取单笔租户信息
	 * @param id
	 * @return
	 */
	public CustomerDto getCustomerById(Long id);

	/**
	 * 保存租户信息
	 * @param customerDto
	 * @return
	 */
	public CustomerDto saveCustomer(CustomerDto customerDto);

	/**
	 * 删除租户信息
	 * @param id
	 */
	public void deleteCustomer(Long id);

	/**
	 * 校验租户标识是否已存在
	 * @param id
	 * @param code
	 * @return
	 */
	public Boolean isUniqueCustomerCode(Long id, String code);

	/**
	 * 连接测试
	 * @param customerDto
	 * @throws Exception 
	 */
	public void testCustomerConn(CustomerDto customerDto) throws Exception;

	/**
	 * 校验域名是否已存在
	 * @param id
	 * @param domain
	 * @return
	 */
	public Boolean isUniqueDomain(Long id, String domain);
}
