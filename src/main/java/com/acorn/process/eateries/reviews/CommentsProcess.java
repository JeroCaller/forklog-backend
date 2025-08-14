package com.acorn.process.eateries.reviews;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acorn.dto.eateries.reviews.CommentsDto;
import com.acorn.entity.Comments;
import com.acorn.repository.CommentsRepository;
import com.acorn.repository.EateriesRepository;
import com.acorn.repository.MembersRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentsProcess {
	private final CommentsRepository commentRepository;
	private final MembersRepository memberRepository;
	private final EateriesRepository eateryRepository;
	
	// Create
	@Transactional
	public CommentsDto createComment(CommentsDto dto) {
        Comments comment = dto.toEntity(memberRepository, eateryRepository);

        if (dto.getParentCommentNo() != null) {
            Comments parentComment = commentRepository.findById(dto.getParentCommentNo())
            	.orElseThrow(() -> new EntityNotFoundException("부모 댓글을 찾을 수 없습니다."));
            parentComment.addChildComment(comment);
        }
        return CommentsDto.fromEntity(commentRepository.save(comment));
    }
	
	// Read
	@Transactional(readOnly = true)
	public Page<CommentsDto> getCommentsByEatery(int eateryNo, Pageable pageable) {
	    // 레포지토리 메서드 호출
	    Page<Object[]> resultPage = commentRepository.findByEatery(eateryNo, pageable);

	    // 댓글 번호를 키로 하고 좋아요 개수를 값으로 하는 맵을 생성
	    Map<Integer, Long> likeCounts = new HashMap<>();
	    
	    // 결과 페이지에서 각 댓글의 좋아요 개수를 맵에 저장
	    resultPage.forEach(objects -> {
	        Comments comment = (Comments) objects[0];   // Comments 엔티티
	        Long likeCount = (Long) objects[1];			// 좋아요 개수 (COUNT)
	        likeCounts.put(comment.getNo(), likeCount); // 댓글 번호와 좋아요 개수를 맵에 저장
	    });

	    // Object[]를 CommentsDto로 매핑
	    return resultPage.map(objects -> {
	        Comments comment = (Comments) objects[0];	// Comments 엔티티

	        // CommentsDto로 변환 (likeCounts 맵을 전달)
	        return CommentsDto.fromEntityWithLikes(comment, likeCounts);
	    });
	}

	
	// Update: 이건 @Transactional 없이 정상작동
	public CommentsDto updateComment(int no, CommentsDto updatedDto) {
	    Comments comment = commentRepository.findById(no)
	    	.orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));
	    
	    return CommentsDto.fromEntity(commentRepository.save(comment.toBuilder().content(updatedDto.getContent()).build()));
	}

	// Delete
	@Transactional
	public void deleteComment(int no) {
	    Comments comment = commentRepository.findById(no)
	        .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

	    // 자식이 있는 부모 댓글을 삭제하면 부모 댓글을 삭제상태로 변경
	    if (comment.hasChildComments()) {
	        Comments deletedComment = comment.toBuilder()
	            .isDeleted(true)
	            .content("이 댓글은 삭제되었습니다.")
	            .build();
	        commentRepository.save(deletedComment);
	    } else {
	    	// 자식을 삭제할 때 부모댓글의 자식 목록에서 해당 댓글을 제거
	    	Comments parentComment = comment.getParentComment();
	        if (parentComment != null) {
	        	parentComment.getChildComments().remove(comment);
	        	
	        	// 부모 댓글이 삭제상태이고 자식이 하나도 없을 경우 부모 댓글을 삭제
	        	if (parentComment.isDeleted() && !parentComment.hasChildComments()) {
	        		commentRepository.delete(parentComment);
	        	}
	        }
	        commentRepository.delete(comment); // 자식이 없는 댓글은 바로 삭제
	    }
	}
}
