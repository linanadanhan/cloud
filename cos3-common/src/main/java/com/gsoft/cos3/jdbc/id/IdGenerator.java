/**
 *
 */
package com.gsoft.cos3.jdbc.id;

import java.util.Map;

/**
 * @author shencq
 *
 * <p>
 * 	数据库表主键生成器
 * </p>
 */
public interface IdGenerator {

	public Map<String, Sequence> getSequences();

	public Long next(String name);

}
