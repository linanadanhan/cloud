package com.gsoft.portal.auth.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.cos3.util.ExpressionUtils;
import com.gsoft.cos3.util.JsonMapper;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.auth.dto.PortalAuthDto;
import com.gsoft.portal.auth.entity.PortalAuthEntity;
import com.gsoft.portal.auth.persistence.PortalAuthPersistence;
import com.gsoft.portal.auth.service.PortalAuthService;
import com.gsoft.portal.system.personnel.dto.RoleDto;
import com.gsoft.portal.system.personnel.service.RoleService;
import com.gsoft.portal.system.perssionitem.service.PerssionItemService;
import com.gsoft.portal.webview.site.dto.SiteDto;
import com.gsoft.portal.webview.site.service.SiteService;

/**
 * 门户授权Service实现类
 * 
 * @author SN
 *
 */
@Service
public class PortalAuthServiceImpl implements PortalAuthService {
	
	/**
	 * 日志对象
	 */
	Logger logger = LoggerFactory.getLogger(PortalAuthServiceImpl.class);

	@Resource
	BaseDao baseDao;
	
	@Resource
	PortalAuthPersistence portalAuthPersistence;
	
	@Resource
	SiteService siteService;
	
	@Resource
	RoleService roleService;
	
	@Resource
	PerssionItemService perssionItemService;

	@Override
	public List<Map<String, Object>> getHasNoAuthPerson(Long ywId, String grantType, String ywType, Long personnelId) {
		
		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		
		sb.append("SELECT * FROM cos_sys_personnel where c_deleted = 0 and c_id != ${personnelId} ");
		params.put("personnelId", personnelId);
		
		return baseDao.query(sb.toString(), params);
	}

	@Override
	public List<Map<String, Object>> getHasAuthPerson(Long ywId, String grantType, String ywType, Long personnelId) {
		
		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		
		//业务类型区分
		if ("0".equals(ywType)) {//站点
			
			sb.append("SELECT p.* FROM cos_portal_permission pp, cos_sys_personnel p, cos_portal_site s WHERE pp.c_grant_id = p.c_id and p.c_id != ${personnelId} ");
			sb.append("AND pp.c_yw_id = s.c_id AND pp.c_yw_type = '0' AND pp.c_grant_type = '1' AND p.c_deleted = 0 and pp.c_yw_id = ${ywId} ");
			sb.append("UNION ");
			sb.append("SELECT p.* FROM cos_sys_personnel p, cos_sys_role_personal rp, cos_sys_role r WHERE p.c_id = rp.c_personnel_id and rp.c_role_id = r.c_id and p.c_id != ${personnelId} ");
			sb.append("AND p.c_deleted = 0 AND EXISTS (SELECT rr.c_id FROM cos_portal_permission pp, cos_sys_role rr, cos_portal_site s WHERE pp.c_grant_id = rr.c_id  ");
			sb.append("AND pp.c_yw_id = s.c_id AND pp.c_yw_type = '0' AND pp.c_grant_type = '0' AND rr.c_id = r.c_id and pp.c_yw_id = ${ywId} )");
			
		}else if ("1".equals(ywType)) {//页面
			
			sb.append("SELECT p.* FROM cos_portal_permission pp, cos_sys_personnel p, cos_portal_page s WHERE pp.c_grant_id = p.c_id and p.c_id != ${personnelId} ");
			sb.append("AND pp.c_yw_id = s.c_id AND pp.c_yw_type = '1' AND pp.c_grant_type = '1' AND p.c_deleted = 0 and pp.c_yw_id = ${ywId} ");
			sb.append("UNION ");
			sb.append("SELECT p.* FROM cos_sys_personnel p, cos_sys_role_personal rp, cos_sys_role r WHERE p.c_id = rp.c_personnel_id and rp.c_role_id = r.c_id and p.c_id != ${personnelId} ");
			sb.append("AND p.c_deleted = 0 AND EXISTS (SELECT rr.c_id FROM cos_portal_permission pp, cos_sys_role rr, cos_portal_page s WHERE pp.c_grant_id = rr.c_id  ");
			sb.append("AND pp.c_yw_id = s.c_id AND pp.c_yw_type = '1' AND pp.c_grant_type = '0' AND rr.c_id = r.c_id and pp.c_yw_id = ${ywId} )");
			
		}else if ("2".equals(ywType)) {//widget
			
			sb.append("SELECT p.* FROM cos_portal_permission pp, cos_sys_personnel p, cos_portal_yw_component s WHERE pp.c_grant_id = p.c_id ");
			sb.append("AND pp.c_yw_id = s.c_id AND pp.c_yw_type = '2' AND pp.c_grant_type = '1' AND p.c_deleted = 0 and pp.c_yw_id = ${ywId} ");
			sb.append("UNION ");
			sb.append("SELECT p.* FROM cos_sys_personnel p, cos_sys_role_personal rp, cos_sys_role r WHERE p.c_id = rp.c_personnel_id and rp.c_role_id = r.c_id ");
			sb.append("AND p.c_deleted = 0 AND EXISTS (SELECT rr.c_id FROM cos_portal_permission pp, cos_sys_role rr, cos_portal_yw_component s WHERE pp.c_grant_id = rr.c_id  ");
			sb.append("AND pp.c_yw_id = s.c_id AND pp.c_yw_type = '2' AND pp.c_grant_type = '0' AND rr.c_id = r.c_id and pp.c_yw_id = ${ywId})");
		}
		params.put("personnelId", personnelId);
		params.put("ywId", ywId);
		
		return baseDao.query(sb.toString(), params);
	}

