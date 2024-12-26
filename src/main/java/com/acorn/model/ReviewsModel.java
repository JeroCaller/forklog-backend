package com.acorn.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
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
	public ReviewImages setReviewImagesRequestDtoToEntity(ReviewImagesRequestDto dto, Reviews reviews) {
		return ReviewImages.builder()
				.imageUrl(dto.getImageUrl())
				.reviews(reviews)
				.build();
	}
	//INSERT용 ReviewRequestDto->Reviews 엔티티 변환
	public Reviews setReviewRequestDtoToEntityForInsert(ReviewRequestDto dto) {
		return Reviews.builder()
				.rating(dto.getRating())
				.content(dto.getContent())
				.hasPhoto(dto.getHasPhoto())
				.membersMain(membersMainRepository.findById(dto.getMemberNo()).get())
				.eateries(eateriesRepository.findById(dto.getEateryNo()).get())
				.build();
	}
	//UPDATE용 ReviewRequestDto->Reviews 엔티티 변환
	public Reviews setReviewRequestDtoToEntityForUpdate(ReviewRequestDto dto) {
		return Reviews.builder()
				.no(dto.getNo())
				.rating(dto.getRating())
				.content(dto.getContent())
				.hasPhoto(dto.getHasPhoto())
				.membersMain(membersMainRepository.findById(dto.getMemberNo()).get())
				.eateries(eateriesRepository.findById(dto.getEateryNo()).get())
				.build();
	}
	//ReviewImage 저장
	@Transactional
	public void registReviewImages(Reviews review,List<MultipartFile> files) throws Exception {
		for (MultipartFile file : files) {
			if (!file.isEmpty()) {
				String fileName = review.getNo() + "_"+file.getOriginalFilename();
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
				reviewImagesRepository.save(setReviewImagesRequestDtoToEntity(reviewImagesRequestDto,review));
			}
		}
	}
	//Review 등록
	@Transactional
	public void registReview(ReviewRequestDto inputDto,List<MultipartFile> files) throws Exception {
		Reviews review= reviewsRepository.save(setReviewRequestDtoToEntityForInsert(inputDto));
		if(inputDto.getHasPhoto()) {
			registReviewImages(review,files);
		}
	}
	//review_image, review 수정은 등록으로부터 3일 이내만 가능=>프론트에서 수정버튼 on,off로 해결
	//review_image 수정
	@Transactional
	public void updateReviewImage(String reviewNo, List<MultipartFile> files) throws Exception {
		Reviews review=reviewsRepository.findById(Integer.parseInt(reviewNo)).get();
		//저장된 경로 리스트
		List<String> existingPhotoPaths= getReviewImagesByReviewNo(reviewNo).stream().map(ReviewImagesResponseDto::getImageUrl).toList();
		List<String> newPhotoPaths= new ArrayList<String>();
		for (MultipartFile file : files) {
			if (!file.isEmpty()) {
				String fileName = reviewNo + "_"+file.getOriginalFilename();
				Path uploadPath = Paths.get(uploadDir);
				if(Files.notExists(uploadPath)) Files.createDirectories(uploadPath);
				Path filePath = uploadPath.resolve(fileName);
				//수정 요청의 Img 경로 리스트
				newPhotoPaths.add(filePath.toString());
			}
		}
	    // 1. 기존 경로 중 삭제할 경로 찾기
	    List<String> pathsToDelete = existingPhotoPaths.stream()
	        .filter(path -> !newPhotoPaths.contains(path))
	        .collect(Collectors.toList());

	    // 2. 새 경로 중 추가할 경로 찾기
	    List<String> pathsToAdd = newPhotoPaths.stream()
	        .filter(path -> !existingPhotoPaths.contains(path))
	        .collect(Collectors.toList());

	    // 3. 파일 삭제 및 DB 삭제
	    for (String path : pathsToDelete) {
	        // 실제 파일 삭제
            Files.deleteIfExists(Paths.get(path));
	        // DB 삭제
            reviewImagesRepository.deleteByImageUrl(path);
	    }
	    
	    // 4. files 중 추가할 file 찾기, 파일 및 DB 추가
	    List<MultipartFile> filesToAdd=files.stream()
	    	    .filter(file -> {
	    	        String fullPath = Paths.get(uploadDir, reviewNo + "_" + file.getOriginalFilename()).toString();
	    	        return pathsToAdd.contains(fullPath);
	    	    })
	    		.collect(Collectors.toList());
	    registReviewImages(review,filesToAdd);
	}
	//review 수정
	@Transactional
	public void updateReview(String reviewNo,ReviewRequestDto inputDto, List<MultipartFile> files) throws Exception {
		reviewsRepository.save(setReviewRequestDtoToEntityForUpdate(inputDto));
		if(inputDto.getHasPhoto()) {
			updateReviewImage(reviewNo, files);
		}else {
			reviewImagesRepository.deleteAllById(reviewImagesRepository.findAllNoByReviewNo(reviewNo));
		}
	}
	//review 삭제
	@Transactional
	public void deleteReview(String reviewNo) {
		//reviewImages 삭제
		reviewImagesRepository.deleteAllById(reviewImagesRepository.findAllNoByReviewNo(reviewNo));
		//review 삭제
		reviewsRepository.deleteById(Integer.parseInt(reviewNo));
	}
}
