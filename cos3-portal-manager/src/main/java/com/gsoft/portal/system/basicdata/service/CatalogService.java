package com.gsoft.portal.system.basicdata.service;

import java.util.List;

import com.gsoft.portal.system.basicdata.dto.CatalogDto;

/**
 * 分类科目Service 接口类
 * @author SN
 *
 */
public interface CatalogService {

	/**
	 * 查询分类科目tree
	 * @return
	 */
	List<CatalogDto> getCatalogTree();

	/**
	 * 保存分类科目信息
	 * @param catalogDto
	 * @return
	 */
	CatalogDto saveCatalog(CatalogDto catalogDto);

	/**
	 * 判断分类科目标识是否已存在
	 * @param id
	 * @param rootkey
	 * @return
	 */
	Boolean isExitRootKey(Long id, String rootkey);

	/**
	 * 根据主键ID获取分类科目信息
	 * @param id
	 * @return
	 */
	CatalogDto getCatalogInfoById(Long id);

	/**
	 * 删除分类科目信息
	 * @param ids
	 */
	void delCatalog(List<Long> ids);

	/**
	 * @param id
	 * @return
	 */
	List<CatalogDto> getCatalogTree(Long id);
	
}
