package com.gsoft.dss.config;

import java.util.concurrent.TimeUnit;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * httpclient连接池设置类
 */
@Configuration
public class MyPoolingHttpClientConnectionManager {
    /**
     * 连接池最大连接数
     */
    @Value("${dss.httpclient.connMaxTotal}")
    private int connMaxTotal = 20;

    /**
     *
     */
    @Value("${dss.httpclient.maxPerRoute}")
    private int maxPerRoute = 20;

    /**
     * 连接存活时间，单位为s
     */
//    @Value("${dss.httpclient.timeToLive}")
//    private int timeToLive = 60;

    /**
     * 生成httpclient连接池Manager
     * @return 连接池管理
     */
    @Bean
    public PoolingHttpClientConnectionManager poolingClientConnectionManager() {
        PoolingHttpClientConnectionManager poolHttpcConnManager = new PoolingHttpClientConnectionManager(60, TimeUnit.SECONDS);
        // 最大连接数
        poolHttpcConnManager.setMaxTotal(this.connMaxTotal);
        // 路由基数
        poolHttpcConnManager.setDefaultMaxPerRoute(this.maxPerRoute);
        return poolHttpcConnManager;
    }
}