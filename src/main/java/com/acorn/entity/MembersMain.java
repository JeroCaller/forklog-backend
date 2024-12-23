package com.acorn.entity;

import java.time.LocalDateTime;

import com.acorn.dto.RegisterRequestDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class MembersMain {
	
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
	
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	@OneToOne(mappedBy = "membersMain", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private MembersDetail membersDetail; // MembersDetail과의 관계
	
	// 회원가입 시 클라이언트로부터 받아온 데이터를 엔티티에 저장하는 메소드
	public static MembersMain registerToEntity(RegisterRequestDto dto) {
		return MembersMain.builder()
				.email(dto.getEmail())
				.password(dto.getPassword())
				.name(dto.getName())
				.postcode(dto.getPostcode())
				.roadAddress(dto.getRoadAddress())
				.detailAddress(dto.getDetailAddress())
				.updatedAt(dto.getUpdatedAt())
				.snsConnected(dto.getSnsConnected())
				.build();
	}
	
	// MembersDetail 설정 메소드
    public void setMembersDetail(MembersDetail membersDetail) {
        this.membersDetail = membersDetail;
        membersDetail.setMembersMain(this);
    }
}
