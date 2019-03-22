package com.gsoft.portal.webview.badge.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.portal.webview.badge.dto.PageBadgeDto;
import com.gsoft.portal.webview.badge.entity.PageBadgeEntity;
import com.gsoft.portal.webview.badge.persistence.PageBadgePersistence;
import com.gsoft.portal.webview.badge.service.PageBadgeService;

/**
 * Badge Service实现类
 * 
 * @author SN
 *
 */
@Service
public class PageBadgeServiceImpl implements PageBadgeService {
	
	@Resource
	PageBadgePersistence pageBadgePersistence;

	@Override
	public PageBadgeDto getPageBadgeInfo(String pageUuId, String widgetUuId) {
		PageBadgeEntity entity = pageBadgePersistence.getPageBadgeInfo(pageUuId, widgetUuId);
		return BeanUtils.convert(entity, PageBadgeDto.class);
	}

	@Override
	public PageBadgeEntity savePageBadgeInfo(PageBadgeDto pageBadgeDto) {
		return pageBadgePersistence.save(BeanUtils.convert(pageBadgeDto, PageBadgeEntity.class));
	}

	@Override
	public Set<String> getBadgeNames(List<String> pageUuIdList) {
		List<String> badgetList = pageBadgePersistence.getBadgeNamesByPageUuId(pageUuIdList);
		Set<String> rtnList = new HashSet<String>();
		if (!Assert.isEmpty(badgetList) && badgetList.size() > 0) {
			for (String badgetName : badgetList) {
				rtnList.add(badgetName);
			}
		}
		return rtnList;
	}
}
