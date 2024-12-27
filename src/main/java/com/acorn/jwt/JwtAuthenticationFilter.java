package com.acorn.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// JwtAuthenticationFilter :
// Spring Security의 필터로, JWT를 사용하여 요청을 인증하는 역할, 이 필터는 모든 요청에 대해 JWT가 유효한지 확인하고, 유효한 경우 인증 정보를 SecurityContext에 설정한다.
// OncePerRequestFilter :
// Spring Framework에서 제공하는 추상 클래스, 이 클래스는 필터가 요청당 한 번만 실행되도록 보장하는 기능을 제공한다. 즉, 동일한 요청에 대해 여러 번 호출되지 않도록 하는 역할을 한다.
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		try {
			// 액세스 토큰이 있는지 확인
			String accessToken = parseAccessToken(request);

			if (accessToken == null) {
				// 액세스 토큰이 없다면 리프레시 토큰으로 액세스 토큰 재발급
				String refreshToken = parseRefreshToken(request);
				if (refreshToken != null && !jwtUtil.isRefreshTokenExpired(refreshToken)) {
					// 리프레시 토큰 검증
					String email = jwtUtil.extractUseremail(refreshToken);
				
					if (email != null) {
						// 리프레시 토큰을 통해 새로운 액세스 토큰 생성
						String newAccessToken = jwtUtil.createAccessToken(email);
						
						UserDetails userDetails = userDetailsService.loadUserByUsername(email);
						
						Authentication authentication = 
								new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

						// 새로 생성한 액세스 토큰을 응답 헤더에 추가
						response.setHeader("Authorization", "Bearer " + newAccessToken);
						
						SecurityContextHolder.getContext().setAuthentication(authentication);
					}
				}
				// 리프레시 토큰도 없다면 그대로 필터 진행
				filterChain.doFilter(request, response);
				return;
			}

			// 액세스 토큰 검증
			if (accessToken != null && jwtUtil.validate(accessToken)) {
				
				// 토큰 주체 객체 생성
				String email = jwtUtil.extractUseremail(accessToken);
				
				// 사용자 인증 정보 객체 생성
				UserDetails userDetails = userDetailsService.loadUserByUsername(email);
				
				Authentication authentication = 
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				
				// 요청 처리 동안, 인증된 사용자 정보가 SecurityContext에 저장
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// FilterChain: 여러 개의 필터가 순차적으로 실행되는 구조이다. 각 필터는 HTTP 요청 처리 후 다음 필터로 요청을 전달한다.
		// JWT를 사용하며 STATELESS 정책을 쓰기 때문에 토큰 기반 인증을 간단히 처리할 수 있다.
		filterChain.doFilter(request, response);
	}

	// 엑세스 토큰을 추출
	private String parseAccessToken(HttpServletRequest request) {
		
		String accessToken = null;
		
		String authHeader = request.getHeader("Authorization");
		System.out.println("authHeader : " + authHeader);
		// 토큰을 얻는 방법 두 가지: 다양한 클라이언트 환경 및 요청에 대해 유연한 대처가 필요하다.
		// 헤더에서 토큰 얻기
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			accessToken = authHeader.substring(7);
			// Authorization: Bearer <JWT_TOKEN>에서 Bearer 접두어를 제거하고 실제 토큰만 추출한다.
		} else {
			// 쿠키에서 토큰 얻기
			Cookie[] cookies = request.getCookies();
			if(cookies != null) {
				for(Cookie cookie : cookies) {
					if("accessToken".equals(cookie.getName())) {
						accessToken = cookie.getValue();
					}
				}
			}
		}
		return null; // 엑세스 토큰이 없으면 null 반환
	}

	// 리프레시 토큰 추출
	private String parseRefreshToken(HttpServletRequest request) {
		
		String refreshToken = null;
		
		String authHeader = request.getHeader("Authorization");
		System.out.println("authHeader : " + authHeader);
		
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			refreshToken = authHeader.substring(7);
		} else {
			Cookie[] cookies = request.getCookies();
			if(cookies != null) {
				for(Cookie cookie : cookies) {
					if("refreshToken".equals(cookie.getName())) {
						refreshToken = cookie.getValue();
					}
				}
			}
		}
		return null; // 엑세스 토큰이 없으면 null 반환
	}
}
