package com.acorn.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatsDto {
	private Integer no;
	
	private String content;
	
    private LocalDateTime createdAt;
	
	private ChatUsersDto user;
}
