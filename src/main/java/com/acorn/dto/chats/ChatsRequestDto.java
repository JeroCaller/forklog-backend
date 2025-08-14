package com.acorn.dto.chats;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatsRequestDto {
	private String content;
    private int memberNo;
}
