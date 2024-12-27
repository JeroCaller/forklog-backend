package com.acorn.api.openfeign;

import com.acorn.dto.KakaoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "KakaoUserOpenFeign",
        url = "https://kapi.kakao.com/v2"
)
public interface KakaoUserOpenFeign {
    @GetMapping("/user/me")
    KakaoDto.KakaoAccount getUserInfo(@RequestHeader("Authorization") String authorization);
}
