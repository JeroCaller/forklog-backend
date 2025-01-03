package com.acorn.dto;

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
public class ReviewMembersDto {	
	private Integer no;
	private String nickname;
	
	// toDto
	public static ReviewMembersDto toDto(Members members) {		
		return ReviewMembersDto.builder()
				.no(members.getNo())
                .nickname(members.getNickname())
                .build();
	}
}
