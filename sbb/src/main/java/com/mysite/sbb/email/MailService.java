package com.mysite.sbb.email;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.QuestionRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.SimpleMailMessage;

import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserRepository;
import com.mysite.sbb.DataNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
@RequiredArgsConstructor
@Service
public class MailService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	// 임시비밀번호로 업데이트
	public void updatePassword(String pwd, String email) {
		String tempPassword = pwd;
		Long userId = this.userRepository.findByEmail(email).get().getUserId();
		String userName = this.userRepository.findByUserId(userId).get().getUserName();
		SiteUser user = new SiteUser();
		user.setUserId(userId);
		user.setPassword(passwordEncoder.encode(tempPassword));
		user.setUserName(userName);
		user.setEmail(email);
		this.userRepository.save(user);
	}
	
	//랜덤함수로 임시비밀번호 구문
    public String getTempPassword(){
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        String str = "";
        // 문자 배열 길이의 값을 랜덤으로 10개를 뽑아 구문을 작성함
        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }
    
    // 메일 보내기
    @Autowired
    JavaMailSender mailSender;
    
    public void mailSend(MailDTO mailDTO) {
		 System.out.println("===== 이메일 전송 =====");
		 SimpleMailMessage message = new SimpleMailMessage();
		 message.setTo(mailDTO.getAddress());
		 message.setSubject(mailDTO.getTitle());
		 message.setText(mailDTO.getMessage());
		 message.setFrom("jihee9711@naver.com");
//		 message.setReplyTo("보낸이@naver.com");
		 System.out.println("message:: "+message);
		 mailSender.send(message);
    }
    
    // 메일 내용을 생성하고 임시비번으로 사용자 비번 변경
    public MailDTO createMailChangePwd(String email) {
    	String tempPwd = getTempPassword();
    	MailDTO dto = new MailDTO();
    	dto.setAddress(email); // 이메일
    	dto.setTitle("게시판 임시비밀번호입니다.");
    	dto.setMessage("임시 비밀번호는 " + tempPwd + "입니다.");
    	updatePassword(tempPwd, email); // 임시 비번으로 바뀜
    	return dto;
    }
    
	// 새 비밀번호로 업데이트
	public void updateNewPassword(String userName, String bPwd, String aPwd) {
		String tempPassword = bPwd; // 임시 비번
		String newPassword= aPwd; // 새 비번
		
		Long userId = this.userRepository.findByUserName(userName).get().getUserId();
		String userEmail = this.userRepository.findByUserName(userName).get().getEmail();
		SiteUser user = new SiteUser();
		user.setUserId(userId);
		user.setPassword(passwordEncoder.encode(newPassword));
		user.setUserName(userName);
		user.setEmail(userEmail);
		this.userRepository.save(user);
	}
}
