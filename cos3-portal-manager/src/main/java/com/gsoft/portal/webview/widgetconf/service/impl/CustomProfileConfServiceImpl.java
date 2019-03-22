package com.gsoft.portal.webview.widgetconf.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.portal.webview.widgetconf.dto.CustomProfileConfDto;
import com.gsoft.portal.webview.widgetconf.entity.CustomProfileConfEntity;
import com.gsoft.portal.webview.widgetconf.persistence.CustomProfileConfPersistence;
import com.gsoft.portal.webview.widgetconf.service.CustomProfileConfService;

/**
 * 个性化偏好配置管理Service实现类
 * 
 * @author SN
 *
 */
@Service
public class CustomProfileConfServiceImpl implements CustomProfileConfService {
	
	@Resource
	CustomProfileConfPersistence customProfileConfPersistence;
	
	@Resource
	BaseDao baseDao;

	@Override
	public CustomProfileConfDto getCustomProfileConfInfo(String widgetId, long personnelId) {
		CustomProfileConfEntity entity = customProfileConfPersistence.findCustomProfileConfInfo(widgetId, personnelId);
		return BeanUtils.convert(entity, CustomProfileConfDto.class);
	}

	@Override
	@Transactional
	public CustomProfileConfDto saveCustomProfileConf(CustomProfileConfDto customProfileConfDto, String delWidgetIds) {
		CustomProfileConfEntity entity = BeanUtils.convert(customProfileConfDto, CustomProfileConfEntity.class);
		CustomProfileConfEntity reEntity = customProfileConfPersistence.save(entity);
		
		if (!Assert.isEmpty(delWidgetIds)) {
			String[] oldArr = delWidgetIds.split(",");
			for (String widgetId : oldArr) {
				baseDao.update("DELETE FROM cos_custom_profile WHERE c_widget_uu_id = ? and c_user_id = ?", widgetId,customProfileConfDto.getUserId());
			}
		}
		return BeanUtils.convert(reEntity, CustomProfileConfDto.class);
	}

	@Override
	public JSONObject getCusConfByPageUuId(String pageUuId, Long personnelId) throws JSONException {
		JSONObject jo = null;
		List<CustomProfileConfEntity> entityList = customProfileConfPersistence.getCusConfByPageUuId(pageUuId, personnelId);
		if (!Assert.isEmpty(entityList) && entityList.size() > 0) {
			JSONObject paramJo = null;
			jo = new JSONObject();
			for (CustomProfileConfEntity entity : entityList) {
				String json = entity.getJson();
				paramJo = new JSONObject(json);
				jo.put(entity.getWidgetUuId(), paramJo);
			}
		}
		return jo;
	}
}
