package com.mysite.sbb.naver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

import org.h2.util.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.mysite.sbb.user.ApplicationEnvironmentConfig;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserRepository;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import org.springframework.security.core.Authentication;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service

public class NaverLoginService {
	private final UserRepository userRepository;
    private final ApplicationEnvironmentConfig envConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final UserDetailsService userDetailsService;
    
	public String getAccessToken(String code, String state) {
		// naver로부터 AccessToken 발급요청
		String tokenRequestUrl = "https://nid.naver.com/oauth2.0/token";
		String clientId = envConfig.getNaverClientId();
		String clientSecret = envConfig.getNaverClientSecret();
		
		
		LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.set("grant_type", "authorization_code");
		params.set("client_id", clientId);
		params.set("client_secret", clientSecret);
		params.set("code", code);
		params.set("state", state); 
		
		// HTTP 요청 보내기
		String response = restTemplate.postForObject(tokenRequestUrl, params, String.class);
		return response;
	}
	
	public String getUserInfo(String accessToken) {
		// 사용자 정보 return
		String RequestUrl = "https://openapi.naver.com/v1/nid/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
 
        // JSON 문자열을 JsonObject로 변환
        JsonObject jsonObject = JsonParser.parseString(accessToken).getAsJsonObject();

        // "access_token" 키에 해당하는 값 추출
        String parseToken = jsonObject.get("access_token").getAsString();

        headers.set("Authorization", "Bearer " + parseToken);
        System.out.println("headers: " + headers);
    
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        String response = restTemplate.postForEntity(RequestUrl, request, String.class).getBody();

        return response;
	}
	
	public void saveUserInfo(String email, String name) {
		SiteUser user = new SiteUser();
		user.setEmail(email);
		user.setUserName(name);
		this.userRepository.save(user);

	}
    public Optional<SiteUser> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
	  public void saveUserIfNotExist(String email, String username) {
	        Optional<SiteUser> existingUser = findByEmail(email);
	        if (!existingUser.isPresent()) {
	            saveUserInfo(email, username);
	        }
	    }
	
}
