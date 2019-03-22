package com.gsoft.portal.webview.page.service;

import java.util.List;
import java.util.Map;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.portal.webview.page.dto.SitePageHelpDto;
import com.gsoft.portal.webview.page.dto.SitePageHelpHideDto;

public interface SitePageHelpService {

	/**
	 * 根据id获取页面帮助信息
	 * @param id
	 * @return
	 */
	SitePageHelpDto getPageHelpInfoById(Long id);

	/**
	 * 保存或修改页面帮助信息
	 * @param sitePageHelpDto
	 * @return
	 */
	SitePageHelpDto saveSitePageHelp(SitePageHelpDto sitePageHelpDto);

	/**
	 * 删除指定id的页面帮助信息
	 * @param id
	 */
	void delSitePageHelp(Long id);

	/**
	 * 分页查询页面帮助信息
	 * @param pageUuId
	 * @param siteCode
	 * @param page
	 * @param size
	 * @param sortProp
	 * @param order
	 * @return
	 */
	PageDto querySitePageHelp(String pageUuId, String siteCode, String type, Integer page, Integer size, String sortProp,
			String order);

	/**
	 * 保存不在提示的人员和站点信息
	 * @param sitePageHelpHideDto
	 * @return
	 */
	SitePageHelpHideDto saveSitePageHelpHide(SitePageHelpHideDto sitePageHelpHideDto);

	/**
	 * 获取站点的所有帮助信息  1.判断是否提示   2.若提示则返回帮助信息  
	 * @param pageUuId
	 * @param siteCode
	 * @param type
	 * @param sortProp
	 * @param order
	 * @return
	 */
	List<Map<String, Object>> querySiteHelp(String siteCode, String owner);

	/**
	 * 获取站点中某页面的帮助信息
	 * @param siteCode
	 * @param pageUuId
	 * @return
	 */
	List<Map<String, Object>> queryPageHelp(String siteCode, String pageUuId);
	
	/**
	 * 根据站点  page  和type获取页面帮助信息
	 * @param siteCode
	 * @param pageUuId
	 * @param Type
	 * @return
	 */
	List<SitePageHelpDto> getPageHelpByParams(String siteCode, String pageUuId, String Type);

}
