package com.acorn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acorn.entity.Members;

public interface MembersRepository extends JpaRepository<Members, Integer> {

	/**
	 *
	 * @author YYUMMMMMMMM
	 * @param email
	 * @return
	 */
	Members findByEmail(String email);

	/**
	 * 이메일 중복 검사
	 *
	 * @author YYUMMMMMMMM
	 * @param email
	 * @return
	 */
	boolean existsByEmail(String email);

	/**
	 * 휴대전화 중복 검사
	 *
	 * @author YYUMMMMMMMM
	 * @param phone
	 * @return
	 */
	boolean existsByPhone(String phone);

	/**
	 * 닉네임 중복 검사
	 *
	 * @author YYUMMMMMMMM
	 * @param nickname
	 * @return
	 */
	boolean existsByNickname(String nickname);

	/**
	 * 이메일 찾기
	 *
	 * @author YYUMMMMMMMM
	 * @param phone
	 * @return
	 */
	Optional<Members> findByPhone(String phone);

	/**
	 * 비밀번호 재설정
	 *
	 * @author YYUMMMMMMMM
	 * @param email
	 * @return
	 */
	Optional<Members> findOptionalByEmail(String email);

	/**
	 * 아이디(email)의 no 조회
	 *
	 * @author jaeuk-choi
	 * @param email
	 * @return
	 */
	@Query("SELECT m.no FROM Members m WHERE m.email = :email")
	int findNoByEmail(@Param("email") String email);
}