	@Override
	@Transactional
	public void savePortalAuthUser(List<PortalAuthDto> list) {
		
        if (list != null && list.size() > 0) {
        	PortalAuthDto dto = list.get(0);
        	if(Assert.isEmpty(dto)){
        		return;
        	}
        	
        	//先删除原业务数据
        	StringBuffer sb = new StringBuffer();
        	sb.append("DELETE FROM cos_portal_permission WHERE c_yw_id = "+dto.getYwId()+" ");
        	sb.append(" and c_grant_type = '"+ dto.getGrantType() +"' ");
        	sb.append(" and c_yw_type = '"+dto.getYwType()+"' ");
        	
        	baseDao.update(sb.toString());
        	
        	//比对业务角色下是否存在新增的用户，若存在则删除原业务角色关系，新增用户级业务授权关系 TODO
        	String userId = "";
        	for (PortalAuthDto portalAuthDto : list) {
        		userId += portalAuthDto.getYwId() + ",";
        	}
        	
        	userId = userId.substring(0, userId.length() - 1);
        	
        	if ("0".equals(dto.getYwType())) {//站点
        		
        		sb = new StringBuffer();
        		
        		sb.append("SELECT r.c_id as c_role_id,p.* FROM cos_sys_personnel p, cos_sys_role_personal rp, cos_sys_role r WHERE p.c_id = rp.c_personnel_id and rp.c_role_id = r.c_id ");
        		sb.append("AND p.c_deleted = 0 AND EXISTS (SELECT rr.c_id FROM cos_portal_permission pp, cos_sys_role rr, cos_portal_site s WHERE pp.c_grant_id = rr.c_id  ");
        		sb.append("AND pp.c_yw_id = s.c_id AND pp.c_yw_type = '0' AND pp.c_grant_type = '0' AND rr.c_id = r.c_id and pp.c_yw_id = "+dto.getYwId()+") AND p.c_id NOT IN ("+userId+") ");
        		
        		List<Map<String, Object>> resList = baseDao.query(sb.toString());
        		
        		if (Assert.isNotEmpty(resList)) {
        			
        			for (Map<String, Object> tmpMap : resList) {
        				//删除业务角色授权信息
        				baseDao.update("DELETE FROM cos_portal_permission where c_yw_id = ? and c_grant_id = ? and c_grant_type = '0' and c_yw_type = '0' ", dto.getYwId(),tmpMap.get("C_ROLE_ID"));
        			}
        		}
        		
        	}else if ("1".equals(dto.getYwType())) {//页面
        		
        		sb = new StringBuffer();
        		
        		sb.append("SELECT r.c_id as c_role_id,p.* FROM cos_sys_personnel p, cos_sys_role_personal rp, cos_sys_role r WHERE p.c_id = rp.c_personnel_id and rp.c_role_id = r.c_id ");
        		sb.append("AND p.c_deleted = 0 AND EXISTS (SELECT rr.c_id FROM cos_portal_permission pp, cos_sys_role rr, cos_portal_page s WHERE pp.c_grant_id = rr.c_id  ");
        		sb.append("AND pp.c_yw_id = s.c_id AND pp.c_yw_type = '1' AND pp.c_grant_type = '0' AND rr.c_id = r.c_id and pp.c_yw_id = "+dto.getYwId()+") AND p.c_id NOT IN ("+userId+") ");
        		
        		List<Map<String, Object>> resList = baseDao.query(sb.toString());
        		
        		if (Assert.isNotEmpty(resList)) {
        			
        			for (Map<String, Object> tmpMap : resList) {
        				//删除业务角色授权信息
        				baseDao.update("DELETE FROM cos_portal_permission where c_yw_id = ? and c_grant_id = ? and c_grant_type = '0' and c_yw_type = '1' ", dto.getYwId(),tmpMap.get("C_ROLE_ID"));
        			}
        		}
        		
        	}else if ("2".equals(dto.getYwType())) {//widget
        		
        		sb = new StringBuffer();
        		
        		sb.append("SELECT r.c_id as c_role_id,p.* FROM cos_sys_personnel p, cos_sys_role_personal rp, cos_sys_role r WHERE p.c_id = rp.c_personnel_id and rp.c_role_id = r.c_id ");
        		sb.append("AND p.c_deleted = 0 AND EXISTS (SELECT rr.c_id FROM cos_portal_permission pp, cos_sys_role rr, cos_portal_yw_component s WHERE pp.c_grant_id = rr.c_id  ");
        		sb.append("AND pp.c_yw_id = s.c_id AND pp.c_yw_type = '2' AND pp.c_grant_type = '0' AND rr.c_id = r.c_id and pp.c_yw_id = "+dto.getYwId()+") AND p.c_id NOT IN ("+userId+") ");
        		
        		List<Map<String, Object>> resList = baseDao.query(sb.toString());
        		
        		if (Assert.isNotEmpty(resList)) {
        			
        			for (Map<String, Object> tmpMap : resList) {
        				//删除业务角色授权信息
        				baseDao.update("DELETE FROM cos_portal_permission where c_yw_id = ? and c_grant_id = ? and c_grant_type = '0' and c_yw_type = '2' ", dto.getYwId(),tmpMap.get("C_ROLE_ID"));
        			}
        		}
        	}
        	
        	if(Assert.isNotEmpty(dto.getGrantId())){ //因为有可能列表是空的
        		portalAuthPersistence.save(BeanUtils.convert(list, PortalAuthEntity.class));
        	}
        }
	}

