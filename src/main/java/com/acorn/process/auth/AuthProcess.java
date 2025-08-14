package com.acorn.process.auth;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.acorn.dto.auth.LoginRequestDto;
import com.acorn.dto.members.RegisterRequestDto;
import com.acorn.dto.auth.LoginRepsonseDto;
import com.acorn.dto.members.RegisterResponseDto;

import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author YYUMMMMMMMM
 */
public interface AuthProcess {
	
	ResponseEntity<? super RegisterResponseDto> register(RegisterRequestDto dto);
	
	ResponseEntity<? super LoginRepsonseDto> login(LoginRequestDto dto, HttpServletResponse response);
	
	ResponseEntity<?> logout(HttpServletResponse response);
	
	ResponseEntity<? super RegisterResponseDto> checkEmailDuplication(String email);
	
	ResponseEntity<Map<String, Object>> findEmail(Map<String, String> user);
	
	ResponseEntity<String> findPassword(Map<String, String> user);
}
