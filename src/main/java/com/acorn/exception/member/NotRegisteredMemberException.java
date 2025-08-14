package com.acorn.exception.member;

import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 사용자로 인식되었으나 DB에 등록되지 않은 사용자일 경우 일으킬 커스텀 예외 클래스
 * 
 * @author JeroCaller (JJH)
 */
@Getter
@Setter
public class NotRegisteredMemberException extends Exception {
	
	// 해당 사용자 정보를 담기 위한 필드.
	private String email;
	private String role;
	
	public NotRegisteredMemberException() {
		super("로그인 사용자로 인식되었으나 등록되지 않은 사용자입니다. ");
	}
}
