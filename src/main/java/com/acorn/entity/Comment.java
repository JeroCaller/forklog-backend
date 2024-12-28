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
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@Getter @Builder(toBuilder = true) @NoArgsConstructor @AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no")
    private int no;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "likes_count", columnDefinition = "INT DEFAULT 0")
    private int likesCount;

    @Column(name = "is_deleted", columnDefinition = "TINYINT DEFAULT 0")
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eatery_no")
    private Eateries eatery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no")
    private Members member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_no")
    private Comment parentComment;
    
    @Builder.Default
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> childComments = new ArrayList<>();
    
    public Comment addChildComment(Comment childComment) {
        if (this.parentComment == null) { // 부모 댓글이 null인 경우에만 자식 댓글 추가 가능
            // 부모 댓글 설정은 새로운 Comment 객체에서 처리
            Comment newChildComment = childComment.toBuilder()
                .parentComment(this) // 새로운 부모 설정
                .build();

            List<Comment> updatedChildComments = new ArrayList<>(this.childComments);
            updatedChildComments.add(newChildComment);

            return this.toBuilder()
                .childComments(updatedChildComments) // 새로운 자식 댓글 리스트로 업데이트
                .build(); // 새로운 Comment 객체 반환
        } else {
            throw new IllegalStateException("Cannot add child comment to a non-parent comment.");
        }
    }
    
    public boolean hasChildComments() {
    	return !childComments.isEmpty();
    }
}
