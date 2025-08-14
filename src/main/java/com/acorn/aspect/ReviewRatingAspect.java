package com.acorn.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.acorn.dto.eateries.reviews.ReviewRequestDto;
import com.acorn.entity.Reviews;
import com.acorn.process.eateries.reviews.ReviewsProcess;
import com.acorn.repository.ReviewsRepository;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class ReviewRatingAspect {
    private final ReviewsProcess reviewsProcess;
    private final ReviewsRepository reviewsRepository;
    private final ThreadLocal<Integer> eateryNoHolder = new ThreadLocal<>();

    // 리뷰 등록 후 평균 별점 업데이트
    @AfterReturning(
        pointcut = "execution(* com.acorn.process.eateries.reviews.ReviewsProcess.registReview(..))"
    )
    public void afterReviewRegist(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof ReviewRequestDto) {
            ReviewRequestDto dto = (ReviewRequestDto) args[0];
            reviewsProcess.updateEateryAverageRating(dto.getEateryNo());
        }
    }

    // 리뷰 수정 후 평균 별점 업데이트
    @AfterReturning(
        pointcut = "execution(* com.acorn.process.eateries.reviews.ReviewsProcess.updateReview(..))"
    )
    public void afterReviewUpdate(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 1 && args[1] instanceof ReviewRequestDto) {
            ReviewRequestDto dto = (ReviewRequestDto) args[1];
            reviewsProcess.updateEateryAverageRating(dto.getEateryNo());
        }
    }

    // 리뷰 삭제 전에 음식점 번호 저장
    @Before("execution(* com.acorn.process.eateries.reviews.ReviewsProcess.deleteReview(..))")
    public void beforeReviewDelete(JoinPoint joinPoint) {
        String reviewNo = (String) joinPoint.getArgs()[0];
        Reviews review = reviewsRepository.findById(Integer.parseInt(reviewNo))
            .orElseThrow(() -> new RuntimeException("Review not found"));
        eateryNoHolder.set(review.getEateries().getNo());
    }

    // 리뷰 삭제 후 평균 별점 업데이트
    @AfterReturning("execution(* com.acorn.process.eateries.reviews.ReviewsProcess.deleteReview(..))")
    public void afterReviewDelete(JoinPoint joinPoint) {
        try {
            Integer eateryNo = eateryNoHolder.get();
            if (eateryNo != null) {
                reviewsProcess.updateEateryAverageRating(eateryNo);
            }
        } finally {
            eateryNoHolder.remove(); // ThreadLocal 변수 정리
        }
    }
}