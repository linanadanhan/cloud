/* ==================================================================   
 * $Id: codetemplates.xml,v 1.1 2006/04/24 09:18:57 刘静 Exp $ 
 * Created [2013-7-1 下午5:33:15] by 刘静 
 * ==================================================================  
 * filestorems-api 
 * ================================================================== 
 * filestorems-api  License v1.0  
 * Copyright (c) Wuhan G-Soft Technology CO.,LTD., 2010-2012 
 * ================================================================== 
 * 武汉中科天翔科技有限公司拥有该文件的使用、复制、修改和分发的许可权
 * 如果你想得到更多信息，请访问 <http://www.g-soft.com.cn>
 *
 * Wuhan G-Soft Technology CO.,LTD. owns permission to use, copy, modify and 
 * distribute this documentation.
 * For more information on filestorems-api, please 
 * see <http://www.g-soft.com.cn>.  
 * ================================================================== 
 */

package com.gsoft.filemanager.local.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gsoft.cos3.entity.BaseEntity;

/**
 * <p> File.java </p>
 * <p> 文件元数据
 * </p>
 * @author $Author: shencq $
 * @version $Revision: V3.0 $
 */
@Entity
@Table(name = "COS_FILE_METADATA")
public class FileMetadata extends BaseEntity{
	
	private static final long serialVersionUID = 6299572567334051755L;
	/**
	 * 指纹码
	 */
	@Column(name = "c_code")
	private String code;
	
	/**
	 * 文件的相对路径
	 */
	@Column(name = "c_path")
	private String path;

	/**
	 * 文件的分组
	 */
	@Column(name = "c_group")
	private String group;

	/**
	 * 是否有从文件（缩略图）
	 */
	@Column(name = "c_slave")
	private boolean slave;

	/**
	 * 使用次数
	 */
	@Column(name = "c_used_num")
	private int usedNum = 0;
	
	/**
	 * 文件大小
	 */
	@Column(name = "c_size")
	private long size;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getUsedNum() {
		return usedNum;
	}

	public void setUsedNum(int usedNum) {
		this.usedNum = usedNum;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public boolean isSlave() {
		return slave;
	}

	public void setSlave(boolean slave) {
		this.slave = slave;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

}

	