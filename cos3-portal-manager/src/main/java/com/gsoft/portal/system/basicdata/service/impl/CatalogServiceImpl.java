package com.gsoft.portal.system.basicdata.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.portal.system.basicdata.dto.CatalogDto;
import com.gsoft.portal.system.basicdata.entity.Catalog;
import com.gsoft.portal.system.basicdata.persistence.CatalogPersistence;
import com.gsoft.portal.system.basicdata.service.CatalogService;

/**
 * 分类科目Service实现类
 * @author SN
 *
 */
@Service
public class CatalogServiceImpl implements CatalogService {
	
	@Resource
	CatalogPersistence catalogPersistence;
	
	@Resource
	BaseDao baseDao;

	@Override
	public List<CatalogDto> getCatalogTree() {
		
		List<Catalog> entityList = catalogPersistence.getCatalogTree();
		
		return BeanUtils.convert(entityList, CatalogDto.class);
	}

	@Override
	public CatalogDto saveCatalog(CatalogDto catalogDto) {
		
		if (Assert.isEmpty(catalogDto.getId())) {//新增
			if (catalogDto.getParentId() == 0 || Assert.isEmpty(catalogDto.getParentId())) {// 添加根科目
				catalogDto.setIsLeaf(true);
				catalogDto.setRootName(catalogDto.getName() + "科目");
				catalogDto.setCascadeid("/");
			}else {
				Catalog pcata = catalogPersistence.findOne(catalogDto.getParentId());
				catalogDto.setCascadeid(pcata.getCascadeid() + pcata.getId() + "/");
				BeanUtils.copyPropertiesByNames(pcata, catalogDto,
						"rootkey, rootName");
				pcata.setIsLeaf(Boolean.FALSE);
				catalogDto.setIsLeaf(false);
			}
		}
		
		Catalog entity = BeanUtils.convert(catalogDto, Catalog.class);
		Catalog reEntity = catalogPersistence.save(entity);
		return BeanUtils.convert(reEntity, CatalogDto.class);
	}

	@Override
	public Boolean isExitRootKey(Long id, String rootkey) {
		Catalog entity = null;
		
		if (Assert.isEmpty(id) || id == 0) {
			entity = catalogPersistence.findByRootKey(rootkey);
		}else {
			//entity = catalogPersistence.findByRootKey(rootkey,id);
		}
		
		if (entity != null) {
			return true;
		}

		return false;
	}

	@Override
	public CatalogDto getCatalogInfoById(Long id) {
		
		Catalog catalog = catalogPersistence.findOne(id);
		return BeanUtils.convert(catalog, CatalogDto.class);
	}

	@Override
	public void delCatalog(List<Long> ids) {
		baseDao.delete("cos_basicdata_catalog", "c_id", ids.toArray());
	}

	@Override
	public List<CatalogDto> getCatalogTree(Long id) {
		
		List<Catalog> catalogList = catalogPersistence.getCatalogTree(id, id);
		
		return BeanUtils.convert(catalogList, CatalogDto.class);
	}

}
