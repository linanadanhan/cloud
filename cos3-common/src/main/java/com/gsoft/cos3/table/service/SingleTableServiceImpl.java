/**
 *
 */
package com.gsoft.cos3.table.service;

import java.io.IOException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.event.EventManager;
import com.gsoft.cos3.exception.BusinessException;
import com.gsoft.cos3.jdbc.ColumnDefinition;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.table.Constant;
import com.gsoft.cos3.table.SubTableDefinition;
import com.gsoft.cos3.table.TableDefinition;
import com.gsoft.cos3.tree.TreeNode;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BooleanUtils;
import com.gsoft.cos3.util.CollectionUtils;
import com.gsoft.cos3.util.DateUtils;
import com.gsoft.cos3.util.JsonMapper;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.cos3.util.StringUtils;
import com.gsoft.cos3.util.TreeUtils;

/**
 * 简单数据表操作基础服务实现类 所有字段名称均处理为大写
 *
 * @author shencq
 */
@Primary
@Service
public class SingleTableServiceImpl implements SingleTableService {

	private static final String JSON_SUB_TABLE_DATA_SUFFIX = "_JSON";

	@Resource
	private BaseDao dao;

	/*
	 * (non-Javadoc)
	 *
	 * @see SingleTableService#queryAll(java.lang.
	 * String, java.lang.String, java.util.Map, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> queryAll(String tableName, String columnNames, Map<String, Object> map,
			String sort, String order) {
		TableDefinition td = getTableDefinition(tableName);
		fireEvent("before." + tableName + ".queryAll", map, Constant.COLUMN_NAMES, columnNames);
		if (BooleanUtils.isEmpty(columnNames)) {
			List<String> cs = td.getColumns();
			if (Assert.isNotEmpty(td.getExtTable())) { // 有扩展表
				Set<String> st = new HashSet<String>(cs);
				TableDefinition extTd = getTableDefinition(td.getExtTable());
				List<String> cs2 = extTd.getColumns();
				st.addAll(cs2);
				if(Assert.isNotEmpty(extTd.getExtTable())) {//目前只考虑支持2层扩展
					TableDefinition extTd1 = getTableDefinition(extTd.getExtTable());
					List<String> cs3 = extTd1.getColumns();
					st.addAll(cs3);
				}
				columnNames = StringUtils.join(st, ", ");
			} else {
				columnNames = StringUtils.join(cs, ", ");
			}
		}
		columnNames = removeNonColumnNames(columnNames, td);
		String sql;
		String where = null;
		List<Object> params = CollectionUtils.list();
		if (Assert.isNotEmpty(td.getExtTable())) {// 存在主表
			TableDefinition extTd = getTableDefinition(td.getExtTable());
			if(Assert.isNotEmpty(extTd.getExtTable())) {
				sql = String.format("SELECT %S FROM %S t,%S ext,%S ext1", columnNames, tableName, td.getExtTable(),extTd.getExtTable());
				where = createQueryWhereSqlAndParams(map, td, params, true,false);
			}else {
				sql = String.format("SELECT %S FROM %S t,%S ext", columnNames, tableName, td.getExtTable());
				where = createQueryWhereSqlAndParams(map, td, params, true,false);
			}
		} else {
			sql = String.format("SELECT %S FROM %S", columnNames, tableName);
			where = createQueryWhereSqlAndParams(map, td, params, false,false);
		}
		if (BooleanUtils.isNotEmpty(where)) {
			sql += " WHERE " + where;
		}
		sort = removeNonColumnNames(sort, td);
		if (BooleanUtils.isNotEmpty(sort)) {
			sql += " ORDER BY " + sort;
			order = order.toUpperCase();
			if (!"ASC".equals(order)) {
				order = "DESC";
			}
			sql += " " + order;
		}
		List<Map<String, Object>> result = dao.query(sql, params.toArray());
		fireEvent("after." + tableName + ".queryAll", map, Constant.KEY_RESULT, result, Constant.COLUMN_NAMES,
				columnNames);
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see SingleTableService#queryTree(java.lang.
	 * String, java.lang.String, java.util.Map, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<TreeNode> queryTree(String tableName, String columnNames, Map<String, Object> map, String sort,
			String order) {
		fireEvent("before." + tableName + ".queryTree", map);
		List<Map<String, Object>> data = queryAll(tableName, columnNames, map, sort, order);
		if (Assert.isEmpty(data)) {
			return new ArrayList<TreeNode>();
		}

		Set<String> keySet = data.get(0).keySet();

//		String[] attrs = ArrayUtils.removeElements(keySet.toArray(new String[keySet.size()]), Constant.COLUMN_NAME_ID,
//				Constant.COLUMN_NAME_PARENT_ID, Constant.COLUMN_NAME_ICON_CLASS_NAME, Constant.COLUMN_NAME_TEXT,
//				Constant.COLUMN_NAME_STATE, Constant.COLUMN_NAME_CHECKED);
		
		String[] attrs = keySet.toArray(new String[keySet.size()]);

		// TODO 此处强制设置所有节点展开，应改为根据数据中节点打开方式来，默认应该是不打开
		List<TreeNode> tree = TreeUtils.convert(data).change(Constant.TREE_NODE_ID, Constant.COLUMN_NAME_ID)
				.change(Constant.TREE_NODE_PARENT_ID, Constant.COLUMN_NAME_PARENT_ID)
				.change(Constant.TREE_NODE_CHECKED, Constant.COLUMN_NAME_CHECKED).set(Constant.TREE_NODE_STATE, "open")
				.change(Constant.TREE_NODE_TEXT, Constant.COLUMN_NAME_TEXT)
				.change(Constant.TREE_NODE_CASCADE, Constant.COLUMN_CASCADE)
				.change(Constant.TREE_NODE_ICON_CLASS_NAME, Constant.COLUMN_NAME_ICON_CLASS_NAME).attrs(attrs).tree();
		fireEvent("after." + tableName + ".queryTree", map, Constant.KEY_RESULT, tree);
		return tree;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see SingleTableService#queryPage(java.lang.
	 * String, java.lang.String, java.util.Map, java.lang.String,
	 * java.lang.String, int, java.lang.String)
	 */
	@Override
	public PageDto queryPage(String tableName, String columnNames, Map<String, Object> map, String sort, String order,
			int pageNum, int pageSize) {
		TableDefinition td = getTableDefinition(tableName);
		fireEvent("before." + tableName + ".queryPage", map, Constant.COLUMN_NAMES, columnNames);
		if (BooleanUtils.isEmpty(columnNames)) {
			List<String> cs = td.getColumns();
			if (Assert.isNotEmpty(td.getExtTable())) { // 有扩展表
				Set<String> st = new HashSet<String>(cs);
				TableDefinition extTd = getTableDefinition(td.getExtTable());
				List<String> cs2 = extTd.getColumns();
				st.addAll(cs2);
				if(Assert.isNotEmpty(extTd.getExtTable())) {//目前只考虑支持2层扩展
					TableDefinition extTd1 = getTableDefinition(extTd.getExtTable());
					List<String> cs3 = extTd1.getColumns();
					st.addAll(cs3);
				}
				columnNames = StringUtils.join(st, ", ");
			} else {
				columnNames = StringUtils.join(cs, ", ");
			}
		}
		columnNames = removeNonColumnNames(columnNames, td);
		String sql;
		String where = null;
		List<Object> params = CollectionUtils.list();
		if (Assert.isNotEmpty(td.getExtTable())) {// 存在主表
			TableDefinition extTd = getTableDefinition(td.getExtTable());
			if(Assert.isNotEmpty(extTd.getExtTable())) {
				sql = String.format("SELECT %S FROM %S t,%S ext,%S ext1", columnNames, tableName, td.getExtTable(),extTd.getExtTable());
				where = createQueryWhereSqlAndParams(map, td, params, true,true);
			}else {
				sql = String.format("SELECT %S FROM %S t,%S ext", columnNames, tableName, td.getExtTable());
				where = createQueryWhereSqlAndParams(map, td, params, true,false);
			}
		} else {
			sql = String.format("SELECT %S FROM %S", columnNames, tableName);
			where = createQueryWhereSqlAndParams(map, td, params, false,false);
		}

		if(map.containsKey("FILTER_EXPRESS")) {
			if (BooleanUtils.isNotEmpty(where)) {
				where += " and " + map.get("FILTER_EXPRESS").toString();
			}else {
				where = map.get("FILTER_EXPRESS").toString();
			}
		}
		
		if (BooleanUtils.isNotEmpty(where)) {
			sql += " WHERE " + where;
		}
		sort = removeNonColumnNames(sort, td);
		if (BooleanUtils.isNotEmpty(sort)) {
			sql += " ORDER BY " + sort;
			order = order.toUpperCase();
			if (!"ASC".equals(order)) {
				order = "DESC";
			}
			sql += " " + order;
		}
		PageDto result = dao.query(pageNum, pageSize, sql, params.toArray());
		fireEvent("after." + tableName + ".queryPage", map, Constant.KEY_RESULT, result, Constant.COLUMN_NAMES,
				columnNames);
		return result;
	}

