package com.mysite.sbb.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationEnvironmentConfig {

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUrl;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    
    public String getNaverClientId() {
        return naverClientId;
    }

    public String getNaverClientSecret() {
        return naverClientSecret;
    }
    
    public String getNaverRedirectUrl() {
        return naverRedirectUrl;
    }
}
