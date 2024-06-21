package com.mysite.sbb.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import javassist.tools.Callback;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.Principal;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mysite.sbb.email.MailService;
//import com.mysite.sbb.user.UserService.LoginService;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
	private final UserService userService;
	private final MailService mailService;
	
	@GetMapping("/signup")
	public String signup(UserCreateForm userCreateForm) {
		return "signup_form";
	}
	
	@PostMapping("/signup")
	public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "signup_form";
		}
		if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
			bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 비밀번호가 일치하지 않습니다.");
			return "signup_form";
		}
		try {
		userService.create(userCreateForm.getUserName(), userCreateForm.getEmail(), userCreateForm.getPassword1());
		} catch(DataIntegrityViolationException e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
			return "signup_form";
		} catch(Exception e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", e.getMessage());
			return "signup_form";
		}
		return "redirect:/";
	}
	
	@GetMapping("/login")
	public String login() {
		return "login_form";
	}
	
	@GetMapping("/findid")
	public String findid() {
		return "findid_form";
	}
	
   @PostMapping("/findid")
   public String findid(Model model, @RequestParam("email") String email) {
      String username = this.userService.findEmail(email);
      if (username != null) 
          model.addAttribute("username", username);
          return "findid_form";   
}
   @GetMapping("/findpwd")
   public String findpwd() {
	   return "findpwd_form";
   }
   

   @GetMapping("/changepwd")
   public String changepwd(UserPwdForm userPwdForm) {
	   return "change_pwd";
   }
   
   @PostMapping("/changepwd")
	public String changepwd(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("newPassword1") String newPassword1, @RequestParam("newPassword2") String newPassword2, @Valid UserPwdForm userPwdForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "change_pwd";
		}
		if (!userPwdForm.getNewPassword1().equals(userPwdForm.getNewPassword2())) {
			bindingResult.rejectValue("newPassword2", "passwordInCorrect", "2개의 비밀번호가 일치하지 않습니다.");
			return "change_pwd";
		}
		if (!this.userService.checkPassword(username, password)) {
	        return "change_pwd";
	    }
		this.mailService.updateNewPassword(username, password, newPassword1);
		return "redirect:/";
	}
   
   @GetMapping("/profile")
   public String myProfile(Model model, Principal principal, HttpSession session, HttpServletRequest request) {
	   String imagePath = this.userService.getProfileImagePath(principal.getName());
       System.out.println("IMAGEPATH: " + imagePath);
       if(imagePath != null && ! imagePath.isEmpty()) {
           model.addAttribute("profileImage", imagePath);
           System.out.println("User's ImagePath");
           
       } else {
           model.addAttribute("profileImage", "/profile.jpg");
           System.out.println("Default ImagePath");
       }
       return "profile";
   }

   
   @PostMapping("/uploadProfileImage")
   public String fileUpload(@RequestParam("profileImage") MultipartFile file, HttpSession session, Principal principal, Model model, HttpServletRequest request) throws Exception {
	   String imagePath = null;
	   if (file != null && !file.isEmpty()) { 
	        imagePath = this.userService.saveProfileImage(principal.getName(), file);
	    }
	   model.addAttribute("profileImage", imagePath );
	   System.out.println("imageName: " + imagePath );
	   session = request.getSession();
	   session.setAttribute("profileImage", imagePath);
	   System.out.println("profileImage: " + session.getAttribute("profileImage"));
	   return "redirect:/user/profile";
   }
   
}
