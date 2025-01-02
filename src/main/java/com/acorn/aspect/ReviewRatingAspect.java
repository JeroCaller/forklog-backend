package com.acorn.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.acorn.dto.ReviewRequestDto;
import com.acorn.entity.Reviews;
import com.acorn.process.ReviewsProcess;
import com.acorn.repository.ReviewsRepository;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class ReviewRatingAspect {
    private final ReviewsProcess reviewsProcess;
    private final ReviewsRepository reviewsRepository;

    // 리뷰 등록,수정 후 평균 별점 업데이트
    @AfterReturning(
        pointcut = "execution(* com.acorn.process.ReviewsProcess.registReview(..)) || " +
                  "execution(* com.acorn.process.ReviewsProcess.updateReview(..))"
    )
    public void afterReviewChange(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof ReviewRequestDto) {
            ReviewRequestDto dto = (ReviewRequestDto) args[0];
            reviewsProcess.updateEateryAverageRating(dto.getEateryNo());
        }
    }

    // 리뷰 삭제 전 평균 별점 업데이트
    @AfterReturning(
        pointcut = "execution(* com.acorn.process.ReviewsProcess.deleteReview(..))"
    )
    public void afterReviewDelete(JoinPoint joinPoint) {
        String reviewNo = (String) joinPoint.getArgs()[0];
        Reviews review = reviewsRepository.findById(Integer.parseInt(reviewNo))
            .orElseThrow(() -> new RuntimeException("Review not found"));
        reviewsProcess.updateEateryAverageRating(review.getEateries().getNo());
    }
}
