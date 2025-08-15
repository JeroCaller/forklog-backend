package com.acorn.exception.member;

/**
 * DB로부터 사용자 정보를 찾지 못했을 때 발생시킬 커스텀 예외 클래스
 * 
 * @author JeroCaller (JJH)
 */
public class NoMemberFoundException extends Exception {
	
	public NoMemberFoundException(String inputInfo) {
 		super("사용자 정보를 찾지 못했습니다. 입력한 사용자 정보: " + inputInfo);
	}
}
