package com.gsoft.portal.component.compmgr.service;

import java.util.List;

import org.json.JSONException;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.tree.TreeNode;
import com.gsoft.portal.component.compmgr.dto.BusinessComponentDto;

/**
 * 业务组件管理Service接口
 * @author SN
 *
 */
public interface BusinessComponentService {

	/**
	 * 分页查询业务组件列表数据
	 * @param search
	 * @param page
	 * @param size
	 * @param sortProp
	 * @param order
	 * @return
	 */
	PageDto getBusinessComponentList(String category, String search, Integer page, Integer size, String sortProp, String order);

	/**
	 * 更新业务组件状态
	 * @param ids
	 * @param status
	 */
	void updateComponentStatus(Long ids, String status);

	/**
	 * 保存业务组件信息
	 * @param businessComponentDto
	 * @return
	 */
	BusinessComponentDto saveBusinessCompInfo(BusinessComponentDto businessComponentDto);

	/**
	 * 删除业务组件信息
	 * @param id
	 * @throws JSONException 
	 */
	ReturnDto delBusinessComp(Long id) throws JSONException;

	/**
	 * 根据主键获取单笔业务组件信息
	 * @param id
	 * @return
	 */
	BusinessComponentDto getBusinessCompById(Long id);

	/**
	 * 查询所有启动并有权限的业务组件
	 * @param personnelId
	 * @return
	 */
	ReturnDto getAllBusinessCompList(Long personnelId);
	
	/**
	 * 获取业务部件级联信息
	 * @return
	 */
	List<TreeNode> getAllCascadeBusCopm();
}
