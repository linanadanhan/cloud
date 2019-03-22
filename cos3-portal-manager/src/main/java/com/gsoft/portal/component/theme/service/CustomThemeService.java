package com.gsoft.portal.component.theme.service;

import java.util.Map;
import com.gsoft.portal.component.theme.dto.CustomThemeDto;

/**
 * 用户自定义主题Service接口
 * @author SN
 *
 */
public interface CustomThemeService {

	/**
	 * 保存用户自定义主题信息
	 * @param customThemeDto
	 * @return
	 */
	CustomThemeDto saveCustomTheme(CustomThemeDto customThemeDto);

	/**
	 * 查询用户自定义主题信息
	 * @param stringObj
	 * @param personnelId
	 * @return
	 */
	Map<String, Object> getCustomThemeInfo(String siteCode, Long personnelId);
}
