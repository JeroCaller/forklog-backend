package com.acorn.api.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.acorn.dto.KakaoDto;

@FeignClient(
		name="KakaoLoginOpenFeign", 
		url = "https://kauth.kakao.com"
)
public interface KakaoLoginOpenFeign {
	
    @PostMapping(value = "/oauth/token", consumes = "application/x-www-form-urlencoded")
    KakaoDto.OAuthToken getToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("code") String code
    );
	
}
