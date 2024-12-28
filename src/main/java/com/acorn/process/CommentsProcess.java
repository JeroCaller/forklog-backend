package com.acorn.process;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.acorn.dto.CommentsDto;
import com.acorn.entity.Comments;
import com.acorn.repository.CommentsRepository;
import com.acorn.repository.EateriesRepository;
import com.acorn.repository.MembersMainRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentsProcess {
	private final CommentsRepository commentRepository;
	private final MembersMainRepository memberRepository;
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
	public List<CommentsDto> getCommentsByEatery(int eateryNo) {
		return commentRepository.findByEateryNoOrderByCreatedAtDesc(eateryNo)
			.stream().map(CommentsDto::fromEntity).collect(Collectors.toList());
	}
	
	public List<CommentsDto> getCommentsByMember(int memberNo) {
		return commentRepository.findByMemberNoOrderByCreatedAtDesc(memberNo)
			.stream().map(CommentsDto::fromEntity).collect(Collectors.toList());
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
