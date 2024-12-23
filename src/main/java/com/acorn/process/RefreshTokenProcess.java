package com.acorn.process;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.acorn.entity.RefreshToken;
import com.acorn.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenProcess {

	private final RefreshTokenRepository refreshTokenRepository;

	// 리프레시 토큰 생성
	public RefreshToken createRefreshToken(String email, String token, long durationInMillis) {
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setEmail(email);
		refreshToken.setRefreshToken(token);
		refreshToken.setExpiryDate(LocalDateTime.now().plus(Duration.ofMillis(durationInMillis)));
		return refreshTokenRepository.save(refreshToken);
	}

	// 리프레시 토큰 검증
	public Optional<RefreshToken> verifyRefreshToken(String token) {
		return refreshTokenRepository.findByRefreshToken(token)
				.filter(rt -> rt.getExpiryDate().isAfter(LocalDateTime.now()));
	}

	// 리프레시 토큰 삭제 메서드
	public void deleteRefreshToken(String refreshToken) {
		if (refreshToken != null) {
			refreshTokenRepository.deleteByRefreshToken(refreshToken); // DB에서 리프레시 토큰 삭제
		}
	}
}
