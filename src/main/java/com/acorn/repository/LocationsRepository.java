package com.acorn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
	 * 지역 대분류 이름과 함께 정확한 지역 중분류 엔티티 하나를 조회.
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	@Query(value = """
		SELECT loc
		FROM Locations loc
		JOIN loc.locationGroups locG
		WHERE locG.name = :largeCity AND
		loc.name = :mediumCity
	""")
	Locations findByNameExactly(
			@Param("largeCity") String largeCity, 
			@Param("mediumCity") String mediumCity
	);
	
	/**
	 * 
	 * DB에 데이터가 없는 경우 null을 반환한다고 함. 그래서 int 대신 Integer로 작성함. 
	 * 
	 * @author JeroCaller (JJH)
	 * @return 현재 테이블 내 최고 PK 값
	 */
	@Query(value = "SELECT MAX(loc.no) FROM Locations loc")
	Integer findbyIdMax();
	
	/**
	 * 주어진 주소 대분류 엔티티의 no 필드에 부합하는 모든 
	 * 중분류 주소 엔티티를 반환.
	 * 예) 서울 -> 강남구, 강서구, ...
	 * 
	 * @param groupNo - locationGroup.no
	 * @return
	 */
	@Query(value = """
		SELECT loc
		FROM Locations loc
		JOIN loc.locationGroups locG
		WHERE locG.no = :gno
	""")
	List<Locations> findAllBy(@Param("gno") int groupNo);
	
}
