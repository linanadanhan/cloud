package com.gsoft.cos3.dto;

import java.text.DecimalFormat;

/**
 * 文件节点信息
 * @author wangfei
 * @Date 2015年8月19日 下午5:49:53
 *
 */
public class FileNode implements java.io.Serializable {

	private static final long serialVersionUID = 4908173099862989147L;
	/**
	 * 文件大小
	 */
	private long size;
	/**
	 * 引用ID
	 */
	private String referenceId;
	/**
	 * 文件名称
	 */
	private String name;
	
	private Boolean del = true;
	
	public FileNode(String name, String referenceId, long size) {
		this.name = name;
		this.referenceId = referenceId;
		this.size = size;
	}
	
	public String getShowSize() {
		Double showSize = new Double(size);
		String[] units = new String[]{"B","KB","MB","G","T"};
		int unitIndex = 0;
		while (showSize > 1024 && unitIndex < 5) {
			showSize = showSize / 1024;
			unitIndex ++ ;
		}
		String result = String.valueOf(showSize);
		if(unitIndex == 0) {
			DecimalFormat df = new DecimalFormat("#");
			result = df.format(showSize);
		} else {
			DecimalFormat df = new DecimalFormat("#.00");
			result = df.format(showSize);
		}
		return result + units[unitIndex];
	}
	
	public FileNode() {
		
	}
	
	public FileNode(long size, String referenceId, String name) {
		this.size = size;
		this.referenceId = referenceId;
		this.name = name;
	}

	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Boolean getDel() {
		return del;
	}

	public void setDel(Boolean del) {
		this.del = del;
	}
	
	
}
