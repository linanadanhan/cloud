package com.gsoft.portal.component.layout.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.portal.common.constans.ParameterConstant;
import com.gsoft.portal.component.layout.dto.LayoutDto;
import com.gsoft.portal.component.layout.entity.LayoutEntity;
import com.gsoft.portal.component.layout.persistence.LayoutPersistence;
import com.gsoft.portal.component.layout.service.LayoutService;
import com.gsoft.portal.system.basicdata.service.ParameterService;

/**
 * 布局管理Service实现类
 * 
 * @author SN
 *
 */
@Service
public class LayoutServiceImpl implements LayoutService {
	
	@Resource
	LayoutPersistence layoutPersistence;

	@Resource
	BaseDao baseDao;
	
	@Resource
	ParameterService parameterService;

	@Override
	public PageDto queryLayoutDataTable(String search, Integer page, Integer size, String sortProp, String order) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM cos_portal_layout where 1=1 ");

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
	public LayoutDto getLayoutInfoById(Long id) {
		LayoutEntity entity = layoutPersistence.findOne(id);
		LayoutDto dto = BeanUtils.convert(entity, LayoutDto.class);
		return dto;
	}

	@Override
	public Boolean isExitLayoutCode(Long id, String code, String projectCode) {
		
		LayoutEntity entity = null;
		
		if (Assert.isEmpty(id)) {
			entity = layoutPersistence.findByCode(code);
		}else {
			entity = layoutPersistence.findByCode(code,id);
		}
		
		if (entity != null) {
			return true;
		}

		return false;
	}

	@Override
	public LayoutDto saveLayout(LayoutDto layoutDto) {
		LayoutEntity entity = BeanUtils.convert(layoutDto, LayoutEntity.class);
		LayoutEntity reEntity = layoutPersistence.save(entity);
		return BeanUtils.convert(reEntity, LayoutDto.class);
	}

	@Override
	@Transactional
	public void delLayout(Long id, String code) {

		LayoutEntity entity = layoutPersistence.findOne(id);
		baseDao.delete("cos_portal_layout", "c_id", id);
		//删除后 页面 widget 默认关联为默认布局
		baseDao.update("UPDATE cos_widget_instance SET c_layout_code = 'default' ,c_layout_position = '1' WHERE c_layout_code = ? ", code);
		baseDao.update("UPDATE cos_portal_page SET c_layout_code = 'default' WHERE c_layout_code = ? ", code);
		
		//删除成功成功后，移除服务器上对应目录包文件
		if (Assert.isNotEmpty(entity.getIsImp()) && "1".equals(entity.getIsImp())) {
			String destDir = parameterService.getParmValueByKey(ParameterConstant.PORTAL_MODULES_PATH[0], ParameterConstant.PORTAL_MODULES_PATH[1]);
			
			if (!destDir.endsWith("/")) {
				destDir = destDir + "/";
			}
			
			destDir = destDir + entity.getProjectCode() + "/layouts/"+ code + "/";
			File toFile = new File(destDir);
			FileUtils.deleteQuietly(toFile);
		}
	}

	@Override
	public List<LayoutDto> getLayoutList() {
		
		List<LayoutEntity> entityList = layoutPersistence.getLayoutList();
		
		return BeanUtils.convert(entityList,LayoutDto.class);
	}

	@Override
	public LayoutDto getLayoutInfoByCode(String layoutCode) {
		
		LayoutEntity entity = layoutPersistence.findByCode(layoutCode);
		
		return BeanUtils.convert(entity, LayoutDto.class);
	}

}
