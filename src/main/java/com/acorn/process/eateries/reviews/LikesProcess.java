package com.acorn.process.eateries.reviews;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.acorn.dto.eateries.reviews.LikesDto;
import com.acorn.repository.CommentsRepository;
import com.acorn.repository.LikesRepository;
import com.acorn.repository.MembersRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author kai-jang99
 */
@Service
@RequiredArgsConstructor
public class LikesProcess {

	private final LikesRepository likesRepository;
	private final CommentsRepository commentsRepository;
	private final MembersRepository membersRepository;

	/**
	 * Create
	 *
	 * @param dto
	 * @return
	 */
	public LikesDto createLike(LikesDto dto) {
		if(likesRepository.existsByMember_NoAndComment_No(dto.getMemberNo(), dto.getCommentNo())) {
			throw new IllegalStateException("이미 좋아요를 누른 상태입니다.");
		}
		return LikesDto.fromEntity(
			likesRepository.save(
				dto.toEntity(commentsRepository, membersRepository)
			)
		);
	}

	/**
	 * Read
	 *
	 * @param eateryNo
	 * @param memberNo
	 * @return
	 */
	public List<LikesDto> findLikesByEateryAndMember(int eateryNo, int memberNo) {
		return likesRepository.findByEateryAndMember(eateryNo, memberNo)
			.stream().map(LikesDto::fromEntity).collect(Collectors.toList());
	}

	/**
	 * Delete
	 *
	 * @param no
	 */
	public void deleteLike(int no) {
		likesRepository.delete(
			likesRepository.findById(no)
				.orElseThrow(() -> new EntityNotFoundException("좋아요를 찾을 수 없습니다.")
				)
		);
	}
}
