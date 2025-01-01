package com.acorn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acorn.entity.Comments;

public interface CommentsRepository extends JpaRepository<Comments, Integer>{
//	@Query("SELECT c FROM Comments c LEFT JOIN FETCH c.member " +
//		   "WHERE c.eatery.no =:eateryNo AND c.parentComment IS NULL " +
//		   "ORDER BY c.createdAt DESC")
//	Page<Comments> findByEatery(@Param("eateryNo") int eateryNo, Pageable pageable);
	
	@Query("SELECT c, COUNT(l) FROM Comments c " +
		   "LEFT JOIN FETCH c.member " +
		   "LEFT JOIN Likes l ON c.no = l.comment.no " +
		   "WHERE c.eatery.no = :eateryNo AND c.parentComment IS NULL " +
		   "GROUP BY c.no " +
		   "ORDER BY c.createdAt DESC")
	Page<Object[]> findByEatery(@Param("eateryNo") int eateryNo, Pageable pageable);
}
