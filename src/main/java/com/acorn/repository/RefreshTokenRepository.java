package com.acorn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acorn.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

	Optional<RefreshToken> findByRefreshToken(String refreshToken);

	Optional<RefreshToken> findByEmail(String email);

	void deleteByRefreshToken(String refreshToken);
}
