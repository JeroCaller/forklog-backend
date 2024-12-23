package com.acorn.dto;

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
public class ReviewMembersDto {	
	private Integer no;
	private String name;
	
	// toDto
	public static ReviewMembersDto toDto(MembersMain membersMain) {		
		return ReviewMembersDto.builder()
				.no(membersMain.getNo())
                .name(membersMain.getName())
                .build();
	}
}
