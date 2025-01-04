package com.acorn.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;
import com.acorn.dto.ChatsDto;
import com.acorn.process.ChatsProcess;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatsController {

    private final ChatsProcess chatsProcess;

    // 모든 채팅 메시지 조회
    @GetMapping("/message")
    public List<ChatsDto> getAllMessages() {
        return chatsProcess.getAllMessages();
    }

    // 새 메시지 전송 (WebSocket을 통해 처리)
    @MessageMapping("/message")
    public void sendMessage(@RequestBody ChatsDto chatDto) {
        chatsProcess.saveAndBroadcastMessage(chatDto);
    }
    
    
}