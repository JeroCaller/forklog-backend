package com.acorn.repository;

import com.acorn.dto.EateryResponseDto;
import com.acorn.entity.Favorites;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoritesRepository extends JpaRepository<Favorites, Integer> {
    // 특정 회원 번호로 음식점 리스트 조회(마이페이지)
    @Query("SELECT new com.acorn.dto.EateryResponseDto(e.no, e.name, e.thumbnail, e.description, e.tel, e.address, e.rating, e.longitude, e.latitude, e.category.name) " +
           "FROM Favorites f JOIN f.eatery e WHERE f.memberNo = :memberNo")
    List<EateryResponseDto> findByMemberNo(@Param("memberNo") int memberNo);
    
    // 특정 회원(memberNo)과 음식점(eateryNo)에 대한 즐겨찾기를 (조회, 수정, 삭제)
    Optional<Favorites> findByMemberNoAndEateryNo(int memberNo, int eateryNo);
    
    // 즐겨찾기 상태 존재 여부만 확인
    boolean existsByMemberNoAndEateryNo(int memberNo, int eateryNo);
    
    // 회원 번호와 status가 true인 즐겨찾기 리스트 조회
    @Query("SELECT f FROM Favorites f WHERE f.memberNo = :memberNo AND f.status = true")
    List<Favorites> findByMemberNoAndStatus(@Param("memberNo") int memberNo);
    
    // status가 true인 즐겨찾기만 조회
    List<Favorites> findByMemberNoAndStatus(int memberNo, Boolean status);
}
