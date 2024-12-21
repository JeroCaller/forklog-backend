package com.acorn.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acorn.entity.Eateries;

public interface EateriesRepository extends JpaRepository<Eateries, Integer> {
	
	boolean existsByNameAndLongitudeAndLatitude(String name, BigDecimal longitude, BigDecimal latitude);
}
