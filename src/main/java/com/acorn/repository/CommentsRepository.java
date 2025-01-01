package com.acorn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acorn.entity.Comments;

public interface CommentsRepository extends JpaRepository<Comments, Integer>{
	//Page<Comments> findByEateryNoAndParentCommentIsNullOrderByCreatedAtDesc(int eateryNo, Pageable pageable);
	
	// SELECT c FROM Comment c LEFT JOIN FETCH c.member WHERE c.deleted = false ORDER BY c.createdAt DESC
	@Query("SELECT c FROM Comments c LEFT JOIN FETCH c.member " +
		   "WHERE c.eatery.no =:eateryNo AND c.parentComment IS NULL " +
		   "ORDER BY c.createdAt DESC")
	Page<Comments> findByEatery(@Param("eateryNo") int eateryNo, Pageable pageable);
}
