package com.acorn.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.acorn.entity.Eateries;

public interface EateriesRepository extends JpaRepository<Eateries, Integer> {
	
	boolean existsByNameAndLongitudeAndLatitude(String name, BigDecimal longitude, BigDecimal latitude);
}
