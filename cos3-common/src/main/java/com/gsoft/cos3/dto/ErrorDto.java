/**
 * 
 */
package com.gsoft.cos3.dto;

/**
 * @author shencq
 *
 */
public class ErrorDto extends ResponseMessageDto {

	/**
	 * @param title
	 */
	public ErrorDto(String title) {
		super(false, title);
	}

	/**
	 * @param title
	 * @param description
	 */
	public ErrorDto(String title, String description) {
		super(false, title, description);
	}

	/**
	 * @param e
	 */
	public ErrorDto(Exception e) {
		super(false, e.getMessage());
		// ByteArrayOutputStream os = new ByteArrayOutputStream();
		// PrintStream ps = new PrintStream(os);
		// e.printStackTrace(ps);
		// setDescription(os.toString()); //TODO 导致前台json解析错误
	}

}
