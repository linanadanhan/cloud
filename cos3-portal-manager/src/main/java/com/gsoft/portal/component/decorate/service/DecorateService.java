package com.gsoft.portal.component.decorate.service;

import java.util.List;
import com.gsoft.cos3.dto.PageDto;
import com.gsoft.portal.component.decorate.dto.DecorateDto;

/**
 * 修饰器管理Service接口
 * @author SN
 *
 */
public interface DecorateService {

	/**
	 * 分页查询修饰器信息
	 * @param page
	 * @param size
	 * @param sortProp
	 * @param order
	 * @return
	 */
	PageDto queryDecorateDataTable(String search, Integer page, Integer size, String sortProp, String order);

	/**
	 * 根据主键获取某一笔修饰器信息
	 * @param id
	 * @return
	 */
	DecorateDto getDecorateInfoById(Long id);

	/**
	 * 判断修饰器代码是否已存在
	 * @param id
	 * @param code
	 * @return
	 */
	Boolean isExitDecorateCode(Long id, String code, String projectCode);

	/**
	 * 保存修饰器信息
	 * @param DecorateDto
	 * @return
	 */
	DecorateDto saveDecorate(DecorateDto DecorateDto);

	/**
	 * 删除修饰器信息
	 * @param id
	 * @param code
	 * @return
	 */
	void delDecorate(Long id, String code);

	/**
	 * 获取所有修饰器信息
	 * @return
	 */
	List<DecorateDto> getDecorateList();

	/**
	 * 根据code获取修饰器信息
	 * @param decorateCode
	 * @return
	 */
	DecorateDto getDecorateInfoByCode(String decorateCode);

}
