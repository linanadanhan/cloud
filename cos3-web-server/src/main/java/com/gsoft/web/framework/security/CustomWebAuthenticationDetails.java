package com.gsoft.web.framework.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * @author plsy
 */
public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {
    
	private static final long serialVersionUID = -2135894122511996600L;
	
	/**
	 * 登录方式，phone：手机验证码登录，其他：用户名密码登录
	 */
	private String type;
	
	/**
	 * 是否为门户前端登录 0 否 1 是
	 */
	private String front;
	
	/**
	 * 是否移动端登录，用来控制设备信息
	 */
	private String device;

    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        type = request.getParameter("type");
        front = request.getParameter("front");
        device = request.getParameter("device");
    }

    public String gettype() {
        return type;
    }
    
    public String getFront() {
        return front;
    }

	public String getDevice() {
		return device;
	}
    
	
}