	/**
	 * 根据提供的Map中的数据，剔除非数据表字段后，创建SQL条件子句，
	 *
	 * 参数集中以MAX_或MIN_+字段名的，转化为>=或<=， 值为集合的或IN_+字段名的，转化为in， 值为字符串并包含%的，转化为like
	 *
	 * @param map
	 * @param td
	 * @param params
	 * @param extTable
	 *            是否考虑扩展表
	 * @return
	 */
	private String createQueryWhereSqlAndParams(Map<String, Object> map, TableDefinition td, List<Object> params,
			Boolean extTable,Boolean extTable1) {
		if (map == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer(200);
		Iterator<Entry<String, Object>> iter = map.entrySet().iterator();
		TableDefinition extTd = null;
		TableDefinition extTd1 = null;
		if (extTable) {
			extTd = getTableDefinition(td.getExtTable());
			sb.append("t.C_ID = ext.C_ID");
		}
		if(extTable1) {
			extTd1 = getTableDefinition(extTd.getExtTable());
			sb.append(" AND ext.C_ID = ext1.C_ID");
		}

		while (iter.hasNext()) {
			Entry<String, Object> item = iter.next();
			String key = item.getKey().toUpperCase();
			String columnName = key.replaceAll("^(MAX_)|(MIN_)|(IN_)|(EMPTY_)", "");
			Object value = item.getValue();
			if (td.column(columnName) && BooleanUtils.isNotEmpty(value)) {
				if (td.dateColumn(columnName) && value instanceof String) {
					value = DateUtils.parseDate((String) value);
				}
				if (extTable) {
					columnName = "t." + columnName;
				}
				createWhere(key, columnName, value, td, sb, params);
				if (extTable) {
					continue;
				}
			}
			if (extTable && extTd.column(columnName) && BooleanUtils.isNotEmpty(value)) {
				if (extTd.dateColumn(columnName) && value instanceof String) {
					value = DateUtils.parseDate((String) value);
				}
				if (extTable) {
					columnName = "ext." + columnName;
				}
				createWhere(key, columnName, value, td, sb, params);
			}

			if (extTable1 && extTd1.column(columnName) && BooleanUtils.isNotEmpty(value)) {
				if (extTd1.dateColumn(columnName) && value instanceof String) {
					value = DateUtils.parseDate((String) value);
				}
				if (extTable) {
					columnName = "ext1." + columnName;
				}
				createWhere(key, columnName, value, td, sb, params);
			}
		}
		return sb.toString();
	}

	private void createWhere(String key, String columnName, Object value, TableDefinition td, StringBuffer sb,
			List<Object> params) {
		if (sb.length() > 0) {
			sb.append(" AND ");
		}
		if (key.startsWith("MAX_")) {
			sb.append(columnName);
			sb.append(" <= ?");
			params.add(value);
		} else if (key.startsWith("EMPTY_")) {
			sb.append(columnName);
			sb.append(" ");
			sb.append(value);
		} else if (key.startsWith("MIN_")) {
			sb.append(columnName);
			sb.append(" >= ?");
			params.add(value);
		} else if (key.startsWith("IN_")) {
			sb.append(columnName);
			sb.append(" IN (");
			String[] vals = StringUtils.splitAndStrip((String) value, ",");
			sb.append(StringUtils.repeat("?", ", ", vals.length));
			sb.append(")");
			params.addAll(CollectionUtils.list(vals));
		} else if (value instanceof Collection) {
			sb.append(key);
			sb.append(" IN (");
			sb.append(StringUtils.repeat("?", ", ", ((Collection<?>) value).size()));
			sb.append(")");
			params.addAll((Collection<?>) value);
		} else if (value instanceof String && ((String) value).indexOf('%') > -1) {
			sb.append(key);
			sb.append(" LIKE ?");
			params.add(value);
		} else {
			sb.append(key);
			sb.append(" = ?");
			params.add(value);
		}
	}

	/**
	 * 删除字符串中不属于数据库表字段的内容
	 *
	 * @param columnNames
	 *            字段名字符串，多值用逗号隔开
	 * @param td
	 *            数据表实际具备字段名集合
	 * @return
	 */
	private String removeNonColumnNames(String columnNames, TableDefinition td) {
		if (BooleanUtils.isNotEmpty(columnNames)) {
			String[] items = StringUtils.splitAndStrip(columnNames.toUpperCase(), ",");
			List<String> names = CollectionUtils.list();

			if (Assert.isNotEmpty(td.getExtTable())) {// 存在主表
				TableDefinition mainTabletd = getTableDefinition(td.getExtTable());
				TableDefinition mainTabletd2 = null;
				if(Assert.isNotEmpty(mainTabletd.getExtTable())) {
					mainTabletd2 = getTableDefinition(mainTabletd.getExtTable());
				}
				for (int i = 0; i < items.length; i++) {
					if (td.column(items[i])) {
						names.add("t." + items[i]);
						continue;
					}
					if (mainTabletd.column(items[i])) {
						names.add("ext." + items[i]);
					}
					if (mainTabletd2 !=null && mainTabletd2.column(items[i])) {
						names.add("ext1." + items[i]);
					}
				}
			} else {
				for (int i = 0; i < items.length; i++) {
					if (td.column(items[i])) {
						names.add(items[i]);
					}
				}
			}
			columnNames = StringUtils.join(names, ", ");
		}
		return columnNames;
	}
	
	@Override
	public void batchSave(String tableName, List<Map<String, Object>> datas) {
		TableDefinition td = getTableDefinition(tableName);
		batchInsertTableData(tableName, datas, td);
	}
	
	private Object[] batchInsertTableData(String tableName, List<Map<String, Object>> datas, TableDefinition td) {
		if (Assert.isNotEmpty(td.getExtTable())) {// 存在主表
			TableDefinition mainTabletd = getTableDefinition(td.getExtTable());
			//map.put(Constant.COLUMN_NAME_ID, insertTableData(td.getExtTable(), map, mainTabletd));
			Object[]  ids =batchInsertTableData(td.getExtTable(), datas, mainTabletd);
			if(ids.length == datas.size()) {
				for(int i=0;i<datas.size();i++) {
					datas.get(i).put(Constant.COLUMN_NAME_ID,ids[i]);
				}
			}
		}
		List<Map<String, Object>> params = createBatchValidParams(datas, td);
		Object[] ids = dao.insert(tableName, Constant.COLUMN_NAME_ID, params);
		return ids;
	}
	
	private List<Map<String, Object>> createBatchValidParams(List<Map<String, Object>> datas, TableDefinition td) {
		List<Map<String, Object>> validDatas = new ArrayList<Map<String, Object>>();
		for(Map<String,Object> map : datas) {
			Map<String,Object> valid = new HashMap<String,Object>();
			Object[] keys = map.keySet().toArray();
			for (Object key : keys) {
				if (td.column(key)) {
					Object value = map.get(key);
					if (value instanceof Iterable) {
						value = StringUtils.join((Iterable<?>) value, ',');
					}
					if (td.dateColumn(key)) {
						if (value instanceof String && BooleanUtils.isNotEmpty(value)) {
							String time = (String) value;
							if(time.endsWith("Z")) {
								time = time.replace("Z", " UTC");
								SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
								try {
									value = format.parse(time);
								} catch (ParseException e) {
									e.printStackTrace();
								}
							}else {
								value = DateUtils.parseDate((String) value);
							}

						} else if (value instanceof Long) {
							value = new Date((Long) value);
						}
					}
					valid.put((String) key, value);
				}
			}
			validDatas.add(valid);
		}
		return validDatas;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * SingleTableService#save(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	@Transactional
	public Long save(String tableName, Map<String, Object> map) {
		TableDefinition td = getTableDefinition(tableName);
		fireEvent("before." + tableName + ".save", map);
		Object id = map.get(Constant.COLUMN_NAME_ID);
		if (BooleanUtils.isEmpty(id)) {
			id = insertTableData(tableName, map, td);
		} else {
			updateTableData(tableName, map, td);
		}
		refreshTableNames(tableName);
		fireEvent("after." + tableName + ".save", map, Constant.COLUMN_NAME_ID, id);
		return Long.valueOf(id.toString());
	}

	private Long insertTableData(String tableName, Map<String, Object> map, TableDefinition td) {
		if (Assert.isNotEmpty(td.getExtTable())) {// 存在主表
			TableDefinition mainTabletd = getTableDefinition(td.getExtTable());
			map.put(Constant.COLUMN_NAME_ID, insertTableData(td.getExtTable(), map, mainTabletd));
		}
		Map<String, Object> params = createValidParams(map, td);
		setCreateInformation(td, params);

		// 处理树形数据保存逻辑
		if (td.column(Constant.COLUMN_NAME_PARENT_ID) && td.column(Constant.COLUMN_NAME_LEVEL)
				&& td.column(Constant.COLUMN_NAME_PARENT_PATH)) {
			Long parentId = MathUtils.numObj2Long(params.get(Constant.COLUMN_NAME_PARENT_ID), 0l);
			if (parentId == 0) { // 说明是顶级节点
				params.put(Constant.COLUMN_NAME_LEVEL, 1);
				params.put(Constant.COLUMN_NAME_PARENT_PATH, "/");
			} else {
				Map<String, Object> parentNode = dao.get(tableName, Constant.COLUMN_NAME_ID, parentId);
				params.put(Constant.COLUMN_NAME_LEVEL,
						MathUtils.numObj2Integer(parentNode.get(Constant.COLUMN_NAME_LEVEL)) + 1);
				params.put(Constant.COLUMN_NAME_PARENT_PATH,
						parentNode.get(Constant.COLUMN_NAME_PARENT_PATH) + parentId.toString() + "/");
			}
		}
		Object id = dao.insert(tableName, Constant.COLUMN_NAME_ID, params);
		// appendRelatedFiles(td, map, tableName, id);
		saveSubTableData(td, map, id);
		return MathUtils.numObj2Long(id);
	}

	private void updateTableData(String tableName, Map<String, Object> map, TableDefinition td) {
		if (Assert.isNotEmpty(td.getExtTable())) {// 存在主表
			TableDefinition mainTabletd = getTableDefinition(td.getExtTable());
			updateTableData(td.getExtTable(), map, mainTabletd);
		}
		Map<String, Object> params = createValidParams(map, td);
		if (Assert.isNotEmpty(params) && params.keySet().size() > 1) {
			setUpdateInformation(td, params);
			dao.modify(tableName, Constant.COLUMN_NAME_ID, params);
			// saveRelatedFiles(td, map, tableName);
			replaceSubTableData(td, map);
		}
	}

	/**
	 * 替换从表关联数据
	 *
	 * @param td
	 *            数据表定义
	 * @param map
	 *            参数集合
	 */
	private void replaceSubTableData(TableDefinition td, Map<String, Object> map) {
		deleteSubTableData(td, map);
		saveSubTableData(td, map, map.get(Constant.COLUMN_NAME_ID));
	}

	/**
	 * 保存从表关联数据
	 *
	 * @param td
	 *            数据表定义
	 * @param map
	 *            参数集合
	 * @param id
	 *            记录主键
	 */
	private void saveSubTableData(TableDefinition td, Map<String, Object> map, Object id) {
		Map<String, SubTableDefinition> subs = td.getSubTableDefinitions();
		if (subs == null) {
			return;
		}
		for (String name : subs.keySet()) {
			Map<String, Object> params = new HashMap<String, Object>();
			Object value = map.get(name);
			if (value != null) {// array
				SubTableDefinition std = subs.get(name);
				if (std != null) {
					params.put(std.getPrimaryForeignKey(), id);
					Map<String, String> fks = std.getAdditionalForeignKeys();
					if (fks != null) {
						for (Entry<String, String> entery : fks.entrySet()) {
							String subColumneName = entery.getKey();
							String columnName = entery.getValue();
							if (map.containsKey(columnName)) {
								params.put(subColumneName, map.get(columnName));
							}
						}
					}
					if (value instanceof Collection) {
						for (Object fkValue : (Collection<?>) value) {
							params.put(std.getRelatedForeignKey(), fkValue);
							params.remove(Constant.COLUMN_NAME_ID);
							save(std.getTableName(), params);
						}
					} else {
						params.put(std.getRelatedForeignKey(), value);
						params.remove(Constant.COLUMN_NAME_ID);
						save(std.getTableName(), params);
					}
				}
			}
			Object json = map.get(name + JSON_SUB_TABLE_DATA_SUFFIX);
			if (BooleanUtils.isNotEmpty(json)) {// json
				SubTableDefinition std = subs.get(name);
				if (std != null) {
					params.put(std.getPrimaryForeignKey().toUpperCase(), id);
					Map<String, String> fks = std.getAdditionalForeignKeys();
					if (fks != null) {
						for (Entry<String, String> entery : fks.entrySet()) {
							String subColumneName = entery.getKey();
							String columnName = entery.getValue();
							if (map.containsKey(columnName)) {
								params.put(subColumneName, map.get(columnName));
							}
						}
					}
					JavaType type = JsonMapper.constructParametricType(HashMap.class, String.class, Object.class);
					type = JsonMapper.constructParametricType(ArrayList.class, type);
					List<Map<String, Object>> subData = null;
					try {
						subData = JsonMapper.fromJson(json.toString(), type);
					} catch (JsonParseException e) {
						throw new BusinessException(name + "的从表Json数据解析失败", e);
					} catch (JsonMappingException e) {
						throw new BusinessException(name + "的从表Json数据转换为List<Map<String, Object>>对象失败", e);
					} catch (IOException e) {
						throw new BusinessException(name + "的从表Json数据读取失败", e);
					}
					if (subData != null) {
						for (Map<String, Object> row : subData) {
							row.putAll(params);
							row.remove(Constant.COLUMN_NAME_ID);
							save(std.getTableName(), row);
						}
					}
				}
			}
		}
	}

	/**
	 * 根据主表外键值，删除从表关联数据
	 *
	 * @param td
	 *            数据表定义
	 * @param map
	 *            参数集
	 */
	private void deleteSubTableData(TableDefinition td, Map<String, Object> map) {
		Map<String, SubTableDefinition> subs = td.getSubTableDefinitions();
		if (subs == null) {
			return;
		}
		for (String name : subs.keySet()) {
			Map<String, Object> params = new HashMap<String, Object>();
			if (map.containsKey(name) || map.containsKey(name + JSON_SUB_TABLE_DATA_SUFFIX)) {
				SubTableDefinition std = subs.get(name);
				if (std != null) {
					params.put(std.getPrimaryForeignKey(), map.get(Constant.COLUMN_NAME_ID));
					Map<String, String> fks = std.getAdditionalForeignKeys();
					if (fks != null) {
						for (Entry<String, String> entery : fks.entrySet()) {
							String subColumneName = entery.getKey();
							String columnName = entery.getValue();
							if (map.containsKey(columnName)) {
								params.put(subColumneName, map.get(columnName));
							}
						}
					}
					delete(std.getTableName(), params);
				}
			}
		}
	}

	/**
	 * 设置数据的基本信息（新增）
	 *
	 * @param td
	 * @param params
	 */
	private void setCreateInformation(TableDefinition td, Map<String, Object> params) {
		setUpdateInformation(td, params);
		if (td.column(Constant.COLUMN_NAME_CREATE_TIME)) {
			params.put(Constant.COLUMN_NAME_CREATE_TIME, new Date());
		}
//		if (td.column(Constant.COLUMN_NAME_CREATEBY)) {
//			params.put(Constant.COLUMN_NAME_CREATEBY, UserContext.getLoginName());
//		}
//		if (td.column(Constant.COLUMN_NAME_CREATOR_ACCOUNT_NAME)) {
//			params.put(Constant.COLUMN_NAME_CREATOR_ACCOUNT_NAME, UserContext.getAccountName());
//		}
//		if (td.column(Constant.COLUMN_NAME_CREATOR_ID)) {
//			if (Assert.isEmpty(params.get(Constant.COLUMN_NAME_CREATOR_ID))) {
//				params.put(Constant.COLUMN_NAME_CREATOR_ID, UserContext.getAccountId());
//			}
//		}
//		if (td.column(Constant.COLUMN_NAME_CREATOR_NAME)) {
//			params.put(Constant.COLUMN_NAME_CREATOR_NAME, UserContext.getUserName());
//		}
		if (td.column(Constant.COLUMN_NAME_DELETED)) {
			params.put(Constant.COLUMN_NAME_DELETED, 0);
		}
		if (td.column(Constant.COLUMN_NAME_DISABLED)) {
			params.put(Constant.COLUMN_NAME_DISABLED, 0);
		}
//		if (td.column(Constant.COLUMN_NAME_YEAR)) {
//			params.put(Constant.COLUMN_NAME_YEAR, UserSessionManager.getUserSession().getAttribute("CURRENT_YEAR"));
//		}
	}

	/**
	 * 在参数集中设置修改相关信息
	 *
	 * @param td
	 *            数据表定义信息
	 * @param params
	 *            参数集合
	 */
	private void setUpdateInformation(TableDefinition td, Map<String, Object> params) {
		if (td.column(Constant.COLUMN_NAME_UPDATE_TIME)) {
			params.put(Constant.COLUMN_NAME_UPDATE_TIME, new Date());
		}
//		if (td.column(Constant.COLUMN_NAME_UPDATER_ACCOUNT_ID)) {
//			params.put(Constant.COLUMN_NAME_UPDATER_ACCOUNT_ID, UserContext.getAccountId());
//		}
//		if (td.column(Constant.COLUMN_NAME_UPDATER_ACCOUNT_NAME)) {
//			params.put(Constant.COLUMN_NAME_UPDATER_ACCOUNT_NAME, UserContext.getAccountName());
//		}
//		if (td.column(Constant.COLUMN_NAME_UPDATER_ID)) {
//			params.put(Constant.COLUMN_NAME_UPDATER_ID, UserContext.getUserId());
//		}
//		if (td.column(Constant.COLUMN_NAME_UPDATER_NAME)) {
//			params.put(Constant.COLUMN_NAME_UPDATER_NAME, UserContext.getUserName());
//		}
	}

	/**
	 * 将关联文件替换保存到文件系统中
	 *
	 * @param td
	 *            数据表定义对象
	 * @param params
	 *            参数集合
	 * @param tableName
	 *            数据表名
	 */
	// private void saveRelatedFiles(TableDefinition td, Map<String, Object>
	// params, String tableName) {
	// for (String name : td.getFileColumns(params)) {
	// fms.register((String) params.get(name),
	// params.get(Constant.COLUMN_NAME_ID).toString(), tableName, name);
	// }
	// }

	/**
	 * 将关联文件追加保存到文件系统中
	 *
	 * @param td
	 *            数据表定义对象
	 * @param params
	 *            参数集合
	 * @param tableName
	 *            数据表名
	 * @param pkValue
	 *            业务主键（C_ID的值）
	 */
	// private void appendRelatedFiles(TableDefinition td, Map<String, Object>
	// params, String tableName, Object pkValue) {
	// for (String name : td.getFileColumns(params)) {
	// fms.registerAppend((String) params.get(name), pkValue.toString(),
	// tableName, name);
	// }
	// }

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * SingleTableService#modify(java.lang.String,
	 * java.lang.String, java.util.Map)
	 */
	@Override
	@Transactional
	public void modify(String tableName, String ids, Map<String, Object> map) {
		TableDefinition td = getTableDefinition(tableName);
		if (Assert.isNotEmpty(td.getExtTable())) {// 存在主表
			modify(td.getExtTable(), ids, map);
		}
		fireEvent("before." + tableName + ".modify", map);
		setUpdateInformation(td, map);
		Map<String, Object> params = createValidParams(map, td);
		if(Assert.isNotEmpty(params)) {
			params.remove(Constant.COLUMN_NAME_ID);
			dao.modify(tableName, Constant.COLUMN_NAME_ID, StringUtils.splitAndStrip(ids, ","), params);
		}
		// saveRelatedFilesByIds(td, map, tableName, ids);
		replaceSubTableDataForModify(td, map, ids);
		refreshTableNames(tableName);
		fireEvent("after." + tableName + ".modify", map, "ids", ids);
	}

	/**
	 * 批量修改主表记录时，同步替换从表关联数据
	 *
	 * @param td
	 *            数据表定义
	 * @param map
	 *            参数集合
	 * @param ids
	 *            主记录主键
	 */
	private void replaceSubTableDataForModify(TableDefinition td, Map<String, Object> map, String ids) {
		if (ids != null) {
			String[] idArray = StringUtils.splitAndStrip(ids, ',');
			for (String id : idArray) {
				map.put(Constant.COLUMN_NAME_ID, id);
				deleteSubTableData(td, map);
				saveSubTableData(td, map, map.get(Constant.COLUMN_NAME_ID));
			}
		}
	}

	/**
	 * 将多条记录的关联文件替换保存到文件系统中
	 *
	 * @param td
	 *            数据表定义对象
	 * @param params
	 *            参数集合
	 * @param tableName
	 *            数据表名
	 * @param ids
	 *            业务主键集合，多值用逗号隔开
	 */
	// private void saveRelatedFilesByIds(TableDefinition td, Map<String,
	// Object> params, String tableName, String ids) {
	// for (String name : td.getFileColumns(params)) {
	// String[] pkValues = StringUtils.splitAndStrip(ids, ",");
	// for (String pkValue : pkValues) {
	// // TODO 可增加接口，支持批量操作，优化性能
	// fms.register((String) params.get(name), pkValue.toString(), tableName,
	// name);
	// }
	// }
	// }

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * SingleTableService#get(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public Map<String, Object> get(String tableName, String columnNames, Object id) {
		return get(tableName, columnNames, CollectionUtils.map(Constant.COLUMN_NAME_ID, id));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * SingleTableService#get(java.lang.String,
	 * java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> get(String tableName, String columnNames, Map<String, ? extends Object> map) {
		TableDefinition td = getTableDefinition(tableName);
		fireEvent("before." + tableName + ".get", (Map<String, Object>) map, Constant.COLUMN_NAMES, columnNames);
		List<Object> params = CollectionUtils.list();
		String where = createWhereSqlAndParams((Map<String, Object>) map, td, params);
		Assert.notEmpty(params, "查询条件不能为空");
		if (BooleanUtils.isEmpty(columnNames)) {
			columnNames = "*";
		}
		String sql = String.format("SELECT %s FROM %S WHERE %s", columnNames, tableName, where);
		Map<String, Object> result = dao.load(sql, params.toArray());
		// 如果存在主表
		if (Assert.isNotEmpty(result) && Assert.isNotEmpty(td.getExtTable())) {// 存在主表
			result.putAll(get(td.getExtTable(), columnNames, map));
		}
		fireEvent("after." + tableName + ".get", (Map<String, Object>) map, Constant.KEY_RESULT, result);
		return result;
	}

	/**
	 * 根据提供的Map中的数据，剔除非数据表字段后，创建SQL条件子句，
	 *
	 * @param map
	 * @param td
	 * @param params
	 * @return
	 */
	private String createWhereSqlAndParams(Map<String, Object> map, TableDefinition td, List<Object> params) {
		if (map == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer(100);
		Iterator<Entry<String, Object>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Object> item = iter.next();
			String key = item.getKey().toUpperCase();
			Object value = item.getValue();
			if (td.column(key) && BooleanUtils.isNotEmpty(value)) {
				if (sb.length() > 0) {
					sb.append(" AND ");
				}
				sb.append(key);
				sb.append(" = ?");
				if (td.dateColumn(key) && value instanceof String) {
					value = DateUtils.parseDate((String) value);
				}
				params.add(value);
			}
		}
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * SingleTableService#delete(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	@Transactional
	public void delete(String tableName, Object id) {
		TableDefinition td = getTableDefinition(tableName);
		if (Assert.isNotEmpty(td.getExtTable())) {// 存在主表
			delete(td.getExtTable(), id);
		}
		String name = Constant.COLUMN_NAME_ID;
		Assert.isTrue(td.column(name), String.format("表（%s）中没有对应的查询字段：%s", tableName, name));
		fireEvent("before." + tableName + ".delete", null, "id", id);

		// 如果有父子结构，要删掉子节点数据
		if (td.column(Constant.COLUMN_NAME_PARENT_ID) && td.column(Constant.COLUMN_NAME_LEVEL)
				&& td.column(Constant.COLUMN_NAME_PARENT_PATH)) {
			// 先把所有子节点的id找出来
			Map<String, Object> node = dao.get(tableName, name, id);
			String sql = String.format("select %S,%S from %S where %S like ?", name, Constant.COLUMN_NAME_PARENT_ID,
					tableName, Constant.COLUMN_NAME_PARENT_PATH);
			List<Map<String, Object>> childrens = dao.query(sql,
					node.get(Constant.COLUMN_NAME_PARENT_PATH).toString() + id + "/%");
			if (Assert.isNotEmpty(childrens)) {
				Object[] ids = new Object[childrens.size()];
				for (int i = 0; i < ids.length; i++) {
					ids[i] = childrens.get(i).get(name);
				}
				String strids = StringUtils.join(ids, ",");
				dao.delete(tableName, name, ids);
				// deleteRelatedFilesByIds(td,tableName,strids);
				deleteSubTableDataByIds(td, strids);
			}
		}
		// 删除自己
		dao.delete(tableName, name, id);
		// deleteRelatedFilesById(td, tableName, id);
		deleteSubTableDataById(td, id);
		refreshTableNames(tableName);
		fireEvent("after." + tableName + ".delete", null, "id", id);
	}

	/**
	 * 根据主表主键，删除从表关联数据
	 *
	 * @param td
	 *            数据表定义
	 * @param id
	 *            记录主键
	 */
	private void deleteSubTableDataById(TableDefinition td, Object id) {
		Map<String, SubTableDefinition> subs = td.getSubTableDefinitions();
		if (subs == null) {
			return;
		}
		for (SubTableDefinition sub : subs.values()) {
			String subTableName = sub.getTableName();
			TableDefinition std = getTableDefinition(subTableName);
			if (std.column(sub.getPrimaryForeignKey())) {
				dao.delete(subTableName, sub.getPrimaryForeignKey(), id);
			}
		}
	}

//	/**
//	 * 删除记录对应的所有关联文件
//	 * 
//	 * @param td
//	 *            数据表定义
//	 * @param tableName
//	 *            数据表名
//	 * @param id
//	 *            主键值
//	 */
	// private void deleteRelatedFilesById(TableDefinition td, String tableName,
	// Object id) {
	// if (td.getFileColumns().size() > 0) {
	// fms.unRegister(String.valueOf(id), tableName);
	// }
	// }

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * SingleTableService#delete(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	@Transactional
	public void delete(String tableName, Map<String, Object> map) {
		TableDefinition td = getTableDefinition(tableName);
		if (Assert.isNotEmpty(td.getExtTable())) {// 存在主表
			delete(td.getExtTable(), map);
		}
		Object id = map.get(Constant.COLUMN_NAME_ID);
		if (map.size() == 1 && td.column(Constant.COLUMN_NAME_ID) && BooleanUtils.isNotEmpty(id)) {
			delete(tableName,id);
			return;
		}
		fireEvent("before." + tableName + ".delete", map);
		List<Object> params = CollectionUtils.list();
		// 删除拼接条件时，不考虑扩展表数据
		String where = createQueryWhereSqlAndParams(map, td, params, false,false);
		Assert.notEmpty(params, "删除条件不能为空");
		// deleteRelatedFilesByParams(td, tableName, where, params.toArray());
		deleteSubTableDataByParams(td, tableName, where, params.toArray());
		String sql = String.format("DELETE FROM %S WHERE %s", tableName, where);
		dao.update(sql, params.toArray());
		fireEvent("after." + tableName + ".delete", map);
	}

	/**
	 * 根据主表条件，删除子表数据
	 *
	 * @param td
	 *            主表定义
	 * @param tableName
	 *            主表名称
	 * @param map
	 *            参数集合(主表条件，不一定包含主表主键值)
	 */
	private void deleteSubTableDataByParams(TableDefinition td, String tableName, String where, Object[] array) {
		Map<String, SubTableDefinition> subs = td.getSubTableDefinitions();
		if (subs == null) {
			return;
		}
		String sql = "DELETE FROM %S B WHERE EXISTS (SELECT 1 FROM %S A WHERE B.%S = A.%S AND %S)";
		for (SubTableDefinition sub : subs.values()) {
			String subTableName = sub.getTableName();
			String pfk = sub.getPrimaryForeignKey();
			TableDefinition std = getTableDefinition(subTableName);
			if (std.column(pfk)) {
				dao.update(String.format(sql, subTableName, tableName, pfk, Constant.COLUMN_NAME_ID, where), array);
			}
		}
	}

	/**
	 * 删除记录对应的所有关联文件
	 *
	 * @param td
	 *            数据表定义
	 * @param tableName
	 *            数据表名
	 * @param where
	 *            查询条件子句
	 * @param values
	 *            查询条件参数值
	 */
	// private void deleteRelatedFilesByParams(TableDefinition td, String
	// tableName, String where, Object[] values) {
	// if (td.getFileColumns().size() > 0) {
	// String sql = String.format("SELECT %s FROM %S WHERE %s",
	// Constant.COLUMN_NAME_ID, tableName, where);
	// List<Map<String, Object>> idMaps = dao.query(sql, values);
	// for (Map<String, Object> map : idMaps) {
	// fms.unRegister(map.get(Constant.COLUMN_NAME_ID).toString(), tableName);
	// }
	// }
	// }

	/*
	 * (non-Javadoc)
	 *
	 * @see SingleTableService#deleteAll(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	@Transactional
	public void deleteAll(String tableName, String ids) {
		TableDefinition td = getTableDefinition(tableName);
		if (Assert.isNotEmpty(td.getExtTable())) {// 存在主表
			dao.delete(td.getExtTable(), Constant.COLUMN_NAME_ID, StringUtils.splitAndStrip(ids, ","));

			TableDefinition mTd = getTableDefinition(td.getExtTable());
			if(Assert.isNotEmpty(mTd.getExtTable())) {//存在顶层主表
				dao.delete(mTd.getExtTable(), Constant.COLUMN_NAME_ID, StringUtils.splitAndStrip(ids, ","));
			}
		}
		String name = Constant.COLUMN_NAME_ID;
		Assert.isTrue(td.column(name), String.format("表（%s）中没有对应的查询字段：%s", tableName, name));
		fireEvent("before." + tableName + ".deleteAll", null, "ids", ids);
		dao.delete(tableName, name, StringUtils.splitAndStrip(ids, ","));
		// deleteRelatedFilesByIds(td, tableName, ids);
		deleteSubTableDataByIds(td, ids);
		refreshTableNames(tableName);
		fireEvent("after." + tableName + ".deleteAll", null, "ids", ids);
	}

	/**
	 * 根据主表主键值集合，删除从表关联数据
	 *
	 * @param td
	 *            数据表定义
	 * @param ids
	 *            记录主键字符串，多值时用逗号隔开
	 */
	private void deleteSubTableDataByIds(TableDefinition td, String ids) {
		Map<String, SubTableDefinition> subs = td.getSubTableDefinitions();
		if (subs == null) {
			return;
		}
		Object[] idArray = StringUtils.splitAndStrip(ids, ',');
		String sql = "DELETE FROM %S WHERE %S in (?";
		for(int i=1;i<idArray.length;i++){
			sql += ",?";
		}
		sql += ")";
		for (SubTableDefinition sub : subs.values()) {
			String subTableName = sub.getTableName();
			String pfk = sub.getPrimaryForeignKey();
			TableDefinition std = getTableDefinition(subTableName);
			if (std.column(pfk)) {
				dao.update(String.format(sql, subTableName, pfk), idArray);
			}
		}
	}

	/**
	 * 删除记录对应的所有关联文件
	 *
	 * @param td
	 *            数据表定义
	 * @param tableName
	 *            数据表名
	 * @param id
	 *            主键值
	 */
	// private void deleteRelatedFilesByIds(TableDefinition td, String
	// tableName, String ids) {
	// if (td.getFileColumns().size() > 0) {
	// fms.unRegister(ids, tableName);
	// }
	// }

	/**
	 * @param tableName
	 */
	private void refreshTableNames(String tableName) {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * SingleTableService#unique(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Boolean unique(String tableName, String id, Map<String, Object> map) {
		TableDefinition td = getTableDefinition(tableName);
		fireEvent("before." + tableName + ".unique", map, "id", id);
		boolean result = true;
		List<Object> params = CollectionUtils.list();
		String where = createUniqueWhereSqlAndParams(map, td, params);
		if (BooleanUtils.isEmpty(where)) {
			return result;
		}
		String sql;
		if (BooleanUtils.isEmpty(id)) {
			sql = String.format("SELECT COUNT(*) FROM %S WHERE %s", tableName, where);
		} else {
			sql = String.format("SELECT COUNT(*) FROM %S WHERE %s and %s <> ?", tableName, where,
					Constant.COLUMN_NAME_ID);
			params.add(id);
		}
		result = dao.queryForObject(sql, Integer.class, params.toArray()) <= 0;
		fireEvent("after." + tableName + ".unique", map, "id", id, Constant.KEY_RESULT, result);
		return result;
	}

	/**
	 * 根据提供的Map中的数据，剔除非数据表字段、主键后，创建SQL条件子句，
	 *
	 * @param map
	 * @param td
	 * @param params
	 * @return
	 */
	private String createUniqueWhereSqlAndParams(Map<String, Object> map, TableDefinition td, List<Object> params) {
		if (map == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer(100);
		Iterator<Entry<String, Object>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Object> item = iter.next();
			String key = item.getKey().toUpperCase();
			Object value = item.getValue();
			if (!Constant.COLUMN_NAME_ID.equals(key) && td.column(key) && BooleanUtils.isNotEmpty(value)) {
				if (sb.length() > 0) {
					sb.append(" AND ");
				}
				sb.append(key);
				sb.append(" = ?");
				if (td.dateColumn(key) && value instanceof String) {
					value = DateUtils.parseDate((String) value);
				}
				params.add(value);
			}
		}
		return sb.toString();
	}

	/**
	 * 根据数据表字段名生成有效参数集合
	 *
	 * @param map
	 *            参数集合
	 * @param td
	 *            数据表字段名集合
	 */
	private Map<String, Object> createValidParams(Map<String, Object> map, TableDefinition td) {
		Map<String, Object> valid = new HashMap<String, Object>();
		Object[] keys = map.keySet().toArray();
		for (Object key : keys) {
			if (td.column(key)) {
				Object value = map.get(key);
				if (value instanceof Iterable) {
					value = StringUtils.join((Iterable<?>) value, ',');
				}
				if (td.dateColumn(key)) {
					if (value instanceof String && BooleanUtils.isNotEmpty(value)) {
						String time = (String) value;
						if(time.endsWith("Z")) {
							time = time.replace("Z", " UTC");
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
							try {
								value = format.parse(time);
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}else {
							value = DateUtils.parseDate((String) value);
						}

					} else if (value instanceof Long) {
						value = new Date((Long) value);
					}else if(BooleanUtils.isEmpty(value)){
						value = null;
					}

				}
				valid.put((String) key, value);
			}
		}
		return valid;
	}

	/**
	 * 阻止非注册的数据表执行相应的服务
	 *
	 * @param tableName
	 *            数据表名
	 */
	private TableDefinition getTableDefinition(String tableName) {
		TableDefinition td = null;
		List<Map<String, Object>> tables = dao.query(
				"SELECT C_ID, C_NAME, C_FILECOLUMNS, C_SUBTABLES,C_EXT_TABLE FROM " + Constant.TABLES_TABLE_NAME);
		for (Map<String, Object> table : tables) {
			String name = ((String) table.get("C_NAME")).toUpperCase();
			String fileColumns = (String) table.get("C_FILECOLUMNS");
			String subtables = (String) table.get("C_SUBTABLES");
			String mainTb = (String) table.get("C_EXT_TABLE");
			if (name.equals(tableName.toUpperCase())) {
				td = new TableDefinition(fileColumns, subtables, mainTb);
				break;
			}
		}
		if (td == null) {
			throw new UnsupportedOperationException(String.format("当前数据表%s服务未定义！", tableName));
		}
		if (!td.columnsInitialized()) {
			List<ColumnDefinition> columnDefinitions = dao.getTableColumnNames(tableName);
			List<String> columns = new ArrayList<String>();
			List<String> dateColumns = new ArrayList<String>();
			for (ColumnDefinition definition : columnDefinitions) {
				switch (definition.getType()) {
				case Types.DATE:
				case Types.TIME:
				case Types.TIMESTAMP:
					dateColumns.add(definition.getName());
				default:
					break;
				}
				columns.add(definition.getName());
			}
			td.setColumns(columns);
			td.setDateColumns(dateColumns);
		}
		return td;
	}

	/**
	 * 触发事件
	 *
	 * @param eventName
	 *            事件名称
	 * @param map
	 *            参数集合
	 * @param other
	 *            附加参数key、value对
	 */
	private void fireEvent(String eventName, Map<String, Object> map, Object... other) {
		if (map == null) {
			map = new HashMap<String, Object>();
		}
		if (other != null && other.length > 1 && other.length % 2 == 0) {
			for (int i = 0; i < other.length; i += 2) {
				map.put((String) other[i], other[i + 1]);
			}
		}
		EventManager.fire(eventName, map);
	}

	@Override
	public List<String> getTableColumnNames(String tableName) {
		Set<String> tmpList = new HashSet<String>();
		List<String> rtnList = new ArrayList<String>();
		TableDefinition td = getTableDefinition(tableName);
		tmpList.addAll(td.getColumns());
		
		// 增加扩展表配置字段
		if(Assert.isNotEmpty(td.getExtTable())) {
			TableDefinition td1 = getTableDefinition(td.getExtTable());
			tmpList.addAll(td1.getColumns());

			if(Assert.isNotEmpty(td1.getExtTable())) {
				TableDefinition td2 = getTableDefinition(td1.getExtTable());
				tmpList.addAll(td2.getColumns());
			}
		}
		rtnList.addAll(tmpList);
		return rtnList;
	}

	@Override
	public Map<String,Object> getTableFields(String tableName){
		Map<String,Object> ret = new HashMap<String,Object>();
		TableDefinition td = getTableDefinition(tableName);
		for(String key : td.getColumns()) {
			ret.put(key, "");
		}
		if(Assert.isNotEmpty(td.getExtTable())) {
			TableDefinition td1 = getTableDefinition(td.getExtTable());
			for(String key : td1.getColumns()) {
				ret.put(key, "");
			}
			if(Assert.isNotEmpty(td1.getExtTable())) {
				TableDefinition td2 = getTableDefinition(td1.getExtTable());
				for(String key : td2.getColumns()) {
					ret.put(key, "");
				}
			}
		}

		return ret;
	}

	@Override
	@Transactional
	public void move(String tableName, Object srcId, Object targetId) {
		// 判断表中是否具有基本的树形数据层次字段
		TableDefinition td = getTableDefinition(tableName);
		Assert.isTrue(
				td.column(Constant.COLUMN_NAME_PARENT_ID) && td.column(Constant.COLUMN_NAME_LEVEL)
						&& td.column(Constant.COLUMN_NAME_PARENT_PATH),
				"执行移动方法的表中必须包含有[C_LEVEL、C_PARENT_ID、C_PARENT_PATH]字段");
		// 判断源数据与目标数据的层次是否有问题
		Map<String, Object> srcNode = get(tableName, "C_PARENT_PATH", srcId);
		Map<String, Object> targetNode = get(tableName, "C_PARENT_PATH", targetId);
		String srcPath = srcNode.get(Constant.COLUMN_NAME_PARENT_PATH).toString();
		String targetPath = "/";
		if (MathUtils.numObj2Long(targetId, 0l) == 0 || targetNode == null) {
			targetId = 0l;
		} else {
			targetPath = targetNode.get(Constant.COLUMN_NAME_PARENT_PATH).toString() + targetId + "/";
		}

		Assert.isTrue(!targetPath.startsWith(srcPath + srcId + "/"), "不能将父节点移动到子节点中");

		// 执行移动逻辑
		// 1、修改源数据的父id为目标id，修改源数据的父path为新父path
		String sql = "UPDATE %S SET %S = ? WHERE %S = ?";
		dao.update(String.format(sql, tableName, Constant.COLUMN_NAME_PARENT_ID, Constant.COLUMN_NAME_ID), targetId,
				srcId);
		// 2、修改源数据所有子节点的父path为新的path
		// TODO 需要考虑mysql与oracle数据库中replace函数的差异性???
		int levelOffset = srcPath.split("/").length - targetPath.split("/").length;
		if (levelOffset > 0) {
			sql = String.format("UPDATE %S SET %S = replace(%S,?,?),C_LEVEL = C_LEVEL - ? WHERE %S LIKE ? or C_ID = ?",
					tableName, Constant.COLUMN_NAME_PARENT_PATH, Constant.COLUMN_NAME_PARENT_PATH,
					Constant.COLUMN_NAME_PARENT_PATH);
		} else {
			levelOffset = 0 - levelOffset;
			sql = String.format("UPDATE %S SET %S = replace(%S,?,?),C_LEVEL = C_LEVEL + ? WHERE %S LIKE ? or C_ID = ?",
					tableName, Constant.COLUMN_NAME_PARENT_PATH, Constant.COLUMN_NAME_PARENT_PATH,
					Constant.COLUMN_NAME_PARENT_PATH);
		}
		dao.update(sql, srcPath, targetPath, levelOffset, srcPath + srcId + "/" + "%", srcId);
	}

	@Override
	public void copy(String tableName, Object srcId, Object targetId) {
		// 判断表中是否具有基本的树形数据层次字段
		TableDefinition td = getTableDefinition(tableName);
		Assert.isTrue(
				td.column(Constant.COLUMN_NAME_PARENT_ID) && td.column(Constant.COLUMN_NAME_LEVEL)
						&& td.column(Constant.COLUMN_NAME_PARENT_PATH),
				"执行复制方法的表中必须包含有[C_LEVEL、C_PARENT_ID、C_PARENT_PATH]字段");
		// 判断源数据与目标数据的层次是否有问题
		Map<String, Object> srcNode = get(tableName, null, srcId);
		Map<String, Object> targetNode = get(tableName, null, targetId);
		String srcPath = srcNode.get(Constant.COLUMN_NAME_PARENT_PATH).toString();
		String targetPath = "/";
		if (MathUtils.numObj2Long(targetId, 0l) == 0 || targetNode == null) {
			targetId = 0l;
		} else {
			targetPath = targetNode.get(Constant.COLUMN_NAME_PARENT_PATH).toString() + targetId + "/";
		}

		Assert.isTrue(!targetPath.startsWith(srcPath + srcId + "/"), "不能将父节点复制到子节点中");

		// 先把顶层节点复制到目标节点下
		srcNode.remove(Constant.COLUMN_NAME_ID);
		srcNode.put(Constant.COLUMN_NAME_PARENT_ID, targetId);
		Long newID = save(tableName, srcNode);

		// 查出src节点下的所有子节点
		String sql = String.format("select %S,%S from %S where %S = ?", Constant.COLUMN_NAME_ID,
				Constant.COLUMN_NAME_PARENT_ID, tableName, Constant.COLUMN_NAME_PARENT_ID);

		List<Map<String, Object>> childrens = dao.query(sql, srcId);
		for (Map<String, Object> cnode : childrens) {
			copy(tableName, cnode.get(Constant.COLUMN_NAME_ID), newID);
		}
	}

	@Override
	public List<String> queryCatalogs() {
        List<Map<String, Object>> query = dao.query("SELECT DISTINCT t.c_catalog FROM tbl_tables t");
        List<String> list = query.stream().map(objectMap -> (String) objectMap.get("C_CATALOG"))
                .filter(s -> Assert.isNotEmpty(s)).collect(Collectors.toList());
        return list;
	}

}
