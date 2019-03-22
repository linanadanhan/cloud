package com.gsoft.cos3.dto;

public class ResponseMessageDto {

	private boolean success;

	private String title;

	private String description;

	private Object data;
	
	
	
	/**
	 * 操作成功
	 */
	public static final ResponseMessageDto SUCCESS = new SuccessDto("success");
	

	public ResponseMessageDto(){};
	/**
	 * @param success
	 * @param title
	 */
	public ResponseMessageDto(boolean success, String title) {
		super();
		this.success = success;
		this.title = title;
	}

	/**
	 * @param success
	 * @param title
	 * @param description
	 */
	public ResponseMessageDto(boolean success, String title, String description) {
		super();
		this.success = success;
		this.title = title;
		this.description = description;
	}

	/**
	 * @param success
	 * @param title
	 * @param description
	 * @param data
	 */
	public ResponseMessageDto(boolean success, String title,
			String description, Object data) {
		super();
		this.success = success;
		this.title = title;
		this.description = description;
		this.data = data;
	}

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @param success
	 *            the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}

}
