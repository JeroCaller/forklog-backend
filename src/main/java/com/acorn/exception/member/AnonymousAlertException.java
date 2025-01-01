package com.acorn.exception.member;

/**
 * 현재 사용자가 비로그인임을 식별하기 위한 용도의 커스텀 예외 클래스
 * 
 * @author JeroCaller (JJH)
 */
public class AnonymousAlertException extends Exception {
	
	public AnonymousAlertException() {
		super("현재 사용자는 익명의 비로그인 사용자입니다.");
	}
	
}
