package com.acorn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acorn.entity.Members;

public interface MembersRepository extends JpaRepository<Members, Integer> {

	Members findByEmail(String email);

	// 이메일 중복 검사
	boolean existsByEmail(String email);

	// 휴대전화 중복 검사
	boolean existsByPhone(String phone);
	
	// 닉네임 중복 검사
	boolean existsByNickname(String nickname);

	// 이메일 찾기
	Optional<Members> findByPhone(String phone);

	// 비밀번호 재설정
	Optional<Members> findOptionalByEmail(String email);
	
	// 재욱 : 아이디(email)의 no 조회
	@Query("SELECT m.no FROM Members m WHERE m.email = :email")
	int findNoByEmail(@Param("email") String email);
}
