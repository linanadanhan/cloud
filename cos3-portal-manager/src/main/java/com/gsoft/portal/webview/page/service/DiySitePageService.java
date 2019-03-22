package com.gsoft.portal.webview.page.service;

import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.portal.webview.page.dto.DiySitePageDto;

/**
 * 个性化页面管理Service接口
 * @author SN
 *
 */
public interface DiySitePageService {

	/**
	 * 获取个性化页面树
	 * @param siteCode
	 * @param personnelId
	 * @return
	 */
	List<DiySitePageDto> getDiyPageTree(String siteCode, Long personnelId);

	/**
	 * 校验个性化页面path是否已存在
	 * @param personnelId
	 * @param id
	 * @param path
	 * @param cascade
	 * @param type
	 * @param siteCode
	 * @return
	 */
	boolean isExitPagePath(Long personnelId, Long id, String path, String cascade, String siteCode);

	/**
	 * 保存个性化站点页面信息
	 * @param diySitePageDto
	 * @return
	 * @throws JSONException 
	 */
	DiySitePageDto saveDiySitePage(DiySitePageDto diySitePageDto) throws JSONException;

	/**
	 * 根据主键获取个性化站点页面信息
	 * @param id
	 * @return
	 */
	DiySitePageDto getDiySitePageInfoById(Long id);

	/**
	 * 删除个性化站点页面信息
	 * @param ids
	 * @param parentId
	 * @param personnelId
	 */
	void delDiySitePage(List<Long> ids, Long parentId, Long personnelId);

	/**
	 * 拖动保存个性化站点页面数据
	 * @param personnelId
	 * @param siteCode
	 * @param draggingNode
	 * @param dataTree
	 * @param parentId
	 * @param isExist
	 * @return
	 */
	ReturnDto saveDiySitePageTree(Long personnelId, String siteCode, String draggingNode, String dataTree,
			Long parentId, boolean isExist);

	/**
	 * 恢复站点默认页面
	 * @param siteCode
	 * @param personnelId
	 */
	void resetDefSitePage(String siteCode, Long personnelId);

	/**
	 * 批量保存个性化站点页面信息
	 * @param item
	 */
	void batchSaveDiySitePage(List<Map<String, Object>> itemList);

	/**
	 * 根据路径查询个性化站点页面信息
	 * @param path
	 * @param cascade
	 * @param siteCode
	 * @param personnelId
	 * @return
	 */
	DiySitePageDto getDiySitePageInfoByPath(String path, String cascade, String siteCode, Long personnelId);

	/**
	 * 复制个性化站点页面
	 * @param diySitePageDto
	 * @return
	 */
	ReturnDto copyDiyPage(DiySitePageDto diySitePageDto);

	/**
	 * 添加系统站点页面信息
	 * @param personnelId
	 * @param siteCode
	 * @param currentNode
	 * @return
	 */
	ReturnDto addSysSitePage(Long personnelId, String siteCode, String currentNode, boolean isExist);

	/**
	 * 判断个性化页面是否已存在
	 * @param personnelId
	 * @param pageUuId
	 * @return
	 */
	DiySitePageDto getDiySitePageInfoByUuId(long personnelId, String pageUuId);

	void changeLayout(String pageUuId, String layoutCode, long personnelId);

}