	@Override
	public List<Map<String, Object>> getPortalHasNoAuthRole(Long ywId, String grantType, String ywType,
			String roleCatalog) {
		
		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		
		sb.append("SELECT * FROM cos_sys_role where c_deleted = 0 ");
		
		if (!Assert.isEmpty(roleCatalog)) {
			sb.append(" and c_type = ${roleCatalog} ");
			params.put("roleCatalog", roleCatalog);
		}

		return baseDao.query(sb.toString(), params);
	}

	@Override
	public List<Map<String, Object>> getPortalHasAuthRole(Long ywId, String grantType, String ywType,
			String roleCatalog) {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		
		//业务类型区分
		if ("0".equals(ywType)) {//站点
			
			sb.append("SELECT p.* FROM cos_portal_permission pp, cos_sys_role p, cos_portal_site s WHERE pp.c_grant_id = p.c_id ");
			sb.append("AND pp.c_yw_id = s.c_id AND pp.c_yw_type = '0' AND p.c_deleted = 0 and pp.c_yw_id = ${ywId} ");
			sb.append("and p.c_type = ${roleCatalog} and pp.c_grant_type = ${grantType} ");

		}else if ("1".equals(ywType)) {//页面
			
			sb.append("SELECT p.* FROM cos_portal_permission pp, cos_sys_role p, cos_portal_page s WHERE pp.c_grant_id = p.c_id ");
			sb.append("AND pp.c_yw_id = s.c_id AND pp.c_yw_type = '1' AND p.c_deleted = 0 and pp.c_yw_id = ${ywId}");
			sb.append("and p.c_type = ${roleCatalog} and pp.c_grant_type = ${grantType}");
			
		}else if ("2".equals(ywType)) {//widget
			
			sb.append("SELECT p.* FROM cos_portal_permission pp, cos_sys_role p, cos_portal_yw_component s WHERE pp.c_grant_id = p.c_id ");
			sb.append("AND pp.c_yw_id = s.c_id AND pp.c_yw_type = '2' AND p.c_deleted = 0 and pp.c_yw_id = ${ywId}");
			sb.append("and p.c_type = ${roleCatalog} and pp.c_grant_type = ${grantType}");
		}
		
		params.put("ywId", ywId);
		params.put("roleCatalog", roleCatalog);
		params.put("grantType", grantType);
		
		return baseDao.query(sb.toString(), params);
	}

