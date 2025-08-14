package com.acorn.dto.eateries.reviews;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.acorn.entity.Comments;
import com.acorn.repository.EateriesRepository;
import com.acorn.repository.MembersRepository;

import jakarta.persistence.EntityNotFoundException;

@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CommentsDto {
    private int no;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;
    private int eateryNo;
    private int memberNo;
    private String nickname;
    private Integer parentCommentNo;
    private Long likeCount;
    
    @Builder.Default
    private List<CommentsDto> childComments = new ArrayList<>();

    // Entity -> Dto
    public static CommentsDto fromEntity(Comments comment) {
        List<CommentsDto> childCommentDtos = new ArrayList<>();
        if (comment.hasChildComments()) { // 자식 댓글이 있을 경우, 각 자식 댓글을 DTO로 변환하여 리스트에 추가
            for (Comments childComment : comment.getChildComments()) {
                childCommentDtos.add(fromEntity(childComment));
            }
        }
        return CommentsDto.builder()
            .no(comment.getNo())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .updatedAt(comment.getUpdatedAt())
            .isDeleted(comment.isDeleted())
            .eateryNo(comment.getEatery().getNo())
            .memberNo(comment.getMember().getNo())
            .nickname(comment.getMember().getNickname())
            .parentCommentNo(comment.getParentComment() != null ? comment.getParentComment().getNo() : null)
            .childComments(childCommentDtos) // childComments 필드에 자식 댓글 리스트 추가
            .build();
    }
    
    // Entity -> Dto (Read Only)
    public static CommentsDto fromEntityWithLikes(Comments comment, Map<Integer, Long> likeCounts) {
        List<CommentsDto> childCommentDtos = new ArrayList<>();
        if (comment.hasChildComments()) {
            for (Comments childComment : comment.getChildComments()) {
                // 자식 댓글을 DTO로 변환하고, 좋아요 개수는 likeCounts 맵에서 가져오기
                childCommentDtos.add(fromEntityWithLikes(childComment, likeCounts));
            }
        }
        
        // 댓글 DTO 변환
        return CommentsDto.builder()
            .no(comment.getNo())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .updatedAt(comment.getUpdatedAt())
            .isDeleted(comment.isDeleted())
            .eateryNo(comment.getEatery().getNo())
            .memberNo(comment.getMember().getNo())
            .nickname(comment.getMember().getNickname())
            .parentCommentNo(comment.getParentComment() != null ? comment.getParentComment().getNo() : null)
            .childComments(childCommentDtos) // 자식 댓글 리스트 추가
            .likeCount(likeCounts.getOrDefault(comment.getNo(), 0L)) // 부모 댓글의 좋아요 개수
            .build();
    }

    
    // Dto -> Entity
    public Comments toEntity(MembersRepository memberRepository, EateriesRepository eateryRepository) {
        return Comments.builder()
	        .content(this.content)
	        .isDeleted(this.isDeleted)
	        .eatery(eateryRepository.findById(this.eateryNo).orElseThrow(()
	        	-> new EntityNotFoundException("식당을 찾을 수 없습니다.")))
	        .member(memberRepository.findById(this.memberNo).orElseThrow(()
	        	-> new EntityNotFoundException("회원을 찾을 수 없습니다.")))
	        .parentComment(this.parentCommentNo != null ? Comments.builder().no(this.parentCommentNo).build() : null)
	        .build();
    }
}
