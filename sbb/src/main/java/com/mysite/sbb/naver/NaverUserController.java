package com.mysite.sbb.naver;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Optional;

import org.h2.util.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.sbb.email.MailService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class NaverUserController {
	private final UserService userService;
	private final NaverLoginService naverUserService;
	
	 // Login with Naver
	   @GetMapping("/oauth2/authorization/naver")
	   public void naverLogin(HttpServletRequest request, HttpServletResponse response) throws MalformedURLException, UnsupportedEncodingException, URISyntaxException {
	       String url = userService.getNaverAuthorizeUrl("authorize");
	       
	       System.out.println("URL:: " + url);
	       // https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=hSmbVr2J0fTOTrorFfBW&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Flogin%2Foauth2%2Fcode%2Fnaver&state=1234
	       try {
	           response.sendRedirect(url);
	           System.out.println("URL1:: " + url);
	       } catch (Exception e) {
	           e.printStackTrace();
	           System.out.println("Error: " + e);
	       }
	
	   }
	
	@GetMapping("/login/oauth2/code/naver")
	public String LoginNaver(HttpServletRequest request,@RequestParam("code") String code, @RequestParam("state")String state) throws JsonMappingException, JsonProcessingException {
		System.out.println("###code" + code);
		System.out.println("###state" + state);
		String accessToken = naverUserService.getAccessToken(code, state);
		System.out.println("accessToken" + accessToken);
		//{"access_token":"AAAAOfK8iNLi5s0LKmxdXqCPRvLDlDywUeLchr6Oap4JZW29BdntKOyVUK0yB-Y2T3t-XhsrorwUqMNswk40YFEmM5g","refresh_token":"7Mii60d6cJRVCbWisNoliiOkDOPUip8XhnZwNCgXIMLQkqBxQznH3Y2ezBLFKE5JY94HOvNBxjCBEj0Z8TZ7UagisnhdGEiinCzrS7hkZj4MNiiaiiD9AtTYpg7BOLOTrGHdkp84","token_type":"bearer","expires_in":"3600"}
		
		String UserInfo = naverUserService.getUserInfo(accessToken);
		System.out.println("UserInfo" + UserInfo); 
		//{"resultcode":"00","message":"success","response":{"id":"zK-ReIzcuJWvVny2KDlXQ2WwIXIU6GHXgC31GqXIOkw","email":"jihee9711@naver.com","name":"\ub0a8\uad81\uc9c0\ud76c"}}
		
		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode userInfoNode = objectMapper.readTree(UserInfo);

        String email = userInfoNode.get("response").get("email").asText();
        String name = userInfoNode.get("response").get("name").asText();
        System.out.println("email: " + email);
        System.out.println("name: " + name);
        
        // db에 존재하는 사용자일 경우에는 db저장 skip
        Optional<SiteUser> existingUser = userService.findByEmail(email);
        System.out.println("existingUser:" + existingUser);
        SiteUser user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            if (user.getUserName().equals(name)) {
                System.out.println("DB에 저장된 userName: " + user.getUserName());
            } 
        } else {
            // 새로운 사용자 생성 및 저장
            naverUserService.saveUserInfo(email, name);
        }
        
        // 사용자를 현재 인증된 사용자로 설정
        UserDetails userDetails = userService.loadUserByUsername(name); 
        // Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // SecurityContext에 Authentication 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 세션에 Authentication 저장
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        
		return "redirect:/"; 
	}

}
