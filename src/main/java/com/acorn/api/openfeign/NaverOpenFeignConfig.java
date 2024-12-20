package com.acorn.api.openfeign;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;

@Configuration
public class NaverOpenFeignConfig {
    @Value("${naver.search.clientId}")
    private String naverClientId;

    @Value("${naver.search.clientSecret}")
    private String naverClientSecret;

    /**
     * Header에 API 아이디, 비밀키를 작성해주는 인터셉터 설정
     * @return lambda : 함수형 인터페이스 구현 객체 반환
     */
    @Bean
    public RequestInterceptor naverRequestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Naver-Client-Id", naverClientId);
            requestTemplate.header("X-Naver-Client-Secret", naverClientSecret);
        };
    }
}
