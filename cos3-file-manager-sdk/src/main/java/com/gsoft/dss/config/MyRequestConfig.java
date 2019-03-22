package com.gsoft.dss.config;

import org.apache.http.client.config.RequestConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * httpclient请求设置类
 */
@Configuration
public class MyRequestConfig {

    @Value("${dss.httpclient.connectTimeout}")
    private int connectTimeout;

    @Value("${dss.httpclient.connectRequestTimeout}")
    private int connectRequestTimeout;

    @Value("${dss.httpclient.socketTimeout}")
    private int socketTimeout;

    /**
     * 设置请求参数
     * @return 请求配置
     */
    @Bean
    public RequestConfig config(){
        return RequestConfig.custom()
                .setConnectionRequestTimeout(this.connectRequestTimeout)
                .setConnectTimeout(this.connectTimeout)
                .setSocketTimeout(this.socketTimeout)
                .build();
    }
}