/**
 *
 */
package com.gsoft.cos3.jdbc.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.gsoft.cos3.jdbc.id.Sequence;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import com.alibaba.druid.sql.PagerUtils;
import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.exception.BusinessException;
import com.gsoft.cos3.jdbc.ColumnDefinition;
import com.gsoft.cos3.jdbc.UpperCaseColumnMapRowMapper;
import com.gsoft.cos3.jdbc.id.IdGenerator;
import com.gsoft.cos3.jdbc.util.SQLUtils;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BooleanUtils;
import com.gsoft.cos3.util.StringUtils;

/**
 * 通用数据库操作实现类
 *
 * @author shencq
 *
 */
@Primary
@Repository("framework.baseDaoImpl")
public class BaseDaoImpl implements BaseDao {

	@Resource
	private JdbcTemplate jdbcTemplate;

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Resource
	private IdGenerator IdGenerator;

	private static Logger log = LoggerFactory.getLogger(BaseDaoImpl.class);

	@Override
	public PageDto query(int pageNum, int pageSize, String sql, Object... values) {
		String totalSql = PagerUtils.count(sql, "mysql");
		Long total = queryForObject(totalSql, Long.class, values);
		int offset = (pageNum - 1) * pageSize;
		if (total > offset) {
			// TODO 将offset和pageSize参数提取出来作为参数处理，优化sql性能
			String pageSql = PagerUtils.limit(sql, "mysql", offset, pageSize);
			List<Map<String, Object>> rows = query(pageSql, values);
			return new PageDto(rows, total);
		} else {
			return new PageDto(Collections.emptyList(), total);
		}
	}

	@Override
	public PageDto query(int pageNum, int pageSize, String sql, Map<String, Object> params) {
		List<Object> values = new ArrayList<Object>();
		StringBuilder out = new StringBuilder(sql.length());
		SQLUtils.recombine(sql, params, out, values, "mysql");

		return query(pageNum, pageSize, out.toString(), values.toArray());
	}

	@Override
	public List<Map<String, Object>> query(String sql, Object... values) {
		if (log.isInfoEnabled()) {
			log.info("SQL: " + sql);
			log.info("SQL Params:  " + StringUtils.join(values, ", "));
		}
		return jdbcTemplate.query(sql, values, new UpperCaseColumnMapRowMapper());
	}

	@Override
	public List<Map<String, Object>> query(String sql, Map<String, Object> params) {
		List<Object> values = new ArrayList<Object>();
		StringBuilder out = new StringBuilder(sql.length());
		SQLUtils.recombine(sql, params, out, values, "mysql");

		return query(out.toString(), values.toArray());
	}

	@Override
	public <T> T queryForObject(String sql, Class<T> objectType, Object... values) {
		if (log.isInfoEnabled()) {
			log.info("SQL: " + sql);
			log.info("SQL Params:  " + StringUtils.join(values, ", "));
		}
		return jdbcTemplate.queryForObject(sql, objectType, values);
	}

	@Override
	public Object insert(String tableName, String pkName, Map<String, Object> values) {
		Assert.notEmpty(values, "新增记录的字段不能为空！");
		tableName = tableName.toUpperCase();
		Object pk = values.get(pkName);
		Object id;
		if (BooleanUtils.isEmpty(pk)) {
			id = IdGenerator.next(tableName);
		} else {
			id = pk;
		}
		values.put(pkName, id);
		String sql = String.format("INSERT INTO %s ( %S ) VALUES ( %s)", tableName,
				StringUtils.join(values.keySet(), ", "), StringUtils.repeat("?", ", ", values.size()));
		if (log.isInfoEnabled()) {
			log.info("SQL: " + sql);
			log.info("SQL Params:  " + StringUtils.join(values.values().toArray(), ", "));
		}
        try {
            jdbcTemplate.update(sql, values.values().toArray());
            return id;
        } catch (DuplicateKeyException e) {
            log.info("处理数据库主键冲突!");
            Map<String, Sequence> sequences = IdGenerator.getSequences();
            Sequence sequence = sequences.get(tableName);
            String format = String.format("SELECT MAX(C_ID) FROM %s", tableName);
            Long count = jdbcTemplate.queryForObject(format, Long.class);
            sequence.setValue(count);
            throw new DuplicateKeyException("主键冲突!");
        }
	}

