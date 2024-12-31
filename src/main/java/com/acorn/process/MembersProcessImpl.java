package com.acorn.process;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.acorn.dto.MembersDto;
import com.acorn.dto.ProfileDto;
import com.acorn.entity.Members;
import com.acorn.repository.MembersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembersProcessImpl implements MembersProcess {

	private final MembersRepository membersRepository;
	private final PasswordEncoder passwordEncoder;

	// 계정 조회
	@Override
	public ResponseEntity<?> readAccount() {

		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			//System.out.println("authentication" +authentication);
			String email = authentication.getName(); // 현재 사용자의 이메일 가져오기
			//System.out.println("인증된 사용자 이메일: " + email); // 인증된 이메일 출력
			Members member = membersRepository.findByEmail(email);

			MembersDto response = MembersDto.builder()
					.nickname(member.getNickname())
					.phone(member.getPhone())
					.postcode(member.getPostcode())
					.roadAddress(member.getRoadAddress())
					.detailAddress(member.getDetailAddress())
					.build();

			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
		}
	}

	// 수정
	@Override
	public ResponseEntity<?> updateAccount(@RequestBody ProfileDto dto) {
		try {
			// 현재 인증된 사용자 가져오기
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			// 인증 정보가 없는 경우
			if (authentication == null || !authentication.isAuthenticated()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 인증이 필요합니다.");
			}

			String email = authentication.getName();
			System.out.println("인증된 사용자 이메일: " + email);
			
			// 이메일이 비어있는 경우
			if (email == null || email.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 이메일을 가져올 수 없습니다.");
			}

			// 사용자 정보 조회
			Members member = membersRepository.findByEmail(email);
			if (member == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
			}

			// 입력된 현재 비밀번호와 DB에 저장된 비밀번호 비교
			if (!passwordEncoder.matches(dto.getCurrentPassword(), member.getPassword())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("현재 비밀번호가 일치하지 않습니다.");
			}

			// 닉네임 중복 체크
			if (membersRepository.existsByNickname(dto.getNickname())
					&& !dto.getNickname().equals(member.getNickname())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 닉네임입니다.");
			}

			// 전화번호 중복 체크
			Optional<Members> existingMember = membersRepository.findByPhone(dto.getPhone());
			if (existingMember.isPresent()) {
				Members existMember = existingMember.get();

				// 로그인한 사용자의 전화번호와 비교하여 다르면 중복으로 처리
				if (!existMember.getEmail().equals(member.getEmail())) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 휴대전화 번호입니다.");
				}
			}

			// 비밀번호 변경
			String password = dto.getChangePassword(); // 회원가입 시 입력받은 패스워드
			String encodedPassword = passwordEncoder.encode(password); // 암호화된 패스워드로 변환

			//System.out.println("현재 비밀번호: " + dto.getCurrentPassword());
			//System.out.println("DB 저장된 비밀번호: " + member.getPassword());

			// 업데이트할 회원 정보 설정
			member.setPassword(encodedPassword);
			member.setNickname(dto.getNickname());
			member.setPhone(dto.getPhone());
			member.setPostcode(dto.getPostcode());
			member.setRoadAddress(dto.getRoadAddress());
			member.setDetailAddress(dto.getDetailAddress());
			member.setUpdatedAt(dto.getUpdatedAt());

			// 데이터베이스에 저장
			membersRepository.save(member);

			return ResponseEntity.ok("회원 정보가 성공적으로 수정되었습니다.");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
		}
	}

	// 닉네임 중복 검사
	@Override
	public ResponseEntity<?> checkNicknameDuplication(String nickname) {

		try {
			boolean existedNickname = membersRepository.existsByNickname(nickname);

			if (existedNickname) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 사용 중인 닉네임입니다.");
			}

			return ResponseEntity.ok("사용 가능한 닉네임입니다.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
		}
	}

	// 탈퇴
	@Override
	public ResponseEntity<?> deleteAccount(ProfileDto dto) {
		try {
			// 현재 인증된 사용자 가져오기
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String email = authentication.getName();
			System.out.println("인증된 사용자 이메일: " + email); // 인증된 이메일 출력
			// 회원 정보 조회
			Members member = membersRepository.findByEmail(email);

			// 입력 정보 검증
			if (!passwordEncoder.matches(dto.getCurrentPassword(), member.getPassword())) {
				throw new RuntimeException("비밀번호가 일치하지 않습니다.");
			}

			if (!member.getPhone().equals(dto.getPhone())) {
				throw new RuntimeException("휴대전화 번호가 일치하지 않습니다.");
			}

			// 회원 삭제
			membersRepository.delete(member); // 실제 삭제

			return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
		}
	}
	
	// 재욱
	@Override
	public ResponseEntity<?> getMemberNo() {
	    try {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        String email = authentication.getName();
	        Members member = membersRepository.findByEmail(email);

	        return ResponseEntity.ok(member.getNo());
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
	    }
	}

}
