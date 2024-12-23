package com.acorn.entity;

import java.time.LocalDateTime;

import com.acorn.dto.RegisterRequestDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
public class MembersDetail {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer no;
	
	@Column(name = "birth_date")
	private String birthDate;
	
	@Column(name = "gender")
	private String gender;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "email_verified")
	private Boolean emailVerified;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "terms")
	private Boolean terms;
	
	@Column(name = "member_no", insertable = false, updatable = false)
    private Integer memberNo;
	
	@OneToOne
	@JoinColumn(name = "member_no", referencedColumnName = "no")
	private MembersMain membersMain; // MembersMain과의 관계
	
	// 회원가입 시 클라이언트로부터 받아온 데이터를 엔티티에 저장하는 메소드
	public static MembersDetail registerToEntity(RegisterRequestDto dto) {
		return MembersDetail.builder()
				.birthDate(dto.getBirthDate())
				.phone(dto.getPhone())
				.emailVerified(dto.getEmailVerified())
				.status(dto.getStatus())
				.createdAt(dto.getCreatedAt())
				.terms(dto.getTerms())
				.build();
	}
}
