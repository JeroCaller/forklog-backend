package com.acorn.api.openfeign;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
