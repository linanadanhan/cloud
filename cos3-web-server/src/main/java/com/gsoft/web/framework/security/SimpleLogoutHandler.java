package com.gsoft.web.framework.security;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import com.gsoft.cos3.util.MathUtils;

/**
 * 登出成功handle
 *
 * @author plsy
 */
@Component
public class SimpleLogoutHandler implements LogoutHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse response, Authentication authentication) {
        Assert.notNull(httpServletRequest, "HttpServletRequest required");
        HttpSession session = httpServletRequest.getSession(false);
        if (session != null) {
            this.logger.debug("Invalidating session: " + session.getId());
//            session.invalidate(); // 租户信息用户登录退出后不能清空
        }

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(null);
        SecurityContextHolder.clearContext();
        
        // 退出退出返回结果
        PrintWriter out = null;
        try {
    		JSONObject jo = new JSONObject();
    		jo.put("status", 200);
    		jo.put("data", "退出成功！");
    		
//    		session.setAttribute("customerCode", customerCode);
            
    		out = response.getWriter();
    		out.append(MathUtils.stringObj(jo));
        }catch (Exception e) {
        	this.logger.error("Exception: ", e);
		}finally {
			if (out != null) {
				out.close();
			}
		}
    }
}

