package com.acorn.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 만약 REST API 응답에서 에러가 발생했을 시 프론트엔드쪽에서도 에러 발생 원인을 좀 더 쉽게 
 * 파악하기 위해 정형화된 응답 데이터 구조를 정의함. 
 * 
 * @author JeroCaller (JJH)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseJson {
	
	private HttpStatus status;
	private String message;
	private Object data;
	
	/**
	 * 현재 ResponseJson 객체를 ResponseEntity 객체로 변환.
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	public ResponseEntity<ResponseJson> toResponseEntity() {
		return ResponseEntity
				.status(this.status)
				.body(this);
	}
	
}
