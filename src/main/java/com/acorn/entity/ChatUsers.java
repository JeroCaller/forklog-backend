package com.acorn.entity;

import java.util.List;
import java.util.stream.Collectors;

import com.acorn.dto.ChatUsersDto;
import com.acorn.dto.MembersDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatUsers {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer no;
	
	@OneToOne
	@JoinColumn(name = "member_no")
	private Members member;
	
	private boolean isLeaved;
	
//	@OneToMany(mappedBy = "user")
//	private List<Chats> chats;
	
	public static ChatUsers toEntity(ChatUsersDto dto, Members member) {
		return ChatUsers.builder()
						.no(dto.getNo())
						.member(member)
						.isLeaved(dto.isLeaved())
//						.chats(dto.getChats().stream().map(Chats::toEntity).collect(Collectors.toList()))
						.build();
	}
	
	public static ChatUsersDto fromEntity(ChatUsers entity, Members member) {
		return ChatUsersDto.builder()
						   .no(entity.getNo())
						   .member(MembersDto.toDto(member))
//						   .chats(entity.getChats().stream().map(Chats::fromEntity).collect(Collectors.toList()))
						   .build();
	}
}
