package com.acorn.controller;


import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import com.acorn.dto.chats.ChatsRequestDto;
import com.acorn.dto.chats.ChatsResponseDto;
import com.acorn.dto.members.MembersDto;
import com.acorn.process.ChatsProcess;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Ajax REST API, WebSocket Subscribe 처리
 * 오픈 채팅 기능 관련 요청 컨트롤러 - 회원 정보 조회, 메시지 조회, 메시지 등록
 * 
 * @author EaseHee
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatsController { 

    private final ChatsProcess chatsProcess;
    
    // 채팅방 이용 회원 정보 조회
    @GetMapping("/chat/member")
    public ResponseEntity<MembersDto> getMember() {
    	return ResponseEntity.ok().body(chatsProcess.getMember());
    }

    /**
     * 채팅 메시지 조회
     * 
     * @param { page, size }
     * @return Slice : 첫페이지, 마지막페이지 여부를 함께 반환하여 무한스크롤 페이징 처리에 적합
     */
    @GetMapping("/chat/message")
    public ResponseEntity<Slice<ChatsResponseDto>> getMessages(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "30") int size
	) {
//    	log.info("========== page: {}, size : {} ==========", page, size);
        return ResponseEntity.ok().body(chatsProcess.getMessages(page, size));
    }

    /**
     * 새 메시지 저장 요청 처리
     * WebSocket - Client 측 publish 요청 경로 
     * WebSocketConfig에서 Destination prefix를 설정
     * 
     * saveAndBroadcastMessage() 에서 subscribe 경로에 브로드캐스트
     * 
     * @param request : ChatContent, MemberNo
     */
    @MessageMapping("/chat/message")
    public void sendMessage(@RequestBody ChatsRequestDto request) {
        chatsProcess.saveAndBroadcastMessage(request);
    }
}