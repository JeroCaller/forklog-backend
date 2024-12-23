package com.acorn.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reviews {
    @Id
    @Column(name = "no", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer no;
    
    @Column(name = "rating", nullable = false)
	private BigDecimal rating;
    
    @Column(name = "content", columnDefinition = "TEXT")
	private String content;
    
    @Column(name = "has_photo", columnDefinition = "TINYINT")
	private Boolean hasPhoto;
    
    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdAt;
    
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private LocalDateTime updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "member_no",referencedColumnName = "no")
    private MembersMain membersMain;
    
    @ManyToOne
    @JoinColumn(name = "eatery_no",referencedColumnName = "no")
    private Eateries eateries;
    
    
}
