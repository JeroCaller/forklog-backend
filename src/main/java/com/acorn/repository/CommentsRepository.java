package com.acorn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.acorn.entity.Comments;

public interface CommentsRepository extends JpaRepository<Comments, Integer>{
	Page<Comments> findByEateryNoAndParentCommentIsNullOrderByCreatedAtDesc(int eateryNo, Pageable pageable);
    // Page<Comments> findByMemberNoOrderByCreatedAtDesc(int memberNo, Pageable pageable);
}
