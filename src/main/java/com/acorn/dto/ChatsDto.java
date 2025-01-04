package com.acorn.dto;

import java.time.LocalDateTime;

import com.acorn.entity.Chats;

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
	
	private MembersDto member;
	
    public static ChatsDto fromEntity(Chats entity) {
        return ChatsDto.builder()
                .no(entity.getNo())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .member(MembersDto.toDto(entity.getMember()))
                .build();
    }
}
