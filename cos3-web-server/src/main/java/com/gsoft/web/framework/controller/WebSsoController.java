package com.gsoft.web.framework.controller;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.web.framework.utils.JwtTokenUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * 单点登陆
 *
 * @author plsy
 */
@Controller
public class WebSsoController {

    static final Logger logger = LoggerFactory.getLogger(WebSsoController.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.header}")
    private String tokenHeader;

    @ApiOperation("单点登录跳转")
    @RequestMapping(value = "createAuthToken", method = RequestMethod.GET)
    public String createAuthToken(@RequestParam String serviceId, @RequestParam Map<String, String> map) {
        //todo 这里请求的地址为web服务地址
        String targetUrl = "http://web-server/" + serviceId + "/createAuthToken";
        map.remove("serviceId");
        HttpHeaders requestHeaders = setJwtHttpHeader();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        for (String key : map.keySet()) {
            params.add(key, map.get(key));
        }

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, requestHeaders);

        ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.POST, requestEntity, String.class);
        return "redirect:" + response.getBody();
    }

    @ApiOperation("通过适配器得到数据")
    @ResponseBody
    @RequestMapping(value = "getDataFromAdapter", method = RequestMethod.GET)
    public ReturnDto getDataFromAdapter(@RequestParam String serviceId, @RequestParam Map<String, String> map) {
        //todo 这里请求的地址为web服务地址
        String targetUrl = "http://web-server/" + serviceId + "/getDataFromAdapter";
        map.remove("serviceId");
        HttpHeaders requestHeaders = setJwtHttpHeader();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(targetUrl);
        for (String key : map.keySet()) {
            builder.queryParam(key, map.get(key));
        }
        HttpEntity<String> entity = new HttpEntity<>(requestHeaders);

        ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
        return new ReturnDto(response.getStatusCodeValue(), response.getBody());
    }

    private HttpHeaders setJwtHttpHeader() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(userDetails);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(tokenHeader, token);
        return requestHeaders;
    }

    @ApiOperation("得到当前登录人的单点登录jwt-token")
    @ResponseBody
    @RequestMapping(value = "getJwtFromUserDetails", method = RequestMethod.GET)
    public String getJwtFromUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(userDetails);
        return token;
    }


}