package com.acorn.entity;

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
public class LocationRoads {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer no;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "location_no", insertable = false, updatable = false)
	private Integer locationNo;
	
	@OneToOne(mappedBy = "locationRoads", fetch = FetchType.LAZY)
	private MembersMain membersMain; // MembersMain과의 관계

	// MembersMain 설정 메소드
    public void setMembersmain(MembersMain membersMain) {
        this.membersMain = membersMain;
        membersMain.setLocationRoads(this);
    }
}
