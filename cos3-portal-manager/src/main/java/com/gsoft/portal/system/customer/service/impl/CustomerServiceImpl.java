package com.gsoft.portal.system.customer.service.impl;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.gsoft.cos3.datasource.DataSourceUtils;
import com.gsoft.cos3.datasource.DynamicDataSourceContextHolder;
import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.portal.system.customer.dto.CustomerDto;
import com.gsoft.portal.system.customer.entity.Customer;
import com.gsoft.portal.system.customer.persistence.CustomerPersistence;
import com.gsoft.portal.system.customer.service.CustomerService;
import com.gsoft.portal.webview.site.entity.SiteCustomerEntity;
import com.gsoft.portal.webview.site.persistence.SiteCustomerPersistence;

/**
 * 租户管理service实现类
 *
 * @author chenxx
 */
@Service
public class CustomerServiceImpl implements CustomerService {

	@Resource
	private CustomerPersistence customerPersistence;

	@Resource
	BaseDao baseDao;

	@Resource
	SiteCustomerPersistence siteCustomerPersistence;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private DiscoveryClient discoveryClient;

	public void refreshDataSource() {
		List<String> services = discoveryClient.getServices();
		services.forEach(serverName -> {
			List<ServiceInstance> instances = discoveryClient.getInstances(serverName);
			instances.forEach(serviceInstance -> {
				@SuppressWarnings("unused")
				String post = restTemplate.postForObject(serviceInstance.getUri() + "/refresh", null, String.class);
			});
		});
	}

	@Override
	public PageDto getCustomerList(String search, Integer page, Integer size, String sortProp, String order) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT c.* FROM COS_SAAS_CUSTOMER c where 1=1 ");
		Map<String, Object> params = new HashMap<String, Object>();

		if (Assert.isNotEmpty(search)) {
			sb.append(" AND c.C_NAME like ${search} ");
			params.put("search", "%" + search + "%");
		}
		sb.append(" ORDER BY c.c_id DESC ");
		PageDto pageDto = baseDao.query(page, size, sb.toString(), params);
		return pageDto;
	}

	@Override
	public CustomerDto getCustomerById(Long id) {
		return BeanUtils.convert(customerPersistence.getOne(id), CustomerDto.class);
	}

	@Override
	public CustomerDto saveCustomer(CustomerDto customerDto) {
		Customer entity = BeanUtils.convert(customerDto, Customer.class);
		Customer reEntity = customerPersistence.save(entity);
		// 同步更新das库记录信息
		syncDasData(customerDto);
		new Thread(() -> {
			// 刷新上下文
			refreshDataSource();
			// 同步更新对应租户web站点的域名信息
			// 切换到租户
			DynamicDataSourceContextHolder.setDataSource(customerDto.getCode());
			baseDao.update("UPDATE cos_portal_site SET c_domain_name = ? WHERE c_code = 'web'", customerDto.getDomain());
		}).start();
		return BeanUtils.convert(reEntity, CustomerDto.class);
	}

	/**
	 * 同步das记录
	 */
	private void syncDasData(CustomerDto customerDto) {
		// 先查询是否已存在
		SiteCustomerEntity entity = siteCustomerPersistence.getSiteCustomerInfo("web", customerDto.getDomain(),
				customerDto.getCode());
		if (Assert.isEmpty(entity)) {
			entity = new SiteCustomerEntity();
			entity.setCreateBy(customerDto.getCreateBy());
			entity.setCreateTime(customerDto.getCreateTime());
		} else {
			entity.setUpdateBy(customerDto.getUpdateBy());
			entity.setUpdateTime(customerDto.getUpdateTime());
		}
		entity.setSiteCode("web");
		entity.setDomain(customerDto.getDomain());
		entity.setCustomer(customerDto.getCode());
		siteCustomerPersistence.save(entity);
	}

	@Override
	public void deleteCustomer(Long id) {
		customerPersistence.delete(id);
		// 刷新上下文
		refreshDataSource();
	}

	@Override
	public Boolean isUniqueCustomerCode(Long id, String code) {
		Customer entity = null;

		if (Assert.isEmpty(id)) {
			entity = customerPersistence.isUniqueCustomerCode(code);
		} else {
			entity = customerPersistence.isUniqueCustomerCode(id, code);
		}

		if (entity != null) {
			return true;
		}
		return false;
	}

	@Override
	public void testCustomerConn(CustomerDto customerDto) throws Exception {
		String type = customerDto.getDbType();
		String url = customerDto.getDsUrl();
		String user = customerDto.getDsUserName();
		String password = customerDto.getDsPassword();
		Connection conn = null;
		try {
			conn = DataSourceUtils.getConnection(url, user, password, type);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceUtils.close(conn);
		}
	}

	@Override
	public Boolean isUniqueDomain(Long id, String domain) {
		Customer entity = null;

		if (Assert.isEmpty(id)) {
			entity = customerPersistence.isUniqueDomain(domain);
		} else {
			entity = customerPersistence.isUniqueDomain(id, domain);
		}

		if (entity != null) {
			return true;
		}
		return false;
	}
}
