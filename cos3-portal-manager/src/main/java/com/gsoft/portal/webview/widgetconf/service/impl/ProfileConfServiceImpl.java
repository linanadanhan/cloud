package com.gsoft.portal.webview.widgetconf.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.table.service.SingleTableService;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.webview.badge.dto.PageBadgeDto;
import com.gsoft.portal.webview.badge.service.PageBadgeService;
import com.gsoft.portal.webview.widgetconf.dto.ProfileConfDto;
import com.gsoft.portal.webview.widgetconf.entity.ProfileConfEntity;
import com.gsoft.portal.webview.widgetconf.persistence.ProfileConfPersistence;
import com.gsoft.portal.webview.widgetconf.service.ProfileConfService;

/**
 * 系统偏好配置管理Service实现类
 * 
 * @author SN
 *
 */
@Service
public class ProfileConfServiceImpl implements ProfileConfService {
	
	@Resource
	ProfileConfPersistence profileConfPersistence;
	
	@Resource
	private SingleTableService singleTableService;
	
	@Resource
	private PageBadgeService pageBadgeService;
	
	@Resource
	BaseDao baseDao;

	@Override
	public ProfileConfDto getProfileConfInfo(String widgetId) {
		ProfileConfEntity entity = profileConfPersistence.findProfileConfInfo(widgetId);
		return BeanUtils.convert(entity, ProfileConfDto.class);
	}

	@Override
	@Transactional
	public ProfileConfDto saveProfileConf(ProfileConfDto profileConfDto, String delWidgetIds, String pageUuId) throws Exception {
		ProfileConfEntity entity = BeanUtils.convert(profileConfDto, ProfileConfEntity.class);
		ProfileConfEntity reEntity = profileConfPersistence.save(entity);

		// 保存页面widget实例的badge信息
		if (!Assert.isEmpty(pageUuId)) {
			String confJson = reEntity.getJson();
			JSONObject jo = new JSONObject(confJson);
			if (jo.has("badge")) {
				PageBadgeDto pageBadgeDto = pageBadgeService.getPageBadgeInfo(pageUuId, entity.getWidgetUuId());
				if (!Assert.isEmpty(jo.get("badge"))) {
					if (!Assert.isEmpty(pageBadgeDto.getId())) {// 修改
						pageBadgeDto.setBadgeName(MathUtils.stringObj(jo.get("badge")));
					}else {// 新增
						pageBadgeDto = new PageBadgeDto();
						pageBadgeDto.setPageUuId(pageUuId);
						pageBadgeDto.setWidgetUuId(entity.getWidgetUuId());
						pageBadgeDto.setBadgeName(MathUtils.stringObj(jo.get("badge")));
					}
					pageBadgeService.savePageBadgeInfo(pageBadgeDto);
				}else {
					if (!Assert.isEmpty(pageBadgeDto.getId())) {
						baseDao.update("DELETE FROM cos_portal_page_badget WHERE c_widget_uu_id = ? and c_page_uu_id = ? ", entity.getWidgetUuId(), pageUuId);
					}
				}
			}
		}
		
		if (!Assert.isEmpty(delWidgetIds)) {
			String[] oldArr = delWidgetIds.split(",");
			for (String widgetId : oldArr) {
				baseDao.update("DELETE FROM cos_sys_profile WHERE c_widget_uu_id = ? ", widgetId);
				if (!Assert.isEmpty(pageUuId)) {
					baseDao.update("DELETE FROM cos_portal_page_badget WHERE c_widget_uu_id = ? and c_page_uu_id = ? ", widgetId, pageUuId);
				}
			}
		}
		
		return BeanUtils.convert(reEntity, ProfileConfDto.class);
	}

