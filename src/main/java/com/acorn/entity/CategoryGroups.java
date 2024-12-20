package com.acorn.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
public class CategoryGroups {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer groupNo;
	
	private String name;
	
	@OneToMany(mappedBy = "group")
	private List<Categories> categories;
}
