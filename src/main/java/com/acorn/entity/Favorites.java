package com.acorn.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Favorites {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int no;
	
	@Column(name = "status", nullable = false, columnDefinition = "int default 0")
    private int status; // 0: 비활성, 1: 활성
	
	@CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 생성일

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일
    
    @Column(name = "member_no")
    private int memberNo;
    
    @Column(name = "eatery_no")
	private int eateryNo;
    
    @ManyToOne
    @JoinColumn(name = "member_no", insertable=false, updatable=false)
    private Members member;

    @ManyToOne
    @JoinColumn(name = "eatery_no", insertable=false, updatable=false)
    private Eateries eatery;
}
