package com.acorn.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatUsersDto {
	private Integer no;
	
	private MembersDto member;
	
	private boolean isLeaved;
	
	private List<ChatsDto> chats;
}
