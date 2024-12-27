package com.acorn.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.acorn.dto.KakaoDto;
import com.acorn.process.KakaoLoginProcess;

@Controller
public class KakaoLoginController {
	
	private KakaoLoginProcess kakaoLoginProcess;
	
	public KakaoLoginController (KakaoLoginProcess kakaoLoginProcess) {
		this.kakaoLoginProcess = kakaoLoginProcess;
	}
	
	@GetMapping("auth/login/kakao")
	public ResponseEntity<KakaoDto.OAuthToken> kakaoLoginProcess (@RequestParam("code") String code) {
		return ResponseEntity.ok().body(kakaoLoginProcess.getToken(code));
	}
}
