package com.gsoft.portal.component.compmgr.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gsoft.cos3.datasource.DynamicDataSourceContextHolder;
import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.cos3.util.FileUtils;
import com.gsoft.cos3.util.HttpClientUtils;
import com.gsoft.cos3.util.JsonMapper;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.portal.common.constans.ParameterConstant;
import com.gsoft.portal.component.compmgr.dto.ComponentDto;
import com.gsoft.portal.component.compmgr.dto.ComponentPackageDto;
import com.gsoft.portal.component.compmgr.entity.ComponentEntity;
import com.gsoft.portal.component.compmgr.entity.ComponentPackageEntity;
import com.gsoft.portal.component.compmgr.persistence.ComponentPackagePersistence;
import com.gsoft.portal.component.compmgr.persistence.ComponentPersistence;
import com.gsoft.portal.component.compmgr.service.ComponentService;
import com.gsoft.portal.system.basicdata.service.ParameterService;

/**
 * 部件管理Service实现类
 * 
 * @author SN
 *
 */
@Service
public class ComponentServiceImpl implements ComponentService {

	@Resource
	ComponentPersistence componentPersistence;
	
	@Resource
	ComponentPackagePersistence componentPackagePersistence;

	@Resource
	BaseDao baseDao;

	@Resource
	ParameterService parameterService;
	
	@Value("${remote.component.package.list.interface}")
	private String url;

	@Override
	public PageDto queryComponentDataTable(String search, Integer page, Integer size, String sortProp, String order) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT c.* FROM cos_portal_component c where 1=1 ");
		Map<String, Object> params = new HashMap<String, Object>();

		if (Assert.isNotEmpty(search)) {
			sb.append(" AND c.c_name like ${search} ");
			params.put("search", "%" + search + "%");
		}
		sb.append(" ORDER BY c.c_id DESC ");

