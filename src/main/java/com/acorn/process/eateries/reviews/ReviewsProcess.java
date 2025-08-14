package com.acorn.process.eateries.reviews;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.acorn.dto.eateries.reviews.ReviewEateriesDto;
import com.acorn.dto.eateries.reviews.ReviewImagesRequestDto;
import com.acorn.dto.eateries.reviews.ReviewImagesResponseDto;
import com.acorn.dto.eateries.reviews.ReviewMembersDto;
import com.acorn.dto.eateries.reviews.ReviewRequestDto;
import com.acorn.dto.eateries.reviews.ReviewResponseDto;
import com.acorn.entity.ReviewImages;
import com.acorn.entity.Reviews;
import com.acorn.repository.EateriesRepository;
import com.acorn.repository.MembersRepository;
import com.acorn.repository.ReviewImagesRepository;
import com.acorn.repository.ReviewsRepository;

@Repository
public class ReviewsProcess {
	private MembersRepository membersMainRepository;
	private EateriesRepository eateriesRepository;
	private ReviewsRepository reviewsRepository;	
	private ReviewImagesRepository reviewImagesRepository;
	
	public ReviewsProcess(ReviewsRepository reviewsRepository,ReviewImagesRepository reviewImagesRepository
			,MembersRepository membersMainRepository, EateriesRepository eateriesRepository) {
		this.reviewsRepository = reviewsRepository;
		this.reviewImagesRepository=reviewImagesRepository;
		this.membersMainRepository=membersMainRepository;
		this.eateriesRepository=eateriesRepository;
	}
	
	@Value("${file.upload-dir}")
	private String uploadDir;
	//member_no으로 ReviewResponseDto리스트 조회
	public Page<ReviewResponseDto> getReviewsByMemberNo(String memberNo,int page){
		Pageable pageable = PageRequest.of(page, 5);
		return reviewsRepository.getReviewsByMemberNo(pageable,memberNo).map(review -> getReviewResponseDto(review));
	}
	//eatery_no으로 ReviewResponseDto리스트 조회
	public Page<ReviewResponseDto> getReviewsByEateryNo(String eateryNo,int page){
		Pageable pageable = PageRequest.of(page, 5);
		return reviewsRepository.getReviewsByEateryNo(pageable,eateryNo).map(review -> getReviewResponseDto(review));
	}
	//review_no에 해당하는 ReviewImagesResponseDto리스트 조회
	public List<ReviewImagesResponseDto> getReviewImagesByReviewNo(String reviewNo){
		return reviewImagesRepository.getReviewImagesByReviewNo(reviewNo).stream()
				.map(ReviewImagesResponseDto::toDto).toList();
	}
	//Reviews->ReviewResponseDto변환
	public ReviewResponseDto getReviewResponseDto(Reviews reviews) {
		return ReviewResponseDto.builder()
				.no(reviews.getNo())
				.rating(reviews.getRating())
				.content(reviews.getContent())
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
		if(!files.isEmpty()) {
			registReviewImages(review,files);
		}
	}
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
		if(!files.isEmpty()) {
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
	
	// 평균 별점 업데이트
    @Transactional
    public void updateEateryAverageRating(int eateryNo) {
        // 해당 음식점의 모든 리뷰 별점의 평균을 계산
        BigDecimal averageRating = reviewsRepository.calculateAverageRatingByEateryNo(eateryNo);
        
        // 리뷰가 없는 경우 0.0으로 설정
        if (averageRating == null) {
            averageRating = BigDecimal.ZERO;
        }
        
        // 음식점의 평균 별점 업데이트
        eateriesRepository.updateRating(eateryNo, averageRating);
    }
}
