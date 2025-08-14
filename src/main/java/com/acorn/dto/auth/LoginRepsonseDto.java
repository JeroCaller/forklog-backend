package com.acorn.dto.auth;

import com.acorn.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.acorn.common.ResponseCode;
import com.acorn.common.ResponseMessage;

import lombok.Getter;

/**
 * LoginRepsonseDto : 로그인 과정에서 발생하는 다양한 응답을 관리하는 DTO
 *
 * @author YYUMMMMMMMM
 */
@Getter
public class LoginRepsonseDto extends ResponseDto {

	private String accessToken;
	private String refreshToken;
	private int accessExpirationTime;
	private int refreshExpirationTime;

	private LoginRepsonseDto(String accessToken, String refreshToken) {
		super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
		this.accessToken = accessToken;
		this.accessExpirationTime = 3600;
		this.refreshExpirationTime = 604800;
	}

	public static ResponseEntity<LoginRepsonseDto> success(String accessToken, String refreshToken) {
		LoginRepsonseDto result = new LoginRepsonseDto(accessToken, refreshToken);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	public static ResponseEntity<ResponseDto> loginFailed() {
		ResponseDto result = new ResponseDto(ResponseCode.SIGN_IN_FAIL, ResponseMessage.SIGN_IN_FAIL);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
	}
}
