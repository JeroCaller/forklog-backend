package com.acorn.dto.members;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileDto {
	
	@NotBlank
	@Email
	private String email;
	
	@NotBlank
	private String nickname;
	
	@NotBlank
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,20}$")
	private String currentPassword;
	
	@NotBlank
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,20}$")
	private String changePassword;

	@NotBlank
	@Pattern(regexp ="^[0-9]{11}$")
	private String phone;
	
	@NotBlank
	private String postcode;
	
	@NotBlank
	private String roadAddress;
	
	@NotBlank
	private String detailAddress;
	
	@NotBlank
	private LocalDateTime updatedAt = LocalDateTime.now();
}
