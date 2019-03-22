/**
 *
 */
package com.gsoft.cos3.jdbc.id;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shencq
 *
 */
@Primary
@Service("framework.sequenceStyleIdGenerator")
public class SequenceStyleIdGenerator implements IdGenerator {

	private static Map<String, Sequence> sequences = new HashMap<String, Sequence>();

	@Resource
	private JdbcTemplate jdbcTemplate;

	@Resource
	private AbstractPlatformTransactionManager transactionManager;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gsoft.framework.jdbc.id.IdGenerator#next()
	 */
	@Override
	public Long next(String name) {
		Sequence seq = sequences.get(name);
		if (seq == null) {
			seq = createSequence(name);
		}
		return seq.next();
	}

	private synchronized Sequence createSequence(String name) {
		Sequence seq = new Sequence(name, jdbcTemplate, transactionManager);
		seq.configuration();
		sequences.put(name, seq);
		return seq;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * @param transactionManager
	 *            the transactionManager to set
	 */
	public void setTransactionManager(AbstractPlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Override
	public Map<String, Sequence> getSequences() {
		return sequences;
	}

}
