package com.gsoft.portal.system.idgenerator.service.impl;

import com.gsoft.cos3.util.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * app2里面id生成器的sequence类
 *
 * @author pilsy
 */
public class AppSequence {

    Logger logger = LoggerFactory.getLogger(getClass());

    private String name;

    private JdbcTemplate jdbcTemplate;

    private static String TABLE_NAME = "APP_SEQUENCE_TABLE";

    private Long initialValue = 0L;

    private AbstractPlatformTransactionManager transactionManager;

    public AppSequence(String name, JdbcTemplate jdbcTemplate, AbstractPlatformTransactionManager transactionManager) {
        this.name = name;
        this.jdbcTemplate = jdbcTemplate;
        this.transactionManager = transactionManager;
    }

    public void configuration() {
        TransactionDefinition definition = new DefaultTransactionDefinition();
        ((DefaultTransactionDefinition) definition).setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(definition);
        try {
            String selectSql = String.format("SELECT C_CUR_VAL FROM %S WHERE C_NAME = ? FOR UPDATE", TABLE_NAME);
            Map<String, Object> row = jdbcTemplate.query(selectSql, new Object[]{name},
                    rs -> {
                        Map<String, Object> map = null;
                        if (rs.next()) {
                            map = new HashMap<>();
                            map.put("C_CUR_VAL", rs.getLong(1));
                        }
                        return map;
                    });
            if (row == null) {
                String sql = String.format("INSERT INTO %S (C_NAME, C_CUR_VAL) VALUES(?, ?)", TABLE_NAME);
                jdbcTemplate.update(sql, name, initialValue);
            }
            transactionManager.commit(status);
        } catch (DataAccessException e) {
            logger.error("初始化序列异常", e);
            transactionManager.rollback(status);
        }

    }

    public synchronized Long next() {
        TransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);
        Long value = null;
        try {
            String selectSql = String.format("SELECT C_CUR_VAL FROM %S WHERE C_NAME = ? FOR UPDATE", TABLE_NAME);
            Map<String, Object> row = jdbcTemplate.query(selectSql, new Object[]{name},
                    rs -> {
                        Map<String, Object> map = new HashMap<String, Object>();
                        if (rs.next()) {
                            map.put("C_CUR_VAL", rs.getLong(1));
                        }
                        return map;
                    });
            String sql = String.format("UPDATE %S SET C_CUR_VAL = C_CUR_VAL + 1 WHERE C_NAME = ?", TABLE_NAME);
            jdbcTemplate.update(sql, name);
            transactionManager.commit(status);
            value = MathUtils.numObj2Long(row.get("C_CUR_VAL"));
        } catch (DataAccessException e) {
            logger.error("读取序列异常", e);
            transactionManager.rollback(status);
        }
        return value;
    }

    public Long currentValue() {
        TransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);
        Long value = null;
        try {
            String selectSql = String.format("SELECT C_CUR_VAL FROM %S WHERE C_NAME = ?", TABLE_NAME);
            Map<String, Object> row = jdbcTemplate.query(selectSql, new Object[]{name},
                    rs -> {
                        Map<String, Object> map = new HashMap<String, Object>();
                        if (rs.next()) {
                            map.put("C_CUR_VAL", rs.getLong(1));
                        }
                        return map;
                    });
            transactionManager.commit(status);
            value = MathUtils.numObj2Long(row.get("C_CUR_VAL"));
        } catch (DataAccessException e) {
            logger.error("读取序列异常", e);
            transactionManager.rollback(status);
        }
        return value;
    }
}
