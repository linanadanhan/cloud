package com.gsoft.web.framework.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "cors")
public class AllowedOrigins {

    private List<String> AllowedOrigin = new ArrayList<String>();

    public List<String> getAllowedOrigin() {
        return AllowedOrigin;
    }

    public void setAllowedOrigin(List<String> allowedOrigin) {
        AllowedOrigin = allowedOrigin;
    }
}
