package com.gsoft.web.framework.controller;

import com.gsoft.web.framework.security.MyGrantedAuthority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.HashMap;

@RestController
public class AdminController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/getAdminSecurity", method = RequestMethod.GET)
    public HashMap<String, Boolean> getAdminSecurity(HttpServletRequest request) {
        //添加当前用户访问前台url权限
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection<? extends MyGrantedAuthority> authorities = (Collection<? extends MyGrantedAuthority>) userDetails.getAuthorities();

        HashMap<String, Boolean> map = new HashMap<String, Boolean>();
        for (MyGrantedAuthority authority : authorities) {
            map.put(authority.getAllowUrl(), true);
//            map.put(authority.getNotAllowUrl(), false);
        }
        return map;
    }

}
