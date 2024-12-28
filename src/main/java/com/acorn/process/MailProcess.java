package com.acorn.process;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.acorn.entity.Members;
import com.acorn.repository.MembersRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

// MailProcess : 이메일에 난수를 포함한 링크를 보내주는 서비스
@Service
@RequiredArgsConstructor
public class MailProcess {
	
	private final JavaMailSender javaMailSender;
	private final CertificationGenerator certificationGenerator;
	private final MembersRepository membersRepository;
	private final PasswordEncoder passwordEncoder;
	
	public void sendEmailForCertification(String email) throws NoSuchAlgorithmException, MessagingException {
		
		// 비밀번호 생성
		String certificationPassword = certificationGenerator.createCertificationPassword();
		
		//String img = "<img src='이미지 주소'/>";
		//String link = "<a href='http://localhost:3000/login'>로그인 링크</a>";
		
		// System.out.println("Link: " + link);
		System.out.println("Certification Password: " + certificationPassword);

		String content = String.format("<br> 임시비밀번호: %s <br><br><br> 로그인 후 마이페이지에서 비밀번호를 수정해주세요.",
	            certificationPassword
	           );

        // 비밀번호 해싱
        String password = passwordEncoder.encode(certificationPassword);
        System.out.println("Hashed password to save: " + password);
        
        // DB에 비밀번호 저장
        Optional<Members> optionalMember = membersRepository.findOptionalByEmail(email);
        if (optionalMember.isPresent()) {
        	Members member = optionalMember.get();
        	member.setPassword(password); // 비밀번호 설정
        	membersRepository.save(member); // 변경된 비밀번호를 DB 저장
        } else {
            System.out.println("No member found with email: " + email); // 이메일로 회원을 찾을 수 없을 때
        }

        // 이메일 전송
        sendPasswordMail(email, content);
        System.out.println("Email sent to: " + email); // 이메일 전송 여부 확인
    }

    private void sendPasswordMail(String email, String content) throws MessagingException {

        // 이메일 객체 생성
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        // 수신자, 제목, 내용 설정
        helper.setTo(email);
        helper.setSubject("PROJECT 비밀번호 변경 메일");
        helper.setText(content, true); // html변환 전달

        // 메일 전송
        javaMailSender.send(mimeMessage);
	}
}
