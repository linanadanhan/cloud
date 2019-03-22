package com.gsoft.portal.system.dynamicview.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.exception.BusinessException;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BooleanUtils;
import com.gsoft.portal.system.dynamicview.service.DynamicViewService;

/**
 * 动态视图业务实现类
 * 
 * @author chenxx
 *
 */
@Service
public class DynamicViewServiceImpl implements DynamicViewService, InitializingBean {

//	private static final String DYNAMIC_VIEW_CACHE = "DYNAMIC_VIEW_CACHE";
//
//	private static final String DYNAMIC_VIEW_NAME_CACHE = "DYNAMIC_VIEW_NAME_CACHE";

	private static final Object DYNAMIC_VIEW_TABLE_NAME = "TBL_VIEWS";

//	private static final String EVENT_NAME_AFTER_VIEWS_DELETEALL = "after." + DYNAMIC_VIEW_TABLE_NAME + ".deleteAll";
//
//	private static final String EVENT_NAME_AFTER_VIEWS_DELETE = "after." + DYNAMIC_VIEW_TABLE_NAME + ".delete";
//
//	private static final String EVENT_NAME_AFTER_VIEWS_MODIFY = "after." + DYNAMIC_VIEW_TABLE_NAME + ".modify";
//
//	private static final String EVENT_NAME_AFTER_VIEWS_SAVE = "after." + DYNAMIC_VIEW_TABLE_NAME + ".save";

//	@Autowired
//	private StringRedisTemplate stringRedisTemplate;

	@Resource
	private BaseDao baseDao;

	@Override
	public PageDto queryDynamicView(String search, Integer page, Integer size) {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		sb.append("select * from tbl_views where 1=1 ");

		if (!Assert.isEmpty(search)) {
			sb.append(" and c_text like ${search} ");
			params.put("search", "%" + search + "%");
		}
		sb.append(" ORDER BY c_id DESC ");
		return baseDao.query(page, size, sb.toString(), params);
	}

	@Override
	public List<Map<String, Object>> queryAll(String viewName, Map<String, Object> map) {
		Map<String, Object> view = getViewByViewName(viewName);
		String dataSourceName = (String) view.get("C_DATASOURCE");
		List<Map<String, Object>> result;
		if (!"_default".equals(dataSourceName) && BooleanUtils.isNotEmpty(dataSourceName)) {
//			DataSourceUtils.switchTo(dataSourceName);
			try {
				if (!Assert.isEmpty(view.get("C_SQL"))) {
					result = baseDao.query((String) view.get("C_SQL"), map);
				}else {
					result = null;
				}
			} finally {
//				DataSourceUtils.restore();
			}
		} else {
			if (!Assert.isEmpty(view.get("C_SQL"))) {
				result = baseDao.query((String) view.get("C_SQL"), map);
			}else {
				result = null;
			}
		}
		return result;
	}

	@Override
	public PageDto queryPage(String viewName, Map<String, Object> map, int pageNum, int pageSize) {
		Map<String, Object> view = getViewByViewName(viewName);
		String dataSourceName = (String) view.get("C_DATASOURCE");
		PageDto result;
		if (!"_default".equals(dataSourceName) && BooleanUtils.isNotEmpty(dataSourceName)) {
//			DataSourceUtils.switchTo(dataSourceName);
			try {
				result = baseDao.query(pageNum, pageSize, (String) view.get("C_SQL"), map);
			} finally {
//				DataSourceUtils.restore();
			}
		} else {
			result = baseDao.query(pageNum, pageSize, (String) view.get("C_SQL"), map);
		}
		return result;
	}

