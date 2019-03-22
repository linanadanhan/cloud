package com.gsoft.cos3.dto;

import com.gsoft.cos3.exception.ReturnCode;

public class ReturnDto {
	
	private int status = ReturnCode.OK_STATUS;
	
	private Object data;

	private Object description;
	
	public ReturnDto() {
		
	}
	
	public ReturnDto(int status,Object data) {
		this.status = status;
		this.data = data;
	}

	public ReturnDto(int status, Object data, Object description) {
		this.status = status;
		this.data = data;
		this.description = description;
	}

	public ReturnDto(Object data) {
		this.data = data;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Object getDescription() {
		return description;
	}

	public void setDescription(Object description) {
		this.description = description;
	}
}
