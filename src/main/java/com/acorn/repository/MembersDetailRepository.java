package com.acorn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acorn.entity.MembersDetail;
import com.acorn.entity.MembersMain;

public interface MembersDetailRepository extends JpaRepository<MembersDetail, Integer> {
	
	// 휴대전화 중복 검사
	boolean existsByPhone(String phone);
	
	// 이메일 찾기
	Optional<MembersDetail> findByPhone(String phone);
}
