package com.acorn.exception.api;

import java.util.List;

import com.acorn.entity.Eateries;

import lombok.Getter;
import lombok.Setter;

/**
 * 확인 결과 kaka API에서 특정 페이지 이상 넘기면 아무리 페이지 번호를 
 * 증가시켜도 똑같은 데이터가 반복적으로 응답되는 현상을 확인함.
 * 따라서 중복 데이터 삽입을 방지하기 위한 커스텀 예외 클래스
 * 
 * @author JeroCaller (JJH)
 */
@Getter
@Setter
public class DuplicatedInDBException extends Exception {
	
	// 중복된 데이터 수집용
	private Eateries duplicated;
	
	private int savedNum;
	
	public DuplicatedInDBException() {
		super("API 호출 결과가 DB에 이미 존재하는 데이터이므로 DB 저장 방지함.");
	}
}
