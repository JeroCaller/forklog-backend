package com.acorn.exception.eatery;

/**
 * 조회된 음식점 정보가 없을 때 일으킬 커스텀 예외 클래스
 * 
 * @author JeroCaller (JJH)
 */
public class NoEateriesFoundException extends Exception {
	
	public NoEateriesFoundException() {
		super("조회된 음식점 정보가 없습니다.");
	}
}
