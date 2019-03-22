package com.gsoft.portal.webview.page.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.cos3.jdbc.dao.BaseDao;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.portal.webview.page.dto.SitePageHelpDto;
import com.gsoft.portal.webview.page.dto.SitePageHelpHideDto;
import com.gsoft.portal.webview.page.entity.SitePageHelpEntity;
import com.gsoft.portal.webview.page.entity.SitePageHelpHideEntity;
import com.gsoft.portal.webview.page.persistence.SitePageHelpHidePersistence;
import com.gsoft.portal.webview.page.persistence.SitePageHelpPersistence;
import com.gsoft.portal.webview.page.service.SitePageHelpService;

@Service
public class SitePageHelpServiceImpl implements SitePageHelpService {
	@Resource
	SitePageHelpPersistence sitePageHelpPersistence;
	@Resource
	SitePageHelpHidePersistence sitePageHelpHidePersistence;
	@Resource
	BaseDao baseDao;
	
	@Override
	public PageDto querySitePageHelp(String pageUuId, String siteCode, String type, Integer page, Integer size, String sortProp,
			String order) {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("select c_id,c_uu_id,c_site_code,c_page_uu_id,c_type,c_photo,c_content,c_title,c_files");
		sb.append(" from cos_page_help_info");
		sb.append(" where c_deleted = 0");
		
		if(Assert.isNotEmpty(type)) {//0=站点  1=页面
			sb.append(" and c_type = ${type}");
			params.put("type", type);
			
			if(Assert.isNotEmpty(siteCode)) {
				sb.append(" and c_site_code = ${siteCode}");
				params.put("siteCode", siteCode);
			}
			if(Assert.isNotEmpty(pageUuId)) {
				sb.append(" and c_page_uu_id = ${pageUuId}");
				params.put("pageUuId", pageUuId);
			}
		}
		sb.append(" order by ").append(sortProp).append(" ").append(order);
		
		PageDto pageDto = baseDao.query(page, size, sb.toString(), params);
		return pageDto;
	}
	
	@Override
	public List<Map<String, Object>> querySiteHelp(String siteCode, String owner) {
		List<Map<String, Object>> res =new ArrayList<Map<String, Object>>();
		if(Assert.isNotEmpty(siteCode) && Assert.isNotEmpty(owner)) {
			//1.判断是否需要提示
			List<SitePageHelpHideEntity> list = sitePageHelpHidePersistence.getSiteOwnner(siteCode, owner);
			if(list.size()==0) {
				//2.返回帮助信息
				StringBuffer sb = new StringBuffer();
				sb.append("select c_id,c_uu_id,c_site_code,c_page_uu_id,c_type,c_photo,c_content,c_title,c_files");
				sb.append(" from cos_page_help_info");
				sb.append(" where c_deleted = 0");
				sb.append(" and c_type = 0 and c_site_code = ${siteCode}");
				sb.append(" order by c_id desc");
				
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("siteCode", siteCode);
				res = baseDao.query(sb.toString(), params);
			}
		}
		return res;
	}
	
	@Override
	public List<Map<String, Object>> queryPageHelp(String siteCode, String pageUuId) {
		List<Map<String, Object>> res =new ArrayList<Map<String, Object>>();
		
		if(Assert.isNotEmpty(siteCode) && Assert.isNotEmpty(pageUuId)) {
			StringBuffer sb = new StringBuffer();
			sb.append("select c_id,c_uu_id,c_site_code,c_page_uu_id,c_type,c_photo,c_content,c_title,c_files");
			sb.append(" from cos_page_help_info");
			sb.append(" where c_deleted = 0");
			sb.append(" and c_type = 1 and c_site_code = ${siteCode} and c_page_uu_id = ${pageUuId}");
			sb.append(" order by c_id desc");
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("siteCode", siteCode);
			params.put("pageUuId", pageUuId);
			
			res = baseDao.query(sb.toString(), params);
		}
		return res;
	}
	
	@Override
	public SitePageHelpDto getPageHelpInfoById(Long id) {
		SitePageHelpEntity entity = sitePageHelpPersistence.findOne(id);
		SitePageHelpDto dto = BeanUtils.convert(entity, SitePageHelpDto.class);
		return dto;
	}
	
	@Override
	public SitePageHelpDto saveSitePageHelp(SitePageHelpDto sitePageHelpDto) {
		SitePageHelpEntity entity = BeanUtils.convert(sitePageHelpDto, SitePageHelpEntity.class);
		SitePageHelpEntity reEntity = sitePageHelpPersistence.save(entity);
		return BeanUtils.convert(reEntity, SitePageHelpDto.class);
	}
	
	@Override
	@Transactional
	public void delSitePageHelp(Long id) {
		sitePageHelpPersistence.delete(id);
	}
	
	@Override
	public SitePageHelpHideDto saveSitePageHelpHide(SitePageHelpHideDto sitePageHelpHideDto) {
		SitePageHelpHideEntity entity = BeanUtils.convert(sitePageHelpHideDto, SitePageHelpHideEntity.class);
		List<SitePageHelpHideEntity> list = sitePageHelpHidePersistence.getSiteOwnner(sitePageHelpHideDto.getSiteCode(), sitePageHelpHideDto.getOwner());
		if(list.size()==0) {
			SitePageHelpHideEntity reEntity = sitePageHelpHidePersistence.save(entity);
			return BeanUtils.convert(reEntity, SitePageHelpHideDto.class);
		}else {
			return BeanUtils.convert(list.get(0), SitePageHelpHideDto.class);
		}
		
		
	}

	@Override
	public List<SitePageHelpDto> getPageHelpByParams(String siteCode, String pageUuId, String Type) {
		List<SitePageHelpEntity> res = null;
		if (!Assert.isEmpty(pageUuId)) {
			res = sitePageHelpPersistence.getSitePgeHelp(siteCode, pageUuId, Type);
		} else {
			res = sitePageHelpPersistence.getSitePgeHelp(siteCode, Type);
		}
		return BeanUtils.convert(res, SitePageHelpDto.class);
	}
}
