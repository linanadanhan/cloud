package com.gsoft.web.framework.security;

import com.gsoft.cos3.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author plsy
 */
public class PhoneAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	CustomUserService customUserService;

	final String LOGIN_TYPE = "phone";

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		CustomWebAuthenticationDetails details = (CustomWebAuthenticationDetails) authentication.getDetails();
		String type = details.gettype();
		if (Assert.isNotEmpty(type) && LOGIN_TYPE.equals(type)) {
			String mobile = authentication.getName();
			String userCode = (String) authentication.getCredentials();
			String systemCode = stringRedisTemplate.opsForValue().get(mobile);
			if (Assert.isEmpty(systemCode)) {
				throw new BadCredentialsException("验证码已失效，请重新发送!");
			}
			if (Assert.isEmpty(userCode)) {
				throw new BadCredentialsException("未提交验证码!");
			}
			if (Assert.isEmpty(mobile)) {
				throw new BadCredentialsException("手机号为空!");
			}
			UserDetails userDetails = (UserDetails) customUserService.loadUserByUsername(mobile);
			if (userCode.equals(systemCode)) { //如果输入的验证码与系统中生成的验证码一致，开始登录
				return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
						userDetails.getAuthorities());
			}
			throw new BadCredentialsException("验证码错误!");
		} else {
			throw new BadCredentialsException("密码错误!");
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}
}
