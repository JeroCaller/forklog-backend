package com.acorn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acorn.entity.Comments;

public interface CommentsRepository extends JpaRepository<Comments, Integer>{
	List<Comments> findByEateryNoOrderByCreatedAtDesc(int eateryNo);
    List<Comments> findByMemberNoOrderByCreatedAtDesc(int memberNo);
}
