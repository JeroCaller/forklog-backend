package com.acorn.process;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.acorn.common.MemberRole;
import com.acorn.dto.MembersResponseDto;
import com.acorn.entity.Members;
import com.acorn.exception.member.AnonymousAlertException;
import com.acorn.exception.member.NotRegisteredMemberException;
import com.acorn.repository.MembersRepository;
import com.acorn.utils.AuthUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembersEateriesProcess {
	
	private final MembersRepository membersRepository;
	
	/**
	 * 현재 로그인한 사용자의 비민감 정보 반환.
	 * 
	 * 패스워드, 거주지 상세 주소 등의 민감한 정보가 사용자 정보에 있으므로 
	 * 보안을 위해 비민감 정보만 반환하도록 함.
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 * @throws AnonymousAlertException - 현재 사용자가 비로그인 익명인일 경우 발생.
	 * @throws NotRegisteredMemberException 
	 * - 현재 사용자가 로그인한 사람으로 인식되었으나 DB에 등록되지 않은 사용자일 때 발생.
	 */
	public MembersResponseDto getLoginedMember()
			throws AnonymousAlertException, NotRegisteredMemberException {
		
		Authentication authentication = SecurityContextHolder
				.getContext()
				.getAuthentication();
		
		String email = authentication.getName();
		String role = AuthUtil.getRole(authentication);
		
		if (role.equals(MemberRole.ROLE_ANONYMOUS)) {
			throw new AnonymousAlertException();
		}
		
		Members members = membersRepository.findByEmail(email);
		if (members == null) {
			NotRegisteredMemberException e = new NotRegisteredMemberException();
			// DB에 등록되지 않은 사용자 정보 확인용
			e.setEmail(email);
			e.setRole(role);
			throw e;
		}
		
		return MembersResponseDto.toDto(members);
	}
	
}
