package com.acorn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.acorn.entity.Comment;
import com.acorn.entity.Eateries;
import com.acorn.entity.Members;
import com.acorn.repository.EateriesRepository;
import com.acorn.repository.MembersRepository;

import jakarta.persistence.EntityNotFoundException;

@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CommentDto {
    private int no;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likesCount;
    private boolean isDeleted;
    private int eateryNo;
    private int memberNo;
    private Integer parentCommentNo;
    
    @Builder.Default
    private List<CommentDto> childComments = new ArrayList<>();

    // Entity -> Dto
    public static CommentDto fromEntity(Comment comment) {
        List<CommentDto> childCommentDtos = new ArrayList<>();
        
        if (comment.hasChildComments()) {   // 자식 댓글이 있을 경우, 각 자식 댓글을 DTO로 변환하여 리스트에 추가
            for (Comment childComment : comment.getChildComments()) {
                childCommentDtos.add(fromEntity(childComment));
            }
        }

        return CommentDto.builder()
            .no(comment.getNo())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .updatedAt(comment.getUpdatedAt())
            .likesCount(comment.getLikesCount())
            .isDeleted(comment.isDeleted())
            .eateryNo(comment.getEatery() != null ? comment.getEatery().getNo() : null)
            .memberNo(comment.getMember() != null ? comment.getMember().getNo() : null)
            .parentCommentNo(comment.getParentComment() != null ? comment.getParentComment().getNo() : null)
            .childComments(childCommentDtos)   // childComments 필드에 자식 댓글 DTO 리스트 추가
            .build();
    }

    // Dto -> Entity
    public Comment toEntity(MembersRepository memberRepository, EateriesRepository eateryRepository) {
        return Comment.builder()
	        .content(this.content)
	        .likesCount(this.likesCount)
	        .isDeleted(this.isDeleted)
	        .eatery(eateryRepository.findById(this.eateryNo).orElseThrow(()
	        	-> new EntityNotFoundException("식당번호에 해당하는 식당을 찾을 수 없습니다.")))
	        .member(memberRepository.findById(this.memberNo).orElseThrow(()
	        	-> new EntityNotFoundException("회원번호에 해당하는 회원을 찾을 수 없습니다.")))
	        // .parentComment(this.parentCommentNo != null ? new Comment(this.parentCommentNo) : null)
	        .build();
    }
}
