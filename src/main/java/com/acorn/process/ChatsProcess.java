package com.acorn.process;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acorn.dto.ChatsDto;
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
    private final MembersProcessImpl membersProcessImpl;
    private final SimpMessagingTemplate messagingTemplate;

    // 모든 메시지 조회
    public List<ChatsDto> getAllMessages() {
        List<Chats> chats = chatsRepository.findAllByOrderByCreatedAtAsc();
        return chats.stream().map(chat -> ChatsDto.fromEntity(chat)).toList();
    }

    // 새 메시지 저장 및 브로드캐스트
    /**
     * Authentication context로부터 사용자의 로그인 여부 및 회원 정보를 반환하여
     * 등록한 메시지를 DB에 저장하고 저장된 메시지를 모든 사용자에게 브로드캐스트 
     * @param content
     */
    @Transactional
    public void saveAndBroadcastMessage(String content) {
        // 회원 정보 조회
    	System.out.println(membersProcessImpl.readAccount().getBody());
    	MembersDto dto = (MembersDto) membersProcessImpl.readAccount().getBody();
    	int memberNo = dto.getNo();
    	Members member = membersRepository.findById(memberNo).orElseThrow(() -> new IllegalArgumentException("회원 조회 실패"));

        // 메시지 엔티티 생성 후 저장
        Chats chat = Chats.builder()
                .content(content)
                .member(member)
                .build();
        Chats savedChat = chatsRepository.save(chat);

        // 저장된 메시지를 모든 사용자에게 브로드캐스트
        messagingTemplate.convertAndSend("/sub/chat/message", ChatsDto.fromEntity(savedChat));
    }
}