package com.gsoft.portal.webview.badge.service;

import java.util.List;
import java.util.Set;

import com.gsoft.portal.webview.badge.dto.PageBadgeDto;
import com.gsoft.portal.webview.badge.entity.PageBadgeEntity;

/**
 * Badge Service接口
 * @author SN
 *
 */
public interface PageBadgeService {
	
	/**
	 * 根据页面ID和badge获取页面badge信息
	 * @param pageUuId
	 * @param widgetUuId
	 * @return
	 */
	PageBadgeDto getPageBadgeInfo(String pageUuId, String widgetUuId);

	/**
	 * 保存页面badge信息
	 * @param pageBadgeDto
	 * @return
	 */
	PageBadgeEntity savePageBadgeInfo(PageBadgeDto pageBadgeDto);

	/**
	 * 根据页面ID获取页面上配置的widget实例的badges
	 * @param pageUuId
	 * @return
	 */
	Set<String> getBadgeNames(List<String> pageUuIdList);
}
