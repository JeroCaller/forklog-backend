package com.acorn.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.dto.JwtDto;
import com.acorn.dto.LoginRepsonseDto;
import com.acorn.dto.LoginRequestDto;
import com.acorn.dto.RegisterRequestDto;
import com.acorn.dto.RegisterResponseDto;
import com.acorn.jwt.JwtProvider;
import com.acorn.process.AuthProcess;
import com.acorn.process.RefreshTokenProcess;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final JwtProvider jwtProvider;
	private final AuthProcess authProcess;
	private final RefreshTokenProcess refreshTokenProcess;

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
	public ResponseEntity<? super LoginRepsonseDto> login(@RequestBody @Valid LoginRequestDto requestBody) {
		// 로그인 처리 및 응답 반환
		ResponseEntity<? super LoginRepsonseDto> response = authProcess.login(requestBody);
		return response;
	}

	// 리프레시 토큰으로 액세스 토큰 갱신
	@PostMapping("/refresh")
	public ResponseEntity<?> refreshAccessToken(@RequestBody JwtDto jwtDto) {

		String email = jwtDto.getEmail();

		return refreshTokenProcess.verifyRefreshToken(jwtDto.getEmail()).map(rt -> {
			String newAccessToken = jwtProvider.createAccessToken(jwtDto);
			return ResponseEntity.ok().body(newAccessToken);
		}).orElseGet(() -> ResponseEntity.status(401).body("Invalid or expired refresh token"));
	}

	// 로그아웃
	@PostMapping("/logout")
	public ResponseEntity<?> logout(@CookieValue(value = "accessToken", required = false) String accessToken,
									@CookieValue(value = "refreshToken", required = false) String refreshToken) {
		// 엑세스 토큰 쿠키 삭제
		ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", "")
				.httpOnly(true) // 클라이언트 스크립트에서 접근 불가
				.secure(true) // HTTPS에서만 전송
				.path("/") // 쿠키의 유효 경로
				.maxAge(0) // 만료된 쿠키로 설정
				.sameSite("None") // SameSite 속성 설정
				.build();

		// 리프레쉬 토큰 쿠키 삭제
		ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
				.httpOnly(true) // 클라이언트 스크립트에서 접근 불가
				.secure(true) // HTTPS에서만 전송
				.path("/") // 쿠키의 유효 경로
				.maxAge(0) // 만료된 쿠키로 설정
				.sameSite("None") // SameSite 속성 설정
				.build();

		// 리프레시 토큰이 있다면 서버에서도 삭제 처리
		if (refreshToken != null) {
			refreshTokenProcess.deleteRefreshToken(refreshToken); // DB에서 리프레시 토큰 삭제
		}

		System.out.println("Set-Cookie: " + accessTokenCookie.toString()); // 쿠키 정보 로그
		System.out.println("Set-Cookie: " + refreshTokenCookie.toString()); // 쿠키 정보 로그

		// 쿠키를 클라이언트에게 전달하여 로그아웃 처리
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
				.header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
				.body("로그아웃 성공");
	}
	
	// 이메일 찾기
	@PostMapping("/find-email")
	public ResponseEntity<Map<String, Object>> findEmail(@RequestBody Map<String, String> user) {
		// System.out.println(user.get("name"));
		// System.out.println(user.get("phone"));
		return authProcess.findEmail(user);
	}
	
	// 비밀번호 찾기
		@PostMapping("/find-password")
		public ResponseEntity<String> findPassword(@RequestBody Map<String, String> user) {
			// System.out.println(user.get("email"));
			// System.out.println(user.get("phone"));
			return authProcess.findPassword(user);
		}
}
