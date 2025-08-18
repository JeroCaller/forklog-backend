package com.acorn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acorn.entity.Likes;

/**
 *
 * @author kai-jang99
 */
public interface LikesRepository extends JpaRepository<Likes, Integer>{

	/**
	 * 좋아요 중복 방지용 메서드
	 *
	 * @param memberNo
	 * @param commentNo
	 * @return
	 */
	boolean existsByMember_NoAndComment_No(int memberNo, int commentNo);

	/**
	 * 삭제할 좋아요 ID 찾는 메서드
	 *
	 * @param eateryNo
	 * @param memberNo
	 * @return
	 */
	@Query("""
		SELECT l
		FROM Likes l
		JOIN l.comment c
		WHERE c.eatery.no = :eateryNo
		AND l.member.no = :memberNo
	""")
	List<Likes> findByEateryAndMember(
		@Param("eateryNo") int eateryNo,
		@Param("memberNo") int memberNo
	);
}
