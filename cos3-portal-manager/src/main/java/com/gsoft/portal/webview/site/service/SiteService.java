package com.gsoft.portal.webview.site.service;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.gsoft.cos3.dto.PageDto;
import com.gsoft.portal.webview.site.dto.SiteDto;
import com.gsoft.portal.component.theme.entity.ThemeEntity;

/**
 * 站点管理Service接口
 * @author SN
 *
 */
public interface SiteService {

	/**
	 * 分页查询主题信息
	 * @param page
	 * @param size
	 * @param sortProp
	 * @param order
	 * @return
	 */
	PageDto querySiteDataTable(String search, Integer page, Integer size, String sortProp, String order);

	/**
	 * 根据主键获取某一笔站点信息
	 * @param id
	 * @return
	 */
	SiteDto getSiteInfoById(Long id);

	/**
	 * 判断站点代码是否已存在
	 * @param id
	 * @param code
	 * @return
	 */
	Boolean isExitSiteCode(Long id, String code);

	/**
	 * 保存站点信息
	 * @param themeDto
	 * @return
	 */
	SiteDto saveSite(SiteDto siteDto);

	/**
	 * 删除站点信息
	 * @param id
	 * @param code
	 * @return
	 */
	void delSite(Long id, String code);
	
	/**
	 * 获取所有站点信息
	 * @return
	 */
	List<SiteDto> getAllSiteList();

	/**
	 * 根据code获取站点信息
	 * @param siteCode
	 * @return
	 */
	SiteDto getSiteInfoByCode(String siteCode);

	/**
	 * 站点导入数据处理
	 * @param jsonObj
	 * @return
	 * @throws JSONException 
	 */
	boolean handleImportData(JSONObject jsonObj) throws Exception;

	/**
	 * 复制站点
	 * @param jo
	 * @param personnelId
	 * @throws Exception 
	 */
	void copySite(JSONObject jo, Long personnelId) throws Exception;

	/**
	 * 获取站点设置的个性化主题
	 * @param siteCode
	 * @return
	 */
	List<ThemeEntity> getProfileThemeList(String siteCode, String isOpen);

	/**
	 * 校验站点域名是否已存在
	 * @param domain
	 * @param siteCode
	 * @return
	 */
	Boolean isExitSiteDomain(String domain, String siteCode);
	

}
