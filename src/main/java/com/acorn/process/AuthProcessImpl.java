package com.acorn.process;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.acorn.dto.LoginRequestDto;
import com.acorn.dto.JwtDto;
import com.acorn.dto.LoginRepsonseDto;
import com.acorn.dto.RegisterRequestDto;
import com.acorn.dto.RegisterResponseDto;
import com.acorn.dto.ResponseDto;
import com.acorn.entity.MembersDetail;
import com.acorn.entity.MembersMain;
import com.acorn.jwt.JwtProvider;
import com.acorn.repository.MembersDetailRepository;
import com.acorn.repository.MembersMainRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthProcessImpl implements AuthProcess {

	private final JwtProvider jwtProvider;
	private final MembersMainRepository membersMainRepository;
	private final MembersDetailRepository membersDetailRepository;
	private final RefreshTokenProcess refreshTokenProcess;
	private final MailProcess mailProcess;

	// 비밀번호 암호화를 위한 인코더
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Override
	public ResponseEntity<? super RegisterResponseDto> register(RegisterRequestDto dto) {

		try {

			String email = dto.getEmail();
			boolean existedEmail = membersMainRepository.existsByEmail(email);
			if (existedEmail)
				return RegisterResponseDto.duplicateEmail();

			String phone = dto.getPhone();
			boolean existedPhone = membersDetailRepository.existsByPhone(phone);
			if (existedPhone)
				return RegisterResponseDto.duplicatePhone();

			String password = dto.getPassword(); // 회원가입 시 입력받은 패스워드
			String encodedPassword = passwordEncoder.encode(password); // 암호화된 패스워드로 변환
			dto.setPassword(encodedPassword); // 암호화된 패스워드 넣어줌

			// MembersMain과 MembersDetail 객체 생성
			MembersMain members = MembersMain.registerToEntity(dto);
			MembersDetail membersDetail = MembersDetail.registerToEntity(dto);

			// 객체 연결 및 memberNo 설정
			membersDetail.setMemberNo(members.getNo()); // memberNo 설정
			members.setMembersDetail(membersDetail); // MembersDetail 설정
			membersDetail.setMembersMain(members); // 양방향 관계 설정

			// 데이터베이스에 저장
			membersMainRepository.save(members);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseDto.databaseError(); // 에러 시 데이터베이스 오류 응답
		}

		return RegisterResponseDto.success(); // 성공 응답
	}
	
	// 로그인
	@Override
	public ResponseEntity<? super LoginRepsonseDto> login(LoginRequestDto dto) {

		String accessToken = null;
		String refreshToken = null;

		try {
			String email = dto.getEmail(); // 로그인 입력 이메일
			MembersMain members = membersMainRepository.findByEmail(email); // 해당 이메일 계정 정보 조회
			if (members == null)
				return LoginRepsonseDto.loginFailed(); // 계정이 없다면 로그인 실패 응답

			String password = dto.getPassword(); // 클라이언트 입력 패스워드
			String encodedPassword = members.getPassword(); // 데이터베이스에 저장되어있는 암호화된 패스워드
			boolean isMatched = passwordEncoder.matches(password, encodedPassword); // 암호화된 패스워드와 입력 패스워드를 검증
			if (!isMatched)
				return LoginRepsonseDto.loginFailed(); // 패스워드가 같지 않다면 로그인 실패 응답

			// JWT 페이로드에 추가할 정보
			JwtDto jwtDto = new JwtDto();
			jwtDto.setEmail(email);

			accessToken = jwtProvider.createAccessToken(jwtDto); // 로그인 성공 시 엑세스 토큰 생성

			// 리프레시 토큰 생성 및 저장
			refreshToken = jwtProvider.createRefreshToken(jwtDto); // 로그인 성공 시 엑세스 토큰 생성
			refreshTokenProcess.createRefreshToken(email, refreshToken, JwtProvider.REFRESH_TOKEN_EXPIRE_TIME); // 7일 만료

			// 액세스 토큰과 리프레시 토큰을 쿠키에 설정
			ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken).httpOnly(true)
					.secure(true).path("/").maxAge(3600) // 1시간
					.sameSite("None").build();

			ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken).httpOnly(true)
					.secure(true).path("/").maxAge(604800) // 7일
					.sameSite("None").build();

			System.out.println(accessTokenCookie);
			System.out.println(refreshTokenCookie);

			return ResponseEntity.ok() // 로그인 성공 시 응답
					.header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString()) // 쿠키를 응답 헤더에 포함
					.header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
					.body(LoginRepsonseDto.success(accessToken)); // 로그인 성공 응답 반환
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseDto.databaseError(); // 실패 시 데이터베이스 오류 응답
		}
	}

	// 이메일 중복 체크
	@Override
	public ResponseEntity<? super RegisterResponseDto> checkEmailDuplication(String email) {
		boolean existedEmail = membersMainRepository.existsByEmail(email);
		if (existedEmail) {
			return RegisterResponseDto.duplicateEmail(); // 중복된 이메일
		}
		return RegisterResponseDto.success(); // 사용 가능한 이메일
	}
	
	// 이메일 찾기
	@Override
	public ResponseEntity<Map<String, Object>> findEmail(Map<String, String> user) {
	    Optional<MembersDetail> memberOptional = membersDetailRepository.findByPhone(user.get("phone"));
	    
	    Map<String, Object> response = new HashMap();
	    
	    if (memberOptional.isPresent()) {
	        MembersDetail membersDetail = memberOptional.get();
	        String name = membersDetail.getMembersMain().getName();
	        String inputName = user.get("name");
	        
	        if (name.equals(inputName)) {
	            try {
	                String email = membersDetail.getMembersMain().getEmail();
	                response.put("status", "success");
	                response.put("email", email);
	                return ResponseEntity.ok(response); // JSON 형식으로 반환
	            } catch (Exception e) {
	                response.put("status", "error");
	                response.put("message", "이메일 반환 중 오류가 발생했습니다.");
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	            }
	        } else {
	            response.put("status", "error");
	            response.put("message", "입력된 이름이 일치하지 않습니다.");
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	        }
	    }

	    response.put("status", "error");
	    response.put("message", "해당 전화번호로 등록된 사용자가 없습니다.");
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	// 비밀번호 재설정
	@Override
	public ResponseEntity<String> findPassword(Map<String, String> user) {
		Optional<MembersMain> memberOptional = membersMainRepository.findOptionalByEmail(user.get("email"));

		if (memberOptional.isPresent()) {
			MembersMain membersMain = memberOptional.get();
			String phone = membersMain.getMembersDetail().getPhone();
			String inputPhone = user.get("phone");

			if (phone.equals(inputPhone)) {
				// CompletableFuture를 사용하여 비동기적으로 메일 전송
				// 이렇게 안하면 메일 전송하고 완료된 후에 넘어가는데 3초의 딜레이 발생
				CompletableFuture.runAsync(() -> {
					try {
						mailProcess.sendEmailForCertification(user.get("email"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				return ResponseEntity.ok("메일 전송 성공");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("입력된 번호가 일치하지 않습니다."); // 휴대전화 불일치
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 이메일로 등록된 사용자가 없습니다."); // 해당 이메일으로 등록된 계정이 없을 경우
		}
	}
}