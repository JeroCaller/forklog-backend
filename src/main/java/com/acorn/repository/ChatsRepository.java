package com.acorn.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.acorn.entity.Chats;

/**
 *
 * @author EaseHee
 */
public interface ChatsRepository extends JpaRepository<Chats, Integer>{
	
	List<Chats> findAllByOrderByCreatedAtAsc();
	
	Slice<Chats> findAllByOrderByNoDesc(Pageable pageRequest);
}
