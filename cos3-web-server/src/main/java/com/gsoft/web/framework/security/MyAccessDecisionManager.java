package com.gsoft.web.framework.security;

import com.gsoft.cos3.util.Assert;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Service
public class MyAccessDecisionManager implements AccessDecisionManager {

    //decide 方法是判定是否拥有权限的决策方法
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
        //如果未登录能访问的权限
        if ("ROLE_ANONYMOUS".equals(authentication.getPrincipal())
                || matchers("/img/**", request)
                || matchers("/js/**", request)
                || matchers("/css/**", request)
                || matchers("/fonts/**", request)
                || matchers("/cos3-sms-service/sms/sendCode", request) // 发送短信验证码
                || matchers("/cos3-portal-manager/personnel/update/resetPassword", request) // 根据手机验证码重置密码
                || matchers("/favicon.ico", request)
                || matchers("/login", request)
                || matchers("/cos3-portal-manager/site/**",request)) {
            return;
        }
        //得到允许集合和不允许集合
        List<String> allowUrls = new ArrayList<>();
        List<String> notAllowUrls = new ArrayList<>();
        for (GrantedAuthority ga : authentication.getAuthorities()) {
            if (ga instanceof MyGrantedAuthority) {
                MyGrantedAuthority urlGrantedAuthority = (MyGrantedAuthority) ga;
                String allowUrl = urlGrantedAuthority.getAllowUrl();
                String notAllowUrl = urlGrantedAuthority.getNotAllowUrl();
                if (Assert.isNotEmpty(allowUrl))
                    allowUrls.add(allowUrl);
                if (Assert.isNotEmpty(notAllowUrl))
                    notAllowUrls.add(notAllowUrl);
            }
        }

        AntPathRequestMatcher matcherAllow;
        AntPathRequestMatcher matcherNotAllow;
        //循环匹配允许集合
        for (String allowUrl : allowUrls) {
            matcherAllow = new AntPathRequestMatcher(allowUrl);
            if (matcherAllow.matches(request)) {
                //匹配到允许后，在跟不允许集合做匹配
                for (String notAllowUrl : notAllowUrls) {
                    matcherNotAllow = new AntPathRequestMatcher(notAllowUrl);
                    if (matcherNotAllow.matches(request)) {
                        throw new AccessDeniedException("no right");
                    } else {
                        continue;
                    }
                }
                //不在不允许集合中，放行
                return;
            } else {
                continue;
            }
        }

     //   throw new AccessDeniedException("no right");
    }

    private boolean matchers(String url, HttpServletRequest request) {
        AntPathRequestMatcher matcher = new AntPathRequestMatcher(url);
        if (matcher.matches(request)) {
            return true;
        }
        return false;
    }


    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
