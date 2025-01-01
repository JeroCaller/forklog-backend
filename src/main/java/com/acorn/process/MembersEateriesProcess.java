package com.acorn.process;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acorn.common.MemberRole;
import com.acorn.dto.EateriesDto;
import com.acorn.dto.MembersResponseDto;
import com.acorn.entity.Categories;
import com.acorn.entity.Eateries;
import com.acorn.entity.Members;
import com.acorn.exception.category.NoCategoryFoundException;
import com.acorn.exception.eatery.NoEateriesFoundException;
import com.acorn.exception.member.AnonymousAlertException;
import com.acorn.exception.member.NoMemberFoundException;
import com.acorn.exception.member.NotRegisteredMemberException;
import com.acorn.repository.CategoriesRepository;
import com.acorn.repository.EateriesRepository;
import com.acorn.repository.MembersRepository;
import com.acorn.utils.AuthUtil;
import com.acorn.utils.ListUtil;
import com.acorn.utils.PageUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembersEateriesProcess {
	
	private final MembersRepository membersRepository;
	private final CategoriesRepository categoriesRepository;
	private final EateriesRepository eateriesRepository;
	
	/**
	 * 현재 로그인한 사용자의 비민감 정보 반환.
	 * 
	 * 패스워드, 거주지 상세 주소 등의 민감한 정보가 사용자 정보에 있으므로 
	 * 보안을 위해 비민감 정보만 반환하도록 함.
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 * @throws AnonymousAlertException - 현재 사용자가 비로그인 익명인일 경우 발생.
	 * @throws NotRegisteredMemberException 
	 * - 현재 사용자가 로그인한 사람으로 인식되었으나 DB에 등록되지 않은 사용자일 때 발생.
	 */
	public MembersResponseDto getLoginedMember()
			throws AnonymousAlertException, NotRegisteredMemberException {
		
		Authentication authentication = SecurityContextHolder
				.getContext()
				.getAuthentication();
		
		String email = authentication.getName();
		String role = AuthUtil.getRole(authentication);
		
		if (role.equals(MemberRole.ROLE_ANONYMOUS)) {
			throw new AnonymousAlertException();
		}
		
		Members members = membersRepository.findByEmail(email);
		if (members == null) {
			NotRegisteredMemberException e = new NotRegisteredMemberException();
			// DB에 등록되지 않은 사용자 정보 확인용
			e.setEmail(email);
			e.setRole(role);
			throw e;
		}
		
		return MembersResponseDto.toDto(members);
		
	}
	
	/**
	 * 현재 사용자가 즐겨찾기한 음식점들의 카테고리와 동일한 카테고리를 가지는 모든 
	 * 음식점들을 가져와 반환한다.
	 * 
	 * @author JeroCaller (JJH)
	 * @param memberNo
	 * @return
	 * @throws NoMemberFoundException 
	 * @throws NoCategoryFoundException 
	 * @throws NoEateriesFoundException 
	 */
	// 여러 엔티티를 조회하므로 이 작업들을 하나의 트랙잰션으로 묶음
	// 또한 readOnly = true로 할 시 조회 과정만 있음을 명시할 수 있을 뿐만 아니라 
	// 조회 과정의 속도가 더 빨라진다고 한다.
	//@Transactional(readOnly = true)  
	public Page<EateriesDto> getEateriesByRecommend(
			int memberNo, 
			Pageable pageRequest
	) throws NoMemberFoundException, 
		NoCategoryFoundException, 
		NoEateriesFoundException 
	{
		
		// 멤버 ID에 해당하는 멤버 엔티티 조회.
		Optional<Members> membersOpt = membersRepository.findById(memberNo);
		Members member = membersOpt.orElseThrow(
				() -> new NoMemberFoundException(String.valueOf(memberNo))
		);
		
		/*
		log.info("현재 로그인 사용자 정보");
		log.info("이메일 : " + member.getEmail());
		log.info("ID 번호: " + member.getNo());
		*/

		List<Categories> memberCategories = categoriesRepository
				.findByMemberFavorite(member);
		if (ListUtil.isEmpty(memberCategories)) {
			throw new NoCategoryFoundException("사용자가 즐겨찾기한 음식점이 없는 듯 합니다.");
		}
		
		/*
		log.info("사용자가 즐겨찾기한 음식점들의 카테고리 정보");
		memberCategories.forEach(cate -> {
			log.info("카테고리 대분류 번호: " + cate.getGroup().getNo());
			log.info("카테고리 소분류 번호: " + cate.getNo());
			log.info("카테고리 명: " + cate.getGroup().getName() + " " + cate.getName());
			log.info("-----------------");
		}); */
		
		Page<Eateries> eateries = eateriesRepository
				.findByCategoryIn(memberCategories, pageRequest);
		
		if (PageUtil.isEmtpy(eateries)) {
			throw new NoEateriesFoundException();
		}
		
		return eateries.map(EateriesDto :: toDto);
	}
	
}
