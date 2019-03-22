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
import com.gsoft.portal.system.perssionitem.service.PerssionItemService;

/**
 * 权限项业务实现类
 * 
 * @author chenxx
 *
 */
@Service
public class PerssionItemServiceImpl implements PerssionItemService {
	
	@Resource
	private BaseDao baseDao;
	
	@Resource
	SingleTableService singleTableService;

	@Override
	public PageDto queryPerssionItem(String search, Integer page, Integer size) {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		sb.append("select * from cos_power_item where 1=1 ");

		if (!Assert.isEmpty(search)) {
			sb.append(" and (C_NAME like ${search} or C_REMARK like ${search}) ");
			params.put("search", "%" + search + "%");
		}
		sb.append(" ORDER BY c_id DESC ");
		return baseDao.query(page, size, sb.toString(), params);
	}

	@Override
	public Map<String, Object> getPermissionItemById(long id) {
		
		//1.先查询权限项信息
		Map<String, Object> itemMap = singleTableService.get("COS_POWER_ITEM", "", id);
		
		//2.查询该权限项关联的权限项组
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("itemId", id);
		List<Map<String, Object>> groupList = baseDao.query("SELECT C_GROUPID FROM COS_POWER_ITEM_GROUP_SHIP WHERE C_ITEMID = ${itemId} ", params);
		String groupId = "";
		if (!Assert.isEmpty(groupList) && groupList.size() > 0) {
			for (Map<String, Object> tmpMap : groupList) {
				groupId += tmpMap.get("C_GROUPID") + ",";
			}
		}
		if (groupId.length() > 0) {
			groupId = groupId.substring(0, groupId.length() - 1);
		}
		itemMap.put("groupId", groupId);
		return itemMap;
	}

