package com.gsoft.portal.webview.page.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import com.gsoft.cos3.entity.BaseEntity;

/**
 * 站点或者页面对应的帮助页面配置表
 * @author zz
 *
 */
@Entity
@Table(name = "cos_page_help_info")
public class SitePageHelpEntity extends BaseEntity{
	
	private static final long serialVersionUID = 7001314681707265358L;

	/**
	 * uuId
	 */
	@Column(name = "c_uu_id", length = 50)
	private String uuId;
	
	/**
	 * page uuId
	 */
	@Column(name = "c_page_uu_id", length = 50)
	private String pageUuId;
	
	/**
	 * 站点代码
	 */
	@Column(name = "c_site_code", length = 50)
	private String siteCode;
	
	/**
	 * 所属类型    0=站点    1=页面
	 */
	@Column(name = "c_type", length = 5)
	private String type;
	
	/**
	 * 图片
	 */
	@Column(name = "c_photo", length = 500)
	private String photo;
	
	/**
	 * 内容描述
	 */
	@Lob
	@Column(name = "c_content")
	private String content;
	
	/**
	 * 标题
	 */
	@Column(name = "c_title")
	private String title;
	
	/**
	 * 附件
	 */
	@Column(name = "c_files", length = 500)
	private String files;

	public String getUuId() {
		return uuId;
	}

	public void setUuId(String uuId) {
		this.uuId = uuId;
	}

	public String getPageUuId() {
		return pageUuId;
	}

	public void setPageUuId(String pageUuId) {
		this.pageUuId = pageUuId;
	}

	public String getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFiles() {
		return files;
	}

	public void setFiles(String files) {
		this.files = files;
	}
}
