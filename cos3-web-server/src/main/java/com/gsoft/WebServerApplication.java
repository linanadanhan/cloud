package com.gsoft;

import com.gsoft.web.framework.filter.BasicAuthFilter;
import com.gsoft.web.framework.filter.LogInfoFilter;
import com.gsoft.webchat.SendVertxMsg;
import io.vertx.core.Vertx;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableAsync
@SpringBootApplication
@EntityScan("com.gsoft")
@EnableJpaRepositories("com.gsoft")
@EnableFeignClients
@EnableZuulProxy
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class WebServerApplication {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(SendVertxMsg.class.getName());
        SpringApplication.run(WebServerApplication.class, args);
    }

    @Bean
    public BasicAuthFilter accessFilter() {
        return new BasicAuthFilter();
    }


    @Bean
    public LogInfoFilter logFilter() {
        return new LogInfoFilter();
    }
}
