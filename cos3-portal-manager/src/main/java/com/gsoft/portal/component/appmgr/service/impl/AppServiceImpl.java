package com.gsoft.portal.component.appmgr.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.json.JSONException;
import org.springframework.stereotype.Service;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.table.Constant;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.cos3.util.StringUtils;
import com.gsoft.portal.component.appmgr.dto.AppDto;
import com.gsoft.portal.component.appmgr.entity.AppEntity;
import com.gsoft.portal.component.appmgr.persistence.AppPersistence;
import com.gsoft.portal.component.appmgr.service.AppService;
import com.gsoft.portal.component.appreltemp.dto.AppRelPageTempDto;
import com.gsoft.portal.component.appreltemp.service.AppRelPageTempService;

/**
 * 应用管理Service实现类
 * 
 * @author SN
 *
 */
@Service
public class AppServiceImpl implements AppService {

	@Resource
	BaseDao baseDao;

	@Resource
	AppPersistence appPersistence;
	
	@Resource
	AppRelPageTempService appRelPageTempService;

	@Override
	public PageDto getAppList(String search, Integer page, Integer size, String sortProp, String order) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT c.* FROM cos_app_info c where 1=1 ");
		Map<String, Object> params = new HashMap<String, Object>();

		if (Assert.isNotEmpty(search)) {
			sb.append(" AND (c.c_name like ${search} or c.c_desc like ${search} )");
			params.put("search", "%" + search + "%");
		}
		
		sb.append(" ORDER BY c.c_id DESC ");
		PageDto pageDto = baseDao.query(page, size, sb.toString(), params);
		return pageDto;
	}

	@Override
	public void updateAppStatus(String ids, String status) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("C_STATUS", status);
		baseDao.modify("cos_app_info", Constant.COLUMN_NAME_ID,
				StringUtils.splitAndStrip(MathUtils.stringObj(ids), ","), map);
	}

	@Override
	public AppDto saveAppInfo(AppDto appDto) {
		AppEntity entity = BeanUtils.convert(appDto, AppEntity.class);
		AppEntity reEntity = appPersistence.save(entity);
		return BeanUtils.convert(reEntity, AppDto.class);
	}

	@Override
	@Transactional
	public ReturnDto delApp(Long id) throws JSONException {
		AppEntity entity = appPersistence.findOne(id);
		appPersistence.delete(id);
		// 删除应用关联页面模版数据
		baseDao.update("DELETE FROM cos_app_rel_template WHERE c_app_code = ?", entity.getCode());
		return new ReturnDto("删除成功!");
	}

	@Override
	public AppDto getAppInfoById(Long id) {
		return BeanUtils.convert(appPersistence.getOne(id), AppDto.class);
	}

	@Override
	public ReturnDto isUniquAppCode(Long id, String code) {
		AppEntity entity = null;

		if (Assert.isEmpty(id)) {
			entity = appPersistence.findByCode(code);
		} else {
			entity = appPersistence.findByCode(code, id);
		}

		if (entity != null) {
			return new ReturnDto(true);
		}

		return new ReturnDto(false);
	}

	@Override
	public AppDto copyAppInfo(AppDto appDto) {
		
		// 根据主键ID查询原应用信息及关联的模版数据
		AppEntity oEntity = appPersistence.findOne(appDto.getId());
		
		List<AppRelPageTempDto> relPageTempList = appRelPageTempService.getRelPageTempList(oEntity.getCode());
		
		if (!Assert.isEmpty(relPageTempList) && relPageTempList.size() > 0) {
			for (AppRelPageTempDto appRelPageTempDto : relPageTempList) {
				appRelPageTempDto.setAppCode(appDto.getCode());
			}
			appRelPageTempService.saveAppRelPageTemp(relPageTempList);
		}
		
		appDto.setId(null);
		appDto.setCode(appDto.getCode());
		appDto.setName(appDto.getName());
		
		AppEntity reEntity = appPersistence.save(BeanUtils.convert(appDto, AppEntity.class));
		return BeanUtils.convert(reEntity, AppDto.class);
	}

	@Override
	public AppDto getAppInfoByCode(String code) {
		return BeanUtils.convert(appPersistence.findByCode(code), AppDto.class);
	}
}
