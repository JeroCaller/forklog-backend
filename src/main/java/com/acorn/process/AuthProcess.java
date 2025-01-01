package com.acorn.process;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.acorn.dto.LoginRequestDto;
import com.acorn.dto.RegisterRequestDto;
import com.acorn.dto.LoginRepsonseDto;
import com.acorn.dto.RegisterResponseDto;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthProcess {
	
	ResponseEntity<? super RegisterResponseDto> register(RegisterRequestDto dto);
	
	ResponseEntity<? super LoginRepsonseDto> login(LoginRequestDto dto, HttpServletResponse response);
	
	ResponseEntity<?> logout(HttpServletResponse response);
	
	ResponseEntity<? super RegisterResponseDto> checkEmailDuplication(String email);
	
	ResponseEntity<Map<String, Object>> findEmail(Map<String, String> user);
	
	ResponseEntity<String> findPassword(Map<String, String> user);
}
