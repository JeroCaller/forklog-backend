package com.acorn.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import com.acorn.dto.ChatsRequestDto;
import com.acorn.dto.ChatsResponseDto;
import com.acorn.dto.MembersDto;
import com.acorn.process.ChatsProcess;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
// @RequestMapping은 @MessageMapping API 경로를 
public class ChatsController {

    private final ChatsProcess chatsProcess;
    
    // 채팅방 이용 회원 정보 조회
    @GetMapping("/chat/member")
    public ResponseEntity<MembersDto> getMember() {
    	return ResponseEntity.ok().body(chatsProcess.getMember());
    }

    // 모든 채팅 메시지 조회
    @GetMapping("/chat/message")
    public ResponseEntity<List<ChatsResponseDto>> getAllMessages() {
        return ResponseEntity.ok().body(chatsProcess.getAllMessages());
    }

    // 새 메시지 전송 (WebSocket을 통해 처리)
    @MessageMapping("/chat/message")
    public void sendMessage(@RequestBody ChatsRequestDto request) {
        chatsProcess.saveAndBroadcastMessage(request);
    }
    
    
}