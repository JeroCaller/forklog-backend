package com.acorn.jwt;

import java.io.IOException;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.acorn.dto.JwtDto;

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

	private final JwtProvider jwtProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			// 액세스 토큰이 있는지 확인
			String accessToken = parseToken(request);

			if (accessToken == null) {
				// 액세스 토큰이 없다면 리프레시 토큰으로 액세스 토큰 재발급
				String refreshToken = parseRefreshToken(request);
				if (refreshToken != null && !jwtProvider.isRefreshTokenExpired(refreshToken)) {
					// 리프레시 토큰 검증
					JwtDto jwtDto = jwtProvider.validate(refreshToken);
					if (jwtDto != null) {
						// 리프레시 토큰을 통해 새로운 액세스 토큰 생성
						String newAccessToken = jwtProvider.createAccessToken(jwtDto);

						// 새로 생성한 액세스 토큰을 응답 헤더에 추가
						response.setHeader("Authorization", "Bearer " + newAccessToken);

						// 인증 객체 생성
						AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
								jwtDto.getEmail(), null);
						authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

						// SecurityContext에 인증 정보 설정
						SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
						securityContext.setAuthentication(authenticationToken);
						SecurityContextHolder.setContext(securityContext);
						
						return;
					}
				}
				// 리프레시 토큰도 없다면 그대로 필터 진행
				filterChain.doFilter(request, response);
				return;
			}

			// 액세스 토큰 검증
			JwtDto jwtDto = jwtProvider.validate(accessToken);
			if (jwtDto == null) {
				filterChain.doFilter(request, response);
				return;
			}

			// 권한 정보 생성
			// List<GrantedAuthority> authorities =
			// AuthorityUtils.createAuthorityList(jwtDto.getRoles());

			// 인증 객체 생성
			AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
					jwtDto.getEmail(),
					null
			// authorities // 권한 정보 설정
			);
			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			// SecurityContext에 인증 정보 설정
			SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
			securityContext.setAuthentication(authenticationToken);
			SecurityContextHolder.setContext(securityContext);

		} catch (Exception e) {
			e.printStackTrace();
		}

		filterChain.doFilter(request, response);
	}

	// 엑세스 토큰을 추출
	private String parseToken(HttpServletRequest request) {
		// Authorization 헤더에서 엑세스 토큰 추출
		String authorization = request.getHeader("Authorization");
		if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
			return authorization.substring(7);
		}
		return null; // 토큰이 없으면 null 반환
	}

	// 리프레시 토큰 추출
	private String parseRefreshToken(HttpServletRequest request) {
		// 쿠키에서 리프레시 토큰 추출
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("refreshToken".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null; // 리프레시 토큰이 없으면 null 반환
	}
}
