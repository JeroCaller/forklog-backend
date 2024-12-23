package com.acorn.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// RegisterRequestDto : 회원가입 시 클라이언트로부터 데이터를 받아오는 DTO
@Getter
@Setter
@NoArgsConstructor
public class RegisterRequestDto {
	
	@NotBlank
	@Email
	private String email;
	
	@NotBlank
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,20}$") // 최소 8자 이상 최대 20자 이하, 숫자, 특수문자, 영문자가 포함
	private String password;
	
	@NotBlank
	@Pattern(regexp = "^[a-zA-Z가-힣]{2,20}$") // 2자 이상 20자 이하, 한글과 영어만 입력
	private String name;
	
	@NotBlank
	@Pattern(regexp = "^\\d{8}$") // 숫자 8자리로 입력
	private String birthDate;
	
	@NotBlank
	@Pattern(regexp ="^[0-9]{11}$") // 숫자 11자리만 허용
	private String phone;
	
	@NotBlank
	private String postcode;
	
	@NotBlank
	private String roadAddress;
	
	@NotBlank
	private String detailAddress;
	
	@NotNull
	private Boolean terms = true;
	
	@NotNull
	private Boolean emailVerified = true;
	
	@NotBlank
	private String status = "Active";
	
	@NotBlank
	private Integer snsConnected = 0;
	
	@NotBlank
	private LocalDateTime updatedAt = LocalDateTime.now();
	
	@NotBlank
	private LocalDateTime createdAt = LocalDateTime.now();
}
