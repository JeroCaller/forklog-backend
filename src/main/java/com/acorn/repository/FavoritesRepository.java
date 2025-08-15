package com.acorn.repository;

import com.acorn.dto.eateries.EateryResponseDto;
import com.acorn.entity.Favorites;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author jaeuk-choi
 */
public interface FavoritesRepository extends JpaRepository<Favorites, Integer> {

    /**
     * 특정 회원 번호로 음식점 리스트 조회(마이페이지)
     *
     * @param memberNo
     * @return
     */
    @Query("""
        SELECT new com.acorn.dto.eateries.EateryResponseDto(
            e.no,
            e.name,
            e.thumbnail,
            e.description,
            e.tel,
            e.address,
            e.rating,
            e.longitude,
            e.latitude,
            e.category.name
        )
        FROM Favorites f
        JOIN f.eatery e
        WHERE f.memberNo = :memberNo
    """)
    List<EateryResponseDto> findByMemberNo(@Param("memberNo") int memberNo);

    /**
     * 특정 회원(memberNo)과 음식점(eateryNo)에 대한 즐겨찾기를 (조회, 수정, 삭제)
     *
     * @param memberNo
     * @param eateryNo
     * @return
     */
    Optional<Favorites> findByMemberNoAndEateryNo(int memberNo, int eateryNo);

    /**
     * 즐겨찾기 상태 존재 여부만 확인
     *
     * @param memberNo
     * @param eateryNo
     * @return
     */
    @Query("""
        SELECT CASE
            WHEN COUNT(f) > 0
            THEN true
            ELSE false
            END
        FROM Favorites f
        WHERE f.memberNo = :memberNo 
        AND f.eateryNo = :eateryNo 
        AND f.status = 1
    """)
    boolean existsByMemberNoAndEateryNo(
        @Param("memberNo") int memberNo,
        @Param("eateryNo") int eateryNo
    );

    /**
     * 회원 번호와 status가 1(활성화)인 즐겨찾기 리스트 조회
     *
     * @param memberNo
     * @return
     */
    @Query("SELECT f FROM Favorites f WHERE f.memberNo = :memberNo AND f.status = 1")
    List<Favorites> findByMemberNoAndStatus(@Param("memberNo") int memberNo);

    /**
     * 특정 status 값을 기반으로 즐겨찾기 조회
     *
     * @param memberNo
     * @param status
     * @return
     */
    List<Favorites> findByMemberNoAndStatus(int memberNo, int status);

    /**
     * 특정 음식점에 대해 즐겨찾기된 수 조회
     *
     * @param eateryNo
     * @return
     */
    @Query("SELECT COUNT(f) FROM Favorites f WHERE f.eateryNo = :eateryNo AND f.status = 1")
    int countFavoritesByEateryNo(@Param("eateryNo") int eateryNo);
}
