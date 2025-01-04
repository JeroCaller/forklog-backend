package com.acorn.process;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acorn.dto.ChatsRequestDto;
import com.acorn.dto.ChatsResponseDto;
import com.acorn.dto.MembersDto;
import com.acorn.entity.Chats;
import com.acorn.entity.Members;
import com.acorn.repository.ChatsRepository;
import com.acorn.repository.MembersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatsProcess {

    private final ChatsRepository chatsRepository;
    private final MembersRepository membersRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 모든 메시지 조회
    public List<ChatsResponseDto> getAllMessages() {
        List<Chats> chats = chatsRepository.findAllByOrderByCreatedAtAsc();
        return chats.stream().map(chat -> ChatsResponseDto.fromEntity(chat)).toList();
    }

    // 새 메시지 저장 및 브로드캐스트
    /**
     * Authentication context로부터 사용자의 로그인 여부 및 회원 정보를 반환하여
     * 등록한 메시지를 DB에 저장하고 저장된 메시지를 모든 사용자에게 브로드캐스트 
     * @param content
     */
    @Transactional
    public void saveAndBroadcastMessage(ChatsRequestDto request) {
        // 메시지 엔티티 생성 후 저장
        Chats chats = Chats.builder()
                .content(request.getContent())
                .member(membersRepository.findById(request.getMemberNo()).orElseThrow(() -> new IllegalArgumentException("회원 정보 조회 실패")))
                .build();
        Chats savedChat = chatsRepository.save(chats);

        // 저장된 메시지를 모든 사용자에게 브로드캐스트
        messagingTemplate.convertAndSend("/sub/chat/message", ChatsResponseDto.fromEntity(savedChat));
    }
}