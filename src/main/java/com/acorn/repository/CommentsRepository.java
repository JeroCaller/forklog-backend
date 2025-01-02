package com.acorn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acorn.entity.Comments;

public interface CommentsRepository extends JpaRepository<Comments, Integer> {
	// Page<Comments> findByEateryNoAndParentCommentIsNullOrderByCreatedAtDesc(int
	// eateryNo, Pageable pageable);

	@Query("SELECT c, COUNT(l) FROM Comments c " +
			"LEFT JOIN FETCH c.member " +
			"LEFT JOIN Likes l ON c.no = l.comment.no " +
			"WHERE c.eatery.no = :eateryNo " +
			"GROUP BY c.no " +
			"ORDER BY c.createdAt DESC")
	Page<Comments> findByEatery(@Param("eateryNo") int eateryNo, Pageable pageable);
}
