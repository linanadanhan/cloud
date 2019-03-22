/**
 * 
 */
package com.gsoft.cos3.jdbc.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.jdbc.ColumnDefinition;

/**
 * 数据库操作类，提供基本的数据库操作方法
 * 
 * @author shencq
 *
 */
public interface BaseDao {

	/**
	 * 根据参数集，重组sql的where子句，删除不匹配参数集的where子句，然后执行分页查询，返回分页数据对象
	 * <p>
	 * 重组规则举例，如：select * from test t where t.age > ${min} and t.age < ${max}，
	 * 参数集中提供min参数，但不提供max参数的话，将重组为：select * from test t where t.age > ?
	 * </p>
	 * 
	 * @param pageNum
	 *            页码，首页为1，依次类推
	 * @param pageSize
	 *            每页显示记录条数
	 * @param sql
	 *            需要执行的查询语句
	 * @param params
	 *            sql语句中用到的变量的参数
	 * @return
	 */
	public PageDto query(int pageNum, int pageSize, String sql, Map<String, Object> params);

	/**
	 * 执行分页查询，返回分页数据对象
	 * 
	 * @param pageNum
	 *            页码，首页为1，依次类推
	 * @param pageSize
	 *            每页显示记录条数
	 * @param sql
	 *            需要执行的查询语句
	 * @param values
	 *            sql语句中用到的变量的参数，注意变量的顺序
	 * @return
	 */
	public PageDto query(int pageNum, int pageSize, String sql, Object... values);

	/**
	 * 根据参数集，重组sql的where子句，删除不匹配参数集的where子句，然后执行查询，返回数据集
	 * <p>
	 * 重组规则举例，如：select * from test t where t.age > ${min} and t.age < ${max}，
	 * 参数集中提供min参数，但不提供max参数的话，将重组为：select * from test t where t.age > ?
	 * </p>
	 * 
	 * @param sql
	 *            需要执行的查询语句
	 * @param params
	 *            sql语句中用到的变量的参数
	 * @return
	 */
	public List<Map<String, Object>> query(String sql, Map<String, Object> params);

	/**
	 * 执行查询，返回数据集
	 * 
	 * @param sql
	 *            需要执行的查询语句
	 * @param values
	 *            sql语句中用到的变量的参数，注意变量的顺序
	 * @return
	 */
	public List<Map<String, Object>> query(String sql, Object... values);

	/**
	 * 执行查询语句，返回单条记录的单个属性，并转化成指定的类型
	 * 
	 * @param sql
	 *            执行的sql语句，通常为统计语句，如：select count(*) from ...
	 * @param objectType
	 *            返回值的类型
	 * @param values
	 *            sql对应的参数值
	 * @return
	 */
	public <T> T queryForObject(String sql, Class<T> objectType, Object... values);

	/**
	 * 单表新增记录，主键为Long类型，采用序列号生成，方法执行完成后，主键值将自动填充到入参的Map中
	 * 
	 * @param tableName
	 *            表名
	 * @param pkName
	 *            主键（自增长）字段名
	 * @param values
	 *            字段属性Map对象
	 * @return 新增记录的id值
	 */
	public Object insert(String tableName, String pkName, Map<String, Object> values);

	/**
	 * 单表批量新增记录（导入场景使用），主键为Long类型，采用序列号自动生成，方法执行完成后，主键值将自动填充到入参对应的Map中
	 * 
	 * @param tableName
	 *            表名
	 * @param pkName
	 *            主键（自增长）字段名
	 * @param values
	 *            字段属性Map对象的List
	 * @return 新增记录的id列表
	 */
	public Object[] insert(String tableName, String pkName, List<Map<String, Object>> values);

	/**
	 * 根据主键获取单表中的指定记录
	 * 
	 * @param tableName
	 *            表名
	 * @param pkName
	 *            主键字段名
	 * @param pkValue
	 *            主键字段值
	 * @return
	 */
	public Map<String, Object> get(String tableName, String pkName, Object pkValue);

	/**
	 * 根据参数，执行sql语句，获取第一条记录
	 * 
	 * @param sql
	 *            获取数据的select语句
	 * @param values
	 *            sql语句对应的参数值
	 * @return
	 */
	public Map<String, Object> load(String sql, Object... values);

	/**
	 * 根据主键修改单表中的指定记录
	 * 
	 * @param tableName
	 *            表名
	 * @param pkName
	 *            主键字段名
	 * @param values
	 *            需要修改的属性，包含主键
	 */
	public void modify(String tableName, String pkName, Map<String, Object> values);

	/**
	 * 批量修改单表中的记录集
	 * 
	 * @param tableName
	 *            表名
	 * @param pkName
	 *            主键字段名
	 * @param values
	 *            需要修改的记录集，属性包含主键
	 */
	public void modify(String tableName, String pkName, List<Map<String, Object>> values);

	/**
	 * 根据主键集合修改单表中的指定记录
	 * 
	 * @param tableName
	 *            表名
	 * @param pkName
	 *            主键字段名
	 * @param pkValues
	 *            逗号隔开的多个主键值
	 * @param values
	 *            需要修改的属性，包含主键
	 */
	public void modify(String tableName, String pkName, Object[] pkValues, Map<String, Object> values);

	/**
	 * 根据主键伤处单表中指定的记录
	 * 
	 * @param tableName
	 *            表名
	 * @param pkName
	 *            主键字段名
	 * @param pkValue
	 *            主键值
	 */
	public void delete(String tableName, String pkName, Object pkValue);

	/**
	 * 根据主键伤处单表中指定的记录
	 * 
	 * @param tableName
	 *            表名
	 * @param pkName
	 *            主键字段名
	 * @param pkValues
	 *            主键值
	 */
	public void delete(String tableName, String pkName, Object[] pkValues);

	/**
	 * 执行修改类型的sql语句，如：delete from 。。。; update ... set ...; insert into ...等
	 * 
	 * @param sql
	 *            需要执行的sql语句
	 * @param values
	 *            sql中对应的参数值
	 * @return
	 */
	public void update(String sql, Object... values);

	/**
	 * 获取更底层JdbcTemplate操作对象，用于复杂的个性化数据库操作要求
	 * 
	 * @return
	 */
	public JdbcTemplate getJdbcTemplate();

	/**
	 * 获取更底层NamedParameterJdbcTemplate操作对象，用于复杂的个性化数据库操作要求。
	 * 与JdbcTemplate相比，参数由?换成:name形式，同时可自动处理in的参数长短问题
	 * 
	 * @return
	 */
	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate();

	/**
	 * 获取数据表的字段名称集合
	 * 
	 * @param tableName
	 *            数据表名
	 * @return
	 */
	public List<ColumnDefinition> getTableColumnNames(String tableName);

}
