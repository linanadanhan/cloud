package com.gsoft.filemanager.local.dto;

/**
 * 用来展示文件系统虚拟目录的数据结构
 * @author wangfei
 *
 */
public class VirtualFolderDto implements java.io.Serializable {

	private static final long serialVersionUID = 5632782578419661269L;
	/**
	 * 目录名称
	 */
	private String name;
	/**
	 * 上级目录名称
	 */
	private String parentFolderName;
	/**
	 * 目录路径
	 */
	private String path;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParentFolderName() {
		return parentFolderName;
	}
	public void setParentFolderName(String parentFolderName) {
		this.parentFolderName = parentFolderName;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
}