	@Override
	public Object[] insert(String tName, final String pkName, final List<Map<String, Object>> rows) {
		Assert.notEmpty(rows, "新增记录不能为空！");
		Map<String, Object> values = rows.get(0);
		Assert.notEmpty(values, "新增记录的字段不能为空！");
		if (!values.containsKey(pkName)) {
			values.put(pkName, null);
		}
		final int size = values.size();
		final String tableName = tName.toUpperCase();
		String sql = String.format("INSERT INTO %s ( %S ) VALUES ( %s)", tableName,
				StringUtils.join(values.keySet(), ", "), StringUtils.repeat("?", ", ", values.size()));
		final Object[] ids = new Object[rows.size()];
		if (log.isInfoEnabled()) {
			log.info("SQL: " + sql);
		}
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Map<String, Object> values = rows.get(i);
				Assert.notEmpty(values, "新增记录不能为空！");
				Object pk = values.get(pkName);
				Object id = pk;
				if (BooleanUtils.isEmpty(pk)) {
					id = IdGenerator.next(tableName);
					values.put(pkName, id);
				}
				Assert.equals(size, values.size(), "批量新增的记录属性名称和数量必须相同");
				ids[i] = id;
				if (log.isInfoEnabled()) {
					log.info("SQL Params:  " + StringUtils.join(values.values().toArray(), ", "));
				}
				int j = 0;
				for (Object value : values.values()) {
					j++;
					StatementCreatorUtils.setParameterValue(ps, j, SqlTypeValue.TYPE_UNKNOWN, value);
				}
			}

