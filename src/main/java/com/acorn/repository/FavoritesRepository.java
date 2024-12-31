package com.acorn.repository;

import com.acorn.entity.Eateries;
import com.acorn.entity.Favorites;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface FavoritesRepository extends JpaRepository<Favorites, Integer> {
	// 회원 번호로 음식점 리스트 조회(마이페이지)
	List<Eateries> findByMemberNo(int memberNo); 
	
	// 특정 회원(memberNo)과 음식점(eateryNo)에 대한 즐겨찾기를 조회
    Optional<Favorites> findByMemberNoAndEateryNo(int memberNo, int eateryNo);
}
