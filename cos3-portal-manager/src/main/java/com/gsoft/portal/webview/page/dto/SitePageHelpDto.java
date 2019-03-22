package com.gsoft.portal.webview.page.dto;

import com.gsoft.cos3.dto.BaseDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("页面帮助DTO")
public class SitePageHelpDto extends BaseDto{

	private static final long serialVersionUID = -4795638547674147880L;
	/**
	 * uuId
	 */
	@ApiModelProperty("uuId")
	private String uuId;
	
	/**
	 * page uuId
	 */
	@ApiModelProperty("页面uuId")
	private String pageUuId;
	
	/**
	 * 站点代码
	 */
	@ApiModelProperty("站点代码")
	private String siteCode;
	
	/**
	 * 所属类型  0=站点 1=页面
	 */
	@ApiModelProperty("类型")
	private String type;
	
	/**
	 * 图片
	 */
	@ApiModelProperty("图片")
	private String photo;
	
	/**
	 * 内容描述
	 */
	@ApiModelProperty("内容描述")
	private String content;
	
	/**
	 * 标题
	 */
	@ApiModelProperty("标题")
	private String title;
	
	/**
	 * 文件
	 */
	@ApiModelProperty("文件")
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
