package com.gsoft.web.framework.security;

import com.gsoft.cos3.jdbc.dao.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * spring security白名单
 * 匹配数据库中的url
 *
 * @author plsy
 */
@Component("antAuthService")
public class AntAuthService {

    @Autowired
    BaseDao baseDao;

    public boolean canAccess(HttpServletRequest request, Authentication authentication) {
        String requestURI = request.getRequestURI();
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT COUNT(*) FROM cos_scan_controller WHERE c_allow = 1 ");
        
        String[] arr = requestURI.split("/");
        
        if (arr.length > 2) {
        	sb.append("and CONCAT('/',c_server,c_path) = ? ");
        } else {
        	sb.append("and c_path = ? ");
        }
        
        boolean result = (Integer) this.baseDao.queryForObject(sb.toString(), Integer.class, new Object[]{requestURI}) <= 0;
        
        if (!result || authentication.isAuthenticated()) {
            return true;
        }
        return false;
    }
}
