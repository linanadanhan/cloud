package com.gsoft.portal.component.layout.service;

import java.util.Map;
import com.gsoft.portal.component.layout.dto.CustomLayoutDto;

/**
 * 用户自定义布局管理Service接口
 * @author SN
 *
 */
public interface CustomLayoutService {

	/**
	 * 保存用户自定义布局信息
	 * @param customLayoutDto
	 * @return
	 */
	CustomLayoutDto saveCustomLayout(CustomLayoutDto customLayoutDto);

	/**
	 * 查询用户自定义布局信息
	 * @param pageUuId
	 * @param personnelId
	 * @return
	 */
	Map<String, Object> getCustomLayoutInfo(String pageUuId, Long personnelId);

}
