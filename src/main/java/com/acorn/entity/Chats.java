package com.acorn.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.acorn.dto.ChatsDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chats {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer no;
	
	@Column(name = "content", columnDefinition = "TEXT")
	private String content;
	
	@CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
	
	@ManyToOne
	@JoinColumn(name = "user_no")
	private ChatUsers user;
	
	public static Chats toEntity(ChatsDto dto, Members member) {
		return Chats.builder()
					.no(dto.getNo())
					.content(dto.getContent())
					.user(ChatUsers.toEntity(dto.getUser(), member))
					.build();
	}
	
	
	public static ChatsDto fromEntity(Chats entity, Members member) {
		return ChatsDto.builder()
					.no(entity.getNo())
					.content(entity.getContent())
					.createdAt(entity.getCreatedAt())
					.user(ChatUsers.fromEntity(entity.getUser(), member))
					.build();
	}
}
