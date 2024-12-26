package com.acorn.entity;

import java.math.BigDecimal;

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
import lombok.ToString;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Eateries {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer no;
	
	@Column(nullable = false, length = 255)
	private String name;
	
	@Column(length = 11)
	private int viewCount;
	
	@Column(length = 11)
	private int favoritesCount;
	
	@Column(length = 500)
	private String thumbnail;
	
	@Column(columnDefinition = "text") // mysql text type
	private String description;
	
	@Column(precision = 2, scale = 1) // decimal(2,1)과 매핑 : 2자리 중 1자리는 소수점 이하
	@Builder.Default
	private BigDecimal rating = new BigDecimal(0.0);
	
	@Column(precision = 11, scale = 8) // decimal(11, 8)과 매핑 - ex. 127.06547254
	private BigDecimal longitude;
	
	@Column(precision = 10, scale = 8) 
	private BigDecimal latitude;
	
	@Column(length = 20)
	private String tel;
	
	@ManyToOne
	@JoinColumn(name = "category_no", referencedColumnName = "no")
	private Categories category;
	
	@ManyToOne
	@JoinColumn(name = "road_no", referencedColumnName = "no")
	private LocationRoads road;
}
