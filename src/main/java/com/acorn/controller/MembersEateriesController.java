package com.acorn.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.common.MemberRole;
import com.acorn.dto.EateriesDto;
import com.acorn.dto.MembersResponseDto;
import com.acorn.exception.category.NoCategoryFoundException;
import com.acorn.exception.eatery.NoEateriesFoundException;
import com.acorn.exception.member.AnonymousAlertException;
import com.acorn.exception.member.NoMemberFoundException;
import com.acorn.exception.member.NotRegisteredMemberException;
import com.acorn.process.MembersEateriesProcess;
import com.acorn.response.ResponseJson;
import com.acorn.response.ResponseStatusMessages;
import com.acorn.utils.PageUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자 로그인 여부에 따른 로그인 정보 및 음식점 정보 관련 컨트롤러.
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
		return getMemberInfo().toResponseEntity();
	}
	
	/**
	 * 사용자 맞춤 추천 음식점 정보 반환
	 * 
	 * 추천 알고리즘
	 * - 사용자 (로그인한 상황 가정) 즐겨찾기한 음식점 목록 추린 후, 
	 * 음식점 목록 내 같은 카테고리의 다른 음식점 조회하여 반환.
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	@GetMapping("/eateries/recommends")
	public ResponseEntity<ResponseJson> getEateriesForMember(
			@RequestParam(name = "page", defaultValue = "1") int page, 
			@RequestParam(name = "size", defaultValue = "10") int size
	) {
		
		ResponseJson responseJson = null;
		ResponseJson memberJson = getMemberInfo();
			
		// 미인증 (비로그인) 사용자로 판별될 시 데이터 미제공.
		if (!memberJson.getStatus().equals(HttpStatus.OK)) {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.UNAUTHORIZED)
					.message(ResponseStatusMessages.UNAUTHORIZED_MEMBER)
					.build();
			return responseJson.toResponseEntity();
		}
			
		Pageable pageRequest = PageUtil.getPageRequestOf(page, size);
		MembersResponseDto memberInfo = (MembersResponseDto) memberJson.getData();
			
		Page<EateriesDto> eateries = null;
		boolean isException = true;
		try {
			eateries = memberEateriesProcess.getEateriesByRecommend(
						memberInfo.getNo() , 
						pageRequest
			);
			isException = false;
		} catch (NoMemberFoundException e) {
			// 미인증된 사용자로 판별
			responseJson = ResponseJson.builder()
					.status(HttpStatus.UNAUTHORIZED)
					.message(e.getMessage())
					.build();
		} catch ( NoCategoryFoundException | NoEateriesFoundException e) {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.NOT_FOUND)
					.message(e.getMessage())
					.build();
		}
			
		if (!isException) {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.OK)
					.message(ResponseStatusMessages.READ_SUCCESS)
					.data(eateries)
					.build();
		}
			
		return responseJson.toResponseEntity();
		
	}
	
	/**
	 * 현재 사용자의 정보(민감한 정보 제외)를 조회 및 반환
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	private ResponseJson getMemberInfo() {
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
		
		if (!isException) {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.OK)
					.message(ResponseStatusMessages.AUTHORIZED_MEMBER)
					.data(memberDto)
					.build();
		}
		
		return responseJson;
	}
	
}
