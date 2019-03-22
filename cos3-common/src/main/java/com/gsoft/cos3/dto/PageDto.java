package com.gsoft.cos3.dto;

import org.springframework.data.domain.Page;

import com.gsoft.cos3.util.BeanUtils;

import java.util.Collections;
import java.util.List;

/**
 * 分页dto.
 * 
 * @author Administrator
 * 
 * @param <T>分页对象
 */
public class PageDto {
	private List<?> rows;

	private long total;

	public PageDto() {
		super();
		this.total = 0;
	}

	/**
	 * @param rows
	 * @param total
	 */
	public PageDto(List<?> rows, long total) {
		super();
		this.rows = rows;
		this.total = total;
	}

	public <E> PageDto(Page<E> page, Class<?> cls) {
		super();
		if (page.getTotalElements() > 0) {
			this.rows = BeanUtils.map(page.getContent(), cls);
		} else {
			this.rows = Collections.emptyList();
		}
		this.total = page.getTotalElements();
	}

	public <E> PageDto(Page<E> page) {
		super();
		if (page.getTotalElements() > 0) {
			this.rows = page.getContent();
		} else {
			this.rows = Collections.emptyList();
		}
		this.total = page.getTotalElements();
	}

	/**
	 * @return the rows
	 */
	public List<?> getRows() {
		return rows;
	}

	/**
	 * @return the total
	 */
	public long getTotal() {
		return total;
	}

	public void setRows(List<?> rows) {
		this.rows = rows;
	}

}
