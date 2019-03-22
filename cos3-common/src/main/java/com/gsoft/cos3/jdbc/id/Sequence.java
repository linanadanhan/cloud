package com.gsoft.cos3.jdbc.id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Sequence {

	Logger logger = LoggerFactory.getLogger(getClass());

	private String name;

	private Long value;

	private Long maxValue;

	private JdbcTemplate jdbcTemplate;

	private static String TABLE_NAME = "SEQUENCE_TABLE";

	private Long initialValue = 0L;

	private int incrementSize = 50;

	private AbstractPlatformTransactionManager transactionManager;

	public Sequence(String name, JdbcTemplate jdbcTemplate, AbstractPlatformTransactionManager transactionManager) {
		this.name = name;
		this.jdbcTemplate = jdbcTemplate;
		this.transactionManager = transactionManager;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public void configuration() {
		TransactionDefinition definition = new DefaultTransactionDefinition();
        ((DefaultTransactionDefinition) definition).setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus status = transactionManager.getTransaction(definition);
		try {
			String selectSql = String.format("SELECT C_CUR_VAL, C_INCREMENT_SIZE FROM %S WHERE C_NAME = ? FOR UPDATE",
					TABLE_NAME);
			Map<String, Object> row = jdbcTemplate.query(selectSql, new Object[] { name },
					new ResultSetExtractor<Map<String, Object>>() {

						@Override
						public Map<String, Object> extractData(ResultSet rs) throws SQLException, DataAccessException {
							Map<String, Object> row = null;
							if (rs.next()) {
								row = new HashMap<String, Object>();
								row.put("C_CUR_VAL", rs.getLong(1));
								row.put("C_INCREMENT_SIZE", rs.getInt(2));
							}
							return row;
						}
					});
			if (row == null) {
				String sql = String.format("INSERT INTO %S (C_NAME, C_CUR_VAL, C_INCREMENT_SIZE) VALUES(?, ?, ?)", TABLE_NAME);
				this.value = initialValue;
				this.maxValue = initialValue + incrementSize;
				jdbcTemplate.update(sql, name, maxValue, incrementSize);
			} else {
				this.value = (Long) row.get("C_CUR_VAL");
				this.incrementSize = (Integer) row.get("C_INCREMENT_SIZE");
				this.maxValue = value + incrementSize;
				String sql = String.format("UPDATE %S SET C_CUR_VAL = C_CUR_VAL + C_INCREMENT_SIZE WHERE C_NAME = ?",
						TABLE_NAME);
				jdbcTemplate.update(sql, name);
			}
			transactionManager.commit(status);
		} catch (DataAccessException e) {
			logger.error("初始化序列异常", e);
			transactionManager.rollback(status);
		}

	}

	public synchronized Long next() {
		value++;
		if (value <= maxValue) {
			return value;
		} else {
			TransactionDefinition definition = new DefaultTransactionDefinition();
			TransactionStatus status = transactionManager.getTransaction(definition);
			try {
				String selectSql = String.format("SELECT C_CUR_VAL, C_INCREMENT_SIZE FROM %S WHERE C_NAME = ? FOR UPDATE",
						TABLE_NAME);
				Map<String, Object> row = jdbcTemplate.query(selectSql, new Object[] { name },
						new ResultSetExtractor<Map<String, Object>>() {

							@Override
							public Map<String, Object> extractData(ResultSet rs)
									throws SQLException, DataAccessException {
								Map<String, Object> row = new HashMap<String, Object>();
								if (rs.next()) {
									row.put("C_CUR_VAL", rs.getLong(1));
									row.put("C_INCREMENT_SIZE", rs.getInt(2));
								}
								return row;
							}
						});
				this.value = (Long) row.get("C_CUR_VAL");
				this.incrementSize = (Integer) row.get("C_INCREMENT_SIZE");
				this.maxValue = value + incrementSize;
				String sql = String.format("UPDATE %S SET C_CUR_VAL = C_CUR_VAL + C_INCREMENT_SIZE WHERE C_NAME = ?",
						TABLE_NAME);
				jdbcTemplate.update(sql, name);
				transactionManager.commit(status);
			} catch (DataAccessException e) {
				logger.error("读取序列异常", e);
				transactionManager.rollback(status);
			}
			value++;
			return value;
		}
	}

}
