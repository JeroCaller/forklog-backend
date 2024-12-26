package com.acorn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.acorn.entity.Locations;

public interface LocationsRepository extends JpaRepository<Locations, Integer> {
	
	/**
	 * 찾고자 하는 지역 중분류 이름을 대입하여 조회
	 * 
	 * @author JeroCaller (JJH)
	 * @param name - 찾고자 하는 지역 중분류 명 
	 * @return
	 */
	List<Locations> findByName(String name);
	
	/**
	 * 
	 * DB에 데이터가 없는 경우 null을 반환한다고 함. 그래서 int 대신 Integer로 작성함. 
	 * 
	 * @author JeroCaller (JJH)
	 * @return 현재 테이블 내 최고 PK 값
	 */
	@Query(value = "SELECT MAX(loc.no) FROM Locations loc")
	Integer findbyIdMax();
	
}
