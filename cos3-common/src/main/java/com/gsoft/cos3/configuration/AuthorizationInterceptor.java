package com.gsoft.cos3.configuration;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * RestTemplate的基础验证拦截器
 *
 * @author plsy
 */
public class AuthorizationInterceptor implements ClientHttpRequestInterceptor {

    private String username = "cos3";

    private String password = "password";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        String plainCreds = username + ":" + password;
        String base64Creds = new String(Base64.encodeBase64(plainCreds.getBytes()));
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + base64Creds);
        return execution.execute(request, body);
    }

}
