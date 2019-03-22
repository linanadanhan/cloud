package com.gsoft.portal.system.antpatterns.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.table.service.SingleTableService;
import com.gsoft.cos3.util.Assert;
import com.gsoft.portal.system.antpatterns.service.AntPatternsService;

/**
 * 白名单管理service实现类
 * 
 * @author chenxx
 *
 */
@Service
public class AntPatternsServiceImpl implements AntPatternsService {

	@Resource
	BaseDao baseDao;
	
	@Resource
	SingleTableService singleTableService;
	
	@Autowired
	RestTemplate restTemplate;

	@Override
	public PageDto getMappingList(String search, Integer page, Integer size, String sortProp, String order) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT c.* FROM cos_scan_controller c where 1=1 ");
		Map<String, Object> params = new HashMap<String, Object>();

		if (Assert.isNotEmpty(search)) {
			sb.append(" AND (c.c_path like ${search} or c.c_details like ${search}) ");
			params.put("search", "%" + search + "%");
		}
		sb.append(" ORDER BY c.c_id DESC ");
		PageDto pageDto = baseDao.query(page, size, sb.toString(), params);
		return pageDto;
	}

	@Override
	public Map<String, Object> getMappingById(Long id) {
		return singleTableService.get("cos_scan_controller", "", id);
	}

	@Override
	public long saveMapping(Map<String, Object> map) {
		long id = singleTableService.save("cos_scan_controller", map);
		return id;
	}

	@Override
	public void deleteMapping(Long id) {
		singleTableService.delete("cos_scan_controller", id);
	}

	@Override
	public Boolean isUniqueMapping(Long id, String path, String server) {
		
		List<Map<String, Object>> rtnMap = null;

		if (Assert.isEmpty(id)) {
			rtnMap = baseDao.query("SELECT * FROM cos_scan_controller WHERE c_path = ? AND c_server = ?", path, server);
			
		} else {
			rtnMap = baseDao.query("SELECT * FROM cos_scan_controller WHERE c_path = ? AND c_server = ? and c_id != ? ", path, server, id);
		}

		if (!Assert.isEmpty(rtnMap) && rtnMap.size() > 0) {
			return true;
		}
		return false;
	}
}
