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

package com.gsoft.filemanager.local.dto;

import com.gsoft.cos3.dto.BaseDto;

/**
 * <p> File.java </p>
 * <p> 文件元数据
 * </p>
 * @author $Author: shencq $
 * @version $Revision: V3.0 $
 */
public class FileMetadataDto extends BaseDto{
	
	private static final long serialVersionUID = 6299572567334051755L;
	/**
	 * 指纹码
	 */
	private String code;
	
	/**
	 * 文件的相对路径
	 */
	private String path;

	private String group;

	/**
	 * 使用次数
	 */
	private int usedNum = 0;
	
	/**
	 * 文件大小
	 */
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

    public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

}

	