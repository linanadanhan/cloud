package com.gsoft.portal.component.appreltemp.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.portal.component.appreltemp.dto.AppRelPageTempDto;
import com.gsoft.portal.component.appreltemp.entity.AppRelPageTempEntity;
import com.gsoft.portal.component.appreltemp.persistence.AppRelPageTempPersistence;
import com.gsoft.portal.component.appreltemp.service.AppRelPageTempService;

/**
 * 应用关联页面模版管理Service实现类
 * 
 * @author SN
 *
 */
@Service
public class AppRelPageTempServiceImpl implements AppRelPageTempService {

	@Resource
	BaseDao baseDao;
	
	@Resource
	AppRelPageTempPersistence appRelPageTempPersistence;

	@Override
	public List<Map<String, Object>> getNoSelectedPageTemp(String appCode) {
		return baseDao.query("SELECT t.* FROM cos_page_template t WHERE NOT EXISTS (SELECT r.* FROM cos_app_rel_template r WHERE r.c_page_temp_code = t.c_code AND r.c_app_code = ?)", appCode);
	}

	@Override
	public List<Map<String, Object>> getHasSelectedPageTemp(String appCode) {
		return baseDao.query("SELECT t.* FROM cos_page_template t WHERE EXISTS (SELECT r.* FROM cos_app_rel_template r WHERE r.c_page_temp_code = t.c_code AND r.c_app_code = ?)", appCode);
	}

	@Override
	@Transactional
	public void saveAppRelPageTemp(List<AppRelPageTempDto> list) {
		if (!Assert.isEmpty(list) && list.size() > 0) {
			
			// 先删除原业务数据
			baseDao.update("DELETE FROM cos_app_rel_template WHERE c_app_code = ?", list.get(0).getAppCode());
			
			// 批量新增
			if (!Assert.isEmpty(list.get(0).getPageTempCode())) {
				appRelPageTempPersistence.save(BeanUtils.convert(list, AppRelPageTempEntity.class));
			}
		}
	}

	@Override
	public List<AppRelPageTempDto> getRelPageTempList(String appCode) {
		List<AppRelPageTempEntity> entityList = appRelPageTempPersistence.getRelPageTempList(appCode);
		return BeanUtils.convert(entityList, AppRelPageTempDto.class);
	}
	
}
