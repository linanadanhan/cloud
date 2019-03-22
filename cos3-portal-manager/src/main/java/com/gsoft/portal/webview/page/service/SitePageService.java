package com.gsoft.portal.webview.page.service;

import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.portal.webview.page.dto.SitePageDto;

/**
 * 页面管理Service接口
 * @author SN
 *
 */
public interface SitePageService {

	/**
	 * 获取站点页面tree
	 * @param siteCode
	 * @return
	 */
	List<SitePageDto> getSitePageTree(String siteCode, String type);

	/**
	 * 根据主键ID获取页面信息
	 * @param id
	 * @return
	 */
	SitePageDto getSitePageInfoById(Long id);

	/**
	 * 保存站点页面信息
	 * @param sitePageDto
	 * @return
	 * @throws JSONException 
	 */
	SitePageDto saveSitePage(SitePageDto sitePageDto) throws JSONException;

	/**
	 * 删除站点页面
	 * @param ids
	 */
	void delSitePage(List<Long> ids,Long parentId);

	/**
	 * 判断页面path是否已存在
	 * @param id
	 * @param path
	 * @return
	 */
	Boolean isExitPagePath(Long id, String path, String cascade, String type, String siteCode);

	/**
	 * 根据path获取页面信息
	 * @param path
	 * @return
	 */
	Map<String, Object> getSitePageInfoByPath(Long personnelId, String path, String type, String siteCode);

	/**
	 * @param isLogin
	 * @param siteCode
	 * @param personnelId
	 * @param string
	 * @return
	 */
	Map<String, Object> getToPath(String siteCode, Long personnelId, String type, boolean isFolder, String vPath);

	/**
	 * 获取页面菜单信息
	 * @param site
	 * @param personnelId
	 * @return
	 */
	List<SitePageDto> getPages(String siteCode, Long personnelId);

	/**
	 * 重置页面
	 * @param pageUuId
	 * @param personnelId
	 */
	void resetPage(String pageUuId, String pageWidgets, String siteCode, Long personnelId);

	/**
	 * 切换布局
	 * @param pageUuId
	 * @param layoutCode
	 */
	void changeLayout(String pageUuId, String layoutCode);

	/**
	 * 拖动保存站点页面tree
	 * @param personnelId
	 * @param siteCode
	 * @param dataTree
	 * @return
	 * @throws JSONException 
	 */
	ReturnDto saveSitePageTree(Long personnelId, String siteCode, String draggingNode, String dataTree, Long parentId) throws JSONException;

	/**
	 * 复制页面信息
	 * @param sitePageDto
	 * @return
	 * @throws JSONException 
	 * @throws Exception 
	 */
	ReturnDto copyPage(SitePageDto sitePageDto) throws JSONException, Exception;

	/**
	 * 站点下该用户所有有权限的页面信息
	 * @param siteCode
	 * @param personnelId
	 * @return
	 */
	List<SitePageDto> getAuthSitePageTree(String siteCode, Long personnelId);

}
