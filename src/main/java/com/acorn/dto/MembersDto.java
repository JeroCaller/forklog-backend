package com.acorn.dto;

import java.time.LocalDateTime;

import com.acorn.entity.MembersDetail;
import com.acorn.entity.MembersMain;

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
	
	// toDto
	public static MembersDto toDto(MembersMain membersMain) {
		
		MembersDetail detail = membersMain.getMembersDetail();
		
		return MembersDto.builder()
				.no(membersMain.getNo())
                .email(membersMain.getEmail())
                .password(membersMain.getPassword())
                .snsConnected(membersMain.getSnsConnected())
                .name(membersMain.getName())
                .postcode(membersMain.getPostcode())
                .roadAddress(membersMain.getRoadAddress())
                .detailAddress(membersMain.getDetailAddress())
                .updatedAt(membersMain.getUpdatedAt())
                .birthDate(detail.getBirthDate())
                .gender(detail.getGender())
                .phone(detail.getPhone())
                .emailVerified(detail.getEmailVerified())
                .status(detail.getStatus())
                .createdAt(detail.getCreatedAt())
                .build();
	}
}
