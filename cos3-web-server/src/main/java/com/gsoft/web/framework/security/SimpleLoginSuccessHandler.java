package com.gsoft.web.framework.security;

import com.gsoft.cos3.exception.BusinessException;
import com.gsoft.cos3.util.Assert;
import com.gsoft.web.framework.dto.LoginUserInfo;
import com.gsoft.web.framework.dto.PersonnelDto;
import com.gsoft.web.framework.persistence.PersonnelRowMapper;
import com.gsoft.web.framework.utils.JwtTokenUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@Service
public class SimpleLoginSuccessHandler implements AuthenticationSuccessHandler {

    protected Log logger = LogFactory.getLog(getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        CustomWebAuthenticationDetails details = (CustomWebAuthenticationDetails) authentication.getDetails();
        this.saveLoginInfo(request, authentication, details);
        String value = "";

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        try {
            // front为2时,小程序登陆
            if ("2".equals(details.getFront())) {
                UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                JSONObject dataJo = new JSONObject();
                dataJo.put("jwt", jwtTokenUtil.generateToken(userDetails));
                value = dataJo.toString();
            } else {
                UserDetails principal = (UserDetails) authentication.getPrincipal();
                String personnelNumber = principal.getUsername();
                PersonnelDto personnelDto = jdbcTemplate.queryForObject(
                        "select * from cos_sys_personnel where c_login_name=? and c_deleted = 0",
                        new Object[]{personnelNumber}, new PersonnelRowMapper());

                JSONObject jo = new JSONObject();
                jo.put("status", 200);

                JSONObject dataJo = new JSONObject();
                JSONObject userJo = new JSONObject();
                userJo.put("id", personnelDto.getId());
                userJo.put("loginName", personnelDto.getLoginName());
                userJo.put("username", personnelDto.getName());
                userJo.put("status", (personnelDto.getStatus() == true) ? 1 : 0);

                dataJo.put("userinfo", userJo);

                jo.put("data", dataJo);
                value = jo.toString();
            }
            out = response.getWriter();
            out.append(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 保存登陆信息
     *
     * @param request
     * @param authentication
     */
    private LoginUserInfo saveLoginInfo(HttpServletRequest request, Authentication authentication, CustomWebAuthenticationDetails details) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String personnelNumber = principal.getUsername();
        LoginUserInfo info = new LoginUserInfo();
        try {
            PersonnelDto personnelDto = jdbcTemplate.queryForObject(
                    "select * from cos_sys_personnel where c_login_name=? and c_deleted = 0",
                    new Object[]{personnelNumber}, new PersonnelRowMapper());

            String ip = this.getIpAddress(request);
            Date date = new Date();

            //1、普通登录，不用记录设备信息    type != "device" && device == ""

            //2、移动设备使用用户名密码或者手机号登录，记录设备信息   type != "device" && device != ""

            //3、移动设备重新登录，不需要记录设备信息 type == "device"

            if ((Assert.isEmpty(details.gettype()) || !"device".equals(details.gettype())) && Assert.isNotEmpty(details.getDevice())) { //移动设备使用用户名密码或者手机号登录，记录设备信息
                String sql = "update cos_sys_personnel p set p.c_last_login=?,p.c_last_login_client_ip=?,C_MOBILE_DEVICE=? where p.c_id=?";
                jdbcTemplate.update(sql, new Object[]{date, ip, details.getDevice(), personnelDto.getId()});
            } else {
                String sql = "update cos_sys_personnel p set p.c_last_login=?,p.c_last_login_client_ip=? where p.c_id=?";
                jdbcTemplate.update(sql, new Object[]{date, ip, personnelDto.getId()});
            }

            info.setId(personnelDto.getId());
            info.setName(personnelDto.getName());
            info.setId(personnelDto.getId());

            // 查询登录用户所属机构信息
            // 机构新增维度信息以后, 用户可以关联多个机构
            // OrganizationDto organizationDto = jdbcTemplate.queryForObject("SELECT o.*
            // FROM cos_organization_org o,cos_sys_user_org po,cos_sys_personnel p WHERE
            // o.c_id = po.c_org_id AND po.c_personnel_id = p.c_id AND p.c_login_name = ?
            // and p.c_deleted = 0 ", new Object[]{personnelNumber}, new OrgRowMapper());
            // info.setOrgCode(organizationDto.getCode());
            // info.setOrgCascade(organizationDto.getCascade());

        } catch (Exception e) {
            if (logger.isWarnEnabled()) {
                logger.info("无法更新用户登录信息至数据库");
            }
            throw new BusinessException("无法更新用户登录信息至数据库");
        }
        return info;
    }

    public String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}