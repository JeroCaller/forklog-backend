package com.acorn.process;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.acorn.entity.ChatUsers;
import com.acorn.repository.ChatUsersRepository;

@Service
public class ChatsProcess {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatUsersRepository chatUsersRepository;
    
    // 새로운 채팅방 생성
    public void createChatRoom(ChatUsers chatUsers) {
    	chatUsersRepository.save(chatUsers);
        updateChatRoom();
    }

    // 클라이언트에 채팅방 목록을 전송
    public void updateChatRoom() {
    	List<ChatUsers> userList = chatUsersRepository.findAll();
    	messagingTemplate.convertAndSend("/sub/chat/room/update", userList);
    }
    
    // 채팅방 종료
    public void closeChatRoom(int userNo) {
    	ChatUsers chatUsers = chatUsersRepository.findById(userNo).orElseThrow();
    	chatUsers.setLeaved(true);
        chatUsersRepository.save(chatUsers);
        updateChatRoom();
    }

}
