package com.gsoft.portal.component.layout.service;

import java.util.List;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.portal.component.layout.dto.LayoutDto;

/**
 * 布局管理Service接口
 * @author SN
 *
 */
public interface LayoutService {

	/**
	 * 分页查询布局信息
	 * @param page
	 * @param size
	 * @param sortProp
	 * @param order
	 * @return
	 */
	PageDto queryLayoutDataTable(String search, Integer page, Integer size, String sortProp, String order);

	/**
	 * 根据主键获取某一笔布局信息
	 * @param id
	 * @return
	 */
	LayoutDto getLayoutInfoById(Long id);

	/**
	 * 判断布局代码是否已存在
	 * @param id
	 * @param code
	 * @return
	 */
	Boolean isExitLayoutCode(Long id, String code, String projectCode);

	/**
	 * 保存布局信息
	 * @param layoutDto
	 * @return
	 */
	LayoutDto saveLayout(LayoutDto layoutDto);

	/**
	 * 删除布局信息
	 * @param id
	 * @param code
	 * @return
	 */
	void delLayout(Long id, String code);

	/**
	 * 获取所有布局信息
	 * @return
	 */
	List<LayoutDto> getLayoutList();

	/**
	 * 根据code 获取布局信息
	 * @param layoutCode
	 * @return
	 */
	LayoutDto getLayoutInfoByCode(String layoutCode);

}