	@Override
	@Transactional
	public void savePortalAuthRole(List<PortalAuthDto> list) {
		
        if (list != null && list.size() > 0) {
        	PortalAuthDto dto = list.get(0);
        	if(Assert.isEmpty(dto)){
        		return;
        	}
        	
        	//先删除原业务数据
        	StringBuffer sb = new StringBuffer();
        	sb.append("DELETE FROM cos_portal_permission WHERE c_yw_id = "+dto.getYwId()+" ");
        	sb.append(" and c_grant_type = '"+ dto.getGrantType() +"' ");
        	sb.append(" and c_yw_type = '"+dto.getYwType()+"' ");
        	
        	baseDao.update(sb.toString());
        	
        	if(Assert.isNotEmpty(dto.getGrantId())){ //因为有可能列表是空的
        		portalAuthPersistence.save(BeanUtils.convert(list, PortalAuthEntity.class));
        	}
        }
		
	}

	@Override
	public String getAuthSiteInfo(Long grantId, String grantType) {
		
		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		
		if ("1".equals(grantType)) {//用户
			sb.append("select DISTINCT y.c_code from (");
			sb.append("SELECT s.* FROM cos_portal_permission pp, cos_sys_personnel p, cos_portal_site s WHERE pp.c_grant_id = p.c_id ");
			sb.append("AND pp.c_yw_id = s.c_id AND pp.c_yw_type = '0' AND pp.c_grant_type = '1' AND p.c_deleted = 0 AND pp.c_grant_id = ${grantId} ");
			sb.append("UNION ALL ");
			sb.append("SELECT s.* FROM cos_portal_permission pp, cos_sys_role rr, cos_portal_site s WHERE pp.c_grant_id = rr.c_id ");
			sb.append("AND pp.c_yw_id = s.c_id AND pp.c_yw_type = '0' AND pp.c_grant_type = '0' AND EXISTS ( ");
			sb.append("SELECT * FROM cos_sys_personnel p, cos_sys_role_personal rp, cos_sys_role r WHERE p.c_id = rp.c_personnel_id ");
			sb.append("AND rp.c_role_id = r.c_id AND p.c_deleted = 0 AND pp.c_grant_id = r.c_id AND p.c_id = ${grantId})");
			
			sb.append(") as y");
			
		}else if ("0".equals(grantType)) {//角色
			sb.append("SELECT DISTINCT s.c_code FROM cos_portal_permission pp, cos_sys_role rr, cos_portal_site s WHERE pp.c_grant_id = rr.c_id ");
			sb.append("AND pp.c_yw_id = s.c_id AND pp.c_yw_type = '0' AND pp.c_grant_type = '0' AND pp.c_grant_id = ${grantId} ");
		}
		
		params.put("grantId", grantId);
		
		List<Map<String, Object>> resList = baseDao.query(sb.toString(), params);
		if (!Assert.isEmpty(resList) && resList.size() > 0) {
			String rtn = "";
			for (Map<String, Object> map : resList) {
				rtn += map.get("C_CODE") + ",";
			}
			
			if (rtn != null && rtn.length() > 0) {
				rtn = rtn.substring(0, rtn.length() - 1);
			}
			return rtn;
		}
		
		return "";
	}

	@Override
	@Transactional
	public void saveSiteAuth(Long grantId, String grantType, String sites) {
		
		String[] arr = sites.split(",");
		
		if (!Assert.isEmpty(arr) && arr.length > 0) {
	    	//先删除原业务数据
	    	StringBuffer sb = new StringBuffer();
	    	sb.append("DELETE FROM cos_portal_permission WHERE c_grant_id = "+grantId+" ");
	    	sb.append(" and c_grant_type = '"+ grantType +"' and c_yw_type = '0' ");
	    	baseDao.update(sb.toString());
	    	
	    	List<PortalAuthEntity> lst = new ArrayList<PortalAuthEntity>();
	    	
	    	for (String siteCode : arr) {
	    		PortalAuthEntity entity = new PortalAuthEntity();
	    		entity.setGrantId(grantId);
	    		entity.setGrantType(grantType);
	    		SiteDto siteDto = siteService.getSiteInfoByCode(siteCode);
	    		entity.setYwId(siteDto.getId());
	    		entity.setYwType("0");
	    		lst.add(entity);
	    	}
	    	portalAuthPersistence.save(lst);
		}
	}

