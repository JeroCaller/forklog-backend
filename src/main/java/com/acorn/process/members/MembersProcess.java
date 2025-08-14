package com.acorn.process.members;

import org.springframework.http.ResponseEntity;

import com.acorn.dto.members.ProfileDto;

public interface MembersProcess {

	/**
	 *
	 * @author YYUMMMMMMMM
	 * @return
	 */
	ResponseEntity<?> readAccount();

	/**
	 *
	 * @author YYUMMMMMMMM
	 * @param dto
	 * @return
	 */
	ResponseEntity<?> updateAccount(ProfileDto dto);

	/**
	 *
	 * @author YYUMMMMMMMM
	 * @param dto
	 * @return
	 */
	ResponseEntity<?> deleteAccount(ProfileDto dto);

	/**
	 *
	 * @author YYUMMMMMMMM
	 * @param nickname
	 * @return
	 */
	ResponseEntity<?> checkNicknameDuplication(String nickname);

	/**
	 *
	 * @author jaeuk-choi
	 * @return
	 */
	ResponseEntity<?> getMemberNo();
}