	/**
	 * 数据表关联关系处理
	 * @param json
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
//	@SuppressWarnings("unchecked")
//	private void handleDataTableRel(String json) throws JSONException, JsonParseException, JsonMappingException, IOException {
//		JSONObject jo = new JSONObject(json);
//		if (jo.has("dataTableRel")) {
//			String dataTableRel = MathUtils.stringObj(jo.get("dataTableRel"));
//			String dataTable = MathUtils.stringObj(jo.get("dataTable"));
//			
//			// 查询原数据表配置信息
//			Map<String, Object> paraMap = new HashMap<String, Object>();
//			paraMap.put("C_NAME", dataTable);
//			Map<String, Object> rtnMap = singleTableService.get("tbl_tables", "", paraMap);
//			
//			if (Assert.isEmpty(rtnMap)) {
//				Long id = singleTableService.save("tbl_tables", paraMap);// 若未注册则重新注册写入
//				rtnMap = singleTableService.get("tbl_tables", "", id);
//			}
//			
//			if ("0".equals(dataTableRel)) {// 主扩
//				String extTables = MathUtils.stringObj(rtnMap.get("C_EXT_TABLE"));
//				if (!Assert.isEmpty(extTables)) {
//					String[] extArr = extTables.split(",");
//					if (!useLoop(extArr,dataTable)) {
//						extTables = extTables + "," + dataTable;
//					}
//				}else {
//					extTables = dataTable;
//				}
//				
//				rtnMap.put("C_EXT_TABLE", extTables);
//				singleTableService.modify("tbl_tables", MathUtils.stringObj(rtnMap.get("C_ID")), rtnMap);
//				
//			}else if ("1".equals(dataTableRel)) {// 主从
//				String dataField = MathUtils.stringObj(jo.get("dataField"));
//				String subTables = MathUtils.stringObj(rtnMap.get("C_SUBTABLES"));
//				
//				Map<String, Object> subJo = new HashMap<String, Object>();
//				
//				if (!Assert.isEmpty(subTables)) {
//					subJo = JsonMapper.fromJson(subTables, Map.class);
//					SubTableDefinition expJo = (SubTableDefinition) subJo.get("C_EXPS");
//					expJo.setTableName(dataTable);
//					expJo.setPrimaryForeignKey(dataField);
//					subJo.put("C_EXPS", expJo);
//				}else {
//					SubTableDefinition subTableDefinition = new SubTableDefinition();
//					subTableDefinition.setTableName(dataTable);
//					subTableDefinition.setPrimaryForeignKey(dataField);
//					subJo.put("C_EXPS", subTableDefinition);
//				}
//				rtnMap.put("C_SUBTABLES", JsonMapper.toJson(subJo));
//				singleTableService.modify("tbl_tables", MathUtils.stringObj(rtnMap.get("C_ID")), rtnMap);
//			}
//		}
//	}
	
	/**
	 * 判断数组中对象是否已存在
	 * @param arr
	 * @param targetValue
	 * @return
	 */
	public static boolean useLoop(String[] arr, String targetValue) {
	    for(String s: arr){
	        if(s.equals(targetValue))
	            return true;
	    }
	    return false;
	}

	@Override
	public List<ProfileConfDto> getRelInstanceList(String widgetUuId) {
		List<ProfileConfEntity> entityList = profileConfPersistence.getRelInstanceList(widgetUuId);
		return BeanUtils.convert(entityList, ProfileConfDto.class);
	}

	@Override
	public void handleBusinessCompInstanceConf(List<Map<String, Object>> syncList) {
		baseDao.modify("cos_sys_profile", "C_ID", syncList);
	}

	@Override
	public JSONObject getSysConfListByPageUuId(String pageUuId) throws JSONException {
		JSONObject jo = null;
		List<ProfileConfEntity> entityList = profileConfPersistence.getSysConfListByPageUuId(pageUuId);
		if (!Assert.isEmpty(entityList) && entityList.size() > 0) {
			JSONObject paramJo = null;
			jo = new JSONObject();
			for (ProfileConfEntity entity : entityList) {
				String json = entity.getJson();
				if (!Assert.isEmpty(json)) {
					paramJo = new JSONObject(json);
					jo.put(entity.getWidgetUuId(), paramJo);
				}
			}
		}
		return jo;
	}

	@Override
	public void batchSave(List<ProfileConfDto> widgetConfList) {
		profileConfPersistence.save(BeanUtils.convert(widgetConfList, ProfileConfEntity.class));
	}
}
