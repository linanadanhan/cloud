package com.gsoft.portal.system.perssionitem.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.table.service.SingleTableService;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.system.perssionitem.service.PerssionGroupService;

/**
 * 权限组业务实现类
 * 
 * @author chenxx
 *
 */
@Service
public class PerssionGroupServiceImpl implements PerssionGroupService {
	
	@Resource
	private BaseDao baseDao;
	
	@Resource
	SingleTableService singleTableService;

	@Override
	public PageDto queryPerssionGroup(String search, Integer page, Integer size) {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		sb.append("select * from COS_POWER_GROUP where 1=1 ");

		if (!Assert.isEmpty(search)) {
			sb.append(" and (C_NAME like ${search} or C_REMARK like ${search}) ");
			params.put("search", "%" + search + "%");
		}
		sb.append(" ORDER BY c_id DESC ");
		return baseDao.query(page, size, sb.toString(), params);
	}

	@Override
	public List<Map<String, Object>> getHasNoAuthPermissionItem(Long groupId) {
		
		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		
		if (Assert.isEmpty(groupId)) {
			sb.append("SELECT * FROM COS_POWER_ITEM i ");
		}else {
			sb.append("SELECT * FROM COS_POWER_ITEM i WHERE NOT EXISTS ");
			sb.append("(SELECT * FROM COS_POWER_ITEM_GROUP_SHIP rel WHERE rel.C_GROUPID = ${groupId} AND rel.C_ITEMID = i.C_ID )");
			params.put("groupId", groupId);
		}
		return baseDao.query(sb.toString(), params);
	}

	@Override
	public List<Map<String, Object>> getHasAuthPermissionItem(Long groupId) {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		if (Assert.isEmpty(groupId)) {
			return null;
		}else {
			sb.append("SELECT * FROM COS_POWER_ITEM i WHERE EXISTS ");
			sb.append("(SELECT * FROM COS_POWER_ITEM_GROUP_SHIP rel WHERE rel.C_GROUPID = ${groupId} AND rel.C_ITEMID = i.C_ID )");
			params.put("groupId", groupId);
			return baseDao.query(sb.toString(), params);
		}
	}

	@Override
	@Transactional
	public void savePermissionGroup(Map<String, Object> map, String personnelId) {
		
		//先插入数据权限组信息表中
		Long groupId = singleTableService.save("COS_POWER_GROUP", map);
		
		// 先删除关联权限项
		baseDao.update("DELETE FROM COS_POWER_ITEM_GROUP_SHIP WHERE C_GROUPID = ? ", groupId);
		
		// 批量插入关联权限项
		if (!Assert.isEmpty(map.get("pItem"))) {
			String[] pItemArr = MathUtils.stringObj(map.get("pItem")).split(",");
			List<Map<String, Object>> ItemRelList = new ArrayList<Map<String, Object>>();
			for (String itemId : pItemArr) {
				Map<String, Object> tmpMap = new HashMap<String, Object>();
				tmpMap.put("C_GROUPID", groupId);
				tmpMap.put("C_ITEMID", itemId);
				ItemRelList.add(tmpMap);
			}
			if (ItemRelList.size() > 0) {
				baseDao.insert("COS_POWER_ITEM_GROUP_SHIP", "C_ID", ItemRelList);
			}
		}
	}

	@Override
	public List<Map<String, Object>> getAllPermissionGroup() {
		return baseDao.query("select * from COS_POWER_GROUP");
	}

	@Override
	@Transactional
	public void delPerssionGroup(Long id) {
		//1. 删除权限组信息
		baseDao.delete("COS_POWER_GROUP", "C_ID", id);
		
		//2. 删除权限组关联的权限项信息
		baseDao.update("DELETE FROM COS_POWER_ITEM_GROUP_SHIP WHERE C_GROUPID = ?", id);
	}
}
