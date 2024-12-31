package com.acorn.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.dto.LoginRepsonseDto;
import com.acorn.dto.LoginRequestDto;
import com.acorn.dto.RegisterRequestDto;
import com.acorn.dto.RegisterResponseDto;
import com.acorn.process.AuthProcess;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthProcess authProcess;

	// 회원가입
	@PostMapping("/register")
	public ResponseEntity<? super RegisterResponseDto> register(@RequestBody @Valid RegisterRequestDto requestBody) {
		// 회원가입 처리 및 응답 반환
		ResponseEntity<? super RegisterResponseDto> response = authProcess.register(requestBody);
		return response;
	}

	// 이메일 중복검사
	@GetMapping("/check-email")
	public ResponseEntity<? super RegisterResponseDto> checkEmailDuplication(@RequestParam("email") String email) {
		return authProcess.checkEmailDuplication(email);
	}

	// 로그인
	@PostMapping("/login")
	public ResponseEntity<? super LoginRepsonseDto> login(@RequestBody @Valid LoginRequestDto dto, HttpServletResponse response) {
		// 로그인 처리 및 응답 반환
		ResponseEntity<? super LoginRepsonseDto> loginResponse = authProcess.login(dto, response);
		return loginResponse;
	}
	
	// 로그아웃
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response) {
		ResponseEntity<?> logout = authProcess.logout(response);
		return logout;
	}

	// 이메일 찾기
	@PostMapping("/find-email")
	public ResponseEntity<Map<String, Object>> findEmail(@RequestBody Map<String, String> user) {
		return authProcess.findEmail(user);
	}

	// 비밀번호 찾기
	@PostMapping("/find-password")
	public ResponseEntity<String> findPassword(@RequestBody Map<String, String> user) {
		return authProcess.findPassword(user);
	}
}
