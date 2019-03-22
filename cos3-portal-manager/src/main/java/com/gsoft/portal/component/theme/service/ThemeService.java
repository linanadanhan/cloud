package com.gsoft.portal.component.theme.service;

import java.util.List;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.portal.component.theme.dto.ThemeDto;

/**
 * 主题管理Service接口
 * @author SN
 *
 */
public interface ThemeService {

	/**
	 * 分页查询主题信息
	 * @param page
	 * @param size
	 * @param sortProp
	 * @param order
	 * @return
	 */
	PageDto queryThemeDataTable(String search, Integer page, Integer size, String sortProp, String order);

	/**
	 * 根据主键获取某一笔主题信息
	 * @param id
	 * @return
	 */
	ThemeDto getThemeInfoById(Long id);
	
	/**
	 * 根据主题代码获取主题信息
	 * @param themeCode
	 * @return
	 */
	ThemeDto getThemeInfoByCode(String themeCode);

	/**
	 * 判断主题代码是否已存在
	 * @param id
	 * @param code
	 * @return
	 */
	Boolean isExitThemeCode(Long id, String code, String projectCode);

	/**
	 * 保存主题信息
	 * @param themeDto
	 * @return
	 */
	ThemeDto saveTheme(ThemeDto themeDto);

	/**
	 * 删除主题信息
	 * @param id
	 * @param code
	 * @return
	 */
	void delTheme(Long id, String code);

	/**
	 * 获取所有主题信息
	 * @return
	 */
	List<ThemeDto> getThemeList(String isOpen, String siteCode);

}
