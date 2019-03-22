package com.gsoft.portal.webview.widget.service;

import java.util.List;

import org.json.JSONException;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.portal.webview.widget.dto.WidgetDto;

/**
 * widget管理Service接口
 * @author SN
 *
 */
public interface WidgetService {

	/**
	 * 分页查询widget信息
	 * @param page
	 * @param size
	 * @param sortProp
	 * @param order
	 * @return
	 */
	PageDto queryWidgetDataTable(String search, Integer page, Integer size, String sortProp, String order);

	/**
	 * 根据主键获取某一笔widget信息
	 * @param id
	 * @return
	 */
	WidgetDto getWidgetInfoById(Long id);

	/**
	 * 判断widget代码是否已存在
	 * @param id
	 * @param code
	 * @return
	 */
	Boolean isExitWidgetCode(Long id, String code, String projectCode);

	/**
	 * 保存widget信息
	 * @param widgetDto
	 * @return
	 */
	WidgetDto saveWidget(WidgetDto widgetDto);

	/**
	 * 删除widget信息
	 * @param id
	 * @param code
	 * @return
	 */
	void delWidget(Long id, String code);

	/**
	 * 查询所有widget集合
	 * @return
	 */
	List<WidgetDto> getWidgetList();

	/**
	 * 根据code获取widget信息
	 * @param widgetCode
	 * @return
	 */
	WidgetDto getWidgetInfoByCode(String widgetCode);

	/**
	 * 获取所有分类widget信息
	 * @return
	 * @throws JSONException 
	 */
	String getCatlogWidgetList() throws JSONException;
	
	/**
	 * 修改widget code 信息
	 */
	void udpWidgetCode();

	/**
	 * 获取widget分类树
	 * @param model
	 * @return
	 * @throws JSONException 
	 */
	ReturnDto getCategoryWidgetTree(String model) throws JSONException;

	/**
	 * 获取对应分类下的所有widget列表数据
	 * @param model
	 * @param category
	 * @param personnelId
	 * @return
	 */
	ReturnDto getWidgetListByCategory(String model, String category, Long personnelId);
}
