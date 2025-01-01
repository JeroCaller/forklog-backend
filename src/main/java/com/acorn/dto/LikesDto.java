package com.acorn.dto;

import java.time.LocalDateTime;

import com.acorn.entity.Likes;
import com.acorn.repository.CommentsRepository;
import com.acorn.repository.MembersRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class LikesDto {
	private int no;
	private int commentNo;
	private int memberNo;
	private LocalDateTime createdAt;
	
	// Entity -> Dto
	public static LikesDto fromEntity(Likes likes) {
		return LikesDto.builder()
			.no(likes.getNo())
			.commentNo(likes.getComment().getNo())
			.memberNo(likes.getMember().getNo())
			.createdAt(likes.getCreatedAt())
			.build();
	}
	
	// Dto -> Entity
	public Likes toEntity(CommentsRepository commentsRepository, MembersRepository membersRepository) {
		return Likes.builder()
			.no(this.no)
			.comment(commentsRepository.findById(this.commentNo).orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다.")))
			.member(membersRepository.findById(this.memberNo).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다.")))
			.createdAt(this.createdAt)
			.build();
	}
}