	/**
	 * 获取视图的sql脚本
	 * 
	 * @param viewName
	 * @return
	 */
	private Map<String, Object> getViewByViewName(String viewName) {
		Map<String, Object> view = null; //getViewFromCacheByName(viewName);
		if (view == null) {
			String sql = String.format("SELECT C_ID, C_NAME, C_SQL, C_DATASOURCE,C_PERMISSION FROM %s WHERE C_NAME = ?",
					DYNAMIC_VIEW_TABLE_NAME);
			view = baseDao.load(sql, viewName);
			if (view != null) {
//				Long id = MathUtils.numObj2Long(view.remove(Constant.COLUMN_NAME_ID));
//				putViewNameInCache(id, viewName, view);
			} else {
				throw new BusinessException(String.format("动态视图(%s)未定义", viewName));
			}
		}
		return view;
	}

//	/**
//	 * 根据视图名称从缓存中获取视图信息
//	 * 
//	 * @param viewName
//	 *            视图名称
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	private Map<String, Object> getViewFromCacheByName(String viewName) {
//		try {
//			String viewJson = (String) stringRedisTemplate.opsForHash().get(DYNAMIC_VIEW_CACHE, viewName);
//			return (Map<String, Object>) JsonMapper.fromJson(viewJson, Map.class);
//		} catch (Exception e) {
//			return null;
//		}
//	}

	/**
	 * 根据id缓存视图名称
	 * 
	 * @param id
	 *            视图ID
	 * @param viewName
	 *            视图名称
	 * @param view
	 *            视图属性集合
	 * @return
	 */
//	private void putViewNameInCache(Long id, String viewName, Map<String, Object> view) {
//		try {
//			stringRedisTemplate.opsForHash().put(DYNAMIC_VIEW_NAME_CACHE, MathUtils.stringObj(id), viewName);
//			stringRedisTemplate.opsForHash().put(DYNAMIC_VIEW_CACHE, viewName, JsonMapper.toJson(view));
//		} catch (Exception e) {
//		}
//	}

//	/**
//	 * 触发事件
//	 * 
//	 * @param eventName
//	 *            事件名称
//	 * @param map
//	 *            参数集合
//	 * @param other
//	 *            附加参数key、value对
//	 */
//	private void fireEvent(String eventName, Map<String, Object> map, Object... other) {
//		if (map == null) {
//			map = new HashMap<String, Object>();
//		}
//		if (other != null && other.length > 1 && other.length % 2 == 0) {
//			for (int i = 0; i < other.length; i += 2) {
//				map.put((String) other[i], other[i + 1]);
//			}
//		}
//		EventManager.fire(eventName, map);
//	}

//	@Override
//	public void onEvent(String eventName, Map<String, Object> event) {
//		// 处理修改、删除时
//		Object id = event.get(Constant.COLUMN_NAME_ID);
//		if (BooleanUtils.isEmpty(id)) {
//			id = event.get("id");
//		}
//		if (BooleanUtils.isNotEmpty(id)) {
//			removeViewFromCacheById(id);
//		}
//		// 处理批量删除或修改业务规则时
//		String ids = (String) event.get("ids");
//		if (BooleanUtils.isNotEmpty(ids)) {
//			String[] sIds = StringUtils.splitAndStrip(ids, ",");
//			for (String sid : sIds) {
//				removeViewFromCacheById(sid);
//			}
//		}
//	}

//	/**
//	 * 根据Id从缓存中删除视图信息
//	 * 
//	 * @param id
//	 *            视图ID
//	 */
//	private void removeViewFromCacheById(Object id) {
//		Object viewName = stringRedisTemplate.opsForHash().get(DYNAMIC_VIEW_NAME_CACHE, MathUtils.stringObj(id));
//		if (viewName != null) {
//			if (BooleanUtils.isNotEmpty(viewName)) {
//				stringRedisTemplate.opsForHash().delete(DYNAMIC_VIEW_CACHE, viewName);
//			}
//		}
//	}

	@Override
	public void afterPropertiesSet() throws Exception {
//		EventManager.on(EVENT_NAME_AFTER_VIEWS_SAVE, this);
//		EventManager.on(EVENT_NAME_AFTER_VIEWS_MODIFY, this);
//		EventManager.on(EVENT_NAME_AFTER_VIEWS_DELETE, this);
//		EventManager.on(EVENT_NAME_AFTER_VIEWS_DELETEALL, this);
	}
	
	@Override
	public List<Map<String, Object>> queryAllByParams(String viewName, Map<String, Object> map) {
		Map<String, Object> view = getViewByViewName(viewName);
		String dataSourceName = (String) view.get("C_DATASOURCE");
		List<Map<String, Object>> result;
		if (!"_default".equals(dataSourceName) && BooleanUtils.isNotEmpty(dataSourceName)) {
//			DataSourceUtils.switchTo(dataSourceName);
			try {
				if (!Assert.isEmpty(view.get("C_SQL"))) {
					result = this.queryByParam(viewName, map, view);
				}else {
					result = null;
				}
			} finally {
//				DataSourceUtils.restore();
			}
		} else {
			if (!Assert.isEmpty(view.get("C_SQL"))) {
				result = this.queryByParam(viewName, map, view);
			}else {
				result = null;
			}
		}
		return result;
	}
	private List<Map<String, Object>> queryByParam(String viewName, Map<String, Object> map, Map<String, Object> view){
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		//1.判断map是否有值，若有值则拼接sql
		if(Assert.isEmpty(map)) {
			result = baseDao.query((String) view.get("C_SQL"), map);
		}else {
			//2.判断map中的key是否在view的属性中，true则拼接sql
			List<Map<String, Object>> resList = this.queryAll(viewName, map);
			if (!Assert.isEmpty(resList) && resList.size() > 0) {
				//移除非属性字段//{viewName=view_OPERATOR, dataSourceType=view, C_PARENT_ID=0, tableName=view_OPERATOR}
				map.remove("viewName");
				map.remove("dataSourceType");
				map.remove("tableName");
				
				StringBuffer sb = new StringBuffer();
				sb.append("select * from ( ");
				sb.append(view.get("C_SQL").toString());
				sb.append(" ) t where 1=1");
				
				/*List<Object> params = new ArrayList<Object>();*/
				Map<String, Object> oneMapData = resList.get(0);
				for (Map.Entry<String, Object> param : map.entrySet()) {
					boolean flag = false;
					String tempField = "";
					for (Map.Entry<String, Object> field : oneMapData.entrySet()) {
						if(field.getKey().toUpperCase().equals(param.getKey().toUpperCase())) {
							flag = true;
							tempField = param.getKey();
							break;
						}
					}
					if(flag) {
						/*sb.append(" and ").append(param.getKey().toUpperCase()).append(" = ?");
						params.add(param.getValue());*/
						sb.append(" and ").append(param.getKey().toUpperCase()).append(" = ${").append(tempField).append("}");
					}
				}
				// 注释的3部分的代码，使用filed=?进行查询不兼容动态视图中新增where参数条件；使用filed=${param}可以兼容view中的参数
				//result = baseDao.query(sb.toString(), params.toArray()); 
				result = baseDao.query(sb.toString(), map);
			}else {
				result = baseDao.query((String) view.get("C_SQL"), map);
			}
		}
		return result;
	}
}
