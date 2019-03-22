package com.gsoft.portal.webview.widgetconf.service.impl;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.portal.webview.widgetconf.dto.CustomWidgetConfDto;
import com.gsoft.portal.webview.widgetconf.entity.CustomWidgetConfEntity;
import com.gsoft.portal.webview.widgetconf.persistence.CustomWidgetConfPersistence;
import com.gsoft.portal.webview.widgetconf.service.CustomWidgetConfService;

/**
 * 用户自定义widget管理Service实现类
 * 
 * @author SN
 *
 */
@Service
public class CustomWidgetConfServiceImpl implements CustomWidgetConfService {
	
	@Resource
	CustomWidgetConfPersistence customWidgetConfPersistence;
	
	@Resource
	BaseDao baseDao;

	@Override
	@Transactional
	public CustomWidgetConfDto saveWidgetInstance(CustomWidgetConfDto customWidgetConfDto, List<String> widgetIds) {
		if (Assert.isEmpty(customWidgetConfDto.getId()) && Assert.isEmpty(customWidgetConfDto.getUuId())) {
			customWidgetConfDto.setUuId(UUID.randomUUID().toString().replace("-", ""));
		}
		CustomWidgetConfEntity entity = BeanUtils.convert(customWidgetConfDto, CustomWidgetConfEntity.class);
		CustomWidgetConfEntity reEntity = customWidgetConfPersistence.save(entity);
		
		if (widgetIds.size() > 0) {
			for (String widgetId : widgetIds) {
				baseDao.update("DELETE FROM cos_custom_profile WHERE c_widget_uu_id = ? and c_user_id = ?", widgetId,customWidgetConfDto.getUserId());
				baseDao.update("DELETE FROM cos_sys_profile WHERE c_widget_uu_id = ? ", widgetId);
			}
		}
		return BeanUtils.convert(reEntity, CustomWidgetConfDto.class);
	}

	@Override
	public CustomWidgetConfDto getCustomWidgetConfInfo(Long personnelId, String pageUuId) {
		CustomWidgetConfEntity entity = customWidgetConfPersistence.getWidgetJson(pageUuId,personnelId);
		return BeanUtils.convert(entity, CustomWidgetConfDto.class);
	}
}
