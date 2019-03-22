package com.gsoft.web.framework.filter;

import static feign.Util.ISO_8859_1;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.gsoft.cos3.util.Base64Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.gsoft.cos3.datasource.DynamicDataSourceContextHolder;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.Base64;
import com.gsoft.cos3.util.MathUtils;
import com.gsoft.web.framework.dto.PersonnelDto;
import com.gsoft.web.framework.persistence.PersonnelRowMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

@RefreshScope
public class BasicAuthFilter extends ZuulFilter {

    @Value("${security.user.name}")
    private String username;

    @Value("${security.user.password}")
    private String password;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        //添加basic验证信息
        RequestContext ctx = RequestContext.getCurrentContext();

        String headerValue = "Basic " + Base64.encode(((username + ":" + password).getBytes(ISO_8859_1)));
        ctx.addZuulRequestHeader(HttpHeaders.AUTHORIZATION, headerValue);
        // 添加租户标识
        ctx.addZuulRequestHeader("Site-info", MathUtils.stringObj(ctx.getRequest().getSession().getAttribute("customerCode")));
        if (Assert.isEmpty(ctx.getRequest().getSession().getAttribute("customerCode"))) {
        	DynamicDataSourceContextHolder.clearDataSource();
        } else {
        	DynamicDataSourceContextHolder.setDataSource(MathUtils.stringObj(ctx.getRequest().getSession().getAttribute("customerCode")));
        }
        //添加当前用户信息
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!"anonymousUser".equals(principal)) {
            UserDetails userDetails = (UserDetails) principal;
            String personnelNumber = userDetails.getUsername();
            PersonnelDto personnelDto = jdbcTemplate.queryForObject("select * from cos_sys_personnel where c_login_name=? and c_deleted = false", new Object[]{personnelNumber}, new PersonnelRowMapper());

            //查询登录用户所属机构信息
            //List<OrganizationDto> orgList = jdbcTemplate.query("SELECT o.* FROM cos_organization_org o,cos_sys_user_org po,cos_sys_personnel p WHERE o.c_id = po.c_org_id AND po.c_personnel_id = p.c_id AND p.c_login_name = ? ", new Object[]{personnelNumber}, new OrgRowMapper());
            //OrganizationDto organizationDto = jdbcTemplate.queryForObject("SELECT o.* FROM cos_organization_org o,cos_sys_user_org po,cos_sys_personnel p WHERE o.c_id = po.c_org_id AND po.c_personnel_id = p.c_id AND p.c_login_name = ? ", new Object[]{personnelNumber}, new OrgRowMapper());

            ctx.addZuulRequestHeader("personnelNumber", personnelNumber);
            ctx.addZuulRequestHeader("personnelId", personnelDto.getId().toString());
            ctx.addZuulRequestHeader("personnelName", Base64Util.getBASE64(personnelDto.getName()));
            ctx.addZuulRequestHeader("session", ctx.getRequest().getSession().getId());
            try {
            	 ctx.addZuulRequestHeader("personName", URLEncoder.encode(personnelDto.getName(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

            //ctx.addZuulRequestHeader("orgCode", organizationDto.getCode());
            //ctx.addZuulRequestHeader("orgCascade", organizationDto.getCascade());
        }
        return null;
    }

}
