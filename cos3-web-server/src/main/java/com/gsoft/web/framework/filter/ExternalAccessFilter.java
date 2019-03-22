package com.gsoft.web.framework.filter;

import com.gsoft.cos3.util.AESUtil;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.JsonUtils;
import com.gsoft.web.framework.dto.ExternalDto;
import com.gsoft.web.framework.persistence.ExternalRowMapper;
import com.gsoft.web.framework.utils.JwtTokenUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 外部接入验证转发
 *
 * @author plsy
 */
@Component
public class ExternalAccessFilter extends ZuulFilter {

    @Value("${external.header}")
    private String externalHeader;

    @Value("${AES.password}")
    private String AES_Password;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Override
    public String filterType() {
        return FilterConstants.ROUTE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        //url链接包含external-api的进行验证
        return RequestContext.getCurrentContext().getRequest().getRequestURI().startsWith("/external-api/") ? true : false;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        String authToken = request.getHeader(externalHeader);
        if (Assert.isEmpty(authToken)) {
            authToken = request.getParameter("external_authorization");
        }
        if (authToken != null && Objects.nonNull(jwtTokenUtil.getClaimsFromToken(authToken))) {
            if (!jwtTokenUtil.isTokenExpired(authToken)) {
                Claims claims = jwtTokenUtil.getClaimsFromToken(authToken);
                String encrypt = String.valueOf(claims.get("encrypt"));
                String decrypt = AESUtil.decrypt(encrypt, AES_Password);
                Map<String, Object> map = null;
                try {
                    map = JsonUtils.fromJsonMap(decrypt, String.class, Object.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Objects.nonNull(map)) {
                    String serverCode = (String) map.get("serverCode");
                    List<ExternalDto> list = jdbcTemplate.query("SELECT * FROM cos_sys_sso_external e WHERE e.c_system_code = ?", new Object[]{serverCode}, new ExternalRowMapper());
                    if (list.size() > 0) {
                        //是否匹配
                        boolean b = list.stream().noneMatch(externalDto -> {
                            String url = "/external-api/" + externalDto.getServerName() + externalDto.getControllerPath();
                            AntPathRequestMatcher matcher = new AntPathRequestMatcher(url);
                            return matcher.matches(request);
                        });
                        if (b) {
                            returnMessage(ctx, "不允许访问此API!");
                        }
                    } else {
                        returnMessage(ctx, "此系统无API权限!");
                    }
                } else {
                    returnMessage(ctx, "token解析失败!");
                }
            } else {
                returnMessage(ctx, "token失效,请重新生成!");
            }
        } else {
            returnMessage(ctx, "token验证失败!");
        }
        return null;
    }

    private void returnMessage(RequestContext ctx, String message) {
        HttpServletResponse response = ctx.getResponse();
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(401);
        ctx.setSendZuulResponse(false);
        try {
            response.getWriter().write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ctx.setResponse(response);
    }

}