			public int getBatchSize() {
				return rows.size();
			}
		});
		return ids;
	}

	@Override
	public Map<String, Object> get(String tableName, String pkName, Object pkValue) {
		String sql = String.format("SELECT * FROM %S WHERE %S = ?", tableName, pkName);
		return load(sql, pkValue);
	}

	@Override
	public Map<String, Object> load(String sql, Object... values) {
		List<Map<String, Object>> results = query(sql, values);
		int size = (results != null ? results.size() : 0);
		if (size > 1) {
			throw new IncorrectResultSizeDataAccessException(1, size);
		}
		return size == 1 ? results.get(0) : null;
	}

	@Override
	public void modify(String tableName, String pkName, Map<String, Object> values) {
		Assert.notEmpty(values, "要修改的属性不能为空");
		Object pkValue = values.get(pkName);
		Assert.notNull(pkValue, "属性集中必须包含主键值");
		values.remove(pkName);
		StringBuilder sb = new StringBuilder();
		Iterator<String> iter = values.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(key).append(" = ?");
		}
		String sql = String.format("UPDATE %S SET %S WHERE %S = ?", tableName, sb.toString(), pkName);
		Object[] params = ArrayUtils.addAll(values.values().toArray(), pkValue);
		if (log.isInfoEnabled()) {
			log.info("SQL: " + sql);
			log.info("SQL Params:  " + StringUtils.join(params, ", "));
		}
		jdbcTemplate.update(sql, params);
	}

	@Override
	public void modify(String tableName, final String pkName, final List<Map<String, Object>> rows) {
		Assert.notEmpty(rows, "修改数据不能为空");
		// 以第一行数据作为构造sql语句的对象

		Map<String, Object> values = rows.get(0);
		Assert.notEmpty(values, "要修改的属性不能为空");
		Object pkValue = values.get(pkName);
		Assert.notNull(pkValue, "属性集中必须包含主键值");
		StringBuilder sb = new StringBuilder();
		Iterator<String> iter = values.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			if (key.equals(pkName)) {
				continue;
			}
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(key).append(" = ?");
		}
		String sql = String.format("UPDATE %S SET %S WHERE %S = ?", tableName, sb.toString(), pkName);

		if (log.isInfoEnabled()) {
			log.info("SQL: " + sql);
		}

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Map<String, Object> values = (Map<String, Object>) rows.get(i);
				Object id = values.remove(pkName);
				if (log.isInfoEnabled()) {
					log.info("SQL Params:  " + StringUtils.join(values.values().toArray(), ", ") + ", " + id);
				}
				int j = 1;
				for (Object value : values.values()) {
					StatementCreatorUtils.setParameterValue(ps, j, SqlTypeValue.TYPE_UNKNOWN, value);
					j++;
				}
				StatementCreatorUtils.setParameterValue(ps, j, SqlTypeValue.TYPE_UNKNOWN, id);
			}

			public int getBatchSize() {
				return rows.size();
			}
		});
	}

	@Override
	public void modify(String tableName, String pkName, Object[] pkValues, Map<String, Object> values) {
		Assert.notEmpty(pkValues, "主键值不能为空");
		Assert.notEmpty(values, "要修改的属性不能为空");
		Assert.isFalse(values.containsKey(pkName), "主键值不能批量修改");
		StringBuilder sb = new StringBuilder();
		Iterator<String> iter = values.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(key).append(" = ?");
		}
		String sql = String.format("UPDATE %S SET %S WHERE %S in (%s)", tableName, sb.toString(), pkName,
				StringUtils.repeat("?", ", ", pkValues.length));
		Object[] params = ArrayUtils.addAll(values.values().toArray(), pkValues);
		if (log.isInfoEnabled()) {
			log.info("SQL: " + sql);
			log.info("SQL Params:  " + StringUtils.join(params, ", "));
		}
		jdbcTemplate.update(sql, params);
	}

	@Override
	public void delete(String tableName, String pkName, Object pkValue) {
		String sql = String.format("DELETE FROM %S WHERE %S = ?", tableName, pkName);
		update(sql, pkValue);
	}

	@Override
	public void delete(String tableName, String pkName, Object[] pkValues) {
		String sql = String.format("DELETE FROM %S WHERE %S IN (%s)", tableName, pkName,
				StringUtils.repeat("?", ", ", pkValues.length));
		update(sql, pkValues);
	}

	@Override
	public void update(String sql, Object... values) {
		if (log.isInfoEnabled()) {
			log.info("SQL: " + sql);
			log.info("SQL Params:  " + StringUtils.join(values, ", "));
		}
		jdbcTemplate.update(sql, values);
	}

	@Override
	public List<ColumnDefinition> getTableColumnNames(String tableName) {
		List<ColumnDefinition> columnNames = new ArrayList<ColumnDefinition>();
		DataSource dataSource = jdbcTemplate.getDataSource();
		Connection con = DataSourceUtils.getConnection(dataSource);
		String databaseSchema = null;
		if (con != null) {
			ResultSet rs = null;
			try {
				DatabaseMetaData md = con.getMetaData();

				rs = md.getColumns(con.getCatalog(), databaseSchema, tableName.toUpperCase(), null);
				while (rs.next()) {
					columnNames.add(new ColumnDefinition(rs.getString("COLUMN_NAME"), rs.getInt("DATA_TYPE")));
				}
			} catch (SQLException e) {
				throw new BusinessException(String.format("获取数据表（%s）字段名失败", tableName), e);
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
					DataSourceUtils.releaseConnection(con, dataSource);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return columnNames;
	}

	/**
	 * @param jdbcTemplate
	 *            the jdbcTemplate to set
	 */
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * @return the jdbcTemplate
	 */
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

//	/**
//	 * @param dialect
//	 *            the dialect to set
//	 */
//	public void setDialect(Dialect dialect) {
//		this.dialect = dialect;
//	}

	/**
	 * @param idGenerator
	 *            the idGenerator to set
	 */
	public void setIdGenerator(IdGenerator idGenerator) {
		IdGenerator = idGenerator;
	}

	/**
	 * @return the namedParameterJdbcTemplate
	 */
	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		if (namedParameterJdbcTemplate == null && jdbcTemplate != null) {
			namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		}
		return namedParameterJdbcTemplate;
	}

}
