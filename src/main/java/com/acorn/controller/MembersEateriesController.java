package com.acorn.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.common.MemberRole;
import com.acorn.common.ResponseMessage;
import com.acorn.dto.MembersResponseDto;
import com.acorn.exception.member.AnonymousAlertException;
import com.acorn.exception.member.NotRegisteredMemberException;
import com.acorn.process.MembersEateriesProcess;
import com.acorn.response.ResponseJson;
import com.acorn.response.ResponseStatusMessages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자 로그인 여부에 따른 로그인 정보 및 음식점 정보 관련 컨트롤러.
 *
 */
@RestController
@RequestMapping("/main/members")
@RequiredArgsConstructor
@Slf4j
public class MembersEateriesController {
	
	private final MembersEateriesProcess memberEateriesProcess;
	
	/**
	 * 사용자 정보 반환 (민감한 정보 X) 컨트롤러 메서드. 
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	@GetMapping("/info")
	public ResponseEntity<ResponseJson> getUserInfo() {
		ResponseJson responseJson = null;
		
		MembersResponseDto memberDto = null;
		boolean isException = true;
		try {
			memberDto = memberEateriesProcess.getLoginedMember();
			isException = false;
		} catch (AnonymousAlertException e) {
			
			memberDto = MembersResponseDto.builder()
					.role(MemberRole.ROLE_ANONYMOUS)
					.build();
			
			responseJson = ResponseJson.builder()
					.status(HttpStatus.UNAUTHORIZED)
					.message(e.getMessage())
					.data(memberDto)
					.build();
			
		} catch (NotRegisteredMemberException e) {
			
			memberDto = MembersResponseDto.builder()
					.email(e.getEmail())
					.role(e.getRole())
					.build();
			
			responseJson = ResponseJson.builder()
					.status(HttpStatus.BAD_REQUEST)
					.message(e.getMessage())
					.data(memberDto)
					.build();
		}
		
		if (isException) {
			return responseJson.toResponseEntity();
		}
		
		responseJson = ResponseJson.builder()
				.status(HttpStatus.OK)
				.message(ResponseStatusMessages.AUTHORIZED_MEMBER)
				.data(memberDto)
				.build();
		
		return responseJson.toResponseEntity();
	}
	
}
