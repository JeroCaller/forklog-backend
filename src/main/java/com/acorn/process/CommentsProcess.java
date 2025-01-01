package com.acorn.process;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.acorn.dto.CommentsDto;
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

	
	// Update
	public CommentsDto updateComment(int no, CommentsDto updatedDto) {
	    Comments comment = commentRepository.findById(no)
	    	.orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));
	    
	    return CommentsDto.fromEntity(commentRepository.save(comment.toBuilder().content(updatedDto.getContent()).build()));
	}

	// Delete
	public void deleteComment(int no) {
        Comments comment = commentRepository.findById(no)
        	.orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        if (comment.hasChildComments()) {
            comment.toBuilder()
            	.isDeleted(true)
            	.content("이 댓글은 삭제되었습니다.")
            	.build();
            commentRepository.save(comment);
        } else {
            if (comment.getParentComment() != null) {
                comment.getParentComment().getChildComments().remove(comment);
            }
            commentRepository.delete(comment);
        }
    }
}
