package com.gsoft.portal.component.pagetemp.service;

import java.util.List;

import org.json.JSONException;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.portal.component.pagetemp.dto.PageTemplateConfDto;
import com.gsoft.portal.component.pagetemp.dto.PageTemplateDto;

/**
 * 页面模版管理Service接口
 * @author SN
 *
 */
public interface PageTemplateService {

	/**
	 * 分页查询页面模版列表数据
	 * @param search
	 * @param page
	 * @param size
	 * @param sortProp
	 * @param order
	 * @return
	 */
	PageDto getPageTempList(String search, Integer page, Integer size, String sortProp, String order);

	/**
	 * 保存页面模版信息
	 * @param appDto
	 * @return
	 * @throws Exception 
	 */
	PageTemplateDto savePageTempInfo(PageTemplateDto pageTemplateDto) throws Exception;

	/**
	 * 删除页面模版信息
	 * @param id
	 * @throws JSONException 
	 */
	ReturnDto delPageTemplate(Long id) throws JSONException;

	/**
	 * 根据主键获取单笔页面模版信息
	 * @param id
	 * @return
	 */
	PageTemplateDto getPageTempInfoById(Long id);

	/**
	 * 校验页面模版代码是否已存在
	 * @param id
	 * @param code
	 * @return
	 */
	ReturnDto isUniquPageTempCode(Long id, String code);

	/**
	 * 保存页面模版配置
	 * @param code
	 * @param json
	 */
	void savePageTempConf(String code, String json);

	/**
	 * 获取页面模版配置
	 * @param layout
	 * @param code
	 * @param pageUuId
	 * @return
	 * @throws JSONException 
	 */
	ReturnDto getPageTempConfInfo(String layout, String code, String pageUuId) throws JSONException;

	/**
	 * 根据页面模版code获取模版配置信息
	 * @param pageTempCode
	 * @return
	 */
	PageTemplateConfDto getPageTempConfInfo(String pageTempCode);

	/**
	 * 查询页面模版信息
	 * @param pageTempCode
	 * @return
	 */
	PageTemplateDto getPageTempInfo(String pageTempCode);

	/**
	 * 获取所有页面模版信息
	 * @return
	 */
	List<PageTemplateDto> getAllPageTempList();
	
	/**
	 * 批量保存模版配置信息
	 * @param nPageTemplateConfList
	 */
	void batchSave(List<PageTemplateConfDto> nPageTemplateConfList);

}
