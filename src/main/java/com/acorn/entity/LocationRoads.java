package com.acorn.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * location_roads 테이블과 매핑될 엔티티
 * 
 * @author JeroCaller (JJH)
 */
@Entity
@Table(name = "location_roads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationRoads {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, length = 11)
	private Integer no;
	
	@Column(nullable = false, length = 50)
	private String name;
	
	// 연관된 엔티티도 같이 한꺼번에 저장하고자 할 때 
	// cascade = CascadeType.PERSIST 속성을 
	// ManyToOne 등의 매핑 어노테이션에 적용한다.
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "location_no", nullable = false)
	private Locations locations;
	
}