	@Override
	public String getAuthSitePageInfo(Long grantId, String grantType) {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		
		if ("1".equals(grantType)) {//用户
			sb.append("SELECT s.* FROM cos_portal_permission pp, cos_sys_personnel p, cos_portal_page s WHERE pp.c_grant_id = p.c_id ");
			sb.append("AND pp.c_yw_id = s.c_id AND pp.c_yw_type = '1' AND pp.c_grant_type = '1' AND p.c_deleted = 0 AND pp.c_grant_id = ${grantId} ");
			sb.append("UNION ALL ");
			sb.append("SELECT s.* FROM cos_portal_permission pp, cos_sys_role rr, cos_portal_page s WHERE pp.c_grant_id = rr.c_id ");
			sb.append("AND pp.c_yw_id = s.c_id AND pp.c_yw_type = '1' AND pp.c_grant_type = '0' AND EXISTS ( ");
			sb.append("SELECT * FROM cos_sys_personnel p, cos_sys_role_personal rp, cos_sys_role r WHERE p.c_id = rp.c_personnel_id ");
			sb.append("AND rp.c_role_id = r.c_id AND p.c_deleted = 0 AND pp.c_grant_id = r.c_id AND p.c_id = ${grantId})");
			
		}else if ("0".equals(grantType)) {//角色
			sb.append("SELECT s.* FROM cos_portal_permission pp, cos_sys_role rr, cos_portal_page s WHERE pp.c_grant_id = rr.c_id ");
			sb.append("AND pp.c_yw_id = s.c_id AND pp.c_yw_type = '1' AND pp.c_grant_type = '0' AND pp.c_grant_id = ${grantId} ");
		}
		
		params.put("grantId", grantId);
		
		List<Map<String, Object>> resList = baseDao.query(sb.toString(), params);
		if (!Assert.isEmpty(resList) && resList.size() > 0) {
			String rtn = "";
			for (Map<String, Object> map : resList) {
				rtn += map.get("C_ID") + ",";
			}
			
			if (rtn != null && rtn.length() > 0) {
				rtn = rtn.substring(0, rtn.length() - 1);
			}
			return rtn;
		}
		
		return "";
	}

	@Override
	public void saveSitePageAuth(Long grantId, String grantType, String ids, String siteCode) {
		String[] arr = ids.split(",");
		
		if (!Assert.isEmpty(arr) && arr.length > 0) {
	    	//先删除原业务数据
	    	StringBuffer sb = new StringBuffer();
	    	sb.append("DELETE FROM cos_portal_permission WHERE c_grant_id = "+grantId+" ");
	    	sb.append(" and c_grant_type = '"+ grantType +"' and c_yw_type = '1' ");
	    	sb.append(" and c_yw_id in (SELECT c_id FROM cos_portal_page WHERE c_site_code = '"+siteCode+"' AND c_type = '0')");
	    	baseDao.update(sb.toString());
	    	
	    	List<PortalAuthEntity> lst = new ArrayList<PortalAuthEntity>();
	    	
	    	for (String id : arr) {
	    		PortalAuthEntity entity = new PortalAuthEntity();
	    		entity.setGrantId(grantId);
	    		entity.setGrantType(grantType);
	    		entity.setYwId(MathUtils.numObj2Long(id));
	    		entity.setYwType("1");
	    		lst.add(entity);
	    	}
	    	portalAuthPersistence.save(lst);
		}
	}

	@Override
	public boolean checkPermission(String expression, long personnelId) {
		// 系统管理员
		if("1".equals(MathUtils.stringObj(personnelId))) {
			return true;
		}
		
		// 未配置可见规则
		if(Assert.isEmpty(expression)) {
			return true;
		}
		
		Map<String,Map<String,Boolean>> map = new HashMap<String,Map<String,Boolean>>();
		Map<String ,Boolean> r = new HashMap<String , Boolean>();
		Map<String ,Boolean> p = new HashMap<String , Boolean>();
		
		// 查询当前用户所拥有的角色信息
		List<RoleDto> roleList = roleService.getHasConnectRole(personnelId, "");
		
		// 查询当前用户所拥有的权限项信息
		List<Map<String, Object>> pItemList = perssionItemService.queryAllPermissionItems(personnelId);
		
		if (!Assert.isEmpty(roleList) && roleList.size() > 0) {
			for (RoleDto roleDto : roleList) {
				r.put(roleDto.getCode(), true);
			}
		}
		
		if (!Assert.isEmpty(pItemList) && pItemList.size() > 0) {
			for (Map<String, Object> pMap : pItemList) {
				p.put(MathUtils.stringObj(pMap.get("C_CODE")), true);
			}
		}
		
		map.put("r", r);
		map.put("p", p);
		
		try {
			if(Assert.isEmpty(map)) {
				return false;
			}
			return ExpressionUtils.confirm(expression, map);
		} catch (Exception e) {
			try {
				logger.error("判断是否具体权限 出错，权限表达式为：{}，用户权限信息为：{}", expression,JsonMapper.toJson(map));
			} catch (JsonProcessingException e1) {
				e1.printStackTrace();
			}
		}
		return false;
	}
}
