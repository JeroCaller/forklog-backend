package com.acorn.entity;

import java.math.BigDecimal;
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
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reviews {
    @Id
    @Column(name = "no")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer no;
    
    @Column(name = "rating", nullable = false)
	private BigDecimal rating;
    
    @Column(name = "content", columnDefinition = "TEXT")
	private String content;
    
    @CreationTimestamp	//mariadb current_timestamp() 매핑 어노테이션 
    @Column(name="created_at", updatable = false)
	private LocalDateTime createdAt;
    
    //mariadb current_timestamp() ON UPDATE current_timestamp() 매핑 어노테이션
    @UpdateTimestamp
    @Column(name = "updated_at")
	private LocalDateTime updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "member_no",referencedColumnName = "no")
    private MembersMain membersMain;
    
    @ManyToOne
    @JoinColumn(name = "eatery_no",referencedColumnName = "no")
    private Eateries eateries;
    
}
