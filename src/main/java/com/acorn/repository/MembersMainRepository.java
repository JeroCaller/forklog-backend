package com.acorn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acorn.entity.MembersMain;

public interface MembersMainRepository extends JpaRepository<MembersMain, Integer> {
	
	MembersMain findByEmail(String email);
	
	// 이메일 중복 검사
	boolean existsByEmail(String email);
	
	// 비밀번호 재설정
	Optional<MembersMain> findOptionalByEmail(String email);
}
