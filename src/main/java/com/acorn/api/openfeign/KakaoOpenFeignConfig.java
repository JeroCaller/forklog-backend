package com.acorn.api.openfeign;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.Logger.Level;
import feign.RequestInterceptor;

@Configuration
public class KakaoOpenFeignConfig {
    @Value("${kakao.rest_api_key}")
    private String kakaoApiKey;

    @Value("${kakao.redirect_uri}")
    private String kakaoRedirectUri;

    @Bean
    public RequestInterceptor kakaoRequestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Authorization", "KakaoAK " + kakaoApiKey);
        };
    }
    
    /**
     * Feign Logging을 위한 설정.
     * 
     * REST API 키가 이클립스 콘솔창에 노출되니 정말 필요할 때만 사용!
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
    	return Level.FULL;
    }
}
