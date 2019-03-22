package com.gsoft.web.framework.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author plsy
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    CustomUserService customUserService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new BadCredentialsException("验证失败!");
        }
        UserDetails details = customUserService.loadUserByUsername(auth.getName());
        return new UsernamePasswordAuthenticationToken(details, details.getPassword(),
                details.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
