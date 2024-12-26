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
				
				String email = jwtUtil.extractUseremail(accessToken);
				
				UserDetails userDetails = userDetailsService.loadUserByUsername(email);
				
				Authentication authentication = 
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		filterChain.doFilter(request, response);
	}

	// 엑세스 토큰을 추출
	private String parseAccessToken(HttpServletRequest request) {
		
		String accessToken = null;
		
		String authHeader = request.getHeader("Authorization");
		System.out.println("authHeader : " + authHeader);
		
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			accessToken = authHeader.substring(7);
		} else {
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
