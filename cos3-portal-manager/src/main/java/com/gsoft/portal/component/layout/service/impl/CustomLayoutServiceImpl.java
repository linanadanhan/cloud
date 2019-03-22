package com.gsoft.portal.component.layout.service.impl;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.portal.component.layout.dto.CustomLayoutDto;
import com.gsoft.portal.component.layout.entity.CustomLayoutEntity;
import com.gsoft.portal.component.layout.persistence.CustomLayoutPersistence;
import com.gsoft.portal.component.layout.service.CustomLayoutService;

/**
 * 用户自定义布局管理Service实现类
 * 
 * @author SN
 *
 */
@Service
public class CustomLayoutServiceImpl implements CustomLayoutService {
	
	@Resource
	CustomLayoutPersistence customLayoutPersistence;

	@Resource
	BaseDao baseDao;

	@Override
	public CustomLayoutDto saveCustomLayout(CustomLayoutDto customLayoutDto) {
		
		CustomLayoutEntity customLayoutEntity = customLayoutPersistence.existsCustomLayout(customLayoutDto.getUserId(),customLayoutDto.getPageUuId());
		
		if (Assert.isEmpty(customLayoutEntity)) {
			customLayoutDto.setCreateTime(new Date());
			customLayoutEntity = BeanUtils.convert(customLayoutDto, CustomLayoutEntity.class);
		} else {
			customLayoutEntity.setUpdateTime(new Date());
			customLayoutEntity.setLayoutCode(customLayoutDto.getLayoutCode());
		}
		CustomLayoutEntity entity = customLayoutPersistence.save(customLayoutEntity);
		return BeanUtils.convert(entity, CustomLayoutDto.class);
	}

	@Override
	public Map<String, Object> getCustomLayoutInfo(String pageUuId, Long personnelId) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT cl.*,l.c_is_system FROM cos_custom_layout cl ");
		sb.append("LEFT JOIN cos_portal_layout l ON cl.c_layout_code = l.c_code ");
		sb.append("WHERE cl.c_page_uu_id = ? AND cl.c_user_id = ? ");
		
		return baseDao.load(sb.toString(), pageUuId, personnelId);
	}

}
