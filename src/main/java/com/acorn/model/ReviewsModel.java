package com.acorn.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.acorn.dto.ReviewEateriesDto;
import com.acorn.dto.ReviewImagesRequestDto;
import com.acorn.dto.ReviewImagesResponseDto;
import com.acorn.dto.ReviewMembersDto;
import com.acorn.dto.ReviewRequestDto;
import com.acorn.dto.ReviewResponseDto;
import com.acorn.entity.ReviewImages;
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
	@Value("${file.upload-dir}")
	private String uploadDir;
	//member_no으로 ReviewResponseDto리스트 조회
	public List<ReviewResponseDto> getReviewsByMemberNo(String memberNo){		
		return reviewsRepository.getReviewsByMemberNo(memberNo).stream()
	               .map(review -> getReviewResponseDto(review)).toList();
	}
	//eatery_no으로 ReviewResponseDto리스트 조회
	public List<ReviewResponseDto> getReviewsByEateryNo(String eateryNo){		
		return reviewsRepository.getReviewsByEateryNo(eateryNo).stream()
	               .map(review -> getReviewResponseDto(review)).toList();
	}
	//review_no에 해당하는 ReviewImagesResponseDto리스트 조회
	public List<ReviewImagesResponseDto> getReviewImagesByReviewNo(String reviewNo){
		return Optional.ofNullable(reviewImagesRepository.getReviewImagesByReviewNo(reviewNo))
				.orElse(Collections.emptyList()).stream() // null일 경우 빈 리스트로 대체				
				.map(ReviewImagesResponseDto::toDto).toList();
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
	//ReviewImagesRequestDto->ReviewImages 엔티티 변환
	public ReviewImages setreviewImagesRequestDtoToEntity(ReviewImagesRequestDto dto, Reviews reviews) {
		return ReviewImages.builder()
				.imageUrl(dto.getImageUrl())
				.reviews(reviews)
				.build();
	}
	//ReviewRequestDto->Reviews 엔티티 변환
	public Reviews setReviewRequestDtoToEntity(ReviewRequestDto dto) {
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
	public void registReview(ReviewRequestDto inputDto,List<MultipartFile> files) throws Exception {
		Reviews review= reviewsRepository.save(setReviewRequestDtoToEntity(inputDto));
		if(inputDto.getHasPhoto()) {
			// 파일 처리 반복문
			for (MultipartFile file : files) {
				if (!file.isEmpty()) {
					String fileName = System.currentTimeMillis() + "_"+file.getOriginalFilename();
					Path uploadPath = Paths.get(uploadDir);
					if(Files.notExists(uploadPath)) Files.createDirectories(uploadPath);
					//resolve : fileName을 결합하여 최종 파일경로를 생성하는 메서드
					Path filePath = uploadPath.resolve(fileName);
					//파일 특정 경로로 복사, 이미 동일 파일 존재시 덮어쓰기
					//Files.copy() : InputStream으로부터 파일을 읽어 filePath위치에 파일을 저장
					Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
					//사진 저장용 객체생성
					ReviewImagesRequestDto reviewImagesRequestDto=new ReviewImagesRequestDto();
					reviewImagesRequestDto.setImageUrl(filePath.toString());
					reviewImagesRequestDto.setReviewNo(review.getNo());
					reviewImagesRepository.save(setreviewImagesRequestDtoToEntity(reviewImagesRequestDto,review));
				}
			}
		}
	}
	//review_image, review 수정은 등록으로부터 3일 이내만 가능=>프론트에서 수정버튼 on,off로 해결
	//review_image 수정
	@Transactional
	public void updateReviewImage(ReviewImagesRequestDto dto) {
		
	}	
	//review 수정
//	@Transactional
//	public void updateReview()
	//review_image 삭제
}