		PageDto pageDto = baseDao.query(page, size, sb.toString(), params);
		return pageDto;
	}

	@Override
	public void updateStatus(Long id, Boolean status) {
		componentPersistence.updateStatus(id, status);
	}

	@Override
	@Transactional
	public ReturnDto uninstall(String code) {
		// 1. 删除部件信息表
		baseDao.update("delete from cos_portal_component where c_code = ? ", code);

		// 2. 删除部件下的组件信息（主题、布局、widget、修饰器）
		baseDao.update("delete from cos_portal_theme where c_project_code = ? ", code);
		baseDao.update("delete from cos_portal_layout where c_project_code = ? ", code);
		baseDao.update("delete from cos_portal_widget where c_project_code = ? ", code);
		baseDao.update("delete from cos_portal_decorate where c_project_code = ? ", code);

		// 3. 清空服务器上已上传的组件信息
		String destDir = parameterService.getParmValueByKey(ParameterConstant.PORTAL_MODULES_PATH[0],
				ParameterConstant.PORTAL_MODULES_PATH[1]);
		if (!destDir.endsWith("/")) {
			destDir = destDir + "/";
		}
		File lineFile = new File(destDir + code + "/");
		FileUtils.deleteQuietly(lineFile);
		return new ReturnDto("卸载成功！");
	}

	@Override
	public List<Map<String, Object>> queryComponentDetailList(String compType, String compCode) {

		StringBuffer sb = new StringBuffer();
		Map<String, Object> parmas = new HashMap<String, Object>();

		if (Assert.isEmpty(compType)) {// 查询所有主题、布局、修饰器、widget
			sb.append(
					"SELECT t.c_code,t.c_name, 'theme' as c_type FROM cos_portal_theme t WHERE t.c_project_code = ${compCode} ");
			sb.append("UNION ALL ");
			sb.append(
					"SELECT l.c_code,l.c_name, 'layout' as c_type FROM cos_portal_layout l WHERE l.c_project_code = ${compCode} ");
			sb.append("UNION ALL ");
			sb.append(
					"SELECT w.c_code,w.c_name, 'widget' as c_type FROM cos_portal_widget w WHERE w.c_project_code = ${compCode} ");
			sb.append("UNION ALL ");
			sb.append(
					"SELECT d.c_code,d.c_name, 'decorate' as c_type FROM cos_portal_decorate d WHERE d.c_project_code = ${compCode} ");

		} else {
			if ("theme".equals(compType)) {
				sb.append(
						"SELECT t.c_code,t.c_name, 'theme' as c_type FROM cos_portal_theme t WHERE t.c_project_code = ${compCode} ");

			} else if ("layout".equals(compType)) {
				sb.append(
						"SELECT l.c_code,l.c_name, 'layout' as c_type FROM cos_portal_layout l WHERE l.c_project_code = ${compCode} ");

			} else if ("decorate".equals(compType)) {
				sb.append(
						"SELECT d.c_code,d.c_name, 'decorate' as c_type FROM cos_portal_decorate d WHERE d.c_project_code = ${compCode} ");

			} else if ("widget".equals(compType)) {
				sb.append(
						"SELECT w.c_code,w.c_name, 'widget' as c_type FROM cos_portal_widget w WHERE w.c_project_code = ${compCode} ");
			}
		}
		parmas.put("compCode", compCode);
		return baseDao.query(sb.toString(), parmas);
	}

	@Override
	public ComponentDto saveComponentInfo(ComponentDto componentDto) {
		ComponentEntity entity = BeanUtils.convert(componentDto, ComponentEntity.class);
		ComponentEntity reEntity = componentPersistence.save(entity);
		return BeanUtils.convert(reEntity, ComponentDto.class);
	}

	@Override
	public ComponentDto getComponentByCode(String compCode) {
		ComponentEntity entity = componentPersistence.getComponentByCode(compCode);
		return BeanUtils.convert(entity, ComponentDto.class);
	}

	@Override
	public ReturnDto getAllSysCompList() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT w.c_id, w.c_code, w.c_name, w.c_desc, w.c_params FROM cos_portal_widget w ");
		sb.append("WHERE EXISTS (SELECT c.* FROM cos_portal_component c ");
		sb.append("WHERE c.c_code = w.c_project_code AND c.c_status = 1) and w.c_is_business = 0 ");
		
		List<Map<String, Object>> resList = baseDao.query(sb.toString());
		List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();
		
		if (!Assert.isEmpty(resList) && resList.size() > 0) {
			Map<String, Object> map = null;
			for (Map<String, Object> tmpMap : resList) {
				map = new HashMap<String, Object>();
				map.put("id", tmpMap.get("C_ID"));
				map.put("name", tmpMap.get("C_CODE"));
				map.put("title", tmpMap.get("C_NAME"));
				map.put("description", tmpMap.get("C_DESC"));
				map.put("singleton", false);
				map.put("params", tmpMap.get("C_PARAMS"));
				rtnList.add(map);
			}
		}
		return new ReturnDto(rtnList);
	}

	@Override
	public ComponentPackageDto saveComponentPackageInfo(ComponentPackageDto componentPackageDto) {
		// 判断对应版本的部件包是否已存在
		ComponentPackageEntity entity = componentPackagePersistence.getComponentPackage(componentPackageDto.getComponentName(), componentPackageDto.getVersion());
		if (!Assert.isEmpty(entity) && !Assert.isEmpty(entity.getId())) {
			entity.setReferenceId(componentPackageDto.getReferenceId());
		} else {
			entity = BeanUtils.convert(componentPackageDto, ComponentPackageEntity.class);
		}
		ComponentPackageEntity rtnEntity = componentPackagePersistence.save(entity);
		return BeanUtils.convert(rtnEntity, ComponentPackageDto.class);
	}

	@Override
	public PageDto queryComponentPackageDataTable(String search, Integer page, Integer size, String sortProp,
			String order) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT c.* FROM cos_component_package c where 1=1 ");
		Map<String, Object> params = new HashMap<String, Object>();

		if (Assert.isNotEmpty(search)) {
			sb.append(" AND c.c_component_name like ${search} ");
			params.put("search", "%" + search + "%");
		}
		sb.append(" ORDER BY c.c_id DESC ");

		PageDto pageDto = baseDao.query(page, size, sb.toString(), params);
		return pageDto;
	}

	@Override
	public boolean isExistCompCode(String compCode) {
		ComponentEntity entity = componentPersistence.getComponentByCode(compCode);
		if (!Assert.isEmpty(entity) && !Assert.isEmpty(entity.getId())) {
			return true;
		}
		return false;
	}

	@Override
	public PageDto getPartPackageList(String customer, String search, Integer page, Integer size, String sortProp, String order) throws Exception {
		
		PageDto dto = null;
		
		// 区分是主库还是租户
		if (Assert.isEmpty(customer)) {
			// 获取远程云端部件信息
			Map<String, String> params = new HashMap<String, String>();
			params.put("search", search);
			params.put("page", MathUtils.stringObj(page));
			params.put("size", MathUtils.stringObj(size));
			params.put("sortProp", sortProp);
			params.put("order", order);
			JSONObject jo = HttpClientUtils.doGet(url, params);
			if (jo != null) {
				System.out.println(jo);
				dto = JsonMapper.fromJson(MathUtils.stringObj(jo.get("data")), PageDto.class);
				
				if (Assert.isEmpty(dto) || dto.getTotal() == 0) {
					return dto;
				}
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> list = (List<Map<String, Object>>) dto.getRows();
				// 判断库中是否已存在，若存在则状态为已发布，若不存在则状态为未发布
				this.mapperStatus(list);
			}
		} else {
			// 切换到主库进行查询
			DynamicDataSourceContextHolder.clearDataSource();
			dto = this.queryComponentPackageDataTable(search, page, size, sortProp, order);
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = (List<Map<String, Object>>) dto.getRows();
			DynamicDataSourceContextHolder.setDataSource(customer);
			// 判断库中是否已存在，若存在则状态为已发布，若不存在则状态为未发布
			this.mapperStatus(list);
		}
		
		return dto;
	}

	/**
	 * 获取部件状态信息
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> mapperStatus(List<Map<String, Object>> list) {
		if (list == null || list.size() == 0) {
			return list;
		}
		
		for (Map<String, Object> map : list) {
			// 根据部件名称+版本号查询当前库中是否已存在
			ComponentPackageEntity entity = componentPackagePersistence.getComponentPackage(MathUtils.stringObj(map.get("C_COMPONENT_NAME")), MathUtils.stringObj(map.get("C_VERSION")));
			if (!Assert.isEmpty(entity) && !Assert.isEmpty(entity.getId())) {
				map.put("C_STATUS", true);
			} else {
				map.put("C_STATUS", false);
			}
		}
		
		return list;
	}

	@Override
	public ReturnDto getModuleFiles() throws JSONException {
		
		List<Map<String, Object>> moduleList = baseDao.query("SELECT c_code, c_chunk_file FROM cos_portal_component WHERE c_status = 1 AND c_chunk_file is NOT NULL");
		JSONObject jo = new JSONObject();
		
		if (!Assert.isEmpty(moduleList) && moduleList.size() > 0) {
			for (Map<String, Object> map : moduleList) {
				jo.put(MathUtils.stringObj(map.get("C_CODE")), map.get("C_CHUNK_FILE"));
			}
		}
		return new ReturnDto(jo.toString());
	}
}
