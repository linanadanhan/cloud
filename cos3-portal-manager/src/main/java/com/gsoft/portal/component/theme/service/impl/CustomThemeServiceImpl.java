package com.gsoft.portal.component.theme.service.impl;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.portal.component.theme.dto.CustomThemeDto;
import com.gsoft.portal.component.theme.entity.CustomThemeEntity;
import com.gsoft.portal.component.theme.persistence.CustomThemePersistence;
import com.gsoft.portal.component.theme.service.CustomThemeService;

/**
 * 用户自定义主题管理Service实现类
 * 
 * @author SN
 *
 */
@Service
public class CustomThemeServiceImpl implements CustomThemeService {

	@Resource
	BaseDao baseDao;
	
	@Resource
	CustomThemePersistence customThemePersistence;

	@Override
	public CustomThemeDto saveCustomTheme(CustomThemeDto customThemeDto) {
		
		CustomThemeEntity customThemeEntity = customThemePersistence.existsCustomTheme(customThemeDto.getUserId(),customThemeDto.getSiteCode());
		
		if (Assert.isEmpty(customThemeEntity)) {
			customThemeDto.setCreateTime(new Date());
			customThemeEntity = BeanUtils.convert(customThemeDto, CustomThemeEntity.class);
		} else {
			customThemeEntity.setUpdateTime(new Date());
			customThemeEntity.setThemeCode(customThemeDto.getThemeCode());
		}
		CustomThemeEntity entity = customThemePersistence.save(customThemeEntity);
		return BeanUtils.convert(entity, CustomThemeDto.class);
	}

	@Override
	public Map<String, Object> getCustomThemeInfo(String siteCode, Long personnelId) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ct.*,t.c_is_system FROM cos_custom_theme ct ");
		sb.append("LEFT JOIN cos_portal_theme t on ct.c_theme_code = t.c_code ");
		sb.append("WHERE ct.c_site_code = ? AND ct.c_user_id = ? ");
		
		return baseDao.load(sb.toString(), siteCode,personnelId);
	}

}
