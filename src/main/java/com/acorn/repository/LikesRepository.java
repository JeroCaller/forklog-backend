package com.acorn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acorn.entity.Likes;

public interface LikesRepository extends JpaRepository<Likes, Integer>{
	// 좋아요 중복 방지용 메서드
	boolean existsByMember_NoAndComment_No(int memberNo, int commentNo);
	
	// 삭제할 좋아요 ID 찾는 메서드
	Likes findByMemberNoAndCommentNo(int memberNo, int commentNo);
}
