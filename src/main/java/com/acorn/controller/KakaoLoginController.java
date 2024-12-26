package com.acorn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class KakaoLoginController {
	
	@GetMapping("auth/login/kakao")
	public Object kakaoLoginProcess (@RequestParam("code") String code) {
		
		return null;
	}
}
