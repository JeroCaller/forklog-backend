package com.acorn.response;

/**
 * 응답 데이터로 HTTP 응답 상태를 상세히 설명한 메시지를 포함시키기 위한 용도.
 * 프론트엔드 단에서 응답 성공 및 실패 이유를 추측할 수 있게 하기 위함. 
 * 
 * 여기서는 일반적인 상황에서의 메시지를 담고 있으므로 특수한 상황에서의 메시지 필요 시 
 * 별도의 인터페이스 (또는 커스텀 예외 클래스 등)를 정의하여 메시지 정의하면 됨. 
 * 
 * 구현 강제 용도 X
 * 
 * @author JeroCaller (JJH)
 */
public interface ResponseStatusMessages {
	String NO_DATA_FOUND = "조회된 데이터가 없습니다.";
	String READ_SUCCESS = "데이터 조회 성공.";
	
	String CREATE_SUCCESS = "데이터 삽입 성공.";
	String CREATE_FAILED = "데이터 삽입 실패.";
	
	String UPDATE_SUCCESS = "데이터 변경 성공.";
	String UPDATE_FAILED = "데이터 변경 실패.";
	
	String DELETE_SUCCESS = "데이터 삭제 성공.";
	String DELETE_FAILED = "데이터 삭제 실패.";
	
	String NO_REQUEST_ARGS = "입력된 요청 인자 없음.";
	
	String AUTHORIZED_MEMBER = "인증된 사용자입니다.";
	String UNAUTHORIZED_MEMBER = "비로그인 또는 인증되지 않은 사용자입니다. 미인증 사용자에게는 제공되지 않는 기능입니다.";
}
