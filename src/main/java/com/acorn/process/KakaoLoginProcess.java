package com.acorn.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.acorn.api.openfeign.KakaoLoginOpenFeign;
import com.acorn.dto.KakaoDto;

@Service
public class KakaoLoginProcess {
	private Logger log = LoggerFactory.getLogger(KakaoLoginProcess.class);

	private KakaoLoginOpenFeign kakaoLoginOpenFeign;
	private KakaoUserProcess kakaoUserProcess;

	private final String clientId;
	private final String redirectUri;

	public KakaoLoginProcess(
			KakaoLoginOpenFeign kakaoLoginOpenFeign, 
			@Value("${kakao.rest_api_key}") String clientId,
			@Value("${kakao.redirect_uri}") String redirectUri,
			KakaoUserProcess kakaoUserProcess
	) {
		this.kakaoLoginOpenFeign = kakaoLoginOpenFeign;
		this.clientId = clientId;
		this.redirectUri = redirectUri;
		this.kakaoUserProcess = kakaoUserProcess;
	}

	/**
	 * 
	 * Token  
	   {
		  "token_type": "bearer",
		  "access_token": "mkDQ9CZ5ADaUE11zUpoh5cIFD-QeyoN9AAAAAQo8JJsAAAGUA217QM2yTeNnt1bO",
		  "expires_in": 21599,
		  "refresh_token": "gSOoiyPJP9An-VB3dwCTLL2cfJ69FeuqAAAAAgo8JJsAAAGUA217Ps2yTeNnt1bO",
		  "refresh_token_expires_in": 5183999
	   }
	 * @param code -> Token을 발급받아 사용자 정보를 조회하는 API의 키로 활용
	 * @return
	 */
	public KakaoDto.OAuthToken getToken(String code) {
		return kakaoLoginOpenFeign.getToken(
				"authorization_code",
				clientId,
				redirectUri,
				code
		);
	}
}