	@Override
	@Transactional
	public void savePermissionItem(Map<String, Object> map, String personnelId) {
		
		//1. 保存权限项信息
		Long itemId = singleTableService.save("COS_POWER_ITEM", map);
		
		//2. 先删除关联权限项
		baseDao.update("DELETE FROM COS_POWER_ITEM_GROUP_SHIP WHERE C_ITEMID = ? ", itemId);
		
		//3. 保存关联权限组信息
		if (!Assert.isEmpty(map.get("groupId"))) {
			String[] groupArr = MathUtils.stringObj(map.get("groupId")).split(",");
			List<Map<String, Object>> ItemRelList = new ArrayList<Map<String, Object>>();
			for (String groupId : groupArr) {
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
	public List<Map<String, Object>> getPermissionHasNoConnectRole(Long itemId, String type) {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("itemId", itemId);
		sb.append("SELECT r.c_id, r.c_code, r.c_name from cos_sys_role r WHERE r.c_id not IN ");
		sb.append("(select s.C_GRANTTARGETID from COS_POWER_GRANT_SHIP s where s.c_itemid = ${itemId} and s.c_type = 2) ");
		
		if (!Assert.isEmpty(type)) {
			sb.append(" and r.c_type = ${type} ");
			params.put("type", type);
		}
		return baseDao.query(sb.toString(), params);
	}

	@Override
	public List<Map<String, Object>> getPermissionHasConnectRole(Long itemId, String type) {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("itemId", itemId);
		sb.append("SELECT r.c_id, r.c_code, r.c_name from cos_sys_role r WHERE r.c_id IN ");
		sb.append("(select s.C_GRANTTARGETID from COS_POWER_GRANT_SHIP s where s.c_itemid = ${itemId} and s.c_type = 2) ");
		
		if (!Assert.isEmpty(type)) {
			sb.append(" and r.c_type = ${type} ");
			params.put("type", type);
		}
		return baseDao.query(sb.toString(), params);
	}

	@Override
	@Transactional
	public void savePermissionRelRole(Long itemId, String roles) {
		
		// 1. 先删除权限项关联的角色信息
		baseDao.update("DELETE FROM COS_POWER_GRANT_SHIP WHERE C_TYPE = 2 AND C_POWERTYPE = 1 AND C_ITEMID = ?", itemId);
		
		// 2. 批量新增权限项关联角色
		List<Map<String, Object>> relRoleList = new ArrayList<Map<String, Object>>();
		
		if (!Assert.isEmpty(roles)) {
			String[] roleArr = roles.split(",");
			for (String roleId : roleArr) {
				Map<String, Object> tmpMap = new HashMap<String, Object>();
				tmpMap.put("C_GRANTTARGETID", roleId);
				tmpMap.put("C_ITEMID", itemId);
				tmpMap.put("C_POWERTYPE", 1l);
				tmpMap.put("C_TYPE", 2l);
				relRoleList.add(tmpMap);
			}
			if (relRoleList.size() > 0) {
				baseDao.insert("COS_POWER_GRANT_SHIP", "C_ID", relRoleList);
			}
		}
	}

	@Override
	public List<Map<String, Object>> getPermissionHasNoConnectPerson(Long itemId) {
        
		Map<String, Object> params = new HashMap<String, Object>();
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT p.* FROM cos_sys_personnel p ");
        sb.append("WHERE p.c_deleted = 0 ");
        sb.append(" and p.c_id not in (select s.C_GRANTTARGETID from COS_POWER_GRANT_SHIP s where s.c_itemid = ${itemId} and s.c_type = 1) ");
        sb.append("and p.c_id not in (SELECT p.c_id FROM cos_sys_personnel p, cos_sys_role_personal rp, cos_sys_role r WHERE p.c_id = rp.c_personnel_id and rp.c_role_id = r.c_id ");
        sb.append("AND p.c_deleted = 0 AND EXISTS (select s.C_GRANTTARGETID from COS_POWER_GRANT_SHIP s where s.c_itemid = ${itemId} and s.c_type = 2 and s.C_GRANTTARGETID = r.c_id)");
        sb.append(")");
        params.put("itemId", itemId);

        List<Map<String, Object>> rList = baseDao.query(sb.toString(), params);
        return rList;
	}

	@Override
	public List<Map<String, Object>> getPermissionHasConnectPerson(Long itemId) {
        
		Map<String, Object> params = new HashMap<String, Object>();
        StringBuffer sb = new StringBuffer();
        
        sb.append("SELECT p.* FROM cos_sys_personnel p WHERE p.c_deleted = 0 ");
        sb.append(" and p.c_id in (select s.C_GRANTTARGETID from COS_POWER_GRANT_SHIP s where s.c_itemid = ${itemId} and s.c_type = 1) ");
        sb.append("UNION ");
        sb.append("SELECT p.* FROM cos_sys_personnel p, cos_sys_role_personal rp, cos_sys_role r WHERE p.c_id = rp.c_personnel_id and rp.c_role_id = r.c_id ");
        sb.append("AND p.c_deleted = 0 AND EXISTS (select s.C_GRANTTARGETID from COS_POWER_GRANT_SHIP s where s.c_itemid = ${itemId} and s.c_type = 2 and s.C_GRANTTARGETID = r.c_id)");
        
        params.put("itemId", itemId);

        List<Map<String, Object>> rList = baseDao.query(sb.toString(), params);
        return rList;
	}

	@Override
	@Transactional
	public void savePermissionRelPerson(Long itemId, String userIds) {
		// 1. 先删除权限项关联的角色信息
		baseDao.update("DELETE FROM COS_POWER_GRANT_SHIP WHERE C_TYPE = 1 AND C_POWERTYPE = 1 AND C_ITEMID = ?", itemId);
		
		// 2. 批量新增权限项关联角色
		List<Map<String, Object>> relRoleList = new ArrayList<Map<String, Object>>();
		
		if (!Assert.isEmpty(userIds)) {
			String[] urserArr = userIds.split(",");
			for (String userId : urserArr) {
				Map<String, Object> tmpMap = new HashMap<String, Object>();
				tmpMap.put("C_GRANTTARGETID", userId);
				tmpMap.put("C_ITEMID", itemId);
				tmpMap.put("C_POWERTYPE", 1l);
				tmpMap.put("C_TYPE", 1l);
				relRoleList.add(tmpMap);
			}
			if (relRoleList.size() > 0) {
				baseDao.insert("COS_POWER_GRANT_SHIP", "C_ID", relRoleList);
			}
		}
	}

	@Override
	public List<Map<String, Object>> queryAllPermissionItems(long personnelId) {
		String sql = "SELECT * FROM cos_power_item i WHERE i.c_id IN ( SELECT g.c_itemid FROM cos_power_grant_ship g WHERE (( g.c_type = 1 AND g.c_granttargetid = ? ) OR ( g.c_type = 2 AND g.c_granttargetid IN ( SELECT a.c_role_id FROM cos_sys_role_personal a WHERE a.c_personnel_id = ? ))))";
		return baseDao.query(sql, personnelId, personnelId);
	}
}
