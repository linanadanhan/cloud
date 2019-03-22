package com.gsoft.portal.component.theme.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.portal.common.constans.ParameterConstant;
import com.gsoft.portal.component.theme.dto.ThemeDto;
import com.gsoft.portal.component.theme.entity.ThemeEntity;
import com.gsoft.portal.component.theme.persistence.ThemePersistence;
import com.gsoft.portal.component.theme.service.ThemeService;
import com.gsoft.portal.system.basicdata.service.ParameterService;
import com.gsoft.portal.webview.site.service.SiteService;

/**
 * 主题管理Service实现类
 * 
 * @author SN
 *
 */
@Service
public class ThemeServiceImpl implements ThemeService {
	
	@Resource
	ThemePersistence themePersistence;

	@Resource
	BaseDao baseDao;
	
	@Resource
	ParameterService parameterService;
	
	@Autowired
	SiteService siteService;

	@Override
	public PageDto queryThemeDataTable(String search, Integer page, Integer size, String sortProp, String order) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM cos_portal_theme where 1=1 ");

		Map<String, Object> params = new HashMap<String, Object>();

		if (Assert.isNotEmpty(search)) {
			sb.append(" AND (c_code like ${search} or c_name like ${search}) ");
			params.put("search", "%" + search + "%");
		}
		sb.append(" ORDER BY c_id DESC ");

		PageDto pageDto = baseDao.query(page, size, sb.toString(), params);
		return pageDto;
	}

	@Override
	public ThemeDto getThemeInfoById(Long id) {
		ThemeEntity entity = themePersistence.findOne(id);
		ThemeDto dto = BeanUtils.convert(entity, ThemeDto.class);
		return dto;
	}

	@Override
	public Boolean isExitThemeCode(Long id, String code, String projectCode) {
		
		ThemeEntity entity = null;
		
		if (Assert.isEmpty(id)) {
			entity = themePersistence.findByCode(code);
		}else {
			entity = themePersistence.findByCode(code,id);
		}
		
		if (entity != null) {
			return true;
		}

		return false;
	}

	@Override
	public ThemeDto saveTheme(ThemeDto themeDto) {
		ThemeEntity entity = BeanUtils.convert(themeDto, ThemeEntity.class);
		ThemeEntity reEntity = themePersistence.save(entity);
		return BeanUtils.convert(reEntity, ThemeDto.class);
	}

	@Override
	@Transactional
	public void delTheme(Long id, String code) {
		
		ThemeEntity entity = themePersistence.findOne(id);
		
		baseDao.delete("cos_portal_theme", "c_id", id);
		//删除后站点 页面 默认关联为默认主题
		baseDao.update("UPDATE cos_portal_site SET c_public_theme = 'default',c_private_theme = 'default' WHERE c_public_theme = ? or c_private_theme = ?", code,code);
		
		//删除成功成功后，移除服务器上对应目录包文件
		if (Assert.isNotEmpty(entity.getIsImp()) && "1".equals(entity.getIsImp())) {
			String destDir = parameterService.getParmValueByKey(ParameterConstant.PORTAL_MODULES_PATH[0], ParameterConstant.PORTAL_MODULES_PATH[1]);
			
			if (!destDir.endsWith("/")) {
				destDir = destDir + "/";
			}
			
			destDir = destDir + entity.getProjectCode() + "/themes/"+ code + "/";
			File toFile = new File(destDir);
			FileUtils.deleteQuietly(toFile);
		}
	}

	@Override
	public List<ThemeDto> getThemeList(String isOpen, String siteCode) {
		
		List<ThemeEntity> entityList = null;
		
		if (Assert.isEmpty(isOpen)) {
			if (Assert.isEmpty(siteCode)) {
				entityList = themePersistence.getThemeList();
			}else {
				entityList = siteService.getProfileThemeList(siteCode, isOpen);
			}
		}else {
			if (Assert.isEmpty(siteCode)) {
				entityList = themePersistence.getThemeList(isOpen);
			}else {
				entityList = siteService.getProfileThemeList(siteCode, isOpen);
			}
		}
		
		return BeanUtils.convert(entityList, ThemeDto.class);
	}

	@Override
	public ThemeDto getThemeInfoByCode(String themeCode) {
		ThemeEntity entity = themePersistence.findByCode(themeCode);
		return BeanUtils.convert(entity, ThemeDto.class);
	}

}
