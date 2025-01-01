package com.acorn.dto;

import com.acorn.entity.Members;
import com.acorn.utils.LocationUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 멤버 정보에는 패스워드, 우편번호, 생일, 거주지 주소 등 민감한 개인 정보가 있으므로 
 * 아예 이를 제외한 필드로만 구성하여 클라이언트에 응답을 보낼 용도의 DTO
 * 
 * @author JeroCaller (JJH)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembersResponseDto {
	
	private Integer no;
	private String email;
	private String role;
	private String nickname;
	
	// 개인 정보 보안 및 지역에 기반한 음식점 조회의 정확성을 위해 
	// 도로명 주소의 중분류(예- 서울 강남구)까지만 설정하도록 함.
	private String mediumRoadAddress;
	
	public static MembersResponseDto toDto(Members entity) {
		return MembersResponseDto.builder()
				.no(entity.getNo())
				.email(entity.getEmail())
				.role(entity.getRole())
				.nickname(entity.getNickname())
				.mediumRoadAddress(LocationUtil
					.getLocationMediumStringByFull(entity.getRoadAddress())
				)
				.build();
	}
	
}
