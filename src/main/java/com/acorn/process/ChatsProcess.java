package com.acorn.process;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acorn.dto.chats.ChatsRequestDto;
import com.acorn.dto.chats.ChatsResponseDto;
import com.acorn.dto.members.MembersDto;
import com.acorn.entity.Chats;
import com.acorn.exception.NotFoundException;
import com.acorn.repository.ChatsRepository;
import com.acorn.repository.MembersRepository;

import lombok.RequiredArgsConstructor;

/**
 * WebSocket 관련 요청 처리 서비스 로직
 * 로그인 회원 정보 반환, 채팅 내역 조회, 등록 메시지 처리 및 Broadcast
 * 
 * @author EaseHee
 */
@Service
@RequiredArgsConstructor
public class ChatsProcess {

	private final SimpMessagingTemplate messagingTemplate;
	private final ChatsRepository chatsRepository;
	private final MembersRepository membersRepository;

	/**
	 * 채팅방 사용 시 현재 로그인한 회원 정보 반환
	 *
	 * @return
	 */
	public MembersDto getMember() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		return MembersDto.toDto(membersRepository.findByEmail(email));
	}

	/**
	 * 생성일을 기준으로 page 수에 해당하는 메시지를 (기본 : 최신 등록) size 크기에 맞게 반환
	 * 최신순으로 정렬하여 메시지를 조회한 후 역순으로 재정렬 
	 * 
	 * @param { page, size }
	 * @return SliceImple : 무한 스크롤 구현용 (last 필드값으로 마지막 페이지 여부 반환)
	 */
	public Slice<ChatsResponseDto> getMessages(int page, int size) {
//        List<Chats> chats = chatsRepository.findAllByOrderByCreatedAtAsc();
		Sort sort = Sort.by(Sort.Direction.DESC, "no");
		Pageable pageRequest = PageRequest.of(page, size, sort);
		Slice<Chats> chats = chatsRepository.findAllByOrderByNoDesc(pageRequest);
		
		// 역순으로 재정렬
		List<ChatsResponseDto> reversedChats = chats.getContent()
			.stream()
			.map( ChatsResponseDto :: fromEntity )
			.collect(Collectors.toList());
		Collections.reverse(reversedChats);

		return new SliceImpl<ChatsResponseDto>(reversedChats, pageRequest, chats.hasNext());
	}

	/**
	 * 등록한 메시지를 DB에 저장하고 저장된 메시지를 모든 사용자에게 브로드캐스트
	 * 
	 * @param RequestDTO : { content, memberNo }
	 */
	@Transactional
	public void saveAndBroadcastMessage(ChatsRequestDto request) {
		// 메시지 엔티티 생성 후 저장
		Chats chats = Chats.builder()
			.content(request.getContent())
			.member(membersRepository
				.findById(request.getMemberNo())
				.orElseThrow(() -> new NotFoundException("회원 정보 조회 실패")))
			.build();

		// 저장된 메시지를 모든 사용자에게 브로드캐스트
		Chats savedChat = chatsRepository.save(chats);
		messagingTemplate.convertAndSend(
			"/sub/chat/message",
			ChatsResponseDto.fromEntity(savedChat)
		);
	}
}