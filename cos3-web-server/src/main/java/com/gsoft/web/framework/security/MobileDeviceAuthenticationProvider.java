package com.gsoft.web.framework.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import com.gsoft.cos3.util.Assert;

/**
 * 移动设备识别码直接登录逻辑
 * @author wangfei
 *
 */
public class MobileDeviceAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	CustomUserService customUserService;

	final String LOGIN_TYPE = "device";

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		CustomWebAuthenticationDetails details = (CustomWebAuthenticationDetails) authentication.getDetails();
		String type = details.gettype();
		if (Assert.isNotEmpty(type) && LOGIN_TYPE.equals(type)) { //使用设备标识登录
			String device = details.getDevice();
			if (Assert.isEmpty(device)) {
				throw new BadCredentialsException("未正确获取设备信息，请重试!");
			}
			UserDetails userDetails = (UserDetails) customUserService.loadUserByUsername(device);
				return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
						userDetails.getAuthorities());
		} else {
			throw new BadCredentialsException("密码错误!");
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}
}
