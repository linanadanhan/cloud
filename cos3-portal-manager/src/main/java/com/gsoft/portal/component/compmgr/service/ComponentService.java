package com.gsoft.portal.component.compmgr.service;

import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.portal.component.compmgr.dto.ComponentDto;
import com.gsoft.portal.component.compmgr.dto.ComponentPackageDto;

/**
 * 部件管理Service接口
 * @author SN
 *
 */
public interface ComponentService {

	/**
	 * 分页查询部件信息列表
	 * @param search
	 * @param page
	 * @param size
	 * @param sortProp
	 * @param order
	 * @return
	 */
	PageDto queryComponentDataTable(String search, Integer page, Integer size, String sortProp, String order);

	/**
	 * 启用或停用
	 * @param id
	 * @param status
	 */
	void updateStatus(Long id, Boolean status);

	/**
	 * 卸载
	 * @param id
	 */
	ReturnDto uninstall(String code);

	/**
	 * 查询部件明细信息
	 * @param compType
	 * @param compCode
	 * @return
	 */
	List<Map<String, Object>> queryComponentDetailList(String compType, String compCode);

	/**
	 * 保存部件信息
	 * @param componentDto
	 * @return
	 */
	ComponentDto saveComponentInfo(ComponentDto componentDto);

	/**
	 * 根据部件代码查询部件信息
	 * @param stringObj
	 * @return
	 */
	ComponentDto getComponentByCode(String compCode);

	/**
	 * 查询所有启用了的系统widget
	 * @return
	 */
	ReturnDto getAllSysCompList();

	/**
	 * 保存部件包记录信息
	 * @param componentPackageDto
	 * @return
	 */
	ComponentPackageDto saveComponentPackageInfo(ComponentPackageDto componentPackageDto);

	/**
	 * 获取部件包基本信息
	 * @param search
	 * @param page
	 * @param size
	 * @param sortProp
	 * @param order
	 * @return
	 */
	PageDto queryComponentPackageDataTable(String search, Integer page, Integer size, String sortProp, String order);

	/**
	 * 校验部件包是否已存在
	 * @param compCode
	 * @return
	 */
	boolean isExistCompCode(String compCode);

	/**
	 * 分页查询本地部件包信息
	 * @param customer
	 * @param search
	 * @param page
	 * @param size
	 * @param sortProp
	 * @param order
	 * @return
	 * @throws JSONException 
	 * @throws Exception 
	 */
	PageDto getPartPackageList(String customer, String search, Integer page, Integer size, String sortProp, String order) throws Exception;

	/**
	 * 获取系统模组文件
	 * @return
	 * @throws JSONException 
	 */
	ReturnDto getModuleFiles() throws JSONException;
}
