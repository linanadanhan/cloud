package com.gsoft.web.framework.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gsoft.web.framework.security.CustomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gsoft.cos3.datasource.DynamicDataSourceContextHolder;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.web.framework.security.CustomWebAuthenticationDetails;
import com.gsoft.web.framework.utils.JwtTokenUtil;

/**
 * jwt filter
 *
 * @author plsy
 */
@Component
@DependsOn("customUserService")
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        logger.info("请求的url:" + request.getRequestURL().toString());
        String authToken = request.getHeader(this.tokenHeader);
        if (Assert.isEmpty(authToken)) {
            authToken = request.getParameter("jwt_auth");
        }
        Map<String, String> parmMap = new HashMap<String, String>();
        //方式一：getParameterMap()，获得请求参数map
        Map<String, String[]> map = request.getParameterMap();
        //参数名称
        Set<String> key = map.keySet();
        //参数迭代器
        Iterator<String> iterator = key.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            parmMap.put(k, map.get(k)[0]);
        }
        System.out.println("parmMap=====" + parmMap.toString());

        if (authToken != null) {
            String username = jwtTokenUtil.getUsernameFromToken(authToken);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, userDetails.getPassword(), userDetails.getAuthorities());
                    authentication.setDetails(new CustomWebAuthenticationDetails(request));
                    logger.info("单点登录,jwt验证用户:" + username + "通过!");
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } 
        }
        
        // 匹配若是登录请求，需根据租户信息切换对应数据源
        if (request.getRequestURL().toString().contains("/login")) {
        	if (Assert.isEmpty(parmMap.get("customerCode"))) {
        		DynamicDataSourceContextHolder.clearDataSource();
        	} else {
        		DynamicDataSourceContextHolder.setDataSource(MathUtils.stringObj(parmMap.get("customerCode")));
                // 添加租户标识
        		request.getSession().setAttribute("customerCode", MathUtils.stringObj(parmMap.get("customerCode")));
        	}
        }

        chain.doFilter(request, response);
    }
}
