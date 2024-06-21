package com.mysite.sbb.email;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.email.MailService;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.UserService;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class MailController {
	   
	   private final MailService mailService;
	   // 이메일 보내기
	    @Transactional
	    @PostMapping("/findpwd")
	    public String sendEmail(Model model, @RequestParam("email") String email){
	       MailDTO dto = mailService.createMailChangePwd(email);
	        mailService.mailSend(dto);
	        model.addAttribute("isSuccess", "true");
	        return "findpwd_form";
	    }
	}
