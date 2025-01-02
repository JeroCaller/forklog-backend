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
			String accessToken = parseAccessToken(request);
			//System.out.println("request accessToken : " + accessToken);

			if (accessToken == null) {
				String refreshToken = parseRefreshToken(request);
				//System.out.println("request refreshToken : " + refreshToken);

				if (refreshToken != null && jwtUtil.validate(refreshToken)) {
					String email = jwtUtil.extractUseremail(refreshToken);

					if (email != null) {
						String newAccessToken = jwtUtil.createAccessToken(email);
						//System.out.println("newAccessToken : " + newAccessToken);

						Cookie newAccessTokenCookie = new Cookie("accessToken", newAccessToken);
						newAccessTokenCookie.setHttpOnly(true);
						newAccessTokenCookie.setSecure(false);
						newAccessTokenCookie.setPath("/");
						newAccessTokenCookie.setMaxAge(3600);

						response.addCookie(newAccessTokenCookie);
						response.setHeader("Authorization", "Bearer " + newAccessToken);

						UserDetails userDetails = userDetailsService.loadUserByUsername(email);

						Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
								userDetails.getAuthorities());

						SecurityContextHolder.getContext().setAuthentication(authentication);

						return;
					}
				}
			} else if (jwtUtil.validate(accessToken)) {
				String email = jwtUtil.extractUseremail(accessToken);

				UserDetails userDetails = userDetailsService.loadUserByUsername(email);

				Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities());

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {
			System.out.println("필터 처리 중 오류 발생: " + e.getMessage());
			e.printStackTrace();
		}
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
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if ("accessToken".equals(cookie.getName())) {
						accessToken = cookie.getValue();
					}
				}
			}
		}
		return accessToken;
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
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if ("refreshToken".equals(cookie.getName())) {
						refreshToken = cookie.getValue();
					}
				}
			}
		}
		return refreshToken;
	}
}
