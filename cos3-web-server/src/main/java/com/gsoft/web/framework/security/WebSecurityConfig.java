package com.gsoft.web.framework.security;

import com.gsoft.cos3.util.MD5Util;
import com.gsoft.web.framework.filter.JwtAuthenticationTokenFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


/**
 * @author plsy
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MyFilterSecurityInterceptor myFilterSecurityInterceptor;

    @Autowired
    SimpleLoginSuccessHandler simpleLoginSuccessHandler;

    @Autowired
    SimpleLogoutHandler simpleLogoutHandler;

    @Autowired
    SimpleFailureHandler simpleFailureHandler;

    @Autowired
    AllowedOrigins AllowedOrigins;

    @Autowired
    private CustomWebAuthenticationDetailsSource authenticationDetailsSource;

    @Bean
    UserDetailsService customUserService() {
        return new CustomUserService();
    }

    /**
     * 用户名密码的数据库验证器
     *
     * @return
     */
    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return MD5Util.encode((String) rawPassword);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encodedPassword.equals(MD5Util.encode((String) rawPassword));
            }
        });
        daoAuthenticationProvider.setUserDetailsService(customUserService());
        return daoAuthenticationProvider;
    }

    @Bean
    PhoneAuthenticationProvider phoneAuthenticationProvider() {
        return new PhoneAuthenticationProvider();
    }

    @Bean
    MobileDeviceAuthenticationProvider mobileDeviceAuthenticationProvider() {
        return new MobileDeviceAuthenticationProvider();
    }

    @Bean
    JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider();
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        //这里会轮询符合条件的AuthenticationProvider，如果成功就终止，不成功就下一个
        ProviderManager authenticationManager = new ProviderManager(Arrays.asList(jwtAuthenticationProvider(), daoAuthenticationProvider(), phoneAuthenticationProvider(), mobileDeviceAuthenticationProvider()));
        return authenticationManager;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();//允许iframe
//        http.sessionManagement().maximumSessions(1).expiredSessionStrategy(sessionInformationExpiredStrategy);
        http.authorizeRequests()
                .antMatchers("/css/**", "/fonts/**", "/images/**", "/js/**").permitAll()
                .antMatchers("/**/v2/api-docs", "/swagger/**", "/swagger-ui.html", "/swagger-resources/**", "/v2/**", "/webjars/**").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers("/external-api/**").permitAll()
                .antMatchers("/getSiteInfoByDomain").permitAll()
                .antMatchers("/cos3-sms-service/sms/sendCode").permitAll()
                .antMatchers("/cos3-portal-manager/personnel/update/resetPassword").permitAll()
                .antMatchers("/cos3-portal-manager/personnel/add/validatePhone").permitAll()
                .antMatchers("/cos3-portal-manager/mobile/noauthor/**").permitAll()
                .antMatchers("/cos3-portal-manager/site/**").permitAll()
                .antMatchers("/cos3-portal-manager/widget/**").permitAll()
                .antMatchers("/cos3-portal-manager/form/getTableDataInfo").permitAll()
                .antMatchers("/cos3-portal-manager/form/getTablePageInfo").permitAll()
                .antMatchers("/cos3-portal-manager/form/getFormConfInfo").permitAll()
                .antMatchers("/cos3-portal-manager/form/getFormDataInfoById").permitAll()
                .antMatchers("/cos3-portal-manager/form/getDicOptions").permitAll()
                .antMatchers("/cos3-portal-manager/form/getDicItemByKey").permitAll()
                .antMatchers("/cos3-portal-manager/import/importDataNoReply").permitAll()
                .antMatchers("/zuul/cos3-file-manager/file/**").permitAll()
                .antMatchers("/cos3-im-manager/contactGroup/**").permitAll()
                .antMatchers("/cos3-im-manager/contact/**").permitAll()
                .antMatchers("/cos3-im-manager/sessionMessage/**").permitAll()
                .antMatchers("/cos3-im-manager/message/**").permitAll()
                .antMatchers("/tzxf-ms/wxApi/**").permitAll()
                .antMatchers("/**").access("@antAuthService.canAccess(request,authentication)")
                .anyRequest()
                .authenticated() //任何请求,登录后可以访问
                .and()
                .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .authenticationDetailsSource(authenticationDetailsSource)
//                .loginPage("/login")//登录页面
//                .defaultSuccessUrl("/")//登陆成功跳转
                .successHandler(simpleLoginSuccessHandler)
                .failureHandler(simpleFailureHandler)
                .permitAll() //登录页面用户任意访问
                .and()
                .logout().addLogoutHandler(simpleLogoutHandler).deleteCookies("JSESSIONID").permitAll()
                .and().exceptionHandling().authenticationEntryPoint(new MyAuthenticationEntryPoint("/login")); //注销行为任意访问
        // 添加JWT filter
        logger.info("====================hashcode:" + authenticationDetailsSource.hashCode());
        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(myFilterSecurityInterceptor, FilterSecurityInterceptor.class).csrf().disable();
        http.cors().configurationSource(corsConfigurationSource());
        
        http.sessionManagement().maximumSessions(1);
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(AllowedOrigins.getAllowedOrigin());
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("DNT", "X-Mx-ReqToken", "Keep-Alive", "User-Agent", "X-Requested-With", "Authorization", "If-Modified-Since", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() {
        return new JwtAuthenticationTokenFilter();
    }

}
