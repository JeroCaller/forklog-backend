package com.acorn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acorn.entity.ChatUsers;

public interface ChatUsersRepository extends JpaRepository<ChatUsers, Integer> {
	
}
