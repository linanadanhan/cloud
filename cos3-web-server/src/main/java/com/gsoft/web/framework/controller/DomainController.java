package com.gsoft.web.framework.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;

/**
 * 域名处理
 * 
 * @author chenxx
 *
 */
@RestController
public class DomainController {

	@Autowired
	BaseDao baseDao;

	@RequestMapping(value = "/getSiteInfoByDomain", method = RequestMethod.GET)
	public ReturnDto getSiteInfoByDomain(HttpServletRequest request, @RequestParam String domain) {
		Map<String, Object> siteInfo = baseDao.load("SELECT c_custormer_code, c_site_code FROM cos_custormer_site_domain WHERE c_domain = ?", domain);
		if (!Assert.isEmpty(siteInfo)) {
			// 租户信息放入会话中
			request.getSession().setAttribute("customerCode", siteInfo.get("C_CUSTORMER_CODE"));
		}
		return new ReturnDto(siteInfo);
	}
}
