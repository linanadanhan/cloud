package com.gsoft.web.framework.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

public class MyAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

	public MyAuthenticationEntryPoint(String loginFormUrl) {
		super(loginFormUrl);
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		if (request.getHeader("X-Requested-With") == null) {
			// 返回json形式的错误信息
			JSONObject returnObj = new JSONObject();
			try {
				returnObj.put("status", "-1");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().print(returnObj.toString());
			response.flushBuffer();
		} else {
			super.commence(request, response, authException);
		}
	}
}
