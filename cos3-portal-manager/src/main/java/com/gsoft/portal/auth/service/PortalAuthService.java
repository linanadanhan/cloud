package com.gsoft.portal.auth.service;

import java.util.List;
import java.util.Map;

import com.gsoft.portal.auth.dto.PortalAuthDto;

/**
 * 门户授权Service接口
 * @author SN
 *
 */
public interface PortalAuthService {

	/**
	 * 查询门户未授权的用户信息
	 * @param ywId
	 * @param grantType
	 * @param ywType
	 * @return
	 */
	List<Map<String, Object>> getHasNoAuthPerson(Long ywId, String grantType, String ywType, Long personnelId);

	/**
	 * 查询门户已授权的用户信息
	 * @param ywId
	 * @param grantType
	 * @param ywType
	 * @return
	 */
	List<Map<String, Object>> getHasAuthPerson(Long ywId, String grantType, String ywType, Long personnelId);

	/**
	 * 保存门户业务授权用户信息
	 * @param list
	 */
	void savePortalAuthUser(List<PortalAuthDto> list);

	/**
	 * 查询门户未授权角色
	 * @param ywId
	 * @param grantType
	 * @param ywType
	 * @param roleCatalog
	 * @return
	 */
	List<Map<String, Object>> getPortalHasNoAuthRole(Long ywId, String grantType, String ywType, String roleCatalog);

	/**
	 * 查询门户已授权角色
	 * @param ywId
	 * @param grantType
	 * @param ywType
	 * @param roleCatalog
	 * @return
	 */
	List<Map<String, Object>> getPortalHasAuthRole(Long ywId, String grantType, String ywType, String roleCatalog);

	/**
	 * 保存门户角色授权信息
	 * @param list
	 */
	void savePortalAuthRole(List<PortalAuthDto> list);

	/**
	 * 查询已授权站点信息
	 * @param grantId
	 * @param ywType
	 * @return
	 */
	String getAuthSiteInfo(Long grantId, String grantType);

	/**
	 * 保存站点授权信息
	 * @param grantId
	 * @param grantType
	 * @param sites
	 */
	void saveSiteAuth(Long grantId, String grantType, String sites);

	/**
	 * 查询已授权站点页面信息
	 * @param grantId
	 * @param grantType
	 * @return
	 */
	String getAuthSitePageInfo(Long grantId, String grantType);

	/**
	 * 保存站点页面授权信息
	 * @param grantId
	 * @param grantType
	 * @param ids
	 */
	void saveSitePageAuth(Long grantId, String grantType, String ids, String siteCode);
	
	/**
	 * 校验是否有权限
	 * @param expression
	 * @param personnelId
	 * @return
	 */
	boolean checkPermission(String expression, long personnelId);

}
