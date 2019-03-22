package com.gsoft.dss.config;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 基础认证的凭证提供类
 */
@Configuration
public class BasicAuthConfig {

    @Value("${dss.connection.username}")
    private String username;

    @Value("${dss.connection.password}")
    private String password;

    /**
     * 根据配置文件参数生成对应的凭证提供者
     * @return 基础验证凭证
     */
    @Bean
    public CredentialsProvider provider() {
        //添加basic验证
        CredentialsProvider provider = new BasicCredentialsProvider();
        // Create the authentication scope
        AuthScope scope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM);
        // Create credential pair，在此处填写用户名和密码
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        // Inject the credentials
        provider.setCredentials(scope, credentials);
        return provider;
    }
}
