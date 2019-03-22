/**
 * 
 */
package com.gsoft.cos3.table.service;

import java.util.List;
import java.util.Map;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.tree.TreeNode;

/**
 * 简单数据表记录维护服务
 * 
 * <p>
 * 包含以下服务：<br>
 * 1、获取所有记录集<br>
 * 2、分页获取记录集（默认排序）<br>
 * 3、新增
 * 
 * @author shencq
 * 
 */
public interface SingleTableService {

	/**
	 * 根据给定的查询条件、查询字段、排序字段、排序方式等，查询所有符合条件的记录集
	 * 
	 * 参数集中以MAX_或MIN_+字段名的，转化为>=或<=， 值为集合的或IN_+字段名的，转化为in， 值为字符串并包含%的，转化为like
	 * 
	 * @param tableName
	 *            数据表名
	 * @param columnNames
	 *            需要查询的字段字符串，多字段以逗号隔开
	 * @param map
	 *            查询条件的参数，所有条件均以等号匹配
	 * @param sort
	 *            排序字段，多字段以逗号隔开
	 * @param order
	 *            排序方式，asc/desc
	 * @return
	 */
	List<Map<String, Object>> queryAll(String tableName, String columnNames, Map<String, Object> map, String sort,
                                       String order);

	/**
	 * 根据给定的查询条件、查询字段、排序字段、排序方式等，查询所有符合条件的记录集，并组装成Tree
	 *
	 * 参数集中以MAX_或MIN_+字段名的，转化为>=或<=， 值为集合的或IN_+字段名的，转化为in， 值为字符串并包含%的，转化为like
	 *
	 * @param tableName
	 *            数据表名
	 * @param columnNames
	 *            需要查询的字段字符串，多字段以逗号隔开
	 * @param map
	 *            查询条件的参数，所有条件均以等号匹配
	 * @param sort
	 *            排序字段，多字段以逗号隔开
	 * @param order
	 *            排序方式，asc/desc
	 * @return
	 */
	List<TreeNode> queryTree(String tableName, String columnNames, Map<String, Object> map, String sort, String order);

	/**
	 * 根据给定的查询条件、查询字段、排序字段、排序方式、页码、每页记录数等，分页查询符合条件的记录集
	 *
	 * 参数集中以MAX_或MIN_+字段名的，转化为>=或<=， 值为集合的或IN_+字段名的，转化为in， 值为字符串并包含%的，转化为like
	 *
	 * @param tableName	数据表名
	 * @param columnNames	查询的字段名字符串，多字段名以逗号隔开
	 * @param map	查询条件集合
	 * @param sort	排序字段字符串，多字段以逗号隔开
	 * @param order	排序方式，asc/desc
	 * @param page	页码
	 * @param rows	每页记录数
	 * @return
	 */
	PageDto queryPage(String tableName, String columnNames, Map<String, Object> map, String sort,
                      String order, int page, int rows);
	
	/**
	 * 获取数据表的字段名集合
	 * @param tableName	数据表名
	 * @return
	 */
	List<String> getTableColumnNames(String tableName);
	
	/**
	 * 获取表字段
	 * @param tableName
	 * @return
	 */
	Map<String,Object> getTableFields(String tableName);


	/**
	 * 将数据集保存到对应的数据表中，其中新增时，主键C_ID为空，否则为修改
	 * @param tableName	数据表名
	 * @param map	参数集，需要保存或修改到数据表中的内容，修改时也保存了主键的值
	 * @return 
	 */
	Long save(String tableName, Map<String, Object> map);
	
	/**
	 * 批量插入,适用于单表、主扩表、主-扩-扩业务，不支持父子表，表中带有树结构的数据
	 * 本接口主要用于数据可以分表导入，提高导入速度
	 * @param tableName
	 * @param datas
	 */
	void batchSave(String tableName,List<Map<String,Object>> datas);

	/**
	 * 批量修改记录的属性值	
	 * @param tableName	数据表名
	 * @param ids	主键值，多值用逗号隔开
	 * @param map	参数集
	 */
	void modify(String tableName, String ids, Map<String, Object> map);

	/**
	 * 根据唯一性约束的属性，获取单条数据的详细信息
	 * @param tableName	数据表名
	 * @param columnNames 获取的列名字符串，多值用逗号隔开
	 * @param id	主键值
	 * @return
	 */
	Map<String, Object> get(String tableName, String columnNames, Object id);

	/**
	 * 根据唯一性约束的属性，获取单条数据的详细信息
	 * @param tableName	数据表名
	 * @param columnNames 获取的列名字符串，多值用逗号隔开
	 * @param map	参数集
	 * @return
	 */
	Map<String, Object> get(String tableName, String columnNames, Map<String, ? extends Object> map);

	/**
	 * 删除指定的数据
	 * @param tableName	数据表名
	 * @param id	主键值
	 */
	void delete(String tableName, Object id);

	/**
	 * 删除指定的数据
	 * @param tableName	数据表名
	 * @param map	参数集
	 */
	void delete(String tableName, Map<String, Object> map);

	/**
	 * 批量删除指定的数据
	 * @param tableName	数据表名
	 * @param ids	主键值
	 */
	void deleteAll(String tableName, String ids);

	/**
	 * 排除指定记录外，判断属性值是否唯一
	 * @param tableName	数据表名
	 * @param id	需要排除的记录的主键值
	 * @param map	判断唯一性的属性集
	 * @return
	 */
	Boolean unique(String tableName, String id, Map<String, Object> map);
	/**
	 * 移动数据
	 * @param tableName
	 * @param srcId
	 * @param targetId
	 */
	void move(String tableName, Object srcId, Object targetId);
	
	/**
	 * 复制数据
	 * @param tableName
	 * @param srcId
	 * @param targetId
	 */
	void copy(String tableName, Object srcId, Object targetId);

	/**
	 * 查找数据表分类
	 * @return
	 */
	List<String> queryCatalogs();

}
