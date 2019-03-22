package com.gsoft.web.framework.fallback;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsoft.cos3.dto.ReturnDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * zuul降级
 *
 * @author plsy
 */
@Component
public class ServerFallback implements ZuulFallbackProvider {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse() {
        return new ClientHttpResponse() {

            @Override
            public InputStream getBody() throws JsonProcessingException, UnsupportedEncodingException {
                ReturnDto returnDto = new ReturnDto();
                returnDto.setStatus(402);
                returnDto.setData("请求超时,请稍后重试!");
                ObjectMapper mapper = new ObjectMapper();
                String value = mapper.writeValueAsString(returnDto);
                return new ByteArrayInputStream(value.getBytes("UTF-8"));
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                return headers;
            }

            @Override
            public HttpStatus getStatusCode() {
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() {
                return HttpStatus.OK.value();
            }

            @Override
            public String getStatusText() {
                return HttpStatus.OK.getReasonPhrase();
            }

            @Override
            public void close() {
                logger.error("=====zuul转发超时,触发降级=====");
            }

        };

    }
}
