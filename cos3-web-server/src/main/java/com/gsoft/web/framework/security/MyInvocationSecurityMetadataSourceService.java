package com.gsoft.web.framework.security;

import com.gsoft.cos3.util.Assert;
import com.gsoft.web.framework.dto.PermissionDto;
import com.gsoft.web.framework.persistence.PermissionRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


@Service
public class MyInvocationSecurityMetadataSourceService implements
        FilterInvocationSecurityMetadataSource {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private HashMap<String, Collection<ConfigAttribute>> map = null;

    /**
     * 加载资源，初始化资源变量
     */
    public void loadResourceDefine() {
        map = new HashMap<String, Collection<ConfigAttribute>>();
        List<PermissionDto> permissionDtos = jdbcTemplate.query("SELECT * FROM cos_sys_permission", new PermissionRowMapper());
        for (PermissionDto permissionDto : permissionDtos) {
            String includeResourceRul = permissionDto.getIncludeResourceRul();
            String excludeResourceRul = permissionDto.getExcludeResourceRul();
            if (Assert.isNotEmpty(includeResourceRul))
                addUrlToConfig(includeResourceRul);
            if (Assert.isNotEmpty(excludeResourceRul))
                addUrlToConfig(excludeResourceRul);
        }
    }

    private void addUrlToConfig(String resourceUrl) {
        Collection<ConfigAttribute> array;
        ConfigAttribute cfg;
        if (resourceUrl != null) {
            if (resourceUrl.contains(",")) {
                String[] urls = resourceUrl.split(",");
                for (String url : urls) {
                    array = new ArrayList<ConfigAttribute>();
                    cfg = new SecurityConfig(url);
                    array.add(cfg);
                    map.put(url, array);
                }
            } else {
                array = new ArrayList<ConfigAttribute>();
                cfg = new SecurityConfig(resourceUrl);
                array.add(cfg);
                map.put(resourceUrl, array);
            }
        }
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object)
            throws IllegalArgumentException {
        if (map == null)
            loadResourceDefine();
        HttpServletRequest request = ((FilterInvocation) object)
                .getHttpRequest();
        AntPathRequestMatcher matcher;
        String resUrl;
        for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext(); ) {
            resUrl = iter.next();
            matcher = new AntPathRequestMatcher(resUrl);
            if (matcher.matches(request)) {
                return map.get(resUrl);
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
