/**
 * 
 */
package com.gsoft.cos3.dto;

/**
 * @author shencq
 * 
 */
public class SuccessDto extends ResponseMessageDto {

	public SuccessDto(String title) {
		super(true, title);
	}

	/**
	 * @param title
	 * @param data
	 */
	public SuccessDto(String title, Object data) {
		super(true, title, null, data);
	}

}
