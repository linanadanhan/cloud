package com.gsoft.portal.component.impmgr.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.portal.component.impmgr.dto.ImportDto;

/**
 * 导入管理Service接口
 * @author SN
 *
 */
public interface ImportService {

	/**
	 * 分页查询导入记录信息
	 * @param search
	 * @param page
	 * @param size
	 * @param sortProp
	 * @param order
	 * @return
	 */
	PageDto queryImportData(String search, Integer page, Integer size, String sortProp, String order);

	/**
	 * 保存导入记录信息
	 * @param importDto
	 * @return
	 */
	ImportDto saveImportRecord(ImportDto importDto);

	/**
	 * 导入数据入库操作
	 * @param jsonObj
	 * @return
	 * @throws JSONException 
	 */
	boolean handleImportData(JSONObject jsonObj) throws JSONException;

}
