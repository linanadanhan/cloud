package com.gsoft.cos3.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 向ioc注册caffeine的缓存接口
 *
 * @author pilsy
 */
@Configuration
public class CacheConfig {

    @Bean
    Cache cache() {
        Cache<String, String> cache = Caffeine.newBuilder()
                //在一定时间内没有创建/覆盖时，会移除该key
                .expireAfterWrite(60, TimeUnit.SECONDS)
                //在一定时间内没有读写，会移除该key
                .expireAfterAccess(60, TimeUnit.SECONDS)
                .maximumSize(100)
                .build();
        return cache;
    }

}
