package com.acorn.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.acorn.dto.ReviewEateriesDto;
import com.acorn.dto.ReviewImagesResponseDto;
import com.acorn.dto.ReviewMembersDto;
import com.acorn.dto.ReviewRequestDto;
import com.acorn.dto.ReviewResponseDto;
import com.acorn.entity.MembersMain;
import com.acorn.entity.Reviews;
import com.acorn.repository.EateriesRepository;
import com.acorn.repository.MembersMainRepository;
import com.acorn.repository.ReviewImagesRepository;
import com.acorn.repository.ReviewsRepository;

@Repository
public class ReviewsModel {
	private MembersMainRepository membersMainRepository;
	private EateriesRepository eateriesRepository;
	private ReviewsRepository reviewsRepository;	
	private ReviewImagesRepository reviewImagesRepository;
	public ReviewsModel(ReviewsRepository reviewsRepository,ReviewImagesRepository reviewImagesRepository
			,MembersMainRepository membersMainRepository, EateriesRepository eateriesRepository) {
		this.reviewsRepository = reviewsRepository;
		this.reviewImagesRepository=reviewImagesRepository;
		this.membersMainRepository=membersMainRepository;
		this.eateriesRepository=eateriesRepository;
	}
	//member_no으로 ReviewResponseDto리스트 조회
	public List<ReviewResponseDto> getReviewsByMemberNo(String memberNo){		
		return reviewsRepository.getReviewsByMemberNo(memberNo)
	               .stream()
	               .map(review -> getReviewResponseDto(review))
	               .toList();
	}
	//eatery_no으로 ReviewResponseDto리스트 조회
	public List<ReviewResponseDto> getReviewsByEateryNo(String eateryNo){		
		return reviewsRepository.getReviewsByEateryNo(eateryNo)
	               .stream()
	               .map(review -> getReviewResponseDto(review))
	               .toList();
	}
	//review_no에 해당하는 ReviewImagesResponseDto리스트 조회
	public List<ReviewImagesResponseDto> getReviewImagesByReviewNo(String reviewNo){
		return Optional.ofNullable(reviewImagesRepository.getReviewImagesByReviewNo(reviewNo))
				.orElse(Collections.emptyList()) // null일 경우 빈 리스트로 대체
				.stream()
				.map(ReviewImagesResponseDto::toDto)
				.toList();
	}
	//Reviews->ReviewResponseDto변환
	public ReviewResponseDto getReviewResponseDto(Reviews reviews) {		
		return ReviewResponseDto.builder()
				.no(reviews.getNo())
				.rating(reviews.getRating())
				.content(reviews.getContent())
				.hasPhoto(reviews.getHasPhoto())
				.createdAt(reviews.getCreatedAt())
				.updatedAt(reviews.getUpdatedAt())
				.reviewMembersDto(ReviewMembersDto.toDto(reviews.getMembersMain()))
				.reviewEateriesDto(ReviewEateriesDto.toDto(reviews.getEateries()))
				.reviewImagesResponseDto(reviewImagesRepository.getReviewImagesByReviewNo(reviews.getNo().toString()).stream().map(ReviewImagesResponseDto::toDto).toList())
				.build();
	}
	//ReviewRequestDto->Reviews 엔티티 변환
	public Reviews setReviewRequestDtoToEntity(ReviewRequestDto dto) {
//		private List<ReviewImagesRequestDto> reviewImagesRequestDtoList;
		return Reviews.builder()
				.rating(dto.getRating())
				.content(dto.getContent())
				.hasPhoto(dto.getHasPhoto())
				.membersMain(membersMainRepository.findById(dto.getMemberNo()).get())
				.eateries(eateriesRepository.findById(dto.getEateryNo()).get())
				.build();
	}
	//Review 등록
	@Transactional
	public void registReview(Reviews reviews) throws IllegalArgumentException, OptimisticLockingFailureException {
		reviewsRepository.save(reviews);
	}
}
