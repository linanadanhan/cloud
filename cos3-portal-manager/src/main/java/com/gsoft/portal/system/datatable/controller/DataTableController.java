package com.gsoft.portal.system.datatable.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import com.gsoft.portal.system.datatable.service.DataTableService;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ResponseMessageDto;
import com.gsoft.cos3.dto.SuccessDto;
import com.gsoft.cos3.table.service.SingleTableService;
import springfox.documentation.annotations.ApiIgnore;


@Controller
@ApiIgnore
public class DataTableController {

	@Resource
	private SingleTableService singleTableService;

	@Resource
	private DataTableService dataTableService;
	/**
	 * 根据条件查询列表数据，不分页
	 *
	 * @param tableName
	 *            数据表名
	 * @param columnNames
	 *            查询的字段名称，多个字段名以逗号隔开
	 * @param map
	 *            查询条件，均以相等匹配
	 * @param order
	 *            排序方式
	 * @param sort
	 *            排序字段，多个字段名以逗号隔开
	 * @return
	 */
	@RequestMapping(value = "/table/{tableName}/queryAll", method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, Object>> queryAll(@PathVariable String tableName, @RequestParam String columnNames,
			@RequestParam MultiValueMap<String, Object> map, @RequestParam(defaultValue = "ASC") String order,
			@RequestParam(defaultValue = "C_ID") String sort) {
		return singleTableService.queryAll(tableName, columnNames, convertMuiltiValueMap(map), sort, order);
	}

	/**
	 * 根据条件查询树形数据，带层级结构
	 *
	 * @param modelName
	 * @param map
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 *
	 * @RequestMapping(value = { "/table/{tableName}/queryTree" })
	 * @ResponseBody public List<TreeNode> queryTree(@PathVariable String tableName,
	 *               String columnNames,
	 * @RequestParam MultiValueMap<String, Object> map, @RequestParam(defaultValue =
	 *               "ASC") String order,
	 * @RequestParam(defaultValue = Constant.COLUMN_NAME_CREATE_TIME) String sort) {
	 *                            return service.queryTree(tableName, columnNames,
	 *                            convertMuiltiValueMap(map), sort, order); }
	 */

	/**
	 * 根据条件查询列表数据，分页
	 *
	 * @param tableName
	 *            数据表名
	 * @param columnNames
	 *            查询的字段名称，多个字段名以逗号隔开
	 * @param map
	 *            查询条件，均以相等匹配
	 * @param order
	 *            排序方式
	 * @param sort
	 *            排序字段，多个字段名以逗号隔开
	 * @return
	 */
	// @RequestMapping(value =
	// "/table/{tableName}/{page}/{rows}/{order}/{sort}/queryPage", method =
	// RequestMethod.POST)
	// @ResponseBody
	// public PageDto queryPage(@PathVariable String tableName, @RequestBody
	// Map<String, Object> map,
	// @PathVariable int page, @PathVariable int rows, @PathVariable String order,
	// @PathVariable String sort) {
	// return singleTableService.queryPage(tableName, null, map, sort, order, page,
	// rows);
	// }

	/**
	 * 根据条件查询列表数据，分页
	 *
	 * @param tableName
	 *            数据表名
	 * @param columnNames
	 *            查询的字段名称，多个字段名以逗号隔开
	 * @param map
	 *            查询条件，均以相等匹配
	 * @param order
	 *            排序方式
	 * @param sort
	 *            排序字段，多个字段名以逗号隔开
	 * @return
	 */
	@RequestMapping(value = "/table/{tableName}/queryPage", method = RequestMethod.GET)
	@ResponseBody
	public PageDto queryPage(@PathVariable String tableName, @RequestParam String columnNames,
			@RequestParam MultiValueMap<String, Object> map, @RequestParam int page, @RequestParam int rows,
			@RequestParam(defaultValue = "ASC") String order, @RequestParam(defaultValue = "C_ID") String sort) {
		return singleTableService.queryPage(tableName, columnNames, convertMuiltiValueMap(map), sort, order, page,
				rows);
	}

	// /**
	// * 保存一条数据，包括新增和修改
	// *
	// * @param modelName
	// * @param map
	// * @return
	// */
	// @RequestMapping(value = "table/{tableName}/save" ,method =
	// RequestMethod.POST)
	// @ResponseBody
	// public ResponseMessageDto save(@PathVariable String tableName, @RequestBody
	// Map<String, Object> map) {
	//// Long id = singleTableService.save(tableName, convertMuiltiValueMap(map));
	// Long id = singleTableService.save(tableName, map);
	// return new SuccessDto("数据保存成功", id);
	// }

	/**
	 * 保存一条数据，包括新增和修改
	 *
	 * @param modelName
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "table/{tableName}/save", method = RequestMethod.POST)
	@ResponseBody
	public ResponseMessageDto save(@PathVariable String tableName,@RequestBody Map<String, Object> map) {
		// Long id = singleTableService.save(tableName, convertMuiltiValueMap(map));
		// Map<String, Object> pramas = new HashMap<String,Object>();
		// try {
		// pramas = JsonUtils.fromJson(map.get("map"), Map.class);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//Long id = singleTableService.save(tableName, map);
		Long id = dataTableService.save(tableName,map);
		return new SuccessDto("数据保存成功", id);
	}

	// /**
	// * 修改一条或多条数据
	// *
	// * @param modelName
	// * 数据表名
	// * @param ids
	// * 主键值，多值用逗号隔开
	// * @param map
	// * 需要修改的参数值
	// * @return
	// */
	// @RequestMapping(value = "table/{tableName}/modify.action")
	// @ResponseBody
	// public ResponseMessageDto modify(@PathVariable String tableName,
	// @RequestParam String ids,
	// @RequestParam MultiValueMap<String, Object> map) {
	// singleTableService.modify(tableName, ids, convertMuiltiValueMap(map));
	// return new SuccessDto("数据保存成功", ids);
	// }

	/**
	 * 根据主键或唯一性约束的字段加载一条数据详情
	 *
	 * @param tableName
	 *            数据表名
	 * @param id
	 *            数据主键值
	 * @param name
	 *            唯一性约束的属性名
	 * @param value
	 *            唯一性约束的属性值
	 * @return
	 */
	@RequestMapping(value = "/table/{tableName}/get", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> get(@PathVariable String tableName, @RequestParam String columnNames,
			@RequestParam MultiValueMap<String, Object> map) {
		Map<String, Object> item = singleTableService.get(tableName, columnNames, convertMuiltiValueMap(map));
		return item != null ? item : new HashMap<String,Object>();
	}

	/**
	 * 根据表名获取字段（多用于新增页面获取表结构）
	 *
	 * @param tableName
	 * @return
	 */
	@RequestMapping(value = "/table/{tableName}/getfield", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getFeild(@PathVariable String tableName) {
		return singleTableService.getTableFields(tableName);
	}

	/**
	 * 删除指定的数据
	 *
	 * @param tableName
	 *            数据表名
	 * @param id
	 *            数据主键值
	 * @return
	 */
	@RequestMapping(value = "/table/{tableName}/delete", method = RequestMethod.GET)
	@ResponseBody
	public ResponseMessageDto delete(@PathVariable String tableName,  @RequestParam MultiValueMap<String, Object> map) {
		singleTableService.delete(tableName, convertMuiltiValueMap(map));
		return new SuccessDto("数据删除成功");
	}

	/**
	 * 删除指定的数据
	 *
	 * @param tableName
	 *            数据表名
	 * @param id
	 *            数据主键值
	 * @return
	 */
	@RequestMapping(value = "/table/{tableName}/deleteAll")
	@ResponseBody
	public ResponseMessageDto deleteAll(@PathVariable String tableName, @RequestParam String ids) {
		singleTableService.deleteAll(tableName, ids);
		return new SuccessDto("数据保存成功", ids);
	}


	@RequestMapping(value = "/table/queryCatalogs")
	@ResponseBody
	public List<String> queryCatalogs() {
		return singleTableService.queryCatalogs();
	}

	 /**
	 * 排除指定记录外，判断属性的值是否唯一
	 *
	 * @param tableName
	 * 数据表名
	 * @param id
	 * 需要排除的记录的主键值
	 * @param key
	 * 需要判断唯一性的属性名称
	 * @param value
	 * 需要判断唯一性的属性值
	 * @return
	 */
	 @RequestMapping(value = "/table/{tableName}/unique")
	 @ResponseBody
	 public Boolean unique(@PathVariable String tableName, @RequestParam String id,
	   @RequestParam MultiValueMap<String, Object> map) {
		 return singleTableService.unique(tableName, id, convertMuiltiValueMap(map));
	 }

	/**
	 * 将多值Map转化为单值Map，其中多值的属性用逗号隔开连接成字符串
	 *
	 * @param mmap
	 *            多值Map
	 * @return
	 */
	private Map<String, Object> convertMuiltiValueMap(MultiValueMap<String, Object> mmap) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Entry<String, List<Object>> entry : mmap.entrySet()) {
			List<Object> values = entry.getValue();
			if (values.size() == 1) {
				map.put(entry.getKey(), values.get(0));
			} else if (values.size() > 1) {
				map.put(entry.getKey(), values);
			}
		}
		return map;
	}

//	@InitBinder
//	public void initBinder(WebDataBinder binder) {
//	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	dateFormat.setLenient(true);
//	binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
//	}
}
