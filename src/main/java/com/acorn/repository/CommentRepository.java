package com.acorn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acorn.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer>{
	List<Comment> findByEateryNoOrderByCreatedAtDesc(int eateryNo);
    List<Comment> findByMemberNoOrderByCreatedAtDesc(int memberNo);
}
