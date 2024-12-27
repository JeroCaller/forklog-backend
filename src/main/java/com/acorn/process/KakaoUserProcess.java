package com.acorn.process;

import com.acorn.api.openfeign.KakaoUserOpenFeign;
import com.acorn.dto.KakaoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class KakaoUserProcess {
    private static final Logger log = LoggerFactory.getLogger(KakaoUserProcess.class);

    private final KakaoUserOpenFeign kakaoUserOpenFeign;

    public KakaoUserProcess(KakaoUserOpenFeign kakaoUserOpenFeign) {
        this.kakaoUserOpenFeign = kakaoUserOpenFeign;
    }

    public KakaoDto.KakaoAccount getUserInfo(String accessToken) {
//        log.info("access_token : {} ", accessToken);
        String authorizationHeader = "Bearer " + accessToken;
        return kakaoUserOpenFeign.getUserInfo(authorizationHeader);
    }
}
