package com.gsoft.portal.system.datasource.service.impl;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.gsoft.cos3.datasource.DataSourceUtils;
import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.table.service.SingleTableService;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.system.datasource.service.DataSourceService;

/**
 * 数据源业务实现类
 * 
 * @author chenxx
 *
 */
@Service
public class DataSourceServiceImpl implements DataSourceService {

	private static final String TABLE_DATASOURCES_NAME = "TBL_DATASOURCES";

	@Resource
	private SingleTableService service;

	@Resource
	private BaseDao dao;

	@Resource
	private JdbcTemplate jdbcTemplate;

	@Override
	public void testAndSave(Map<String, Object> map) throws Exception {
		String type = MathUtils.stringObj(map.get("C_TYPE"));
		String url = MathUtils.stringObj(map.get("C_URL"));
		String user = MathUtils.stringObj(map.get("C_USER"));
		String password = MathUtils.stringObj(map.get("C_PASSWORD"));
		Connection conn = null;
		try {
			conn = DataSourceUtils.getConnection(url, user, password, type);
			service.save(TABLE_DATASOURCES_NAME, map);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceUtils.close(conn);
		}
	}

	@Override
	public PageDto queryDataSourceTable(String search, Integer page, Integer size) {

		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		sb.append("select * from tbl_datasources where 1=1 ");

		if (!Assert.isEmpty(search)) {
			sb.append(" and c_text like ${search} ");
			params.put("search", "%" + search + "%");
		}
		sb.append(" ORDER BY c_id DESC ");
		return dao.query(page, size, sb.toString(), params);
	}

	@Override
	public List<Map<String, Object>> getAll() {
		List<Map<String, Object>> list = dao.query("select c_name from tbl_datasources");
		return list;
	}

}
