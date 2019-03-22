package com.gsoft.portal.component.decorate.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.portal.common.constans.ParameterConstant;
import com.gsoft.portal.component.decorate.dto.DecorateDto;
import com.gsoft.portal.component.decorate.entity.DecorateEntity;
import com.gsoft.portal.component.decorate.persistence.DecoratePersistence;
import com.gsoft.portal.component.decorate.service.DecorateService;
import com.gsoft.portal.system.basicdata.service.ParameterService;

/**
 * 修饰器管理Service实现类
 * 
 * @author SN
 *
 */
@Service
public class DecorateServiceImpl implements DecorateService {
	
	@Resource
	DecoratePersistence decoratePersistence;

	@Resource
	BaseDao baseDao;
	
	@Resource
	ParameterService parameterService;

	@Override
	public PageDto queryDecorateDataTable(String search, Integer page, Integer size, String sortProp, String order) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM cos_portal_Decorate where 1=1 ");

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
	public DecorateDto getDecorateInfoById(Long id) {
		DecorateEntity entity = decoratePersistence.findOne(id);
		DecorateDto dto = BeanUtils.convert(entity, DecorateDto.class);
		return dto;
	}

	@Override
	public Boolean isExitDecorateCode(Long id, String code, String projectCode) {
		
		DecorateEntity entity = null;
		
		if (Assert.isEmpty(id)) {
			entity = decoratePersistence.findByCode(code);
		}else {
			entity = decoratePersistence.findByCode(code, id);
		}
		
		if (entity != null) {
			return true;
		}

		return false;
	}

	@Override
	public DecorateDto saveDecorate(DecorateDto DecorateDto) {
		DecorateEntity entity = BeanUtils.convert(DecorateDto, DecorateEntity.class);
		DecorateEntity reEntity = decoratePersistence.save(entity);
		return BeanUtils.convert(reEntity, DecorateDto.class);
	}

	@Override
	public void delDecorate(Long id, String code) {
		
		DecorateEntity entity = decoratePersistence.findOne(id);
		baseDao.delete("cos_portal_decorate", "c_id", id);
		
		//删除成功成功后，移除服务器上对应目录包文件
		if (Assert.isNotEmpty(entity.getIsImp()) && "1".equals(entity.getIsImp())) {
			String destDir = parameterService.getParmValueByKey(ParameterConstant.PORTAL_MODULES_PATH[0], ParameterConstant.PORTAL_MODULES_PATH[1]);
			
			if (!destDir.endsWith("/")) {
				destDir = destDir + "/";
			}
			
			destDir = destDir + entity.getProjectCode() + "/decorators/"+ code + "/";
			File toFile = new File(destDir);
			FileUtils.deleteQuietly(toFile);
		}
	}

	@Override
	public List<DecorateDto> getDecorateList() {
		
		List<DecorateEntity> entityList = decoratePersistence.getDecorateList();
		
		return BeanUtils.convert(entityList,DecorateDto.class);
	}

	@Override
	public DecorateDto getDecorateInfoByCode(String decorateCode) {
		DecorateEntity entity = decoratePersistence.findByCode(decorateCode);
		return BeanUtils.convert(entity, DecorateDto.class);
	}

}
