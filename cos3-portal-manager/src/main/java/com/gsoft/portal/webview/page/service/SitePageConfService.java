package com.gsoft.portal.webview.page.service;

import org.json.JSONArray;
import org.json.JSONException;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.portal.webview.page.dto.SitePageConfDto;

/**
 * 页面配置管理Service接口
 * @author SN
 *
 */
public interface SitePageConfService {

	/**
	 * 分页查询页面配置信息
	 * @param search
	 * @param pageUuId
	 * @param page
	 * @param size
	 * @param sortProp
	 * @param order
	 * @return
	 */
	PageDto querySitePageConfInfo(String search, String pageUuId, Integer page, Integer size, String sortProp,
			String order);

	/**
	 * 根据主键ID获取页面配置信息
	 * @param id
	 * @return
	 */
	SitePageConfDto getPageConfInfoById(Long id);

	/**
	 * 保存页面配置信息
	 * @param sitePageConfDto
	 * @return
	 */
	SitePageConfDto saveSitePageConf(SitePageConfDto sitePageConfDto);

	/**
	 * 删除页面配置信息
	 * @param uuId
	 * @throws JSONException 
	 */
	void delSitePageConf(String uuId, String pageUuId) throws JSONException;

	/**
	 * 获取页面下所有配置的widget信息
	 * @param pageUuId
	 * @return
	 * @throws JSONException 
	 */
	JSONArray getPageWidgetJson(String pageUuId) throws JSONException;

	/**
	 * 根据uuid获取页面配置widget信息
	 * @param uuId
	 * @return
	 */
	SitePageConfDto getPageConfInfoByUuId(String uuId);

}
