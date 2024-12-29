package com.acorn.dto;

import java.time.LocalDateTime;

import com.acorn.entity.Members;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembersDto {
	
	private Integer no;
	private String email;
	private String password;
	private Integer snsConnected;
	private String name;
	private String postcode;
	private String roadAddress;
	private String detailAddress;
	private String birthDate;
    private String gender;
    private String phone;
    private Boolean emailVerified;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String nickname;
	
	// toDto
	public static MembersDto toDto(Members members) {
		
		return MembersDto.builder()
				.no(members.getNo())
                .email(members.getEmail())
                .password(members.getPassword())
                .snsConnected(members.getSnsConnected())
                .name(members.getName())
                .postcode(members.getPostcode())
                .roadAddress(members.getRoadAddress())
                .detailAddress(members.getDetailAddress())
                .updatedAt(members.getUpdatedAt())
                .birthDate(members.getBirthDate())
                .gender(members.getGender())
                .phone(members.getPhone())
                .emailVerified(members.getEmailVerified())
                .status(members.getStatus())
                .createdAt(members.getCreatedAt())
                .nickname(members.getNickname())
                .build();
	}
}
