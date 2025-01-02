package com.acorn.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 지역 필터 등의 이유로 필요한 엔티티
 */
@Entity
@Table(name = "location_groups")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationGroups {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, length = 11)
	private int no;
	
	@Column(nullable = false, length = 50)
	private String name;
	
	@OneToMany(mappedBy = "groups", fetch = FetchType.EAGER)
	private List<Locations> locations;
}
