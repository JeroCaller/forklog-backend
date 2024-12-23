package com.acorn.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.acorn.dto.JwtDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

//JwtProvider : JWT 처리에 필요한 로직을 제공하는 클래스, 토큰의 생성과 유효성을 검증하고, 사용자 정보를 추출하는 역할
@Component
public class JwtProvider {

	@Value("${secret-key}") // @Value : application.properties에 등록한 secretKey를 가져온다.
	private String secretKey;

	// 액세스 토큰 만료 시간 (1시간)
	private static final long ACCESS_TOKEN_EXPIRE_TIME = 60 * 1000L;

	// 리프레시 토큰 만료 시간 (7일)
	public static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;
	
	
	// SecretKey 객체로 변환
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes()); // 비밀 키를 SecretKey 객체로 변환
    }
    
	// 액세스 토큰 생성
	public String createAccessToken(JwtDto jwtDto) {
		return create(jwtDto, ACCESS_TOKEN_EXPIRE_TIME);
	}

	// 리프레시 토큰 생성
	public String createRefreshToken(JwtDto jwtDto) {
		return create(jwtDto, REFRESH_TOKEN_EXPIRE_TIME);
	}

	// JWT 생성
	public String create(JwtDto jwtDto, long expireTime) {

		Date expiredDate = new Date(System.currentTimeMillis() + expireTime);

		// JWT 생성
		return Jwts.builder()
				.signWith(getSecretKey(), SignatureAlgorithm.HS256) // SecretKey 객체 사용, 서명 및 알고리즘 설정
				.setSubject(jwtDto.getEmail()) // JWT 주체 설정
				.claim("email", jwtDto.getEmail()) // JWT 클레임 추기
				.setIssuedAt(new Date()) // JWT 생성 시간 설정
				.setExpiration(expiredDate) // JWT 만료 시간 설정
				.compact(); // JWT 문자열로 반환
	}

	// JWT 검증 및 JwtDto 반환
	public JwtDto validate(String token) {
		try {
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(getSecretKey())
					.build()
					.parseClaimsJws(token)
					.getBody();

			String email = claims.get("email", String.class);
			return new JwtDto(email);

		} catch (ExpiredJwtException e) {
			System.err.println("JWT has expired: " + e.getMessage());
			return null;
		} catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
			System.err.println("Invalid JWT: " + e.getMessage());
			return null;
		}
	}

	// 리프레시 토큰 만료 여부 확인
	public boolean isRefreshTokenExpired(String token) {
		try {
			Jwts.parserBuilder()
			.setSigningKey(getSecretKey())
			.build()
			.parseClaimsJws(token);
			
			return false; // 만료되지 않음
		} catch (ExpiredJwtException e) {
			return true; // 만료됨
		}
	}
}