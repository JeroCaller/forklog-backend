package com.acorn.entity;

import java.time.LocalDateTime;

import com.acorn.dto.RegisterRequestDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Members {
	
	@Id
	@Column(name = "no")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer no;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "sns_connected")
	private Integer snsConnected;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "postcode")
	private String postcode;
	
	@Column(name = "road_address")
	private String roadAddress;
	
	@Column(name = "detail_address")
	private String detailAddress;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
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
	
	@Column(name = "terms")
	private Boolean terms;
	
	@Column(name = "nickname")
	private String nickname;
	
	// 회원가입 시 클라이언트로부터 받아온 데이터를 엔티티에 저장하는 메소드
	public static Members registerToEntity(RegisterRequestDto dto) {
		return Members.builder()
				.email(dto.getEmail())
				.password(dto.getPassword())
				.snsConnected(dto.getSnsConnected())
				.name(dto.getName())
				.postcode(dto.getPostcode())
				.roadAddress(dto.getRoadAddress())
				.detailAddress(dto.getDetailAddress())
				.createdAt(dto.getCreatedAt())
				.updatedAt(dto.getUpdatedAt())
				.birthDate(dto.getBirthDate())
				.gender(dto.getGender())
				.phone(dto.getPhone())
				.emailVerified(dto.getEmailVerified())
				.status(dto.getStatus())
				.terms(dto.getTerms())
				.nickname(dto.getNickname() != null ? dto.getNickname() : dto.getName())
				.build();
	}
}
