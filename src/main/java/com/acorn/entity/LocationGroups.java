package com.acorn.entity;

import java.util.List;

import com.acorn.dto.LocationGroupsDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * locations_groups 테이블과 매핑될 엔티티
 * 
 * @author JeroCaller (JJH)
 */
@Entity
@Table(name = "location_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationGroups {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, length = 11)
	private Integer no;
	
	@Column(nullable = false, length = 50)
	private String name;
	
	@OneToMany(mappedBy = "locationGroups")
	List<Locations> locations;
	
	public static LocationGroups toEntity(LocationGroupsDto dto) {
		return LocationGroups.builder()
				.no(dto.getNo())
				.name(dto.getName())
				.build();
	}
}
