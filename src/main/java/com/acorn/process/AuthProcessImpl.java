package com.acorn.process;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.acorn.dto.LoginRequestDto;
import com.acorn.dto.LoginRepsonseDto;
import com.acorn.dto.RegisterRequestDto;
import com.acorn.dto.RegisterResponseDto;
import com.acorn.dto.ResponseDto;
import com.acorn.entity.Members;
import com.acorn.entity.RefreshToken;
import com.acorn.jwt.JwtUtil;
import com.acorn.repository.MembersRepository;
import com.acorn.repository.RefreshTokenRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthProcessImpl implements AuthProcess {

	private final JwtUtil jwtUtil;
	private final AuthenticationManager authenticationManager;
	private final MembersRepository membersRepository;
	private final CustomUserDetailService customUserDetailService;
	//private final RefreshTokenRepository refreshTokenRepository;
	private final MailProcess mailProcess;
	private final PasswordEncoder passwordEncoder;

	// 회원가입
	@Override
	public ResponseEntity<? super RegisterResponseDto> register(RegisterRequestDto dto) {

		try {

			String email = dto.getEmail();
			boolean existedEmail = membersRepository.existsByEmail(email);
			if (existedEmail)
				return RegisterResponseDto.duplicateEmail();

			String phone = dto.getPhone();
			boolean existedPhone = membersRepository.existsByPhone(phone);
			if (existedPhone)
				return RegisterResponseDto.duplicatePhone();

			String password = dto.getPassword(); // 회원가입 시 입력받은 패스워드
			String encodedPassword = passwordEncoder.encode(password); // 암호화된 패스워드로 변환
			dto.setPassword(encodedPassword); // 암호화된 패스워드 넣어줌

			Members members = Members.registerToEntity(dto);

			// 데이터베이스에 저장
			membersRepository.save(members);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseDto.databaseError(); // 에러 시 데이터베이스 오류 응답
		}

		return RegisterResponseDto.success(); // 성공 응답
	}
	
	// 로그인
	@Override
	public ResponseEntity<? super LoginRepsonseDto> login(@RequestBody LoginRequestDto dto,
	                                                      HttpServletResponse response) {
	    String email = dto.getEmail();
	    String password = dto.getPassword();

	    try {
	        // 이메일 유효성 검사
	        UserDetails userDetails = customUserDetailService.loadUserByUsername(email);
	        if (userDetails == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                                 .body("가입된 계정이 없습니다.");
	        }

	        // 비밀번호 유효성 검사
	        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                                 .body("이메일 또는 비밀번호가 잘못되었습니다.");
	        }

	        // JWT 토큰 생성
	        String accessToken = jwtUtil.createAccessToken(email);
	        String refreshToken = jwtUtil.createRefreshToken(email);

	        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
	        accessTokenCookie.setHttpOnly(true);
	        accessTokenCookie.setSecure(false); // HTTPS 사용하는 경우 true로 변경
	        accessTokenCookie.setPath("/");
	        accessTokenCookie.setMaxAge(3600);
	        response.addCookie(accessTokenCookie);

	        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
	        refreshTokenCookie.setHttpOnly(true);
	        refreshTokenCookie.setSecure(false); // HTTPS 사용하는 경우 true로 변경
	        refreshTokenCookie.setPath("/");
	        refreshTokenCookie.setMaxAge(604800);
	        response.addCookie(refreshTokenCookie);

	        return ResponseEntity.ok(LoginRepsonseDto.success(accessToken, refreshToken));

	    } catch (BadCredentialsException e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                             .body("이메일 또는 비밀번호가 잘못되었습니다.");
	    } catch (UsernameNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                             .body("가입된 계정이 없습니다.");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("로그인 중 오류 발생: " + e.getMessage());
	    }
	}

	// 로그아웃
	@Override
	public ResponseEntity<?> logout(HttpServletResponse response) {

		try {
//			// 클라이언트로부터 받은 리프레시 토큰을 쿠키에서 추출
//			String refreshToken = null;
//			Cookie[] cookies = request.getCookies();
//			if (cookies != null) {
//				for (Cookie cookie : cookies) {
//					System.out.println("Cookie name: " + cookie.getName() + ", Cookie value: " + cookie.getValue());
//					if ("refreshToken".equals(cookie.getName())) {
//						refreshToken = cookie.getValue();
//						System.out.println("Found refreshToken in cookie: " + refreshToken);
//						break;
//					}
//				}
//			}
//			System.out.println("Received refreshToken: " + refreshToken); // 로그 찍어서 토큰 값 확인
//
//			if (refreshToken != null) {
//				// 리프레시 토큰을 DB에서 삭제
//				refreshTokenRepository.deleteByRefreshToken(refreshToken);
//			}

			// 엑세스 토큰 쿠키 제거
			Cookie accessTokenCookie = new Cookie("accessToken", null);
			accessTokenCookie.setHttpOnly(true);
			accessTokenCookie.setSecure(false);
			accessTokenCookie.setPath("/");
			accessTokenCookie.setMaxAge(0);
			response.addCookie(accessTokenCookie);

			// 엑세스 토큰 쿠키 제거
			Cookie refreshTokenCookie = new Cookie("refreshToken", null);
			refreshTokenCookie.setHttpOnly(true);
			refreshTokenCookie.setSecure(false);
			refreshTokenCookie.setPath("/");
			refreshTokenCookie.setMaxAge(0);
			response.addCookie(refreshTokenCookie);

			SecurityContextHolder.clearContext();

			System.out.println(
					"Logout accessToken : " + accessTokenCookie.getName() + "=" + accessTokenCookie.getValue());
			System.out.println(
					"Logout refreshToken : " + refreshTokenCookie.getName() + "=" + refreshTokenCookie.getValue());

			return ResponseEntity.ok().body("로그아웃 성공"); // 로그아웃 성공 시 응답
		} catch (Exception e) {
			return ResponseDto.databaseError();
		}
	}

	// 이메일 중복 체크
	@Override
	public ResponseEntity<? super RegisterResponseDto> checkEmailDuplication(String email) {
		boolean existedEmail = membersRepository.existsByEmail(email);
		if (existedEmail) {
			return RegisterResponseDto.duplicateEmail(); // 중복된 이메일
		}
		return RegisterResponseDto.success(); // 사용 가능한 이메일
	}

	// 이메일 찾기
	@Override
	public ResponseEntity<Map<String, Object>> findEmail(Map<String, String> user) {
		Optional<Members> memberOptional = membersRepository.findByPhone(user.get("phone"));

		Map<String, Object> response = new HashMap();

		if (memberOptional.isPresent()) {
			Members membersMain = memberOptional.get();
			String name = membersMain.getName();
			String inputName = user.get("name");

			if (name.equals(inputName)) {
				try {
					String email = membersMain.getEmail();
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
		Optional<Members> memberOptional = membersRepository.findOptionalByEmail(user.get("email"));

		if (memberOptional.isPresent()) {
			Members membersMain = memberOptional.get();
			String phone = membersMain.getPhone();
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

//	// 리프레쉬 토큰 DB 저장
//	private void saveRefreshToken(String email, String refreshToken) {
//		// 기존에 리프레시 토큰이 있으면 삭제하고 새로 저장
//		Optional<RefreshToken> existingToken = refreshTokenRepository.findByEmail(email);
//		existingToken.ifPresent(refreshTokenRepository::delete); // 기존 리프레시 토큰 삭제
//
//		// 새로운 리프레시 토큰 저장
//		RefreshToken refreshTokenEntity = new RefreshToken();
//		refreshTokenEntity.setEmail(email);
//		refreshTokenEntity.setRefreshToken(refreshToken);
//		refreshTokenEntity.setExpiryDate(LocalDateTime.now().plusWeeks(1)); // 리프레시 토큰의 만료일을 설정 (1주일)
//		refreshTokenRepository.save(refreshTokenEntity);
//	}
}