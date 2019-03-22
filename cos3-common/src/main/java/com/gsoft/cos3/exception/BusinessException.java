package com.gsoft.cos3.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gsoft.cos3.dto.ErrorDto;
import com.gsoft.cos3.util.JsonMapper;

/**
 * @author shencq
 */
public class BusinessException extends RuntimeException{
	
	private boolean ajax = false;

	/**
	 * 
	 */
	private static final long serialVersionUID = -817022750637302614L;

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessException(String message) {
		super(message);
	}

	public String toJson() throws JsonProcessingException {
		ErrorDto error = new ErrorDto(this);
		return JsonMapper.toJson(error);
	}

	
	/**
	 * @param ajax the ajax to set
	 */
	public void setAjax(boolean ajax) {
		this.ajax = ajax;
	}

	/**
	 * @return the ajax
	 */
	public boolean isAjax() {
		return ajax;
	}
	
	

}
