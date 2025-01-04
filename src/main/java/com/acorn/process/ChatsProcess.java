package com.acorn.process;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acorn.dto.ChatsDto;
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
    public List<ChatsDto> getAllMessages() {
        List<Chats> chats = chatsRepository.findAllByOrderByCreatedAtAsc();
        return chats.stream().map(chat -> Chats.fromEntity(chat, chat.getUser().getMember())).toList();
    }

    // 새 메시지 저장 및 브로드캐스트
    @Transactional
    public void saveAndBroadcastMessage(ChatsDto chatDto) {
        // 유저 정보 조회
        Members member = membersRepository.findById(chatDto.getUser().getMember().getNo())
                                          .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        // 메시지 엔티티 생성 후 저장
        Chats chat = Chats.toEntity(chatDto, member);
        Chats savedChat = chatsRepository.save(chat);

        // 저장된 메시지를 모든 사용자에게 브로드캐스트
        messagingTemplate.convertAndSend("/sub/chat/room", Chats.fromEntity(savedChat, member));
    }
}