package com.acorn.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Builder(toBuilder = true) @NoArgsConstructor @AllArgsConstructor
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no")
    private int no; // 댓글번호

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content; // 내용

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 생성일

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일

    @Column(name = "is_deleted", columnDefinition = "TINYINT DEFAULT 0")
    private boolean isDeleted; // 삭제 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eatery_no")
    private Eateries eatery; // 식당

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_no")
    private Members member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_no")
    private Comments parentComment; // 부모댓글
    
    @Builder.Default
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comments> childComments = new ArrayList<>(); // 자식댓글 목록
    
    public Comments addChildComment(Comments childComment) {
        if (this.parentComment == null) { // 부모 댓글이 null인 경우에만 자식 댓글 추가 가능
            // 부모 댓글 설정은 새로운 Comment 객체에서 처리
            Comments newChildComment = childComment.toBuilder()
                .parentComment(this) // 새로운 부모 설정
                .build();
            List<Comments> updatedChildComments = new ArrayList<>(this.childComments);
            updatedChildComments.add(newChildComment);
            return this.toBuilder()
                .childComments(updatedChildComments) // 새로운 자식 댓글 리스트로 업데이트
                .build(); // 새로운 Comment 객체 반환
        } else {
            throw new IllegalStateException("대댓글에는 다시 대댓글을 작성할 수 없습니다.");
        }
    }
    
    public boolean hasChildComments() {
    	return !childComments.isEmpty();
    }
}
