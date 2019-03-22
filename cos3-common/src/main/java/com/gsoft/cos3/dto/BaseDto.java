package com.gsoft.cos3.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.DateUtils;
import com.gsoft.cos3.util.JsonMapper;

/**
 * 基础DTO
 *
 * @author helx
 * @date 2017年8月7日 上午11:55:07
 */
public class BaseDto implements java.io.Serializable {

	private static final long serialVersionUID = -8876044241016678909L;
	/**
	 * hibernate策略生成的id
	 */

	
	private Long id;
	
	/**
	 * 创建者
	 */
	private Long createBy;
	/**
	 * 最后修改者
	 */
	private Long updateBy;
	/**
	 * 创建时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime = new Date();
	/**
	 * 修改时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	/**
	 * 删除标记
	 */
	private Boolean deleted = false;

	/**
	 * 打印json格式的dto
	 */
	@Override
	public String toString() {
		try {
			return JsonMapper.toJson(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}

	
	
	public Long getCreateBy() {
		return createBy;
	}



	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}


	public Long getUpdateBy() {
		return updateBy;
	}



	public void setUpdateBy(Long updateBy) {
		this.updateBy = updateBy;
	}



	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getFormatCreateTime() {
		if(Assert.isNotEmpty(createTime)) {
			return DateUtils.format(getCreateTime(), "yyy-MM-dd HH:mm:ss");
		}
		return "";
	}
	public String getFormatUpdateTime() {
		if(Assert.isNotEmpty(updateTime)) {
			return DateUtils.format(getUpdateTime(), "yyy-MM-dd HH:mm:ss");
		}
		return "";
	}
	
	
}
