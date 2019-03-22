package com.gsoft.web.framework.security;

import org.springframework.security.core.GrantedAuthority;


public class MyGrantedAuthority implements GrantedAuthority {

	private static final long serialVersionUID = 194920586769228337L;

	private String allowUrl;

    private String notAllowUrl;

    @Override
    public String getAuthority() {
        return this.allowUrl + ";" + this.notAllowUrl;
    }

    public MyGrantedAuthority(String allowUrl, String notAllowUrl) {
        this.allowUrl = allowUrl;
        this.notAllowUrl = notAllowUrl;
    }

    public String getAllowUrl() {
        return allowUrl;
    }

    public void setAllowUrl(String allowUrl) {
        this.allowUrl = allowUrl;
    }

    public String getNotAllowUrl() {
        return notAllowUrl;
    }

    public void setNotAllowUrl(String notAllowUrl) {
        this.notAllowUrl = notAllowUrl;
    }

}