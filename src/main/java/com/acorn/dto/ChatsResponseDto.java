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
public class ChatsResponseDto {
	private Integer no;
	
	private String content;
	
    private LocalDateTime createdAt;
    
    private Integer memberNo;
    private String nickname;
	
    public static ChatsResponseDto fromEntity(Chats entity) {
        return ChatsResponseDto.builder()
                .no(entity.getNo())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .memberNo(entity.getMember().getNo()) // 멤버 번호만 포함
                .nickname(entity.getMember().getNickname())
                .build();
    }
}
