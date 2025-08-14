package com.acorn.exception.category;

/**
 * 조회된 카테고리가 없을 때 발생시킬 커스텀 예외 클래스. 
 * 
 * RuntimeException을 상속받도록 하면 메서드 선언부에 throws를 추가하라는 
 * 이클립스 자동 기능이 제공되지 않음. 이를 위해 Exception을 대신 상속받도록 함.
 * 
 * @author JeroCaller (JJH)
 */
public class NoCategoryFoundException extends Exception {
	
	public NoCategoryFoundException(String input) {
		super("해당 카테고리 조회 결과가 없습니다. 입력한 카테고리 관련 정보: " + input);
	}
}
