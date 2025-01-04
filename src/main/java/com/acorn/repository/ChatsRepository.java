package com.acorn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acorn.entity.Chats;

public interface ChatsRepository extends JpaRepository<Chats, Integer>{
	
	List<Chats> findByUserNo(Integer userNo); 
	
	List<Chats> findAllByOrderByCreatedAtAsc();
	
}
